package com.femsfe;

import com.femsfe.Geometrias.*;


public final class CreateGeometry  {
	private static GeometryType type = GeometryType.NONE;
	private static Geometry2D geometry;
	private static boolean started = false;

	public static void begin(GeometryType type){
				end();
				CreateGeometry.type = type;	
				started = true;
	}
	public static boolean started(){
		return started;
	}
	public static boolean finished(){
		return geometry == null;
	}
	public static GeometryType getType(){
		return type;
	}
	public static Geometry2D getGeometry(){
		return geometry;
	}
	public static void addPoint(float x,float y){
			Point2D point = null;
			for (Point2D point2d : Project.getPointList()) {
				if(point2d.isNear(x, y)){
					point = point2d;
					break;
				}
			}

			if(point == null){
				point = new Point2D(x, y);
			}

			switch (type) {
				case POINT:
					if(geometry == null){
						Project.addGeometry(point);
						reset();
					}
					break;
				case LINE:
					if(geometry == null){ 
						geometry = new Line2D(point,null);	
					}else{
						Line2D line = (Line2D) geometry;
						if(line.getP0().distanceTo(point) > Geometry2D.EPSILON){
							line.setP1(point);
							Project.addGeometry(line.copy());
							reset();
						}
					}
					break;
				case POLYLINE:
					if(geometry == null){
						Polyline2D polyline2d = new Polyline2D();
						polyline2d.addPoint(point);
						geometry = polyline2d;
					}else{
						Polyline2D polyline2d = (Polyline2D) geometry;
						if(polyline2d.getListPoint().get(polyline2d.size()-1).distanceTo(point) > Geometry2D.EPSILON){
							polyline2d.addPoint(point);
						}
					}
					break;
				case RECTANGLE:
					if(geometry == null){
						geometry = new Rectangle2D(point,null);
					}else{
						Rectangle2D rect = (Rectangle2D) geometry;
						rect.setP1(point);
						Project.addGeometry(rect.copy());
						
						reset();
					}
					break;
				case POLYGON:
					if(geometry == null){
						Polygon2D poly  = new Polygon2D();
						poly.addPoint(point);
						geometry = poly;
					}else{
						Polygon2D poly = (Polygon2D) geometry;
						poly.addPoint(point);
						if(poly.isClosed()){
							Project.addGeometry(poly);
							reset();
						}
					}
					break;
				case CIRCLE:
					if(geometry == null){
						geometry = new Circle2D(-1, point);
					}else{
						Circle2D circle2d = (Circle2D) geometry;
						circle2d.setRadius(circle2d.getCenterPoint().distanceTo(x, y));
						Project.addGeometry(circle2d.copy());
						reset();
					}
					break;
				case BEZIER:
					if(geometry == null){
						BezierCurve2D bezier = new BezierCurve2D();
						bezier.addPoint(point);
						
						geometry = bezier;
					}else{
						((BezierCurve2D)geometry).addPoint(point);
					}
					break;
				case ARC:
					if(geometry == null){
						Arc2D arc = new Arc2D();
						arc.setCenterPoint(point);
						arc.setEndAngle(-1);
						arc.setStartAngle(-1);
						geometry = arc;	
					}else{
						Arc2D arc = (Arc2D) geometry;
						if(arc.getRadius() == -1){
							arc.setStartPoint(point);
						}else{
							arc.setEndPoint(point);
							Project.addGeometry(arc.copy());
							reset();
						}
					}
					break;

				default:
					break;
			}
		
	}
	public static void end(){
		reset();
		CreateGeometry.type = GeometryType.NONE;
		started = false;
	}
	public static boolean isReseted(){
		return geometry == null;
	}
	public static void reset(){
		if(geometry != null){
			geometry = null;
		}
	}
	
	
	
	
	
	

}
