package com.femsfe.Geometries;

import com.femsfe.enums.GeometryType;
import java.util.ArrayList;
import java.util.List;

public class Polyline2D extends Geometry2D  {	
	private List<Point2D> points ;
	public Polyline2D() {
		this.type = GeometryType.POLYLINE;
		this.points = new ArrayList<>();
	}
	public Polyline2D(List<Point2D> points) {
		this();
		for (Point2D point2d : points) {
			this.addPoint(point2d);
		} 
		
	}
	public void remove(int index){
		this.points.remove(index);
	}
	public int size(){
		return this.points.size();
	}
	
	public List<Point2D> getListPoint(){
		return this.points;
	}
	
	public void addPoint(Point2D point2d){
		points.add(point2d);
	}
	public void addPoint(double x,double y){
		points.add(new Point2D(x, y));
	}
	
	public void clear(){
		points.clear();
	}
	public boolean isEmpty(){
		return points.isEmpty();
	}

	@Override
	public Geometry2D copy() {
		// TODO Auto-generated method stub
		return new Polyline2D(points);
	}
	public List<Line2D> edges(){
		List<Line2D> edges = new ArrayList<>();
		for (int i = 1; i < points.size(); i++) {
			edges.add(new Line2D(points.get(i-1), points.get(i)) );
		}
		return edges;
	}
	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}


	

	@Override
	public boolean isNear(double x, double y) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean isNear(double x, double y, double epsilon) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean isNear(Point2D point2d) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean isNear(Point2D point2d, double epsilon) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public double getMaxCoordinate() {
		// TODO Auto-generated method stub
		return 0;
	}


	
	
}
