 package com.femsfe;
import com.femsfe.Geometries.Arc2D;
import com.femsfe.Geometries.Geometry2D;
import com.femsfe.enums.GeometryType;
import com.femsfe.Geometries.Circle2D;
import com.femsfe.Geometries.Border2D;
import com.femsfe.Geometries.Face2D;
import com.femsfe.Geometries.Line2D;
import com.femsfe.Geometries.Rectangle2D;
import com.femsfe.Geometries.BezierCurve2D;
import com.femsfe.Geometries.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * That class storage all the geometry selected by the user. 
 * @author Maicon Alc�ntara
 * @since 1.0
 * */
 public final class SelectGeometry {
	/**	
	 * Where the points selected by user are storage.	
	 * */
	private static List<Point2D> points = new ArrayList<>();
	/**	
	 * Where the lines selected by user are storage.	
	 * */
	private static List<Line2D> lines = new ArrayList<>();
	/**	
	 * Where the circles selected by user are storage.	
	 * */
	private static List<Circle2D> circles = new ArrayList<>();
	/**	
	 * Where the arcs selected by user are storage.
	 * */
	private static List<Arc2D> arcs = new ArrayList<>();
	/**	
	 * Where the bezier selected by user are storage.
	 * */
	private static List<BezierCurve2D> beziers = new ArrayList<>();
	/**	
	 * Where the faces selected by user are storage.	
	 * */
	private static List<Face2D> faces = new ArrayList<>();

	/**
	 * Kept the status of selection.
	 * */
	private static boolean started = false;
	/**
	 * Selection type.
	 * */
	private static GeometryType type;
	/**	
	 * Private constructor 
	 * */
	private SelectGeometry() {}
	
	public static boolean toRemove = false;
	/**	
	 * Start a selection based on a type of  geometry.
	 * @param type of geometry to select.
	 * @since 1.0
	 *  */
	public static void begin(GeometryType type){
		started = true;
		SelectGeometry.type = type;
	}
	/**
	 * Verify if the selection has benn started.
	 * @return boolean value.
	 * @since 1.0
	 * */
	public static boolean started(){
		return started;
	}
	/**
	 * Finished the selection. Clear the selection list.
	 * @since 1.0
	 * */
	public static void end(){
		clear();
		started = false;
		type = null;
		toRemove = false;
	}
	public static GeometryType getGeometryType(){
		return type;
	}
	
	/**
	 * 	Select the geometry near a coordinate(x,y) on the project selected.
	 * 
	 * */
	public static void selectGeometryNear(float x,float y) {
		if(!started){ return; } 
		Geometry2D geometry = null;
		switch (type) {
			case POINT:
				geometry = Project.getPointNearPoint(x, y); 
				break;
			case LINE:
				geometry = Project.getLineNearPoint(x, y);
				break;
			case CIRCLE:
				geometry = Project.getCircleNearPoint(x, y);
				break;
			case ARC:
				geometry = Project.getArcNearPoint(x, y);
				break;
			case FACE:
				Face2D selected = null;
				Face2D external = null;
				for (Face2D face2d : Project.getFacesWithPointInside(x, y)) {
					if(face2d.getTypeFace() != Face2D.EXTERNAL){
						selected = face2d;
						 break;
					}else{
						external = face2d;
					}
				}
				if(selected == null && external != null){
					geometry = external;
				}else if(selected != null && (external != null || external == null)){
					geometry = selected;
				}
				break;
			case BEZIER:
				geometry = Project.getBezierNearPoint(x,y);
				break;
			case BORDER:
				geometry = Project.getBorderNearPoint(x,y);
				break;
			default:
				geometry = Project.getGeometryNearPoint(x, y);
				break;
		}
		if(geometry == null) { 
			return;
		}
		if(geometry.isSelected()){
			geometry.deselectThis();
		}else{
			geometry.selectThis();
		}	
	}
	
	
	/**
	 * 	Selects the geometries that intersect or are contained in a given region by user. That region is a rectangle area.
	 * @param clip   area.
	 * @since 1.0.
	 * */
	public static void selectByClipArea(Rectangle2D clip){
		if(started){
			switch (type) {
				case POINT:
					List<Point2D> points = Project.getPointList();
					for (Point2D point2d : points) {
						if(!point2d.isSelected()){
				 			if(clip.contains(point2d)){
				 				point2d.selectThis();
				 			}
						}
					}
					break;
				case LINE:
					List<Line2D> lines = Project.getLineList();
					for (Line2D line2d : lines) {
						if(!line2d.isSelected()){
				 			if(clip.intersect(line2d)){
				 				line2d.selectThis();
				 			}else{
				 				if(clip.contains(line2d)){
				 					line2d.selectThis();
				 				}
				 			}
				 		}
					}
					break;
				case CIRCLE:
					List<Circle2D> circles = Project.getCircleList();
					for (Circle2D circle2d : circles) {
						if(!circle2d.isSelected()){
				 			if(circle2d.intersect(clip)){
				 				circle2d.selectThis();
				 			}else{
				 				if(clip.contains(circle2d.getBounds())){
				 					circle2d.selectThis();
				 				}
				 			}
				 		}
					}
					break;
				case ARC:
					List<Arc2D> arcs = Project.getArcList();
					for (Arc2D arc2d : arcs) {
						Line2D chords = arc2d.getChords();
				 		if(!arc2d.isSelected()){
				 			if(clip.contains(chords)){
				 				arc2d.selectThis();
				 			}else{
				 				if(arc2d.intersect(clip)){
				 					arc2d.selectThis();
				 				}
				 			}
				 		}
					}
					break;
				case BEZIER:
					List<BezierCurve2D> curves = Project.getBezierList();
					List<Point2D> pts;
					Line2D line;
					for (BezierCurve2D bezierCurve2D : curves) {
						if(!bezierCurve2D.isSelected()){
							pts = bezierCurve2D.computeBezierCurve(100);
							for (int i = 1; i < pts.size(); i++) {
								
								line = new Line2D(pts.get(i-1), pts.get(i));
								if(clip.intersect(line)){
					 				bezierCurve2D.selectThis();
					 				return;
					 			}else{
					 				if(clip.contains(line)){
					 					bezierCurve2D.selectThis();
					 					return;
					 				}
					 			}
							}
							
							
						}
					}
					break;
				case BORDER:
					List<Line2D> liness = Project.getLineList();
					for (Line2D line2d : liness) {
						if(!line2d.isSelected()){
				 			if(clip.intersect(line2d)){
				 				line2d.selectThis();
				 			}else{
				 				if(clip.contains(line2d)){
				 					line2d.selectThis();
				 				}
				 			}
				 		}
					}
					List<Arc2D> arcss = Project.getArcList();
					for (Arc2D arc2d : arcss) {
						Line2D chords = arc2d.getChords();
				 		if(!arc2d.isSelected()){
				 			if(clip.contains(chords)){
				 				arc2d.selectThis();
				 			}else{
				 				if(arc2d.intersect(clip)){
				 					arc2d.selectThis();
				 				}
				 			}
				 		}
					}
					
					List<Circle2D> circlesL = Project.getCircleList();
					for (Circle2D circle2d : circlesL) {
						if(!circle2d.isSelected()){
							if(circle2d.intersect(clip)){
								circle2d.selectThis();
							}
						}
					}
					
					
					List<BezierCurve2D> curvas = Project.getBezierList();
					List<Point2D> ptss;
					Line2D linee;
					for (BezierCurve2D bezierCurve2D : curvas) {
						if(!bezierCurve2D.isSelected()){
							ptss= bezierCurve2D.computeBezierCurve(100);
							for (int i = 1; i < ptss.size(); i++) {
								linee = new Line2D(ptss.get(i-1), ptss.get(i));
								if(clip.intersect(linee)){
					 				bezierCurve2D.selectThis();
					 				return;
					 			}else{
					 				if(clip.contains(linee)){
					 					bezierCurve2D.selectThis();
					 					return;
					 				}
					 			}
							}
							
							
						}
					}
					
					break;

				default:
					break;
			
			}
		}
	}
	/**
	 * Insert a geometry into a selection based on your geometry type.
	 * @param geometry2d geometry 2D.
	 * @since 1.0 
	 * */
	public static void selectGeometry(Geometry2D geometry2d){
		if(!started){ return;}
		switch (geometry2d.getType()) {
			case POINT:
				points.add((Point2D) geometry2d);
				break;
			case LINE:
				lines.add((Line2D) geometry2d);
				break;
			case CIRCLE:
				circles.add((Circle2D) geometry2d);
				break;
			case ARC:
				arcs.add((Arc2D) geometry2d);
				break;
			case BEZIER:
				beziers.add((BezierCurve2D) geometry2d);
				break;
			case FACE:
				faces.add((Face2D) geometry2d);
				break;
			default:
				break;
		}
		
	}
	/**
	 * Remove a geometry from a selection list based on your geometry type.
	 * @param geometry2d geometry 2D.
	 * @since 1.0 
	 * */
	public static void deselectGeometry(Geometry2D geometry2d){
		if(!started){ return;}
		switch (geometry2d.getType()) {
			case POINT:
				points.remove((Point2D) geometry2d);
				break;
			case LINE:
				lines.remove((Line2D) geometry2d);
				break;
			case CIRCLE:
				circles.remove((Circle2D) geometry2d);
				break;
			case ARC:
				arcs.remove((Arc2D) geometry2d);
				break;
			case BEZIER:
				beziers.remove((BezierCurve2D) geometry2d);
				break;
			case FACE:
				faces.remove((Face2D) geometry2d);
				break;
			default:
				
				break;
		}
	}
	public static List<Point2D> getPointsSelected(){
		return points;
	}
	public static List<Line2D> getLinesSelected(){
		return lines;
	}
	public static List<Arc2D> getArcsSelected(){
		return arcs;
	}
	public static List<Circle2D> getCirclesSelected(){
		return circles;
	}
	public static List<BezierCurve2D> getBezierSelected(){
		return beziers;
	}
	public static List<Border2D> getBordersSelected(){
		List<Border2D> borders = new ArrayList<>();
		borders.addAll(lines);
		borders.addAll(arcs);
		borders.addAll(beziers);
		return borders;
	}
	public static List<Face2D> getFacesSelected(){
		return faces;
	}

	
	public static List<Geometry2D> getSelection(){
		List<Geometry2D> selection = new ArrayList<>();
		selection.addAll(points);
		selection.addAll(lines);
		selection.addAll(circles);
		selection.addAll(arcs);
		selection.addAll(faces);
		selection.addAll(beziers);
		return selection;
	}
	/**
	 * Deselect all the geoemtries.
	 * @since 1.0.
	 * */
	public  static void clear(){
		List<Geometry2D> all = SelectGeometry.getSelection();
		Iterator<Geometry2D> iterator = all.iterator();
		while (iterator.hasNext()) {
			iterator.next().deselectThis();
		}
		for (Face2D face2d : faces) {
			face2d.isOver = false;
		}		
	}
	/**
	 * Returns the number of selected geometries.
	 * @return integer number.
	 * @since 1.0.
	 * */
	public static int size(){
		if(started){
			return points.size() + lines.size() + circles.size() + arcs.size() + faces.size()+beziers.size();
		}else{
			return -1;
		}
	}
	/**
	 * Verify if is an empty selection.
	 * @return <code>true</code>  if is empty. Otherwise, <code>false</code>.
	 * */
	public static boolean isEmpty(){
		if (started) {
				return size() == 0;
		}
		return true;
	}
	

	

}
