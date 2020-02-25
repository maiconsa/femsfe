package com.femsfe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

import com.femsfe.Analysis.AnalyticSolution;
import com.femsfe.Analysis.FiniteAnalysis;
import com.femsfe.enums.ProblemType;
import com.femsfe.BoundaryCondition.expressions.DirichletConditionExpression;
import com.femsfe.BoundaryCondition.value.DirichletConditionValue;
import com.femsfe.BoundaryCondition.expressions.RobinConditionExpression;
import com.femsfe.BoundaryCondition.value.RobinConditionValue;
import com.femsfe.BoundaryCondition.value.SimpleBCValue;
import com.femsfe.ComplexNumber.ComplexNumber;
import com.femsfe.Geometries.Arc2D;
import com.femsfe.Geometries.BezierCurve2D;
import com.femsfe.Geometries.Border2D;
import com.femsfe.Geometries.Circle2D;
import com.femsfe.Geometries.Face2D;
import com.femsfe.Geometries.Geometry2D;
import com.femsfe.enums.GeometryType;
import com.femsfe.Geometries.Line2D;
import com.femsfe.Geometries.Point2D;
import com.femsfe.Geometries.Polygon2D;
import com.femsfe.Geometries.Rectangle2D;
import com.femsfe.Geometries.Triangle2D;
import com.femsfe.enums.Units;
import com.femsfe.Triangulation.ConectivityList;
import com.femsfe.Triangulation.Triangulation;

public final class Project {
	private static LinkedHashSet<Point2D> pointList = new LinkedHashSet<>();
	private static List<Line2D> lineList = new ArrayList<>();
	private static List<Circle2D> circleList = new ArrayList<>();
	private static List<Arc2D> arcList = new ArrayList<>();
	private static List<BezierCurve2D> bezierList = new ArrayList<>();
	private static List<Face2D> faces = new ArrayList<>();
	private static Face2D externalFace;
	
	private static boolean isMaterialVisible = false;

	public static TreeViewProject treeView;
	private static String projectPath = null;

	public static String getProjectPath() {
		return projectPath;
	}

	public static void setProjectPath(String projectPath) {
		Project.projectPath = projectPath;
	}
	
	public static Face2D getExternalFace(){
		return externalFace;
	}
	public static Wavelenght wavelenght = new Wavelenght(0.0f, Units.METER);
	public static ProblemType problemType = ProblemType.ELETROSTATIC;
	private static ConectivityList mesh = null;

	private static FiniteAnalysis finiteAnalysis = new FiniteAnalysis();
	
	public static void setModelVisibility(boolean status){
		for (Face2D face2d : Project.getFaceList()) {
			face2d.setVisible(status);
		}
	}
	public static boolean allModelHasMaterial(){
		for (Face2D face2d : Project.getFaceList()) {
			if( face2d.getTypeFace() !=  Face2D.HOLE && face2d.getMaterial() == null) return false;
		}
		return true;
	}
	
	public static void computeAnalysis() throws OutOfMemoryError {
		finiteAnalysis.clear();
		finiteAnalysis.setMesh(mesh);
		finiteAnalysis.setProblemType(problemType);
		finiteAnalysis.compute();

		double x, y;
		System.out.println("VALUES ON LINE");
		Line2D line = new Line2D(0, 0.0f, 1, 0.0f);
		double valueOnLine;
		ArrayList<Point2D> points = line.divisionLineInPoint(25);

		Triangle2D triangle2d = null;

		for (Point2D point2d : points) {
			triangle2d = Triangulation.findTriangleWithPointInside(mesh.getElements(), point2d);
			x = point2d.getX();
			y = point2d.getY();
			if (triangle2d != null) {
				valueOnLine = triangle2d.getValueInsideElement((float) x, (float) y);
				System.out.println(point2d.getX() + "," + AnalyticSolution.valueOnConductorEsphereOnEF(x, y, 1, 0.2, 0)
						+ valueOnLine);
			}
		}

	}

	public static ConectivityList getMesh() {
		return mesh;
	}

	public static void setMesh(ConectivityList mesh) {
		Project.mesh = mesh;
	}
	
	public static float[] getOrtho(){
		float ortho[] = new float[4];
		if(pointList.size() == 0) {
			return null;
		}
		
		float xMin = Float.POSITIVE_INFINITY,xMax = Float.NEGATIVE_INFINITY,yMin = Float.POSITIVE_INFINITY,yMax = Float.NEGATIVE_INFINITY;
		for (Point2D point2d : pointList) {
			xMin  = (float) Math.min(xMin, point2d.getX());
			xMax  = (float) Math.max(xMax, point2d.getX());
			
			yMin  = (float) Math.min(yMin, point2d.getY());
			yMax  = (float) Math.max(yMax, point2d.getY());
		}
		float xMed = (xMin+xMax)/2;
		float yMed = (yMin+yMax)/2;
		
		float w = (xMax - xMin);
		float h = (yMax - yMin);
		
		float L = (float) Math.sqrt(w*h);
		
		 ortho[0] = xMed-(L*0.7f);
		 ortho[1] = xMed+(L*0.7f);
		 ortho[2] = yMed-(L*0.7f);
		 ortho[3] = yMed+(L*0.7f);
		return ortho;
		
	}

	public static Wavelenght getWavelenght() {
		return wavelenght;
	}

	public static boolean isMaterialVisible(){
		return isMaterialVisible;
	}
	public static void setIsMaterialVisible(boolean status){
		Project.isMaterialVisible = status;
	}
	public static boolean isInsideDomain(Point2D point2d) {
		if (Project.externalFace != null) {
			if (Project.externalFace.getPolygon().contains(point2d)) {
				for (Face2D face : faces) {
					if (face.getTypeFace() == Face2D.HOLE) {
						if (face.getPolygon().contains(point2d)) {
							return false;
						}
					}
				}
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public static void setWavelenght(Wavelenght wavelenght) {
		Project.wavelenght = wavelenght;
	}

	public static FiniteAnalysis getFiniteAnalysis() {
		return finiteAnalysis;
	}

	public static void setProblemType(ProblemType problemType) {
		Project.problemType = problemType;
	}

	public static ProblemType getProblemType() {
		return Project.problemType;
	}

	/**
	 * ADD AND REMOVE GEOMETRY
	 */
	public static void addGeometry(Geometry2D geometry) {
		if (geometry == null) {
			return;
		}

		switch (geometry.getType()) {
		case POINT:
			pointList.add((Point2D) geometry);
			treeView.updatePointsItems();
			break;
		case LINE:
			Line2D line = (Line2D) geometry;
			lineList.add(line);

			line.getP0().setIsChild(true);
			line.getP1().setIsChild(true);

			line.getP0().numberLinkedBorders++;
			line.getP1().numberLinkedBorders++;

			pointList.add(line.getP0());
			pointList.add(line.getP1());

			treeView.updatePointsItems();
			treeView.updateLinesItems();
			break;
		case CIRCLE:
			circleList.add((Circle2D) geometry);
			treeView.updateCirclesItems();
			break;
		case ARC:
			Arc2D arc = (Arc2D) geometry;
			arcList.add(arc);

			pointList.add(arc.getCenterPoint());
			pointList.add(arc.getEndPoint());

			pointList.add(arc.getStartPoint());

			arc.getCenterPoint().setIsChild(true);
			arc.getStartPoint().setIsChild(true);
			arc.getEndPoint().setIsChild(true);

			arc.getCenterPoint().setVisible(false);
			arc.getCenterPoint().numberLinkedBorders++;
			arc.getStartPoint().numberLinkedBorders++;
			arc.getEndPoint().numberLinkedBorders++;

			treeView.updatePointsItems();
			treeView.updateArcsItems();
			break;
		case RECTANGLE:
			Rectangle2D rect = (Rectangle2D) geometry;
			for (Line2D line2d : rect.getBoundary()) {
				lineList.add(line2d);
			}
			rect.getP0().setIsChild(true);
			rect.getP1().setIsChild(true);
			rect.getQ0().setIsChild(true);
			rect.getQ1().setIsChild(true);

			rect.getP0().numberLinkedBorders++;
			rect.getP1().numberLinkedBorders++;
			rect.getQ0().numberLinkedBorders++;
			rect.getQ1().numberLinkedBorders++;

			pointList.add(rect.getP0());
			pointList.add(rect.getP1());
			pointList.add(rect.getQ0());
			pointList.add(rect.getQ1());

			treeView.updatePointsItems();
			treeView.updateLinesItems();
			break;
		case POLYGON:
			Polygon2D poly = (Polygon2D) geometry;
			for (Line2D line2d : poly.getBoundary()) {
				lineList.add(line2d);
			}
			HashSet<Point2D> pts = new HashSet<>(poly.getPointList());
			for (Point2D point2d : pts) {
				point2d.setIsChild(true);
				point2d.numberLinkedBorders++;
				pointList.add(point2d);
			}

			treeView.updatePointsItems();
			treeView.updateLinesItems();
			break;
		case BEZIER:
			BezierCurve2D bezier = (BezierCurve2D) geometry;
			bezier.pointCtrlVisible = false;
			bezierList.add(bezier);
			Point2D startPoint = bezier.getControlPoints().get(0);
			Point2D endPoint = bezier.getControlPoints().get(bezier.getControlPoints().size()-1);
			
			pointList.add(startPoint);
			pointList.add(endPoint);
			pointList.addAll(bezier.getControlPoints());
			
			startPoint.setIsChild(true);
			endPoint.setIsChild(true);
			
			startPoint.numberLinkedBorders++;
			endPoint.numberLinkedBorders++;
			
			treeView.updateBezierItems();
			treeView.updatePointsItems();
			break;
		case FACE:
			Face2D face = (Face2D) geometry;
				
			if (face.getTypeFace() == Face2D.EXTERNAL) {
				Project.externalFace = face;
				faces.add(0, face);
			}else{
				faces.add(face);
			}
			for (Border2D border2d : face.getBoundaries()) {
				border2d.setIsChild(true);
				border2d.numberLinkedFaces++;
			}
			treeView.updateFacesItems();
			break;
		default:
			break;
		}

	}
	


	public static void addAllGeometry(Collection<? extends Geometry2D> list) {
		if (list == null) {
			return;
		}
		for (Geometry2D geometry2d : list) {
			addGeometry(geometry2d);
		}

	}

	public static void removeGeometry(Geometry2D geometry) {
		if (geometry == null) {
			return;
		}
		if (geometry.isChild()) {
			return;
		}
		switch (geometry.getType()) {
		case POINT:
			pointList.remove(geometry);
			treeView.updatePointsItems();
			break;
		case LINE:
			Line2D line = (Line2D) geometry;

			if (line.getP0().numberLinkedBorders == 1) {
				line.getP0().setIsChild(false);
				line.getP0().numberLinkedBorders = 0;
			} else {
				line.getP0().numberLinkedBorders--;
			}
			if (line.getP1().numberLinkedBorders == 1) {
				line.getP1().setIsChild(false);
				line.getP1().numberLinkedBorders = 0;
			} else {
				line.getP1().numberLinkedBorders--;
			}

			lineList.remove(line);
			treeView.updateLinesItems();
			break;
		case CIRCLE:
			circleList.remove(geometry);
			treeView.updateCirclesItems();
			break;
		case ARC:
			Arc2D arc = (Arc2D) geometry;

			if (arc.getCenterPoint().numberLinkedBorders == 1) {
				arc.getCenterPoint().setIsChild(false);
				arc.getCenterPoint().numberLinkedBorders = 0;
			} else {
				arc.getCenterPoint().numberLinkedBorders--;
			}

			if (arc.getStartPoint().numberLinkedBorders == 1) {
				arc.getStartPoint().setIsChild(false);
				arc.getStartPoint().numberLinkedBorders = 0;
			} else {
				arc.getStartPoint().numberLinkedBorders--;
			}

			if (arc.getEndPoint().numberLinkedBorders == 1) {
				arc.getEndPoint().setIsChild(false);
				arc.getEndPoint().numberLinkedBorders = 0;
			} else {
				arc.getEndPoint().numberLinkedBorders--;
			}

			arcList.remove(geometry);
			treeView.updateArcsItems();
			break;
		case BEZIER:
			bezierList.remove(geometry);
			BezierCurve2D bezier = (BezierCurve2D) geometry;
			Point2D startPoint = bezier.getControlPoints().get(0);
			Point2D endPoint = bezier.getControlPoints().get(bezier.getControlPoints().size()-1);
			

			
			if (startPoint.numberLinkedBorders == 1) {
				startPoint.setIsChild(false);
				startPoint.numberLinkedBorders = 0;
			} else {
				startPoint.numberLinkedBorders--;
			}

			if (endPoint.numberLinkedBorders == 1) {
				endPoint.setIsChild(false);
				endPoint.numberLinkedBorders = 0;
			} else {
				endPoint.numberLinkedBorders--;
			}
			
			treeView.updateBezierItems();
			treeView.updatePointsItems();
			break;
		case FACE:
			faces.remove(geometry);
			treeView.updateFacesItems();
			Face2D face = (Face2D) geometry;
			if (face.getTypeFace() == Face2D.EXTERNAL) {
				Project.externalFace = null;
			}
			for (Border2D border2d : face.getBoundaries()) {
				if (border2d.numberLinkedFaces == 1) {
					border2d.numberLinkedFaces = 0;
					border2d.setIsChild(false);
				} else {
					border2d.numberLinkedFaces--;
				}

			}
			break;
		default:
			break;
		}

	}

	public static void removeAllGeometry(Collection<? extends Geometry2D> list) {
		if (list == null) {
			return;
		}
		for (Geometry2D geometry2d : list) {
			removeGeometry(geometry2d);
		}

	}

	/**
	 * GETTERS
	 */
	public static int pointSize() {
		return pointList.size();
	}

	public static int lineSize() {
		return lineList.size();
	}

	public static int circleSize() {
		return circleList.size();
	}

	public static int arcSize() {
		return arcList.size();
	}

	public static int bezierSize() {
		return bezierList.size();
	}

	public static int faceSize() {
		return faces.size();
	}

	public static List<Point2D> getPointList() {
		return new ArrayList<>(pointList);
	}

	public static List<Line2D> getLineList() {
		return lineList;
	}

	public static List<Circle2D> getCircleList() {
		return circleList;
	}

	public static List<Arc2D> getArcList() {
		return arcList;
	}

	public static List<BezierCurve2D> getBezierList() {
		return bezierList;
	}

	public static List<Face2D> getFaceList() {
		return faces;
	}

	public static List<Border2D> getBordersList() {
		List<Border2D> borders = new ArrayList<>();
		borders.addAll(getLineList());
		borders.addAll(getArcList());
		borders.addAll(getBezierList());
		return borders;
	}

	public static List<? extends Geometry2D> getGeometryList() {
		List<Geometry2D> geometryList = new ArrayList<>();
		geometryList.addAll(faces);
		geometryList.addAll(pointList);
		geometryList.addAll(lineList);
		geometryList.addAll(circleList);
		geometryList.addAll(arcList);
		geometryList.addAll(bezierList);
		
		return geometryList;

	}

	/*
	 * Methods for take the geometry near of a point The epsilon distance is
	 * Geometry.EPSILON
	 */
	public Point2D getPointNearPoint(Point2D point) {
		if (point == null) {
			return null;
		}
		return getPointNearPoint(point.getX(), point.getY());
	}

	public static Point2D getPointNearPoint(double x, double y) {
		for (Point2D point2d : getPointList()) {
			if (point2d.isNear(x, y)) {
				return point2d;
			}
		}
		return null;
	}

	public Line2D getLineNearPoint(Point2D point) {
		if (point == null) {
			return null;
		}
		return getLineNearPoint(point.getX(), point.getY());
	}

	public static Line2D getLineNearPoint(double x, double y) {
		double dist;
		for (Line2D line2d : getLineList()) {
			dist = line2d.distanceTo(x, y);
			if (dist >= 0 && dist <= Geometry2D.EPSILON) {
				return line2d;
			}
		}
		return null;
	}

	public Circle2D getCircleNearPoint(Point2D point) {
		return getCircleNearPoint(point.getX(), point.getY());
	}

	public static Circle2D getCircleNearPoint(double x, double y) {
		for (Circle2D circle2d : getCircleList()) {
			if (circle2d.isNear(x, y)) {
				return circle2d;
			}
		}
		return null;
	}

	public Arc2D getArcNearPoint(Point2D point) {
		return getArcNearPoint(point.getX(), point.getY());
	}

	public static Arc2D getArcNearPoint(double x, double y) {
		for (Arc2D arc2d : getArcList()) {
			if (arc2d.isNear(x, y))
				return arc2d;
		}
		return null;
	}
	public static BezierCurve2D getBezierNearPoint(double x, double y) {
		for (BezierCurve2D bezier : getBezierList()) {
			if (bezier.isNear(x, y))
				return bezier;
		}
		return null;
	}
	

	public Face2D getPartNearPoint(double x, double y) {
		for (Face2D part : faces) {
			if (part.isNear(x, y))
				return part;
		}
		return null;
	}

	public static Border2D getBorderNearPoint(double x, double y) {
		Border2D path = getLineNearPoint(x, y);
		if (path == null) {
			path = getCircleNearPoint(x, y);
			if (path == null) {
				path = getArcNearPoint(x, y);
				if(path == null){
					path = getBezierNearPoint(x,y);
				}
			}
		}
		return path;
	}

	public static List<Face2D> getFacesWithPointInside(double x, double y) {
		List<Face2D> faces = new ArrayList<>();
		for (Face2D face2d : getFaceList()) {
			if (face2d.getPolygon().contains(x, y)) {
				faces.add(face2d);
			}
		}
		return faces;
	}

	public static List<Face2D> getFacesWithPointInside(Point2D point) {
		return getFacesWithPointInside(point.getX(), point.getY());
	}

	public static Geometry2D getGeometryNearPoint(GeometryType type, Point2D point2d) {
		return getGeometryNearPoint(type, point2d.getX(), point2d.getY());
	}

	public static Geometry2D getGeometryNearPoint(double x, double y) {
		List<GeometryType> types = new ArrayList<>(8);
		types.add(GeometryType.POINT);
		types.add(GeometryType.LINE);
		types.add(GeometryType.RECTANGLE);
		types.add(GeometryType.POLYGON);
		types.add(GeometryType.CIRCLE);
		types.add(GeometryType.ARC);
		types.add(GeometryType.BEZIER);
		Geometry2D geometry2d;
		for (GeometryType geometryType : types) {
			geometry2d = getGeometryNearPoint(geometryType, x, y);
			if (geometry2d != null) {
				return geometry2d;
			}
		}
		return null;

	}

	public static Geometry2D getGeometryNearPoint(GeometryType type, double x, double y) {
		switch (type) {
		case POINT:
			return getPointNearPoint(x, y);
		case LINE:
			return getLineNearPoint(x, y);
		case CIRCLE:
			return getCircleNearPoint(x, y);
		case ARC:
			return getArcNearPoint(x, y);
		default:
			break;
		}
		return null;
	}

	public static List<DirichletConditionValue> getDirichletCondition() {

		float factor = (float) Geometry2D.getUnit().getFactor();
		ArrayList<DirichletConditionValue> dirichletCondition = new ArrayList<DirichletConditionValue>();
		HashSet<Border2D> borders = new HashSet<>();
		for (Face2D face : getFaceList()) {
			borders.addAll(face.getBoundaries());
		}
		for (Border2D border2d : borders) {
			if (border2d.isDirichletCondition()) {
				DirichletConditionExpression dirichletExpression = (DirichletConditionExpression) border2d
						.getConditionExpression();
				for (Point2D point2d : border2d.getChewPoints()) {
					dirichletCondition.add(new DirichletConditionValue(point2d.getIndex(),
							dirichletExpression.getValue(point2d.getX() * factor, point2d.getY() * factor)));
				}

			}
		}
		return dirichletCondition;

	}

	public static List<RobinConditionValue> getRobinCondition() {
		float factor = (float) Geometry2D.getUnit().getFactor();
		ArrayList<RobinConditionValue> robinCondition = new ArrayList<RobinConditionValue>();
		HashSet<Border2D> borders = new HashSet<>();
		for (Face2D face : getFaceList()) {
			borders.addAll(face.getBoundaries());
		}
		for (Border2D border2d : borders) {

			if (border2d.isRobinCondition()) {
				RobinConditionExpression robinExpression = (RobinConditionExpression) border2d.getConditionExpression();
				List<Point2D> chewPts = border2d.getChewPoints();
				for (int i = 1; i < chewPts.size(); i++) {
					Line2D line = new Line2D(chewPts.get(i - 1), chewPts.get(i));
					Point2D midPoint = line.midPoint();
					RobinConditionValue robinConditionValue = new RobinConditionValue(chewPts.get(i - 1).getIndex(),
							chewPts.get(i).getIndex(),
							robinExpression.getQValue(midPoint.getX() * factor, midPoint.getY() * factor),
							robinExpression.getYValue(midPoint.getX() * factor, midPoint.getY() * factor));
					System.out.println("QVALUE :" + robinConditionValue.getqValue().getReal() + "GamaVALUE: "
							+ robinConditionValue.getyValue().getReal());
					robinCondition.add(robinConditionValue);
				}
			}
		}
		return robinCondition;

	}

	public static List<SimpleBCValue> getSimpleBoundaryCondition() {

		float factor = (float) Geometry2D.getUnit().getFactor();
		ArrayList<SimpleBCValue> simpleBoundaryCondition = new ArrayList<SimpleBCValue>();
		HashSet<Border2D> borders = new HashSet<>();
		for (Face2D face : getFaceList()) {
			borders.addAll(face.getBoundaries());
		}
		for (Border2D border2d : borders) {
			if (border2d.isBoundaryCondition() && border2d.isSBC()) {
				List<Point2D> chewPts = border2d.getChewPoints();
				ComplexNumber value0 = new ComplexNumber(0, 0), value1 = new ComplexNumber(0, 0);
				float A = 1;
				Point2D p0, p1;
				for (int i = 1; i < chewPts.size(); i++) {
					p0 = chewPts.get(i - 1);
					p1 = chewPts.get(i);
					if (border2d.isInputPort()) {
						// A = (float) Math.exp(-Math.pow(p0.getY()/0.7, 2));
						value0.setReal((float) (A * Math.cos(wavelenght.getK0() * p0.getX() * factor)));
						value0.setImg((float) (A * Math.sin(wavelenght.getK0() * p0.getX() * factor)));

						// A = (float) Math.exp(-Math.pow(p1.getY()/0.7, 2));
						value1.setReal((float) (A * Math.cos(wavelenght.getK0() * p1.getX() * factor)));
						value1.setImg((float) (A * Math.sin(wavelenght.getK0() * p1.getX() * factor)));

					} else {
						value0.setReal(0);
						value0.setImg(0);
						value1.setReal(0);
						value1.setImg(0);
					}
					simpleBoundaryCondition.add(
							new SimpleBCValue(p0.getIndex(), p1.getIndex(), value0, value1, border2d.getPortType()));
				}

			}
		}
		return simpleBoundaryCondition;
	}

	public static List<Line2D> chewSegmentation(float hmin) {
		List<Line2D> list = new ArrayList<>();
		for (Border2D border2d : getBordersFromFaces()) {
			list.addAll(border2d.chewSegmentation(hmin));
		}
		return list;
	}

	public static List<Border2D> getBordersFromFaces() {
		HashSet<Border2D> borders = new HashSet<>();
		for (Face2D face : getFaceList()) {
			borders.addAll(face.getBoundaries());
		}
		return new ArrayList<>(borders);
	}

	public static void clearAll() {
		pointList.clear();
		lineList.clear();
		arcList.clear();
		circleList.clear();
		bezierList.clear();
		faces.clear();

		mesh = null;
		wavelenght = new Wavelenght(0.0f, Units.METER);
		problemType = ProblemType.ELETROSTATIC;

		finiteAnalysis.clear();

		treeView.update();
	}

}
