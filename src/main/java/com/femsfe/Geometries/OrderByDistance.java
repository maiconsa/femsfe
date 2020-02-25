package com.femsfe.Geometries;

import java.util.Comparator;

public class OrderByDistance implements Comparator<Point2D> {
	Point2D reference;
	public OrderByDistance(Point2D reference) {
		this.reference = reference;
	}

	@Override
	public int compare(Point2D o1, Point2D o2) {
		double dist1 = o1.distanceTo(reference);
		double dist2 =  o2.distanceTo(reference);
		if(dist1 > dist2){
			return 1;
		}else if(dist1 < dist2){
			return -1;
		}else{
			return 0;
		}
	}

}
