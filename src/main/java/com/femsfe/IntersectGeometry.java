package com.femsfe;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import com.femsfe.Geometries.Arc2D;
import com.femsfe.Geometries.Circle2D;
import com.femsfe.Geometries.Geometry2D;
import com.femsfe.Geometries.Line2D;
import com.femsfe.Geometries.Point2D;

public class IntersectGeometry {

    public final static String LINES = "INTER_LINES";
    public final static String LINES_CIRCLE = "INTER_LINES_CIRCLE";
    public final static String LINES_ARC = "INTER_LINES_ARC";
    public final static String CIRCLE_CIRCLE = "INTER_CIRCLE_CIRCLE";
    public final static String NONE = "NONE";

    private static String type;
    private static boolean start = false;
    private static boolean finished = false;

    public static void begin(String type) {
        IntersectGeometry.type = type;
        start = true;
    }

    public static boolean started() {
        return start;
    }

    public static boolean finished() {
        return finished;
    }

    public static String getType() {
        return type;
    }

    public static void compute() {
        switch (type) {
            case IntersectGeometry.LINES:
                List<Line2D> linesL = SelectGeometry.getLinesSelected();
                if (linesL.size() < 2) {
                    SelectGeometry.clear();
                    return;
                }
                List<Line2D> intersections = bruteSegmentsIntersections(linesL);
                System.out.println("INTER SIZE:" + intersections.size());
                Project.removeAllGeometry(linesL);
                Project.addAllGeometry(intersections);
                SelectGeometry.clear();
                break;
            case IntersectGeometry.LINES_CIRCLE:
                List<Line2D> segments = SelectGeometry.getLinesSelected();
                Circle2D circle = SelectGeometry.getCirclesSelected().get(0);

                List<Geometry2D> inter = (List<Geometry2D>) segmentsCircle(segments, circle);
                Project.removeAllGeometry(segments);
                Project.removeGeometry(circle);
                Project.addAllGeometry(inter);
                SelectGeometry.clear();
                break;
            case IntersectGeometry.LINES_ARC:
                if (SelectGeometry.started()) {
                    List<Line2D> lines = SelectGeometry.getLinesSelected();
                    Arc2D arc = SelectGeometry.getArcsSelected().get(0);
                    Project.addAllGeometry(segmentsArc(lines, arc));
                    Project.removeAllGeometry(lines);
                    Project.removeGeometry(arc);
                    SelectGeometry.clear();
                }
                break;
            case IntersectGeometry.CIRCLE_CIRCLE:
                Circle2D a = SelectGeometry.getCirclesSelected().get(0);
                Circle2D b = SelectGeometry.getCirclesSelected().get(1);
                circleCircle((Circle2D) a, (Circle2D) b);
                break;
            default:
                break;
        }
    }

    public static void end() {
        start = false;
        reset();
    }

    public static void reset() {
        finished = false;
    }

    /* ********************************************************************************************
	 * 			INTERSECTION  METHODS  -  line and others geometries
	 * ********************************************************************************************/
    private static void segmentSegment(Line2D a, Line2D b) {
        if (!started()) {
            return;
        }
        List<Line2D> list = Line2D.intersection(a, b);
        if (!list.isEmpty()) {
            Project.addAllGeometry(list);
            a.deselectThis();
            b.deselectThis();
            Project.removeGeometry(a);
            Project.removeGeometry(b);
        } else {
            JOptionPane.showMessageDialog(null, "Ponto de intersecçãoo não encontrado!!", "Ponto de intersecçãoo não encontrado", JOptionPane.INFORMATION_MESSAGE);
            a.deselectThis();
            b.deselectThis();
        }
    }

    private static List<Line2D> bruteSegmentsIntersections(List<Line2D> segments) {
        for (Line2D a : segments) {
            for (Line2D b : segments) {
                if (!b.equals(a)) {

                    Point2D I = a.intersection(b);
                    if (I != null) {

                        for (Point2D point2d : b.intersections) {
                            if (point2d.distanceTo(I) <= 0.000001) {
                                I = point2d;
                                break;
                            }
                        }
                        if (!a.containIntersection(I) && (!I.equals(a.getP0()) && !I.equals(a.getP1()))) {
                            a.addIntersection(I);

                        }

                    }
                }
            }
        }
        List<Line2D> intersections = new ArrayList<>();
        segments.forEach((line2d) -> {intersections.addAll(line2d.getLinesFromInterPoints());});
        return intersections;

    }

    private static List<Geometry2D> segmentsArc(List<Line2D> segments, Arc2D arc2d) {

        Point2D[] interPoints;
        for (Line2D line2d : segments) {
            interPoints = arc2d.intersection(line2d);
            if (interPoints != null) {
                for (int i = 0; i < interPoints.length; i++) {
                    arc2d.addIntersectionPoint(interPoints[i]);
                    line2d.addIntersection(interPoints[i]);
                }
            }
        }
        List<Geometry2D> list = new ArrayList<>();
        for (Line2D line2d2 : segments) {
            list.addAll(line2d2.getLinesFromInterPoints());
        }

        list.addAll(arc2d.getArcsFromIntersection());
        return list;
    }

    private static void segmentArc(Line2D line2d, Arc2D arc2d) {
        Point2D[] interPoints = arc2d.intersection(line2d);
        if (interPoints != null) {
            for (int i = 0; i < interPoints.length; i++) {
                arc2d.addIntersectionPoint(interPoints[i]);
                line2d.addIntersection(interPoints[i]);
            }
            List<Arc2D> arcs = arc2d.getArcsFromIntersection();
            if (arcs != null && arcs.size() >= 2) {
                Project.addAllGeometry(arcs);
                Project.removeGeometry(arc2d);
                arc2d.deselectThis();
            }
            line2d.deselectThis();

            Project.addAllGeometry(line2d.getLinesFromInterPoints());
            Project.removeGeometry(line2d);
        } else {
            JOptionPane.showMessageDialog(null, "O segmento e arcos n�o possuem ponto de intersec��o",
                    "Ponto de interse��o n�o encontrado", JOptionPane.INFORMATION_MESSAGE);
            line2d.deselectThis();
            arc2d.deselectThis();
        }
    }

    private static void segmentCircle(Line2D line2d, Circle2D circle2d) {
        Point2D[] interPoints = circle2d.intersection(line2d);
        if (interPoints != null) {
            for (int i = 0; i < interPoints.length; i++) {
                circle2d.addIntersectionPoint(interPoints[i]);
                line2d.addIntersection(interPoints[i]);
            }
            List<Arc2D> arcs = circle2d.getArcsFromInterAngles();
            if (arcs != null && arcs.size() >= 2) {
                Project.addAllGeometry(arcs);
                Project.removeGeometry(circle2d);
                circle2d.deselectThis();
            }
            line2d.deselectThis();

            Project.addAllGeometry(line2d.getLinesFromInterPoints());
            Project.removeGeometry(line2d);
        } else {
            JOptionPane.showMessageDialog(null, "O segmento e c�rculo n�o possuem ponto de intersec��o",
                    "Ponto de interse��o n�o encontrado", JOptionPane.INFORMATION_MESSAGE);
            line2d.deselectThis();
            circle2d.deselectThis();
        }
    }

    private static List<Geometry2D> segmentsCircle(List<Line2D> segments, Circle2D circle2d) {
        List<Geometry2D> newGeometryies = new ArrayList<>();
        boolean hasIntersection = false;
        for (Line2D line2d : segments) {
            Point2D[] intersections = circle2d.intersection(line2d);
            if (null == intersections) {
                line2d.deselectThis();
                newGeometryies.add(line2d);
            } else {
                for (Point2D intersection1 : intersections) {
                    line2d.addIntersection(intersection1);
                    circle2d.addIntersectionPoint(intersection1);
                }
                newGeometryies.addAll(line2d.getLinesFromInterPoints());
                hasIntersection = true;
            }
        }
        if (hasIntersection) {
            newGeometryies.addAll(circle2d.getArcsFromInterAngles());
            return newGeometryies;
        } else {
            newGeometryies.clear();
            return new ArrayList<>();
        }
    }

    private static void circleCircle(Circle2D a, Circle2D b) {
        Point2D[] intersection = a.intersection(b);
        if (intersection != null) {
            if (intersection.length == 2) {
                Point2D I1 = intersection[0];
                Point2D I2 = intersection[1];
                Project.addGeometry(new Arc2D(a.getCenterPoint(), I1, I2));
                Project.addGeometry(new Arc2D(a.getCenterPoint(), I2, I1));

                Project.addGeometry(new Arc2D(b.getCenterPoint(), I1, I2));
                Project.addGeometry(new Arc2D(b.getCenterPoint(), I2, I1));

                a.deselectThis();
                b.deselectThis();
                Project.removeGeometry(a);
                Project.removeGeometry(b);
            } else {
                JOptionPane.showMessageDialog(null, "Possu� apena 1 ponto de intersec��o",
                        "Ponto de interse��o �nico", JOptionPane.INFORMATION_MESSAGE);

                a.deselectThis();
                b.deselectThis();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Os c�ruclos n�o se interceptam ",
                    "Ponto de interse��o nulo", JOptionPane.INFORMATION_MESSAGE);

            a.deselectThis();
            b.deselectThis();
        }

    }

}
