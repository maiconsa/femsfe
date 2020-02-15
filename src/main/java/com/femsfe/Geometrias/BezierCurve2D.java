package com.femsfe.Geometrias;

import java.util.ArrayList;
import java.util.List;


public class BezierCurve2D extends Border2D {
	private List<Point2D> controlPoints;
	public boolean pointCtrlVisible = true;
	public boolean polygonBezierVisible = true;
	public List<Point2D> curvePoints;
	public boolean isClosed = false;
	public int m = 0;
	/* **********************************************************
	 * 				CONSTRCUTORS
	 * *********************************************************/
	public BezierCurve2D(List<Point2D> controlPoints) {
		 this();
		 for (Point2D ponto2d : controlPoints) {
			this.addPoint(ponto2d);
		}
		 
		
	}
	public void setCurvePoints(int numPoints){
		curvePoints  =  computeBezierCurve(numPoints);
	}
	 public BezierCurve2D(){
		 this.type = GeometryType.BEZIER;
		 controlPoints = new ArrayList<>();
		 
	 }
	 
	 
	 /*	***************************************************************
	  * 			ADD,GET AND REMOVE METHODS
	  * **************************************************************/
	 public void addPoint(Point2D point){
			 controlPoints.add(point);
	 }
	 public void addPoint(double x,double y){
		 controlPoints.add(new Point2D(x, y));
 }
	 public void removePoint(Point2D point){
		 controlPoints.remove(point);
	 }
	 public void removePoint(int index){
		 controlPoints.remove(index);
	 }
	 public Point2D getPoint(int index){
		return  controlPoints.get(index);
	 }
	 public void clearPointList(){
		 controlPoints.clear();	 
	 }
	 public Point2D getFirstCtrlPoint(){
		 if( controlPoints == null ||controlPoints.isEmpty()) return null;
		 return controlPoints.get(0);
	 }
	 public Point2D getLastCtrlPoint(){
		 if( controlPoints == null ||controlPoints.isEmpty()) return null;
		 return controlPoints.get(controlPoints.size()-1);
	 }

	 public List<Point2D> getControlPoints(){
		 return controlPoints;
	 }
	 public int sizePointList(){
		 return controlPoints.size();
	 }
	 
	 /*	***************************************************************
	  *				MATH METHODS
	  * **************************************************************/
	 private double factorial(int  n){
		 if(n == 0){
			 return 1;
		 }else{
			 return n*factorial(n-1);
		 }
	 }
	 
	 public List<Point2D> computeBezierCurve(int m ){
		 this.m = m ;
		 List<Point2D> bezierPoints = new ArrayList<>();
		 bezierPoints.add(getFirstCtrlPoint());
		 for (int i = 1; i <= m-1; i++) { 
			bezierPoints.add(bezierPoint((double)i/m));
		}
		 bezierPoints.add(getLastCtrlPoint());
		 return bezierPoints;
	 }
	 private Point2D bezierPoint(double t){
		 double x = 0,y = 0;
		 int s = controlPoints.size();
		 int n = s-1;
		 double nFac = factorial(n);
		 Point2D currentPoint;
		 for (int k = 0; k < s; k++) {
			 currentPoint = controlPoints.get(k);

			 double Bkn = (nFac*Math.pow(t, k)*Math.pow(1-t, n-k))/
					 (factorial(k)*factorial(n-k));
			 x+= currentPoint.getX()*Bkn;
			 y+= currentPoint.getY()*Bkn;
		 }
		 return  new Point2D(x,y);
	 }
	 

	public Geometry2D copy() {
		return new BezierCurve2D(getControlPoints());
	}

	public boolean isNear(Point2D point2d) {
		// TODO Auto-generated method stub
		return isNear(point2d, Geometry2D.EPSILON);
	}
	
	public boolean isNear(Point2D point2d, double epsilon) {
		// TODO Auto-generated method stub
		return isNear(point2d.getX(), point2d.getY(), epsilon);
	}

	public boolean isNear(double x, double y) {
		// TODO Auto-generated method stub
		return isNear(x, y, Geometry2D.EPSILON);
	}

	public boolean isNear(double x, double y, double epsilon) {
		List<Point2D> pts = computeBezierCurve(100);
		Point2D last = pts.get(0);
		for (int i = 1; i < pts.size(); i++) {
			Line2D line = new Line2D(last, pts.get(i));
			if(line.isNear(x, y, epsilon)){
				return true;
			}
		}
		return false;
	}



	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public Point2D[] getEndpoints() {
		Point2D[] pts = new Point2D[2];
		pts[0] = controlPoints.get(0);
		pts[1] = controlPoints.get(controlPoints.size()-1);
		return pts;
	}
	@Override
	public double getMaxCoordinate() {
		
	
		return 0;
	}
	@Override
	public List<Line2D> chewSegmentation(double hmin) {
		getChewPoints().clear();
		double length = getLength();
		int n = (int) (length/hmin);
		List<Point2D> pts = computeBezierCurve(n);
		
		Point2D firstPoint = getFirstCtrlPoint();
		Point2D lastPoint = getLastCtrlPoint();
		
		pts.add(0,firstPoint);
		pts.add(pts.size()-1,lastPoint);
		
		firstPoint.wasIntserted = false;
		firstPoint.clearLinkedPoints();
		firstPoint.clearLinkedTriangles();
		firstPoint.setIndex(-1);
		firstPoint.setValue(null);
		
		lastPoint.wasIntserted = false;
		lastPoint.clearLinkedPoints();
		lastPoint.clearLinkedTriangles();
		lastPoint.setIndex(-1);
		lastPoint.setValue(null);
		
		List<Line2D> lines = new ArrayList<>();
		Line2D line ;
		for (int i = 1; i < pts.size(); i++) {
			line = new Line2D(pts.get(i-1), pts.get(i));
			line.setAsFixedEdge();
			lines.add(line);
			pts.get(i).setFixed(true);
			addChewPoint(pts.get(i));
		}
		firstPoint.removeLinkedPoint(lastPoint);
		lastPoint.removeLinkedPoint(firstPoint);
		
		return lines;
	}
	@Override
	public double getLength() {
		double length = 0;
		if(curvePoints == null){
			curvePoints = computeBezierCurve(100);
			for (int i = 1; i < curvePoints.size(); i++) {
				length+= curvePoints.get(i).distanceTo(curvePoints.get(i-1));
			}
		}else{
			for (int i = 1; i < curvePoints.size(); i++) {
				length+= curvePoints.get(i).distanceTo(curvePoints.get(i-1));
			}
		}
		return length;
	}
	
	
	

	 
	 
	
	 
	

}
