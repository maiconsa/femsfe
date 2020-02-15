package com.femsfe.Geometrias;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

/**
 * Represent a Line in two dimensional space,delimeter by two points.It is a subclass of <code>Geometry2D<code>.
 * @author Maicon Alc�ntara
 * @since 1.0
 * */
public class Line2D extends Border2D{
	/**
	 * The start point of this <code>Line2D</Cucode>.
	 * */
	private Point2D p0;
	/**
	 * The end point of this <code>Line2D</code>.
	 * */
	private Point2D p1;
	
	public List<Point2D> intersections = new ArrayList<>();

	/**
	 * Default constructor with  start and end point <code>null</code>.
	 * */
	public Line2D(){
		this.type = GeometryType.LINE;
		this.p0 = null;
		this.p1 = null;
	}
	/**
	 * Constructor and initializes the start and end points.<p>
	 * @param x0 x coordinate of the start point.
	 * @param y0 y coordinate of the start point.
	 * @param x1 x coordinate of the end point.
	 * @param y1 y coordinate of the end point.
	 * @since 1.0
	 * */
	public Line2D(double x0,double y0,double x1,double y1){
		this(new Point2D(x0, y0),new Point2D(x1, y1));
	}
	/**
	 * Constructor and initializes the start and end points.<p>
	 * @param p0 start point.
	 * @param p1 end point.
	 * @since 1.0
	 * */
	public Line2D(Point2D p0,Point2D p1) {
		this();
		this.p0 = p0;
		this.p1 = p1;
	}
	/**
	 * Set the start and end points.<p>
	 * @param x0 x coordinate of the start point.
	 * @param y0 y coordinate of the start point.
	 * @param x1 x coordinate of the end point.
	 * @param y1 y coordinate of the end point.
	 * @since 1.0
	 * */
	public void setLine(double x0,double y0,double x1,double y1 ){
		setP0(x0, y0);
		setP1(x1, y1);
	}
	/**
	 * Set the start and end points.<p>
	 * @param p0 the start point.
	 * @param p1 the start point;
	 * @since 1.0
	 * */
	public void setLine(Point2D p0,Point2D p1){
		setP0(p0);
		setP1(p1);	
	}
	/**
	 * Set the start point.<p>
	 * @param x  coordinate of start point.
	 * @param y  coordinate of start point;
	 * @since 1.0
	 * */
	public void setP0(double  x, double y ){
		if(getP0() != null){
			getP0().setCoordinates(x, y);
		}else{
			this.p0 = new Point2D(x, y);
		}
	}
	/**
	 * Set the start point.<p>
	 * @param p  point.
	 * @since 1.0
	 * */
	public void setP0(Point2D p){
		this.p0 = p;
	}
	/**
	 * Set the endpoint.<p>
	 * @param x  coordinate of end point.
	 * @param y  coordinate of end point;
	 * @since 1.0
	 * */
	public void setP1(double x, double y ){
		if(getP1() != null){
			getP1().setCoordinates(x, y);
		}else{
			this.p1 = new Point2D(x, y);
		}
	}
	/**
	 * Set the end point.<p>
	 * @param p  point.
	 * @since 1.0
	 * */
	public void setP1(Point2D p){
		this.p1 = p;
	}
	/**
	 * Return the start point of this <code>Line2D</code>.
	 * @return a point.
	 * */
	public Point2D getP0(){
		return p0;
	}
	/**
	 * Return the end point of this <code>Line2D</code>.
	 * @return a point.
	 * */
	public Point2D getP1(){
		return p1;
	}
	/**
	 * Return the x coordinate of start point of this <code>Line2D</code>.
	 * @return a <code>double</code> value.
	 * */
	public double getX0(){
		return p0.getX();
	}
	/**
	 * Return the y coordinate of start point of this <code>Line2D</code>.
	 * @return a <code>double</code> value.
	 * */
	public double getY0(){
		return p0.getY();
	}
	/**
	 * Return the x coordinate of end point of this <code>Line2D</code>.
	 * @return a <code>double</code> value.
	 * */
	public double getX1(){
		return p1.getX();
	}
	/**
	 * Return the y coordinate of end point of this <code>Line2D</code>.
	 * @return a <code>double</code> value.
	 * */
	public double getY1(){
		return p1.getY();
	}
	

	public boolean isFixedEdge(){
		if(p0.hasLinkedPoint(p1) && p1.hasLinkedPoint(p0)){
			return true;
		}else{
			return false;
		}	
	}
	
	public void setAsFixedEdge(){
		p0.addLinkedPoint(p1);
		p1.addLinkedPoint(p0);
	}
	
	public boolean isVertical(){
		return p0.getX() == p1.getX();
	}
	public boolean isHorizontal(){
		return p0.getY() == p1.getY();
	}
	public boolean hasVertex(Point2D v ){
		return p0.equals(v) || p1.equals(v);
	}

	
	public double distanceTo(Point2D point){
		return distanceTo(point.getX(), point.getY());
	}
	public double distanceTo(double  x,double y){
		/* Reference: http://geomalgorithms.com/a02-_lines.html */
		Point2D v = new Point2D(p1.getX() - p0.getX(),p1.getY() - p0.getY());
		Point2D w = new Point2D(x - p0.getX(),y- p0.getY());
		
		double c1 = (w.getX() * v.getX() + w.getY() * v.getY()) ;
		if(c1 <= 0){
			return p0.distanceTo(x,y);
		}
		double c2 = v.getX() * v.getX() + v.getY() * v.getY();
		if(c2 <= c1){
			return p1.distanceTo(x,y);
		}
		double b = c1/c2;
		Point2D pb = new Point2D((float)(p0.getX()+ b*v.getX()) ,(float)( p0.getY()+ b*v.getY()));
		return pb.distanceTo(x,y);
	}
	public boolean sharePoint(Line2D line2d){
		return this.hasVertex(line2d.getP0()) || this.hasVertex(line2d.getP1());
	}
	public boolean inLine(Point2D point2d){
		return inLine(point2d.getX(), point2d.getY());
	}
	
	public boolean inLine(double x,double y){
		if(Double.isNaN(x) || Double.isNaN(y)){ 
			return false;
		}
		if(getX0() == getX1()){
			double yMin = Math.min(getY0(), getY1());
			double yMax = Math.max(getY0(), getY1());
			
			return (y > yMin && y < yMax) && Triangle2D.area2(p0, p1, new Point2D(x, y)) == 0;
		}else if(getY0() == getY1()){
			double xMin = Math.min(getX0(), getX1());
			double xMax = Math.max(getX0(), getX1());
			
			return (x > xMin && x < xMax) && Triangle2D.area2(p0, p1, new Point2D(x, y)) == 0;
		}else{
			double xMax = Math.max(getX0(), getX1());
			double yMax = Math.max(getY0(), getY1());
			
			double xMin = Math.min(getX0(), getX1());
			double yMin = Math.min(getY0(), getY1());
			
			return (x > xMin && x < xMax) && (y> yMin && y< yMax) && Triangle2D.area2(p0, p1, new Point2D(x, y)) == 0;
		}
		
	
		
	}

	public  Point2D midPoint(){
		return new Point2D(( p1.getX() + p0.getX())/2, (p1.getY()+p0.getY())/2);
	}
	public double dx(){
		return p1.getX() - p0.getX();
	}
	public double dy(){
		return p1.getY() - p0.getY();
	}

	public boolean isEqual(Line2D line){
		return (line.getP0().equals(p0) || line.getP0().equals(p1)) && (line.getP1().equals(p0)  || line.getP1().equals(p1));
	}
	
	
	public void addIntersection(Point2D point){
			intersections.add(point);
		
	}
	public boolean containIntersection(Point2D point2d){
		return intersections.contains(point2d);
		
		
	}
	public List<Line2D> getLinesFromInterPoints(){
		List<Line2D> lines = new Vector<>();
		if(!intersections.isEmpty()){
	
			
			Collections.sort(intersections, new OrderByDistance(p0));
			Point2D last = p0;
			for (Point2D point2d : intersections) {
				lines.add(new Line2D(last, point2d));
				last = point2d;
			}
			lines.add(new Line2D(last, p1));
			clearIntersection();
		}else{
			lines.add(this);
		}
			return lines;
	
	}
	
	public void clearIntersection(){
		intersections.clear();
	}
	@Override
	public String toString() {

	return "[P0("+ p0.getX() +","+ p0.getY()+ "),P1("+ p1.getX() +","+ p1.getY()+ ")]";
	}
	@Override
	public Geometry2D copy() {
		return new Line2D(getP0(), getP1());

	}

	public int sizeIntersection(){
		return intersections.size();
	}
	public List<Point2D> getIntersections(){
		return intersections;
	}
	
	/*	ESTE METODOS ESTA FUNCIONANDO CORRETAMENTE**/
	public static boolean intersect(Point2D a,Point2D b,Point2D c,Point2D d){
		return	Triangle2D.area2(a,b,c)*Triangle2D.area2(a, b,d) < 0 && 
				Triangle2D.area2(c, d, a)*Triangle2D.area2(c, d,b ) < 0;
	}
	public static boolean intersect(Line2D a,Line2D b){
		return intersect(a.getP0(), a.getP1(), b.getP0(), b.getP1());
	}
	
	public boolean intersect(Point2D a,Point2D b){
		return	intersect(getP0(), getP1(), a, b);
	}
	public boolean intersect(Line2D line){
		return intersect(line.getP0(), line.getP1());
	}
	
	public static List<Line2D> intersection(Line2D ab,Line2D cd){
		List<Line2D> newLines = new Vector<>(4);
		//Reference: http://www.ahinson.com/algorithms_general/Sections/Geometry/ParametricLineIntersection.pdf
		if(ab.sharePoint(cd)){   return newLines;}
			double x21 = ab.dx();
			double y21 = ab.dy();
			
			double x43 = cd.dx();
			double y43 = cd.dy();
			
			double x31 = cd.getX0() - ab.getX0();
			double y31 = cd.getY0() - ab.getY0();
			
			double det = x43*y21 - x21*y43;
			
			double s = (x43*y31 - x31*y43)/det;
			double t = (x21*y31 - x31*y21)/det;
			if(s >= 0  && s <= 1 && t >= 0 && t <= 1){
				Point2D point =  new Point2D((float)(ab.getX0() + s*ab.dx()),(float)( ab.getY0() + s*ab.dy()));
				if(ab.inLine(point) && cd.hasVertex(point)){
					newLines.add(new Line2D(ab.getP0(), point));
					newLines.add(new Line2D(ab.getP1(), point));
					newLines.add(cd);
				}else if(cd.inLine(point) && ab.hasVertex(point)){
					newLines.add(new Line2D(cd.getP0(), point));
					newLines.add(new Line2D(cd.getP1(), point));
					newLines.add(ab);
				}else{
					newLines.add(new Line2D(ab.getP0(), point));
					newLines.add(new Line2D(ab.getP1(), point));
					newLines.add(new Line2D(cd.getP0(), point));
					newLines.add(new Line2D(cd.getP1(), point));
				}
				return newLines;
	
			}else{
				return newLines;
			}

	}
	
	
	
	public  Point2D intersection(Line2D cd){
		//Reference: http://www.ahinson.com/algorithms_general/Sections/Geometry/ParametricLineIntersection.pdf
		if(sharePoint(cd)){   
			return null;
			}
			double x21 = dx();
			double y21 = dy();
			
			double x43 = cd.dx();
			double y43 = cd.dy();
			
			double x31 = cd.getX0() - getX0();
			double y31 = cd.getY0() - getY0();
			
			double det = x43*y21 - x21*y43;
			
			double s = (x43*y31 - x31*y43)/det;
			double t = (x21*y31 - x31*y21)/det;
		
			if(s >= 0  && s <= 1 && t >= 0 && t <= 1){
				Point2D newPoint =  new Point2D((float) (getX0() + s*dx()),(float)( getY0() + s*dy()));
				if(s==0){
					newPoint = getP0();
				}
				if(s == 1){
					newPoint = getP1();
				}
				return newPoint;
	
			}else{
				return null;
			}

	}
	
	public boolean isNear(Point2D point2d) {
		return isNear(point2d, Geometry2D.EPSILON);
	}

	public boolean isNear(Point2D point2d, double epsilon) {
		double dist = this.distanceTo(point2d);
		return dist>=0 && dist <= epsilon;
	}

	public boolean isNear(double x, double y) {
		return isNear(x, y, Geometry2D.EPSILON);
	}

	public boolean isNear(double x, double y, double epsilon) {
		double dist = this.distanceTo(x, y);
		return dist>= 0 && dist <= epsilon;
	} 
	
	
	public void reset(){
		this.p0.reset();
		this.p1.reset();
		clearIntersection();
		
	}
	
	public void chewDivision(List<Point2D> list,Line2D ab,double hmin){
		double dist = ab.getLength();
		boolean chewCondition =  (dist >= hmin && dist <= Math.sqrt(3)*hmin);
			if(chewCondition){	
				return;
			}else{
				if(dist < hmin) return;
				Point2D c = ab.midPoint();
				Line2D ac = new Line2D(ab.getP0(), c);
				Line2D cb = new Line2D(c,ab.getP1());
				
				list.add(c);	
				chewDivision(list, ac, hmin);
				chewDivision(list, cb, hmin);
			}
	}
	public ArrayList<Point2D> divisionLineInPoint(int n){

				Point2D newPoint = null;
				ArrayList<Point2D> pts  = new ArrayList<>();
				pts.add(this.getP0());
				double t0 = (double)(1.0/n);
				for (int i = 1; i <= n-1; i++) {
					newPoint = new Point2D((float)(getX0() + i*t0*(getX1()- getX0())),(float)(getY0() + i*t0*(getY1()- getY0())));		
					pts.add(newPoint);
				}
				pts.add(this.getP1());
			return (ArrayList<Point2D>) pts;

}
	public List<Line2D> division(int n){
			List<Line2D> newLines= new ArrayList<>();
					Point2D lastPoint = this.getP0();
					Point2D newPoint = null;
					Line2D newLine = null;
					double t0 = (double)(1.0/n);
					for (int i = 1; i <= n-1; i++) {
						newPoint = new Point2D((float)(getX0() + i*t0*(getX1()- getX0())),(float)(getY0() + i*t0*(getY1()- getY0())));		
						newLine = new Line2D(lastPoint, newPoint);
						newLine.setAsFixedEdge();
						newLines.add(newLine);
						lastPoint = newPoint;
						newPoint = null;
					}
					Line2D lastLine = new Line2D(lastPoint, this.getP1());
							lastLine.setAsFixedEdge();
					newLines.add(lastLine);
				return newLines;
	
	}
	

	
	@Override
	public List<Line2D> chewSegmentation(double hmin) {
		getChewPoints().clear();
		this.getP0().setFixed(true);
		this.getP1().setFixed(true);
		this.getP0().wasIntserted = false;
		this.getP1().wasIntserted = false;
		this.getP0().clearLinkedTriangles();
		this.getP1().clearLinkedTriangles();
		
		List<Point2D> points= new ArrayList<>();
		List<Line2D> lines = new ArrayList<>();
		
		chewDivision(points,this, hmin);
		if(points.size() != 0 ){
			Collections.sort(points, new OrderByDistance(this.getP0()));
			Point2D last = this.getP0();
			Line2D newLine;
			addChewPoint(last);
			for (Point2D point2d : points) {
				point2d.setFixed(true);
				point2d.wasIntserted = false;
				addChewPoint(point2d);
				newLine = new Line2D(last, point2d);
				newLine.setAsFixedEdge();
				last = point2d;
				lines.add(newLine);
			}
			addChewPoint(this.getP1());
			newLine = new Line2D(last, this.getP1());	
			newLine.setAsFixedEdge();
			
			this.getP0().removeLinkedPoint(this.getP1());
			this.getP1().removeLinkedPoint(this.getP0());
			lines.add(newLine);
		}else{
			lines.add(this);
			this.setAsFixedEdge();
		}
		return lines;
			
		
	}
	@Override
	public double getMaxCoordinate() {
		return Math.max(Math.max(getX0(), getY0()), Math.max(getX1(), getY1())) ;
	}
	@Override
	public Point2D[] getEndpoints() {
		Point2D[] endpoints = new Point2D[2];
		endpoints[0] = getP0();
		endpoints[1] = getP1();
		return endpoints;
	}
	
	@Override
	public double getLength() {
		 return   Math.sqrt(dx()*dx() + dy()*dy());
	}


	

	
	

	


}