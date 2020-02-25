package com.femsfe.Geometries;

import java.util.Comparator;

public class OrderByY implements Comparator<Point2D>  {

	public OrderByY() {
		
	}

	@Override
	public int compare(Point2D o1, Point2D o2) {
		if(o1.getY() > o2.getY()){
			return 1;
		}else  if(o1.getY() < o2.getY()){
			return -1;
		}else{
			return 0;
		}
	}

}
