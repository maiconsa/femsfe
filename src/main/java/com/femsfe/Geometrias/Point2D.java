package com.femsfe.Geometrias;


import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import com.femsfe.ComplexNumber.ComplexNumber;

/**
 * Represent a Point in two dimensional space.it a subclass of <code>Geometry2D<code>
 * @author Maicon Alc�ntara
 * @since 1.0
 * */
public  class Point2D extends Geometry2D    {
	/**
	 * The x coordinate of this <code>Point2D<code>
	 * */
    private double x;
    
    /**
	 * The y coordinate of this <code>Point2D<code>
	 * */
    private double y;;
    /**
     * Index o this <code>Point2D</code> on the <code>ConectivityList</code>.
     * */
	private int index = -1;
	 /**
     * Index o this <code>Point2D</code> for save project geometries.
     * */
	public int indexForSave;
	
	/**
	 * Status of this <code>Point2D</code>. Avoid duplicated insertion in triangulation step.
	 * */
	public boolean wasIntserted = false;
	/**
	 * Fixed status for detect fixed point
	 * */
	public boolean fixed = false;
	/**
	 * Value of this <code>Poin2D</code>. The value change when is applied a boundary condition or finite element analysis.
	 * */
	private ComplexNumber value = new ComplexNumber(Float.NaN, Float.NaN);
	/**
	 * Linked Points for fixed edge detection.
	 * */
	private List<Point2D> linkedPoints;
	/**
	 * Linked Border for Ordered Operation in Face Creation.Each Point just can be liked by 2 borders.
	 * */
	public HashSet<Border2D> linkedBorders = new HashSet<>();
	/**
	 * A <code>List</code> of element that contain this <code>Point2D</code> as vertex. Used for a discretization of a model domain.
	 * */
	private List<Triangle2D> linkedTriangles;

	public int numberLinkedBorders = 0;
	/**
	 * Default constructor with  {@code (0,0)} coordinates space.
	 * */
	public Point2D(){
		this.type = GeometryType.POINT;
		linkedPoints = new Vector<>(0);
		initLinkedTrianglesVector();
		
	}
	/**
	 * The constructor of this <code>Point2D<code> with {@code (x,y)} coordinates  space.
	 * @param x abscissa coordinate.
	 * @param y ordered coordinate.
	 * */
	public Point2D(double x,double y) {	
		this.type = GeometryType.POINT;
		linkedPoints = new Vector<>();
		initLinkedTrianglesVector();
		this.x = x;
		this.y = y;	
	}
	/**
	 * Set this <code>Point2D</code> as fixed;
	 * @param fixed boolean value
	 * */
	public void setFixed(boolean fixed){
		this.fixed = fixed;
	}
	/**
	 * Get the status of this <code>Point2D</code>. if is fixed return <code>true</code>, otherwise <code>fale</code>.
	 * @return boolean value
	 * */
	public boolean isFixed( ){
		return this.fixed;
	}
	/**
	 * Return the x coordinate of this <code>Point2D</code>.
	 * @return a double value.
	 * @since 1.0
	 * */
	public double getX(){
		return this.x;
	}
	/**
	 * Return the y coordinate of this <code>Point2D</code>.
	 * @return a double value.
	 * @since 1.0
	 * */
	public double getY(){
		return this.y;
	}
	/**
	 * Set the {@code (x,y)} coordinates  space.
	* @param x abscissa coordinate.
	 * @param y ordered coordinate.
	 * */
	public void setCoordinates(double x, double y){
		this.x = x;
		this.y = y;
	}

	/**
	 * Set the index of this <code>Point2D</code> in a <code>Model2D</code>.
	 * @param  index integer 
	 * @since 1.0
	 * */
	public void setIndex(int index){
		this.index = index;
	}
	/**
	 * Return  the index of this <code>Point2D</code> in a <code>Model2D</code>.
	 * @return integer value
	 * @since 1.0
	 * */
	public int getIndex(){
		return index;
	}
	/**
	 * Set the phi value of this <code>Point2D</code>.
	 * @param value  of this point.
	 * @since 1.0
	 * */
	public void setValue(ComplexNumber value) {
		this.value = value;
	}
	/**
	 * Return  the value of this <code>Point2D</code> in a <code>Model2D</code>.
	 * @return double value
	 * @since 1.0
	 * */
	public ComplexNumber getValue() {
		return value;
	}
	/**
	 * Add a <code>Point2D</code> to linked point list.
	 * @param point to be inserted.
	 * @since 1.0
	 * */
	public void addLinkedPoint(Point2D point){
		linkedPoints.add(point);
	}
	/**
	 * Remove a <code>Point2D</code> to linked point list.
	 * @param point to be removed.
	 * @since 1.0
	 * */
	public void removeLinkedPoint(Point2D point){
		linkedPoints.remove(point);
	}
	/**
	 * Return if linked point list contain the point.
	 * @param point  be checked in list.
	 * @return <code>true</code> if contain the point, <code>false</code> otherwise.
	 * @since 1.0
	 * */
	public boolean hasLinkedPoint(Point2D point){
		return linkedPoints.contains(point);
	}
	
	public void clearLinkedPoints(){
		linkedBorders.clear();
	}
	
	
	/**
	 * Add a <code>Triangle2D</code> to linked triangle list.
	 * @param triangle2d to be inserted.
	 * @since 1.0
	 * */
	public void addLinkedTriangle(Triangle2D triangle2d){
		this.linkedTriangles.add(triangle2d);
	}
	/**
	 * Remove  a <code>Triangle2D</code> from linked triangle list.
	 * @param triangle2d to be removed.
	 * @since 1.0
	 * */
	public void removeLinkedTriangle(Triangle2D triangle2d){
		this.linkedTriangles.remove(triangle2d);
	}
	/**
	 * Return the liked triangle list of this<code>Poin2D</code>.
	 * @return a list o linked triangles.
	 * @since 1.0
	 * */
	public List<Triangle2D> getLinkedTriangles(){
		return this.linkedTriangles;
	}
	public void setLinkedTriangles(List<Triangle2D> triangles){
		this.linkedTriangles = triangles;
	}
	public void setNullLinkedTriangles(){
		linkedTriangles = null;
	}
	public void clearLinkedTriangles(){
		if(linkedTriangles == null) {
			initLinkedTrianglesVector();
			return;}
		linkedTriangles.clear();;
	}
	public void initLinkedTrianglesVector(){
		linkedTriangles = new Vector<>(1);
	}

	/**
	 * Find the angle of this <code>Point2D</code> based on a reference point with {@code (xRef,yRef)} coordidnates.
	 * @param xRef  coordinate x reference.
	 *  @param yRef  coordinate y reference.
	 *  @return the angle of this point with {@code (xRef,yRef)}  coordinates
	 * */
	public float toPolar(double xRef,double yRef){
		double dx = getX() - xRef;
		double dy = getY() - yRef;
		double rad = Math.atan2(dy, dx);
		double ang = Math.toDegrees(rad);
		if(ang < 0){
			ang+=360;
		}
		return (float) ang;	
	}
	/**
	 * Find the angle of this <code>Point2D</code> based on a reference point.
	 * @param reference is a {@code Reference Point2D}.
	 *  @return the angle of this point with {@code Point2D}.
	 * */
	public double toPolar(Point2D reference){
		return toPolar(reference.getX(), reference.getY());
	}
	
	public  double distanceTo(Point2D point){
		return distanceTo(point.x, point.y);
	}
	/**
	 * Find the distance between this <code>Point2D</code> and a point with {@code (x,y)} coordinates.
	 * @param x coordinate.
	 * @param y coordinate.
	 * @return double value.
	 * */
	public double distanceTo(double x,double y){
		double dx = this.x - x;
		double dy = this.y - y;
		return   Math.sqrt(dx*dx + dy*dy);
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "[" + this.x+","+this.y+ "]" ;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(x);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(y);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Point2D other = (Point2D) obj;
		if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
			return false;
		if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
			return false;
		return true;
	}
	@Override
	public Geometry2D copy() {
		return new Point2D(x, y);
	}
	public boolean isNear(Point2D point2d) {
		return isNear(point2d, Geometry2D.EPSILON);
	}

	public boolean isNear(Point2D point2d,double epsilon) {
		double dist = this.distanceTo(point2d);
		return  dist>= 0 && dist <= epsilon;
	}

	public boolean isNear(double x, double y) {
		return isNear(x, y,Geometry2D.EPSILON);
	}
	public boolean isNear(double x, double y, double epsilon) {
		double dist = this.distanceTo(x,y);
		return dist>= 0 && dist <= epsilon;
	}

	public void reset() {
		deselectThis();
		this.x = Float.NaN;
		this.y = Float.NaN;
	}
	@Override
	public double getMaxCoordinate() {
		return Math.max(x, y);
	}
	
	public void laplacianSmooth(){
		if(this.isFixed()) return;
		HashSet<Point2D> pts = new HashSet<>();
		for (Triangle2D triangle2d : getLinkedTriangles()) {
			if(!triangle2d.p0.equals(this)){
				pts.add(triangle2d.p0);
			}
			if(!triangle2d.p1.equals(this)){
				pts.add(triangle2d.p1);
			}
			if(!triangle2d.p2.equals(this)){
				pts.add(triangle2d.p2);
			}
		}
		double x = 0,y = 0;
		for (Point2D point2d : pts) {
			x+= point2d.getX();
			y+=point2d.getY();
		}
		this.setCoordinates(x/pts.size(), y/pts.size());
	}



	
}
