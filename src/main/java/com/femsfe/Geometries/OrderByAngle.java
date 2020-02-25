package com.femsfe.Geometries;

import java.util.Comparator;

public class OrderByAngle implements Comparator<Point2D> {
	private Point2D reference;
	public OrderByAngle(Point2D reference) {
		this.reference = reference;
	}

	@Override
	public int compare(Point2D o1, Point2D o2) {
		if(o1.toPolar(reference)> o2.toPolar(reference)){
			return 1;
		}else if(o1.toPolar(reference)< o2.toPolar(reference)){
			return -1;
		}else{
			return 0;
		}
	}
	

}
