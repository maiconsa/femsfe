package com.femsfe.Geometrias;

import java.util.Comparator;

public class OrderByX implements Comparator<Point2D> {

	public OrderByX() {
		
	}

	@Override
	public int compare(Point2D o1, Point2D o2) {
		if(o1.getX() > o2.getX()){
			return 1;
		}else  if(o1.getX()< o2.getX()){
			return -1;
		}else{
			return 0;
		}
	}

}
