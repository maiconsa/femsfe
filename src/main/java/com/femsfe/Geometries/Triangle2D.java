package com.femsfe.Geometries;

import java.util.List;

import com.femsfe.enums.ProblemType;
import com.femsfe.BoundaryCondition.PermanentField;
import com.femsfe.BoundaryCondition.SpaceDensityFunction;
import com.femsfe.ComplexNumber.ComplexNumber;
import com.femsfe.PML.PML;



public class Triangle2D  extends Geometry2D {
	/*	GEOMETRIC PARMETERS	*/
	public Point2D p0,p1,p2;
	private Line2D edge0,edge1,edge2;
	public boolean marked = false;
	private Material material;
	private PML pml;
	/*	FEM PARAMETERS */
	private ComplexNumber alphax = new ComplexNumber(-1, 0);
	private ComplexNumber alphay = new ComplexNumber(-1, 0);
	private ComplexNumber beta = new ComplexNumber(0, 0);
	public SpaceDensityFunction spaceDensityFunction;
	private PermanentField permField = null;
	
	public void setPermanentField(PermanentField permField){
		this.permField = permField;
	}
	public PermanentField getPermanentField( ){
		return this.permField; 
	}
	public boolean isPermanentFieldRegion(){
		return this.permField != null;
	}
	
	public ComplexNumber getAlphax() {
		return this.alphax;
	}
	
	public void setAlphax(ComplexNumber value) {
		this.alphax = value;
	}

	public ComplexNumber getAlphay() {
		return this.alphay;
	}

	public void setAlphay(ComplexNumber value) {
		this.alphay = value;
	}

	public ComplexNumber getBeta() {
		return beta;
	}

	public void setBeta(ComplexNumber  value) {
		this.beta = value;
	}

	public Triangle2D(Point2D p0,Point2D p1,Point2D p2) {
		this.p0 = p0;
		this.p1 = p1;
		this.p2 = p2;
		edge0 = new Line2D(p0, p1);
		edge1 = new Line2D(p1, p2);
		edge2 = new Line2D(p2, p0);
		spaceDensityFunction = new SpaceDensityFunction("0");
		
		
	}
	public void setMaterial(Material material){
		this.material = material;
	}
	public Material getMaterial(){
		return this.material;
	}
	public void setPML(PML pml){
		this.pml = pml;
	}
	public PML getPML(){
		return this.pml;
	}
	public Line2D getEdge0(){
		return  edge0;
	}
	public Line2D getEdge1(){
		return  edge1;
	}
	public Line2D getEdge2(){
		return  edge2;
	}
	public Point2D centroid(){
		return new Point2D((p0.getX()+p1.getX()+p2.getX())/3,(p0.getY()+p1.getY()+p2.getY())/3);
	} 
	public static double area2(Point2D a,Point2D b,Point2D c){
		 return (b.getX() - a.getX())*(c.getY() - b.getY()) - 
				(c.getX() - b.getX())*(b.getY() - a.getY());
	}
	public double area(){
		double  area = Triangle2D.area2(p0, p1, p2);
		if(area < 0){
			area*=-1;
		}
		return area/2;
	}
	public Point2D circumcenter(){
		/*	REFERENCE: http://mathworld.wolfram.com/Circumcircle.html*/
		double r1 = p0.getX()*p0.getX() + p0.getY()*p0.getY();
		double r2 = p1.getX()*p1.getX() + p1.getY()*p1.getY();
		double r3 = p2.getX()*p2.getX() + p2.getY()*p2.getY();
		
		double x1 = p0.getX(), x2 = p1.getX(), x3 = p2.getX();
		double y1 = p0.getY(), y2 = p1.getY(), y3 = p2.getY();
		
		double a = x1*y2 - x2*y1 - x1*y3 + x3*y1 + x2*y3 - x3*y2;
		double by = r1*x2 - r2*x1 - r1*x3 + r3*x1 + r2*x3 - r3*x2;
		double bx = (-1)*(r1*y2 - r2*y1 - r1*y3 + r3*y1 + r2*y3 - r3*y2);
		
		double x = - bx/(2*a);
		double y = -by/(2*a);
		if(Double.isInfinite(x) || Double.isInfinite(y) || Double.isNaN(x) || Double.isNaN(y)){
			return null;
		}else{
			return new Point2D((float)x ,(float)y );
		}
	
	}
	public double circumradius(){
		/*	REFERENCE: http://mathworld.wolfram.com/Circumradius.html*/
		double a = edge0.getLength();
		double b = edge1.getLength();
		double c = edge2.getLength();
		
		return (a*b*c)/Math.sqrt((a+b+c)*(b+c-a)*(c+a-b)*(a+b-c));
		
	}
	
	public boolean hasVertex(Point2D point2d){
		return (p0.equals(point2d) || p1.equals(point2d) || p2.equals(point2d));
	
	}
	public boolean hasEdge(Line2D edge){
		return (p0.equals(edge.getP0()) || p1.equals(edge.getP0()) || p2.equals(edge.getP0())) &&
				(p0.equals(edge.getP1()) || p1.equals(edge.getP1()) || p2.equals(edge.getP1()));
	}
	public Line2D getOpposedEdge(Point2D a){
		if(hasVertex(a)){
			if(a.equals(p0)){
				return getEdge1();
			}
			if(a.equals(p1)){
				return getEdge2();
			}
			if(a.equals(p2)){
				return getEdge0();
			}
		}else{
			return null;
		}
		return null;
		
	}
		
	public Point2D getOpposedVertex(Line2D  edge){
		if(edge == null){ return null;}
		if(hasEdge(edge)){
			if(getEdge0().isEqual(edge)){
				return p2;
			}
			if(getEdge1().isEqual(edge)){
				return p0;
			}
			if(getEdge2().isEqual(edge)){
				return p1;
			}
		}
		return null; 
	}
	public boolean isOnEdge0(Point2D p){
		return edge0.inLine(p);
	}
	public boolean isOnEdge1(Point2D p){
		return edge1.inLine(p);
	}
	public boolean isOnEdge2(Point2D p){
		return edge2.inLine(p);
	}
	public boolean intersect(Line2D edge){
		return edge.intersect(edge0)||edge.intersect(edge1) || edge.intersect(edge2);
	}
	
	public void clockwiseOrientation(){
		if(Triangle2D.area2(p0, p1, p2) < 0){
			Point2D aux  = p1;
			List<Triangle2D> linkedTriangles1 = p1.getLinkedTriangles(); 
			List<Triangle2D> linkedTriangles2 = p2.getLinkedTriangles(); 
			this.p1 = p2;
			this.p1.setLinkedTriangles(linkedTriangles2);
			this.p2 =  aux;
			this.p2.setLinkedTriangles(linkedTriangles1);
			
			this.getEdge0().setLine(this.p0, this.p1);
			this.getEdge1().setLine(this.p1, this.p2);
			this.getEdge2().setLine(this.p2, this.p0);
		}
	}
	
	
	public static boolean isInsideTriangle(Point2D p1,Point2D p2,Point2D p3,Point2D p){
		double area0 = Triangle2D.area2(p1, p2, p3);
		double area1 = Triangle2D.area2(p, p2, p3);
		double area2 = Triangle2D.area2(p1, p, p3);
		double area3 = Triangle2D.area2(p1, p2, p);
		return (area1/area0 > 0 && area2/area0 >0  && area3/area0 > 0);
		
	}
	public static boolean isInsideTriangle(Triangle2D triangle2d,Point2D point2d){
		return isInsideTriangle(triangle2d.p0, triangle2d.p1, triangle2d.p2, point2d);
	}
	public boolean isInsideTriangle(Point2D point2d){
		return Triangle2D.isInsideTriangle(p0, p1, p2, point2d);
	}
	public static boolean isOutSideTriangle(Point2D p1,Point2D p2,Point2D p3,Point2D p){
		double area0 = Triangle2D.area2(p1, p2, p3);
		double area1 = Triangle2D.area2(p, p2, p3);
		double area2 = Triangle2D.area2(p1, p, p3);
		double area3 = Triangle2D.area2(p1, p2, p);
		
		double lambda1 = area1/area0;
		double lambda2 = area2/area0;
		double lambda3 = area3/area0;
		
		return	(lambda1  < 0 && lambda2 > 0 && lambda3 < 0) ||
				(lambda1 < 0 && lambda2 > 0 && lambda3 > 0) ||
				(lambda1 < 0 && lambda2  < 0 && lambda3 > 0) ||
				(lambda1 > 0 && lambda2 > 0 && lambda3 < 0) ||
				(lambda1 > 0 && lambda2 < 0 && lambda3 > 0) ||
				(lambda1 > 0 && lambda2  < 0 && lambda3 < 0);

	}
	public static boolean isOutSideTriangle(Triangle2D triangle,Point2D p){
		return Triangle2D.isOutSideTriangle(triangle.p0, triangle.p1, triangle.p2, p);
	}
	public boolean isOutSideTriangle(Point2D point){
		return Triangle2D.isOutSideTriangle(this, point);
	}
	public Line2D getEdgeWithPoint(Point2D point2d){
		Line2D edge = null;
		if(getEdge0().inLine(point2d)){
			edge  =  getEdge0();
		}
		if(getEdge1().inLine( point2d)){
			edge  =  getEdge1();
		}
		if(getEdge2().inLine( point2d)){
			edge  =  getEdge2();
		}
		return edge;
	}
	public static boolean isOnTriangle(Triangle2D triangle,Point2D p){
		return triangle.isOnEdge0(p) || triangle.isOnEdge1(p)  || triangle.isOnEdge2(p) ;
	}
	public boolean isOnTriangle(Point2D point){
		return Triangle2D.isOnTriangle(this, point);
	}
	
	public boolean isNeighborTriangle(Triangle2D triangle){
		return hasEdge(triangle.getEdge0()) ||  hasEdge(triangle.getEdge1()) || hasEdge(triangle.getEdge2());
	}
	
	

	@Override
	public String toString() {
		String str  = "[P0("	+	p0.getX()	+	","	+	p0.getY()	+	")";
		str+= ",P1("	+	p1.getX()	+	","	+	p1.getY()	+	")";
		str+=",P2("	+	p2.getX()	+	","	+	p2.getY()	+	")]";
		return str;
	}

	@Override
	public Geometry2D copy() {
		return new Triangle2D(p0, p1, p2);
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}


	double perimeter() {
		// TODO Auto-generated method stub
		return 0;
	}


	boolean contains(double x, double y) {
		// TODO Auto-generated method stub
		return false;
	}

	
	boolean contains(Point2D point2d) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isNear(double x, double y) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isNear(double x, double y, double epsilon) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isNear(Point2D point2d) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isNear(Point2D point2d, double epsilon) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public double getMaxCoordinate() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	
	
	public float qualityFactor(){
		double a = edge0.getLength();
		double b = edge1.getLength();
		double c = edge2.getLength();
		double s = (a+b+c)/2;
		return (float) (8*(s -a )*(s-b)*(s-c)/(a*b*c));
	}
	
	public double[] getField(ProblemType problemType){
		double f1 = p0.getValue().getReal();
		double f2 = p1.getValue().getReal();
		double f3 = p2.getValue().getReal();
		
		double x1 = p0.getX(),x2 = p1.getX(),x3 = p2.getX();
		double y1 = p0.getY(),y2 = p1.getY(),y3 = p2.getY();
		
		@SuppressWarnings("unused")
		double a1 = x2*y3 - y2*x3;
		@SuppressWarnings("unused")
		double a2 = x3*y1 - y3*x1;
		@SuppressWarnings("unused")
		double a3 = x1*y2 - y1*x2;
		
		double b1 = y2 - y3;
		double b2 = y3 - y1;
		double b3 = y1 - y2;
		
		double c1 = x3 - x2;
		double c2 = x1 - x3;
		double c3  = x2 - x1;
		
		double delta = 0.5*(b1*c2 - b2*c1);
		double x = 0,y = 0;
		switch (problemType) {
		case ELETROSTATIC:
			x = -(b1*f1 + b2*f2 + b3*f3)/(2*delta);
			y = -(c1*f1 + c2*f2 + c3*f3)/(2*delta);
			break;
		case MAGNETOSTATIC_VECTOR_POTENTIAL:
			x = (c1*f1 + c2*f2 + c3*f3)/(2*delta);
			y = -(b1*f1 + b2*f2 + b3*f3)/(2*delta);
			break;
		case MAGNETOSTATIC_SCALAR_POTENTIAL:
			x = (b1*f1 + b2*f2 + b3*f3)/(2*delta);
			y = (c1*f1 + c2*f2 + c3*f3)/(2*delta);
			break;
		default:
			break;
		}
		
	
		
		double v[] = { x,y};
		return v;
	}
	
	public Line2D getArrow(ProblemType type){
		double[] E = getField(type);
		double modE = Math.sqrt(E[0]*E[0] + E[1]*E[1] );
		double dl = Math.sqrt(this.area())/1.5;
		
		Point2D centroid = this.centroid();
		
		double x  = centroid.getX() + dl*(E[0]/modE);
		double y  = centroid.getY() + dl*(E[1]/modE);
		
		return new Line2D(centroid, new Point2D((float)x, (float)y));
	}
	
	public Line2D getIsoLine(double isoValue){
		double f1 = p0.getValue().getReal();
		double f2 = p1.getValue().getReal();
		double f3 = p2.getValue().getReal();
		
		
		
		double x1 = p0.getX(),x2 = p1.getX(),x3 = p2.getX();
		double y1 = p0.getY(),y2 = p1.getY(),y3 = p2.getY();
		
		double a1 = x2*y3 - y2*x3;
		double a2 = x3*y1 - y3*x1;
		double a3 = x1*y2 - y1*x2;
		
		double b1 = y2 - y3;
		double b2 = y3 - y1;
		double b3 = y1 - y2;
		
		double c1 = x3 - x2;
		double c2 = x1 - x3;
		double c3  = x2 - x1;
		
		double delta = 0.5*(b1*c2 - b2*c1);
		
		@SuppressWarnings("unused")
		double A = (a1*f1 + a2*f2 + a3*f3)/(2*delta);
		@SuppressWarnings("unused")
		double B = (b1*f1 + b2*f2 + b3*f3)/(2*delta);
		@SuppressWarnings("unused")
		double C = (c1*f1 + c2*f2 + c3*f3)/(2*delta);
		
		double t1 = (isoValue - f1) /(f2 - f1);
		double t2 = (isoValue - f2) /(f3 - f2);
		double t3 = (isoValue - f3) /(f1 - f3);
		
		Point2D p1 = null,p2= null;
		if(t1 >= 0 && t1 <= 1){
			p1 = new Point2D((float)(x1 + t1*(x2-x1)), (float)(y1 + t1*(y2-y1)));
		}
		if(t2 >=0 && t2 <= 1){
			if(p1 == null){
				p1 = new Point2D((float)(x2 + t2*(x3-x2)),(float)( y2 + t2*(y3-y2)));
			}else{
				p2 = new Point2D((float)(x2 + t2*(x3-x2)), (float)(y2 + t2*(y3-y2)));
			}
		}
		if(t3 >=0 && t3 <=1){
			if(p2 == null){
				p2 = new Point2D((float)(x3 + t3*(x1-x3)), (float)(y3 + t3*(y1-y3)));
			}
		}
		
		if(p2 != null && p1 != null){
			return new Line2D(p1, p2);
		}else{
			return null;
		}
		
		
	}
	
	

	public double getValueInsideElement(double x, double y){
			
			double f1 = p0.getValue().getReal();
			double f2 = p1.getValue().getReal();
			double f3 = p2.getValue().getReal();
	
			double x1 = p0.getX(),x2 = p1.getX(),x3 = p2.getX();
			double y1 = p0.getY(),y2 = p1.getY(),y3 = p2.getY();
		
			double a1 = x2*y3 - y2*x3;
			double a2 = x3*y1 - y3*x1;
			double a3 = x1*y2 - y1*x2;
			
			double b1 = y2 - y3;
			double b2 = y3 - y1;
			double b3 = y1 - y2;
			
			double c1 = x3 - x2;
			double c2 = x1 - x3;
			double c3  = x2 - x1;
			
			double delta = 0.5*(b1*c2 - b2*c1);
			
			double N1 = (a1 + b1*x + c1*y)/(2*delta);
			double N2 = (a2 + b2*x + c2*y)/(2*delta);
			double N3 = (a3 + b3*x + c3*y)/(2*delta);
			
			double value = (N1*f1 + N2*f2 + N3*f3);
			System.out.println(value);
			if(isInsideTriangle(new Point2D(x, y))){
				return  value ;
			}else{
				if(isOnTriangle(new Point2D(x, y))){
					return value;
				}else{
					if(p0.equals(new Point2D(x, y))){
						return f1;
					}else if(p1.equals(new Point2D(x, y))){
						return f2;
					}else if(p2.equals(new Point2D(x, y))){
						return f3;
					}else{
						return Double.NaN;
					}
				}
			}
			
		
	}
	

	
}
