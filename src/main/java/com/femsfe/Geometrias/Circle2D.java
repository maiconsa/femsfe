package com.femsfe.Geometrias;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

public class Circle2D extends Border2D  {
	private double radius;
	private Point2D centerPoint = null;
	private List<Point2D> intersectionPoints = new ArrayList<>();
	
	/*	CONSTRUCTORS	*/
	public Circle2D(double radius,Point2D centerPoint) {
		this();
		this.radius = radius;
		this.centerPoint = centerPoint;
	}
	public Circle2D(){
		this.type = GeometryType.CIRCLE;
		}
	
	/*	SETTERS */
	public void setRadius(double radius){
		this.radius = radius;
	}
	public void setCenterPoint(Point2D centerPoint){
		this.centerPoint = centerPoint;
	}
	public void setCenterPoint(double x,double y){
		setCenterPoint(new Point2D(x, y));
	}
	
	/*	GETTERS	*/
	public double getRadius() {
		return radius;
	}
	public Point2D getCenterPoint() {
		return centerPoint;
	}
	public double getX0(){
		return getCenterPoint().getX();
	}
	public double getY0(){
		return getCenterPoint().getY();
	}
	
	
	/* GEOMETRIC PROPERTIES	*/
	public double area(){
		return (float) (Math.PI*radius*radius);
	}
	public double perimeter(){
		return (float) (Math.PI*radius*2);
	}
	public boolean contains(Rectangle2D rect){
		return contains(rect.getP0()) &&  contains(rect.getP1()) && 
			   contains(rect.getQ0()) &&  contains(rect.getQ1()); 
}
	public boolean contains(Line2D line){
		return contains(line.getP0()) && contains(line.getP1());
	}
	
	public boolean contains(Point2D point){
		return contains(point.getX(), point.getY());
	}

	public boolean contains(double x ,double y){
		if(getCenterPoint().distanceTo(x, y) >= getRadius()){
			return false;
		}else{
			return true;
		}
	}


	
	
	public List<Arc2D> getArcsFromInterAngles(){
		Collections.sort(intersectionPoints,new OrderByAngle(getCenterPoint()));
		List<Arc2D> arcs = new Vector<>();
		int n = intersectionPoints.size();
		System.out.println(n);
		if(n >=2){
			for (int i = 1; i < n ; i++) {
				arcs.add(new Arc2D(centerPoint, intersectionPoints.get(i-1),intersectionPoints.get(i)));
			}
			arcs.add(new Arc2D(centerPoint, intersectionPoints.get(n-1), intersectionPoints.get(0)));
			intersectionPoints.remove(n-1);
			clearInterPoints();
			return arcs;
		}else{
			return null;
		}
	}
	
	public void addIntersectionPoint(Point2D point2d){
		intersectionPoints.add(point2d);
	}
	public void clearInterPoints(){
		intersectionPoints.clear();
	}
	public List<Point2D> getInterPoints(){
		return intersectionPoints;
	}
	@Override
	public Geometry2D copy() {
		return new Circle2D(getRadius(), getCenterPoint());
	}
	
	
	public boolean isNear(Point2D point2d) {
		return isNear(point2d);
	}
	
	public boolean isNear(Point2D point2d, double epsilon) {
		double dist = Math.abs(getCenterPoint().distanceTo(point2d));
		return dist>=0 && dist<= epsilon;
	}
	
	public boolean isNear(double x, double y) {
		return isNear(x, y,Geometry2D.EPSILON);
	}
	
	public boolean isNear(double x, double y, double epsilon) {
		double dist = Math.abs(getCenterPoint().distanceTo(x, y) - getRadius());
		return dist>=0 && dist<= epsilon;
	}
	
	public void reset() {
		setRadius(-1);
		setCenterPoint(null);
		deselectThis();
		clearInterPoints();
		
	}
	
	public boolean intersect(Circle2D circle2d){
		double  r0 = this.getRadius();
		double  r1 = circle2d.getRadius();
	    double dist = this.getCenterPoint().distanceTo(circle2d.getCenterPoint());
	    if(dist > r0 + r1 ||  dist < Math.abs(r0 - r1) || (dist == 0) && (r0 == r1) ) {return false;}
	    return true;
	    
	}
	
	public boolean intersect(Rectangle2D rect){
		if(contains(rect)){
			return false;
		}else{
			for (Line2D line2d : rect.getBoundary()) {
				if(intersect(line2d)){
						return true;
				}
			}
				
			}
			return false;
			
	}
	
	public boolean intersect(Line2D line){
		//Reference: http://paulbourke.net/geometry/circlesphere/
		double r = getRadius();
		Point2D p1 = line.getP0();
		Point2D p2 = line.getP1(); 	
		Point2D dp = new Point2D(p2.getX() - p1.getX(), p2.getY() - p1.getY());

		double  a = dp.getX() * dp.getX() + dp.getY() * dp.getY();
		double  b = 2 * (dp.getX() * (p1.getX() - this.getX0()) + dp.getY() * (p1.getY() - this.getY0()));
		double  c = this.getX0() * this.getX0() + this.getY0() * this.getY0();
			c += p1.getX() * p1.getX() + p1.getY() * p1.getY();
			c -= 2 * (this.getX0() * p1.getX() + this.getY0() * p1.getY()) ;
			c -= r * r;
		double delta = b*b - 4*a*c;
		if(delta  > 0 ){
			double t1 = (-b + Math.sqrt(delta))/(2*a);
			double t2 = (-b + Math.sqrt(delta))/(2*a);
			if(t1> 0 && t1 <1 && t2> 0 && t2 <1){
				return true;
			}else{
					return false;
				}
			
		}else{
			return false;
		}

	}
	
	public Rectangle2D getBounds(){
		Point2D p0 = new Point2D(getX0()+getRadius(), getY0()+ getRadius());
		Point2D p1 = new Point2D(getX0()-getRadius(), getY0()- getRadius());
		return new Rectangle2D(p0, p1);
	}
	
	public Point2D[] intersection(Circle2D circle2d){
		//Reference: http://csharphelper.com/blog/2014/09/determine-where-two-circles-intersect-in-c/c
		if( circle2d != null && !intersect(circle2d)){ 
			return null;
		}else{
			double cx0 = this.getCenterPoint().getX();
			double cy0 = this.getCenterPoint().getY();
			double  r0 = this.getRadius();
			double cx1 = circle2d.getCenterPoint().getX();
			double cy1 = circle2d.getCenterPoint().getY();
			double  r1 = circle2d.getRadius();
		
			double dist = this.getCenterPoint().distanceTo(circle2d.getCenterPoint());
			// Find a and h.
			double a = (r0 * r0 - r1 * r1 + dist * dist) / (2 * dist);
			double h = Math.sqrt(r0 * r0 - a * a);
			// Find P2.
			double cx2 = cx0 + a * (cx1 - cx0) / dist;
			double cy2 = cy0 + a * (cy1 - cy0) / dist;
		    // Get the points P3.
			double x3 = cx2 + h * (cy1 - cy0) / dist;
			double y3 = cy2 - h * (cx1 - cx0) / dist;
			
			double x4 = cx2 - h * (cy1 - cy0) / dist;
			double y4 = cy2 + h * (cx1 - cx0) / dist;
			if(x3 != Double.NaN && y3 != Double.NaN && x4 != Double.NaN && y4 != Double.NaN ){ 
				Point2D[] points = new Point2D[2];
				points[0] = new Point2D((float)x3, (float)y3);
				points[1] = new Point2D((float)x4, (float)y4);
				return points;
			}else{
			     if(x3 != Double.NaN && y3 != Double.NaN){
			    	 Point2D[] points = new Point2D[1];
			         points[0] = new Point2D((float)x3,(float) y3);
			        return points;
			     }
			     	if(x4 != Double.NaN && y4 != Double.NaN){
			     		Point2D[] points = new Point2D[1];
			        	points[0] = new Point2D((float)x4, (float)y4);
			        	return points;
			        }
			        return null;
			}

	      
	    }
	}
	

	public Point2D[] intersection(Line2D line){
		//Reference: http://paulbourke.net/geometry/circlesphere/
		double r = this.getRadius();
		Point2D p1 = line.getP0();
		Point2D p2 = line.getP1(); 	
		Point2D dp = new Point2D(p2.getX() - p1.getX(), p2.getY() - p1.getY());

	    double  a = dp.getX() * dp.getX() + dp.getY() * dp.getY();
		double  b = 2 * (dp.getX() * (p1.getX() - this.getX0()) + dp.getY() * (p1.getY() - this.getY0()));
		double  c = this.getX0() * this.getX0() + this.getY0() * this.getY0();
		   c += p1.getX() * p1.getX() + p1.getY() * p1.getY();
		   c -= 2 * (this.getX0() * p1.getX() + this.getY0() * p1.getY()) ;
		   c -= r * r;
		double delta = b*b - 4*a*c;
		if(delta < 0 ){
			return null;
		}else if(delta == 0 ){
			return null;
		}else{
			double x1 = p1.getX(),x2 = p2.getX();
			double y1 = p1.getY(),y2 = p2.getY();
			double t1 = (-b + Math.sqrt(delta))/(2*a);
			double t2 = (-b - Math.sqrt(delta))/(2*a);
			if(t1 >=0 && t1 <=1  && t2 >=0 && t2 <= 1){
				Point2D[] points = new Point2D[2];
				points[0] = new Point2D( (float)(x1 + t1*(x2-x1)),(float)(y1+ t1*(y2-y1)));
				points[1] = new Point2D((float) (x1 + t2*(x2-x1)),(float)(y1+ t2*(y2-y1)));
				return points;
			}
			if(t1 >= 0 && t1 <= 1){
				Point2D[] points = new Point2D[1];
				points[0] = new Point2D( (float)(x1 + t1*(x2-x1)),(float)(y1+ t1*(y2-y1)));
				return points;
			}
			if(t2 >= 0 && t2 <=1){
				Point2D[] points = new Point2D[1];
				points[0] = new Point2D( (float)(x1 + t2*(x2-x1)),(float)(y1+ t2*(y2-y1)));
				return points;
			}
			
			return null;
		}
			
		
	}

	/*
	public List<Line2D> chewSegmentation(double hmin) {
		ArrayList<Line2D> newLines = new ArrayList<>();
		int lineAmount = (int) (this.perimeter()/hmin);
		Point2D center = this.getCenterPoint();
		Point2D firstPoint = new Point2D(center.getX() + (this.getRadius()* Math.cos(0)), center.getY() + (this.getRadius()* Math.sin(0)));
		Point2D lastPoint = firstPoint;
		Point2D newPoint = null;
		Line2D newLine = null;
		double twicePi = 2.0f *Math.PI;
			for(int i = 1; i < lineAmount;i++) { 
				newPoint = new Point2D(
				    center.getX() + (this.getRadius()* Math.cos(i *  twicePi / lineAmount)), 
				    center.getY() + (this.getRadius()* Math.sin(i * twicePi / lineAmount)));
				newPoint.setValue(this.getValue());
				newPoint.setTypeCondition(this.getTypeCondition());
				
				lastPoint.setValue(this.getValue());
				lastPoint.setTypeCondition(this.getTypeCondition());
				
				newLine = new Line2D(lastPoint, newPoint);
				newLine.setTypeCondition(this.getTypeCondition());
				newLine.setValue(this.getValue());
				newLine.setAsFixedEdge();
				newLines.add(newLine);
				lastPoint = newPoint;
				newPoint = null;
			}
			lastPoint.setValue(this.getValue());
			lastPoint.setTypeCondition(this.getTypeCondition());
			
			firstPoint.setValue(this.getValue());
			
		newLine = new Line2D(lastPoint, firstPoint);
		newLine.setAsFixedEdge();
		newLine.setTypeCondition(this.getTypeCondition());
		newLine.setValue(this.getValue());
	
		newLines.add(newLine);
		return newLines;
	}
*/
	public double getMaxCoordinate() {
		Math.max(centerPoint.getX() + radius, centerPoint.getY() + radius);
		return  Math.max(centerPoint.getX() + radius, centerPoint.getY() + radius);
	}
	
	public List<Point2D> getPointList() {
		int lineAmount = 100;
		Point2D center = this.getCenterPoint();
		ArrayList<Point2D> pts = new ArrayList<>();
		double twicePi = 2.0f *Math.PI;
		
			for(int i = 0; i <= lineAmount;i++) { 
				Point2D newPoint =  new Point2D(
				  (float)(  center.getX() + (this.getRadius()* Math.cos(i *  twicePi / lineAmount))), 
				  (float)( center.getY() + (this.getRadius()* Math.sin(i * twicePi / lineAmount)))
				);
				pts.add(newPoint);
			}
			return pts;
		
	}
	@Override
	public List<Line2D> chewSegmentation(double hmin) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Point2D[] getEndpoints() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Arc2D> division(int n){
		ArrayList<Arc2D> newArcs = new ArrayList<>();
		
		Point2D center = this.getCenterPoint();
		Point2D firstPoint = new Point2D((float)(center.getX() + (this.getRadius()* Math.cos(0))),(float)( center.getY() + (this.getRadius()* Math.sin(0))));
		Point2D lastPoint = firstPoint;
		Point2D newPoint = null;
		Arc2D newArc = null;
		double delta = 360/n;
		double angle = delta;
		while(angle < 360){
			newPoint = new Point2D((float)(center.getX() + (this.getRadius()* Math.cos(Math.toRadians(angle)))),(float)( center.getY() + (this.getRadius()* Math.sin(Math.toRadians(angle)))));
			newArc = new Arc2D(center, lastPoint, newPoint);
			newArcs.add(newArc);
			lastPoint = newPoint;
			angle+=delta;
		}
		newArcs.add(new Arc2D(center, lastPoint, firstPoint));
		return newArcs;
		
	}
	@Override
	public double getLength() {
		// TODO Auto-generated method stub
		return perimeter() ;
	}





	



}
