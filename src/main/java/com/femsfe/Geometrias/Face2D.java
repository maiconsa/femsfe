package com.femsfe.Geometrias;

import java.util.ArrayList;

import java.util.LinkedHashSet;
import java.util.List;

import com.femsfe.BoundaryCondition.PermanentField;
import com.femsfe.BoundaryCondition.SpaceDensityFunction;
import com.femsfe.PML.PML;

public class Face2D extends Geometry2D {
	public static final int EXTERNAL = 0;
	public static final int NORMAL = 1;
	public static  final int HOLE = 2;
	
	private Material material;
	private PML pml;
	private List<Border2D> boundaries;
	private Polygon2D polygon;
	public Point2D center;
	private int typeFace; 
	public SpaceDensityFunction spaceDensityFunction;
	private PermanentField permField = null;
	
	public boolean isOver = false;
	
	public List<Triangle2D> trianglesForDrawMaterial;

	public Face2D(){
		super.type = GeometryType.FACE;
		boundaries = new ArrayList<>();
		spaceDensityFunction = new SpaceDensityFunction("0");
	}
	
	private void automaticSetPML(Material material ){
		if(material == null) return;
		if(material.isPML()){
			double xMin = Float.POSITIVE_INFINITY, xMax = Float.NEGATIVE_INFINITY;
			double yMin = Float.POSITIVE_INFINITY, yMax = Float.NEGATIVE_INFINITY;
			Point2D endpoints[];
			for (Border2D border2d : boundaries) {
				endpoints = border2d.getEndpoints();
				
				xMin = Math.min(Math.min(xMin, endpoints[0].getX()),  endpoints[1].getX());
				xMax = Math.max(Math.max(xMax, endpoints[0].getX()),  endpoints[1].getX());
				
				yMin = Math.min(Math.min(yMin, endpoints[0].getY()),  endpoints[1].getY());
				yMax = Math.max(Math.max(yMax, endpoints[0].getY()),  endpoints[1].getY());
			}
			PML pml= new PML(material.getPMLRegion());
			switch (pml.getRegion()) {
				case TOP_CENTER:
					pml.setBeginPoint(0, yMin);
					break;
				case BOTTOM_CENTER:
					pml.setBeginPoint(0, yMax);
					break;
				case LEFT_CENTER:
					pml.setBeginPoint(xMax, 0);
					break;
				case RIGHT_CENTER:
					pml.setBeginPoint(xMin, 0);
					break;
				case LEFT_TOP:
					pml.setBeginPoint(xMax, yMin);
					break;
				case RIGHT_TOP:
					pml.setBeginPoint(xMin, yMin);
					break;
				case LEFT_BOTTOM:
					pml.setBeginPoint(xMax, yMax);
					break;
				case RIGHT_BOTTOM:
					pml.setBeginPoint(xMin, yMax);
					break;
				default:
					break;
			}
			pml.setBeginPoint(xMin,yMin);
			pml.setWidth(xMax - xMin);
			pml.setHeight(yMax - yMin);
			this.pml = pml;
		}else{
			this.pml = null;
		}
	}
	
	public void setMaterial(Material material){
		this.material = material;
		automaticSetPML(material);
		
		
	}
	
	public PML getPML(){
		return this.pml;
	}
	public Material getMaterial(){
		return this.material;
	}
	
	public void setTypeFace(int type){
		this.typeFace = type;
	}
	public int getTypeFace(){
		return typeFace;
	}
		
	public void addBorder(Border2D border){
		this.boundaries.add(border);
	}
	
	public List<Border2D> getBoundaries(){
		return this.boundaries;
	}
	public Border2D getBorders(int index){
		return this.boundaries.get(index);
	}
	public  List<Line2D> chewSegmentation(float hmin) {
		List<Line2D> list = new ArrayList<>();
		for (Border2D border2d : boundaries) {
			list.addAll(border2d.chewSegmentation(hmin));
		}
		return list;
	}
	public Polygon2D getPolygon(){
		return polygon;
	}
	
	public static List<Border2D> organizeBorders(List<Border2D> borders){
		if(borders.isEmpty()) {return null;}
			for (Border2D border2d : borders) {
				border2d.clearLinkedBorders();
			}
			for (Border2D border2d : borders) {
				border2d.linkBorderToPoint();
			}
			for (Border2D border2d : borders) {
				if(!border2d.checkLinked()){
					System.out.println("Borda com mais ou menos de duas outras bordas");
					return null;
				}
			}
	
			List<Border2D> list = new ArrayList<>();
			Border2D first = borders.get(0);

			if(first.checkLinked()){
					boolean closed = true;
					Border2D next = first.getLinkedBorders().get(0);
					Border2D last = first;
	
					List<Border2D> aux;	
					while(next != first ){
						if(next.checkLinked()){
							aux = next.getLinkedBorders();
							list.add(last);
							if(aux.get(0).equals(last)){
								last = next;
								next = aux.get(1);
							}else{
								last = next;
								next = aux.get(0);
							}
						}else{
							closed = false;
							break;
						}	
					}
					if(next.equals(first)){
						list.add(last);
					}
					if(closed){
						return list;
					}else{
						return null;
					}
			}else{
				return null;
			}	
	}
	
	public void findPolygon(){
		if(polygon != null) return;
		LinkedHashSet<Point2D> pts = new LinkedHashSet<>();
		if(boundaries.size() >2){
		
				Border2D current = boundaries.get(0);
				Point2D equalPoint = getSharedPoint(boundaries.get(0),boundaries.get(1));
				
				switch (current.type) {
					case LINE:
						Line2D line = (Line2D) current;
						if(line.getP0().equals(equalPoint)){
							pts.add(line.getP1());
						}else{
							pts.add(line.getP0());
						}
						pts.add(equalPoint);
						break;
					case ARC:
						Arc2D arc = (Arc2D) current;
						List<Point2D> list = arc.getPointList();
						if(list.get(0).equals(equalPoint)){
							for (int j = list.size()-1; j >= 0 ; j--) {
								pts.add(list.get(j));
							}
							
						}else {
							pts.addAll(list);
						}
						pts.add(equalPoint);
						break;
					case BEZIER:
						BezierCurve2D curve = (BezierCurve2D) current;
						curve.setCurvePoints(100);
						List<Point2D> curvePts = curve.curvePoints;
						if(curvePts.get(0).equals(equalPoint)){
							pts.addAll(curvePts);
						}else {
							
							for (int j = curvePts.size()-1; j >= 0 ; j--) {
								pts.add(curvePts.get(j));
							}
							
						}
						pts.add(equalPoint);
						break;
					default:
						break;
			}
				
				for (int i = 1; i < boundaries.size(); i++) {
					current = boundaries.get(i);
					equalPoint = getSharedPoint(boundaries.get(i-1), current);
					switch (current.type) {
						case LINE:	
							pts.add(equalPoint);
							Line2D line = (Line2D) current;
							if(line.getP0() ==  equalPoint){
								pts.add(line.getP1());
							}else{
								pts.add(line.getP0());
							}
							break;
						case ARC:
							pts.add(equalPoint);
							Arc2D arc = (Arc2D) current;
							List<Point2D> list = arc.getPointList();
							if(list.get(0).equals(equalPoint)){
								pts.addAll(list);
								
							}else{
								for (int j = list.size()-1; j >= 0 ; j--) {
									pts.add(list.get(j));
								}
							}
							break;
						case BEZIER:
							BezierCurve2D curve = (BezierCurve2D) current;
							curve.setCurvePoints(100);
							List<Point2D> curvePts = curve.curvePoints;
							if(curvePts.get(0).equals(equalPoint)){
								pts.addAll(curvePts);
							}else {
								for (int j = curvePts.size()-1; j >= 0 ; j--) {
									pts.add(curvePts.get(j));
								}
								
							}
							pts.add(equalPoint);
							
							break;
						default:
							break;
					}
				}
		}else{
			Border2D b0 = boundaries.get(0);
			Border2D b1 = boundaries.get(1);
			if(b0.getType() == GeometryType.ARC && b1.getType() == GeometryType.ARC ){
				Arc2D arc0 = (Arc2D) b0;
				Arc2D arc1 = (Arc2D) b1;
				List<Point2D> list0 = arc0.getPointList();
				List<Point2D> list1 = arc1.getPointList();
				pts.addAll(arc0.getPointList());
				if(list1.get(0) == list0.get(list0.size()-1)){
					pts.addAll(list1);
				}else{
					for (int i = list1.size()-1; i >=0 ; i--) {
						pts.add(list1.get(i));
					}
				}
			}
			if((b0.getType() == GeometryType.LINE && b1.getType() == GeometryType.ARC) || (b1.getType() == GeometryType.LINE && b0.getType() == GeometryType.ARC)){
				if(b0.getType() == GeometryType.ARC){
					Arc2D arc = (Arc2D) b0;
					pts.addAll(arc.getPointList());
				}
				if(b1.getType() == GeometryType.ARC){
					Arc2D arc = (Arc2D) b1;
					pts.addAll(arc.getPointList());
				}
			}
		}

		
		List<Point2D> ptsPoly = new ArrayList<>(pts);
		ptsPoly.add(ptsPoly.get(0));
		polygon = new Polygon2D(ptsPoly);
		
		this.center = polygon.centroid();
			
		}

	private Point2D getSharedPoint(Border2D b0,Border2D b1){
		
		Point2D[] pts0 = b0.getEndpoints();
		Point2D[] pts1 = b1.getEndpoints();
		
			for (int i = 0; i < pts0.length; i++) {
				for (int j = 0; j < pts1.length; j++) {
					if(pts0[i] == (pts1[j])) return pts0[i];
				}
			}
			return null;
		
	}
	
	@Override
	public Geometry2D copy() {
		return null;
	}
	
	public double getMaxCoordinate() {
		double max = 0;
		for (Border2D border2d : getBoundaries()) {
			max = Math.max(max, border2d.getMaxCoordinate());
		}
		return max;
	}
	
	@Override
	public void reset() {}

	@Override
	public void selectThis() {
		super.selectThis();
		for (Border2D border2d: boundaries) {
			border2d.setSelected(true);
		}	
	}
	
	@Override
	public void deselectThis() {
		// TODO Auto-generated method stub
		super.deselectThis();
		for (Border2D border2d: boundaries) {
			border2d.setSelected(false);
		}
	}

	@Override
	public boolean isNear(Point2D point2d) {
		return false;
	}

	@Override
	public boolean isNear(Point2D point2d, double epsilon) {
		return false;
	} 
	
	public boolean isNear(double x,double y){
		return isNear(x, y,Geometry2D.EPSILON);
	}
	
	public boolean isNear(double x,double y,double epsilon){
		for (Border2D border2d: boundaries) {
			if(border2d.isNear(x, y, epsilon)) return true;	
		}
		return false;
	}

	@Override
	public void setVisible(boolean status) {
		// TODO Auto-generated method stub
		super.setVisible(status);
		for (Border2D border2d : boundaries) {
			border2d.setVisible(status);
		}
	}
	
	public boolean isPermanentFieldRegion(){
		return permField != null;
	}
	public void setPermanentField(PermanentField permField){
		this.permField = permField;
	}
	public PermanentField getPermanentField( ){
		return this.permField; 
	}
	
	

	

}
	
	
