package com.femsfe.Geometrias;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class Polygon2D extends Geometry2D   {
	private List<Point2D> pointList;
	private boolean isClosed = false;
	
	public Polygon2D(List<Point2D> pointList) {
		this.type = GeometryType.POLYGON;
		if(pointList != null && pointList.size() >=4 ){
			List<Point2D> copy = new ArrayList<>();
			for (Point2D ponto2d : pointList) {
				copy.add(new Point2D(ponto2d.getX(), ponto2d.getY()));
			}
			this.pointList = copy;
			isClosed = true;
		}else{
			this.pointList = new ArrayList<>();
		}
	}
	public Polygon2D(){
		this.type = GeometryType.POLYGON;
		pointList = new ArrayList<>();
	}
	
	
	/* ********************************************************************
	 * 				GEOMETRICS METHODS - AREA,CENTROID 
	 *********************************************************************/

	public Point2D centroid(){
		double xTotal = 0,yTotal = 0;
		for (Point2D ponto2d : pointList) {
			xTotal+=ponto2d.getX();
			yTotal+=ponto2d.getY();
		}
		return new Point2D(xTotal/getSize(), yTotal/getSize());
	}
	public double area(){
		double area = 0;
		for (int i = 1; i < getSize(); i++) {
			Point2D n = pointList.get(i);
			Point2D m = pointList.get(i-1);
			area+= (m.getX()*n.getY() - n.getX()*m.getY()); 
		}
		return area;
	}
	
	public double perimeter() {
		return 0;
	}

	public boolean contains(double x, double y) {
		return contains(new Point2D(x, y));
	}
	 public boolean contains(Point2D point) {
		 /*	Reference: http://geomalgorithms.com/a03-_inclusion.html	*/
	     int winding = 0;
	     for (int i = 0; i < getSize()-1; i++) {
	            double ccw = Triangle2D.area2(pointList.get(i), pointList.get(i+1),point);
	            if (pointList.get(i+1).getY() >  point.getY() && point.getY() >= pointList.get(i).getY())  // upward crossing
	                if (ccw > 0) winding++;
	            if (pointList.get(i+1).getY() <= point.getY() && point.getY() <  pointList.get(i).getY())  // downward crossing
	                if (ccw < 0) winding--;
	        }
	        return winding != 0;
	   }

	public void addPoint(Point2D point){
			if(isClosed) {return;}
			if(pointList.size() >=3){
				boolean near = isNear(point);
				if(!near){
						pointList.add(new Point2D(point.getX(), point.getY()));
				}else{
					pointList.add(pointList.get(0));
					isClosed = true;
				}
			}else{
				pointList.add(new Point2D(point.getX(), point.getY()));
			}
		
	
	}
	public void addPoint(double x,double y){
		addPoint(new Point2D(x, y));
	}
	public List<Point2D> getPointList(){
		return pointList;
	}

	public boolean isClosed(){
		return isClosed;
	}
	public Point2D getFirstPoint(){
		if(pointList.isEmpty()){return null;}
		return pointList.get(0);
	}
	public Point2D getLastPoint(){
		if(pointList.isEmpty()){return null;}
		return pointList.get(pointList.size()-1);
	}
	
	public List<Line2D> getBoundary(){
		if(pointList.isEmpty()){ return null;}
		List<Line2D> edges = new Vector<>();
		for (int i = 1; i < pointList.size(); i++) {
			edges.add(new Line2D(pointList.get(i-1), pointList.get(i)));
		}
		return edges;
	}
	
	
	public int getSize(){
		return pointList.size();
	}
	public boolean isEmpty(){
		return pointList.isEmpty();
	}
	public void  clearPoints(){
		pointList.clear();
		isClosed = false;
	}

	public Geometry2D copy() {
		return new Polygon2D(getPointList());
	}

	public boolean isNear(Point2D point2d) {
		return isNear(point2d, Geometry2D.EPSILON);
	}

	public boolean isNear(Point2D point2d, double epsilon) {
		for (Line2D line2d : getBoundary()) {
			if(line2d.isNear(point2d, epsilon)){ return true;}
		}
		return false;
	}
	
	public boolean isNear(double x, double y) {
		return isNear(x, y,Geometry2D.EPSILON);
	}
	
	public boolean isNear(double x, double y, double epsilon) {
		for (Line2D line2d : getBoundary()) {
			if(line2d.isNear(x,y, epsilon)){ return true;}
		}
		return false;
	}
	
	public void reset() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public double getMaxCoordinate() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	
	
	
	
	

}
