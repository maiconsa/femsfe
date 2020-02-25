package com.femsfe.Geometries;

import com.femsfe.enums.GeometryType;
import java.util.ArrayList;
import java.util.List;

public class Rectangle2D extends Geometry2D     {
	private Point2D p0,	p1	,q0 ,q1 ;
	public Rectangle2D(){
		this.type = GeometryType.RECTANGLE;
	}
	public Rectangle2D(Point2D  p0,Point2D p1) {
		this();
		this.p0 = p0;
		this.p1 = p1;
		if(p1 != null){
			this.q0 = new Point2D(p1.getX(), p0.getY());
			this.q1 = new Point2D(p0.getX(), p1.getY());
		}else{
			this.p1 = null;
			this.q0 = null;
			this.q1 = null;
		}

	}
	
	public void setP0(Point2D p0){
		this.p0 = p0;
	}
	public void setP0(double x,double y){
		setP0(new Point2D(x, y));
	}
	public void setP1(Point2D p1){
		this.p1 = p1;
		if(p1 != null){
			this.q0 = new Point2D(p1.getX(), p0.getY());
			this.q1 = new Point2D(p0.getX(), p1.getY());
		}else{
			this.q0 = null;
			this.q1 = null;
		}
	}
	public void setP1(double x,double y){
		setP1(new Point2D(x, y));
	}
	public Point2D getP0(){
		return p0;
	}
	public Point2D getP1(){
		return p1;
	}
	
	public Point2D getQ0(){
		return 	q0;
	}
	public Point2D getQ1(){
		return q1;
	}
	
	public List<Line2D> getBoundary(){
		List<Line2D> edgeList = new ArrayList<>(4);
		edgeList.add(new Line2D(p0, q0));
		edgeList.add(new Line2D(q0,p1));
		edgeList.add(new Line2D(p1,q1));
		edgeList.add(new Line2D(q1,p0));
		return edgeList;
	}
	
	public double getWidth(){
		return Math.abs(p0.getX() - p1.getX());

	}
	public double getHeight(){
		return Math.abs(p0.getY() - p1.getY());
	
	}
	public double area(){
		return getHeight()*getWidth();
	}
	public Line2D getDiagonalP(){
		return new Line2D(p0,p1);
	}
	public Line2D getDiagonalQ(){
		return new Line2D(q0,q1);
	}
	public double getXmin(){
		return Math.min(getP0().getX(), getP1().getX());
						
	}
	public double getYmin(){
		return Math.min(getP0().getY(), getP1().getY());
						
	}
	public double getXmax(){
		return Math.max(getP0().getX(), getP1().getX());
	}
	public double getYmax(){
		return Math.max(getP0().getY(), getP1().getY());
	}
	

	public Geometry2D copy() {
		// TODO Auto-generated method stub
		return new Rectangle2D(getP0(), getP1());
	}

	public double perimeter() {
		double per = 0;
		for (Line2D line2d : getBoundary()) {
			per+= line2d.getLength();
		}
		return per;
	}

	public boolean intersect(Point2D a,Point2D b){
		for (Line2D line2d : getBoundary()) {
			if(line2d.intersect(a, b)){
				return true;
			}
		}
		return false;
	}
	public boolean intersect(Line2D line){
		return intersect(line.getP0(), line.getP1());
	}
	public boolean intersect(Rectangle2D rect){
		for (Line2D line : rect.getBoundary()) {
			if(this.intersect(line)){
				return true;
			}
		}
		return false;
					
	}
	
	public boolean contains(Rectangle2D rect){
			return contains(rect.getP0()) &&  contains(rect.getP1()) && 
				   contains(rect.getQ0()) &&  contains(rect.getQ1()); 
	}
	public boolean contains(Line2D line){
		return contains(line.getP0()) && contains(line.getP1());
	}

	
	public boolean contains(Point2D point2d) {
		return contains(point2d.getX(), point2d.getY());
	}
	
	public boolean contains(double x,double y) {
		return  (x >  getXmin() &&  x < getXmax())  && 	(y >  getYmin() &&  y < getYmax());
	}

	
	public boolean isNear(Point2D point2d) {
		return isNear(point2d, Geometry2D.EPSILON);
	}
	
	public boolean isNear(Point2D point2d, double epsilon) {
		for (Line2D line2d : getBoundary()) {
			if(line2d.isNear(point2d,epsilon)){ return true;}
		}
		return false;
	}
	
	public boolean isNear(double x, double y) {
		return isNear(x, y, Geometry2D.EPSILON);
	}

	public boolean isNear(double x, double y, double epsilon) {
		for (Line2D line2d : getBoundary()) {
			if(line2d.isNear(x, y,epsilon)){ return true;}
		}
		return false;
	}
	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public double getMaxCoordinate() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	
	
}
