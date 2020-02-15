package com.femsfe.PML;

import com.femsfe.Geometrias.Point2D;

public class PML {
	private PMLRegions region;
	private static double R = 1.0E-10;
	private double width = -1,height = -1;
	private Point2D beginPoint = null;
	public PML(PMLRegions region) {
		this.region = region;
	}
	public PMLRegions getRegion(){
		return this.region;
	}
	public void setBeginPoint(double x, double y){
		this.beginPoint = new Point2D(x, y);
	}
	public Point2D getBeginPoint(){
		return this.beginPoint;
	}
	public void setWidth(double width){
		this.width = width;
	}
	public void setHeight(double height){
		this.height = height;
	}
	public double getWidth(){
		return this.width;
	}
	public double getHeight(){
		return this.height;
	}
	public static void setR(double r){
		PML.R = r;
	}
	public static double getR(){
		return PML.R;
	} 

	public double getXBegin(){
		return beginPoint.getX();
	}
	public double getYBegin(){
		return beginPoint.getY();
	}

}
