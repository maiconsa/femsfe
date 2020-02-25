package com.femsfe.Geometries;

import com.femsfe.enums.GeometryType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Maicon Alcântara
 */
public class Arc2D extends Border2D {

    private double radius = -1, startAngle = -1, endAngle = -1;
    private Point2D centerPoint = null, startPoint = null, endPoint = null;
    private List<Point2D> intersectionPoints = new ArrayList<>();

    public Arc2D(Point2D centerPoint, Point2D startPoint, Point2D endPoint) {
        this();
        this.radius = centerPoint.distanceTo(startPoint);
        this.centerPoint = centerPoint;
        setStartPoint(startPoint);
        setEndPoint(endPoint);
    }

    public Arc2D() {
        this.type = GeometryType.ARC;
    }

    /*	SETTERS */
    public void setRadius(double radius) {
        this.radius = radius;
    }

    public void setCenterPoint(Point2D centerPoint) {
        this.centerPoint = centerPoint;
    }

    public void setStartAngle(double start) {
        this.startAngle = start;

        double x = centerPoint.getX() + radius * Math.cos(Math.toRadians(start));
        double y = centerPoint.getY() + radius * Math.sin(Math.toRadians(start));

        startPoint = new Point2D(x, y);

    }

    public void setEndAngle(double end) {
        this.endAngle = end;
        double x = centerPoint.getX() + radius * Math.cos(Math.toRadians(end));
        double y = centerPoint.getY() + radius * Math.sin(Math.toRadians(end));

        endPoint = new Point2D(x, y);

    }

    public void setStartPoint(double x, double y) {
        double dx = x - getCenterPoint().getX();
        double dy = y - getCenterPoint().getY();
        double angle = Math.toDegrees(Math.atan2(dy, dx));
        if (angle < 0) {
            angle += 360;
        }
        startAngle = angle;
        startPoint = new Point2D(x, y);
        if (radius == -1) {
            setRadius(getCenterPoint().distanceTo(x, y));
        }
    }

    public void setStartPoint(Point2D point2d) {
        if (point2d == null) {
            return;
        }
        double dx = point2d.getX() - getCenterPoint().getX();
        double dy = point2d.getY() - getCenterPoint().getY();
        double angle = Math.toDegrees(Math.atan2(dy, dx));
        if (angle < 0) {
            angle += 360;
        }
        startAngle = (float) angle;
        startPoint = point2d;
        if (radius == -1) {
            setRadius(getCenterPoint().distanceTo(point2d.getX(), point2d.getY()));
        }
    }

    public void setEndPoint(Point2D point2d) {
        if (point2d == null) {
            return;
        }
        double dx = point2d.getX() - getCenterPoint().getX();
        double dy = point2d.getY() - getCenterPoint().getY();
        double angle = Math.toDegrees(Math.atan2(dy, dx));
        if (angle < 0) {
            angle += 360;
        }
        endAngle = angle;
        endPoint = point2d;
        if (endPoint.distanceTo(getCenterPoint()) != getRadius()) {
            double x = centerPoint.getX() + radius * Math.cos(Math.toRadians(endAngle));
            double y = centerPoint.getY() + radius * Math.sin(Math.toRadians(endAngle));
            endPoint.setCoordinates(x, y);
        }

        if (radius == -1) {
            setRadius(getCenterPoint().distanceTo(point2d.getX(), point2d.getY()));
        }
    }

    public void setEndPoint(double x, double y) {
        double dx = x - getCenterPoint().getX();
        double dy = y - getCenterPoint().getY();
        double angle = Math.toDegrees(Math.atan2(dy, dx));
        if (angle < 0) {
            angle += 360;
        }
        endAngle = angle;
        endPoint = new Point2D(x, y);
        if (radius == -1) {
            setRadius(getCenterPoint().distanceTo(x, y));
        }

    }

    /*	GETTERS	*/
    public double getRadius() {
        return radius;
    }

    public Point2D getCenterPoint() {
        return centerPoint;
    }

    public double getStartAngle() {
        return this.startAngle;
    }

    public double getEndAngle() {
        return this.endAngle;
    }

    public Point2D getStartPoint() {
        return startPoint;
    }

    public Point2D getEndPoint() {
        return endPoint;
    }

    public Line2D getChords() {
        Point2D start = getStartPoint();
        Point2D end = getEndPoint();
        if (start != null && end != null) {
            return new Line2D(start, end);
        } else {
            return null;
        }
    }

    public void swapAngles() {
        double aux = getStartAngle();
        setStartAngle(getEndAngle());
        setEndAngle(aux);
    }

    public void reset() {
        startAngle = -1;
        endAngle = -1;
        startPoint = null;
        endPoint = null;
        centerPoint = null;
        radius = -1;
    }

    @Override
    public Geometry2D copy() {
        return new Arc2D(getCenterPoint(), getStartPoint(), getEndPoint());
    }

    public void addIntersectionPoint(Point2D point2d) {
        intersectionPoints.add(point2d);
    }

    public List<Arc2D> getArcsFromIntersection() {
        List<Arc2D> arcs = new ArrayList<>();
        if (intersectionPoints.size() >= 1) {
            intersectionPoints.add(getStartPoint());
            intersectionPoints.add(getEndPoint());
            Collections.sort(intersectionPoints, new OrderByAngle(getCenterPoint()));
            for (int i = 1; i < intersectionPoints.size(); i++) {
                arcs.add(new Arc2D(getCenterPoint(), intersectionPoints.get(i - 1), intersectionPoints.get(i)));
            }
            arcs.add(new Arc2D(getCenterPoint(), intersectionPoints.get(intersectionPoints.size() - 1), intersectionPoints.get(0)));

            for (Arc2D arc2d : arcs) {
                if (arc2d.getStartPoint().equals(this.getEndPoint()) && arc2d.getEndPoint().equals(this.getStartPoint())) {
                    arcs.remove(arc2d);
                    break;
                }
            }
            return arcs;
        } else {
            return null;
        }
    }

    public Point2D[] intersection(Line2D ab) {
        Circle2D circle2d = new Circle2D(getRadius(), getCenterPoint());
        Point2D[] inter = circle2d.intersection(ab);
        switch (inter.length) {
            case 2:
                if (this.between(inter[0]) && this.between(inter[1])) {
                    Point2D[] intersection = new Point2D[2];
                    intersection[0] = inter[0];
                    intersection[1] = inter[1];
                    return intersection;
                } else if (this.between(inter[0])) {
                    Point2D[] intersection = new Point2D[1];
                    intersection[0] = inter[0];
                    return intersection;
                } else if (this.between(inter[1])) {
                    Point2D[] intersection = new Point2D[1];
                    intersection[0] = inter[1];
                    return intersection;
                }
            case 1:
                if (this.between(inter[0])) {
                    Point2D[] intersection = new Point2D[1];
                    intersection[0] = inter[0];
                    return intersection;
                }
            default:
                return null;
        }

    }

    @Override
    public boolean isNear(Point2D point2d) {
        return isNear(point2d, Geometry2D.EPSILON);
    }

    @Override
    public boolean isNear(Point2D point2d, double epsilon) {
        return isNear(point2d.getX(), point2d.getY(), epsilon);
    }

    @Override
    public boolean isNear(double x, double y) {
        return isNear(x, y, Geometry2D.EPSILON);
    }

    @Override
    public boolean isNear(double x, double y, double epsilon) {
        if (between(x, y)) {
            double dist = Math.abs(getCenterPoint().distanceTo(x, y) - getRadius());
            return dist >= 0 && dist <= epsilon;
        }
        return false;
    }

    public boolean intersect(Rectangle2D rect) {
        for (Line2D line2d : rect.getBoundary()) {
            if (intersect(line2d)) {
                return true;
            }
        }
        return false;
    }

    public boolean between(double x, double y) {
        return Triangle2D.area2(getEndPoint(), getStartPoint(), new Point2D(x, y)) > 0;
    }

    public boolean between(Point2D point) {
        return Triangle2D.area2(getEndPoint(), getStartPoint(), point) > 0;
    }

    public boolean intersect(Line2D line) {
        Circle2D circle = new Circle2D(getRadius(), getCenterPoint());
        Point2D[] points = circle.intersection(line);
        if (points == null) {
            return false;
        } else {
            if (points.length == 1) {
                if (between(points[0])) {
                    return true;
                }
            } else {
                if (between(points[0])) {
                    return true;
                }
                if (between(points[1])) {
                    return true;
                }
            }
            return false;
        }

    }

    @Override
    public List<Line2D> chewSegmentation(double hmin) {
        getChewPoints().clear();
        ArrayList<Line2D> list = new ArrayList<>();

        Point2D center = this.getCenterPoint();
        double radius = this.getRadius();
        double angStart = this.getStartAngle();
        double angEnd = this.getEndAngle();
        if (angStart == angEnd) {
            return null;
        }

        Point2D pStart = this.getStartPoint();
        Point2D pEnd = this.getEndPoint();

        pStart.fixed = true;
        pEnd.fixed = true;

        pStart.setIndex(-1);
        pStart.setValue(null);
        pEnd.setIndex(-1);
        pEnd.setValue(null);

        Point2D lastPoint = pStart;
        Point2D newPoint = null;

        Line2D newLine = null;

        pStart.wasIntserted = false;
        pStart.clearLinkedTriangles();
        pStart.clearLinkedPoints();

        pEnd.wasIntserted = false;
        pEnd.clearLinkedTriangles();
        pEnd.clearLinkedPoints();

        angStart = Math.toDegrees(Math.atan2(pStart.getY() - center.getY(), pStart.getX() - center.getX()));
        angEnd = Math.toDegrees(Math.atan2(pEnd.getY() - center.getY(), pEnd.getX() - center.getX()));

        double factor = Math.toDegrees(hmin / radius);
        double angle = angStart + factor;
        if (angEnd < angStart) {
            angEnd += 360;
        }
        addChewPoint(lastPoint);
        while (Math.abs(angle - angEnd) > factor) {
            newPoint = new Point2D(
                    (float) (center.getX() + radius * Math.cos(Math.toRadians(angle))),
                    (float) (center.getY() + radius * Math.sin(Math.toRadians(angle)))
            );
            newPoint.fixed = true;

            addChewPoint(newPoint);

            newLine = new Line2D(lastPoint, newPoint);
            newLine.setAsFixedEdge();

            list.add(newLine);
            lastPoint = newPoint;
            angle += factor;
        }
        Line2D lastLine = new Line2D(lastPoint, pEnd);
        lastLine.setAsFixedEdge();
        addChewPoint(pEnd);
        list.add(lastLine);

        pStart.removeLinkedPoint(pEnd);
        pEnd.removeLinkedPoint(pStart);

        return list;
    }

    @Override
    public double getMaxCoordinate() {
        Point2D pStart = getStartPoint();
        Point2D pEnd = getEndPoint();
        double max0 = Math.max(pStart.getX(), pStart.getY());
        double max1 = Math.max(pEnd.getX(), pEnd.getY());
        return Math.max(max0, max1);
    }

    public List<Point2D> getPointList() {
        Point2D center = this.getCenterPoint();
        double radius = this.getRadius();
        double angStart = this.getStartAngle();
        double angEnd = this.getEndAngle();
        if (angStart == angEnd) {
            return null;
        }

        Point2D pStart = this.getStartPoint();
        Point2D pEnd = this.getEndPoint();

        angStart = Math.toDegrees(Math.atan2(pStart.getY() - center.getY(), pStart.getX() - center.getX()));
        angEnd = Math.toDegrees(Math.atan2(pEnd.getY() - center.getY(), pEnd.getX() - center.getX()));

        ArrayList<Point2D> pts = new ArrayList<>();
        pts.add(startPoint);

        double factor = 0.1;
        double angle = angStart + factor;
        if (angEnd < angStart) {
            angEnd += 360;
        }
        while (Math.abs(angle - angEnd) > factor) {
            Point2D newPoint = new Point2D((float) (center.getX() + radius * Math.cos(Math.toRadians(angle))), (float) (center.getY() + radius * Math.sin(Math.toRadians(angle))));
            pts.add(newPoint);
            angle += factor;
        }
        pts.add(endPoint);

        return pts;
    }

    @Override
    public Point2D[] getEndpoints() {
        Point2D[] pts = new Point2D[2];
        pts[0] = getStartPoint();
        pts[1] = getEndPoint();
        return pts;
    }

    @Override
    public double getLength() {
        // TODO Auto-generated method stub
        if (endAngle == 0) {
            return Math.abs(radius * (360 - startAngle) * Math.PI / 180);
        }
        if (startAngle == 360) {
            return Math.abs(radius * (endAngle) * Math.PI / 180);
        } else {
            return Math.abs(radius * (endAngle - startAngle) * Math.PI / 180);
        }
    }

}
