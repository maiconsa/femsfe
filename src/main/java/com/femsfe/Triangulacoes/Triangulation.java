package com.femsfe.Triangulacoes;

import java.util.ArrayList;
import java.util.List;

import com.femsfe.Geometrias.Face2D;
import com.femsfe.Geometrias.Line2D;
import com.femsfe.Geometrias.Point2D;
import com.femsfe.Geometrias.Triangle2D;
import com.femsfe.Project;


/**
 * A abstract class of a Triangulation used for domain discretization of a model 2d .
 * @author Maicon Alc�ntara
 * @since 1.0
 * */
public abstract class Triangulation {
	
	
	/**
	 * Faces 2D for discretization
	 * */
	protected List<Face2D> faces;
	
	/**
	 * Triangle list from discretization
	 */
	protected List<Triangle2D> triangles;
	
	private boolean status;
	
	protected long initTime = -1;
	
	protected long endTime = -1;
	
	public boolean isStart(){
		return status;
	}
	public boolean isEnd(){
		return !status;
	}
	public void stop(){
		status  = false;
		endTime = System.nanoTime();
	}
	protected void start( ){
		status = true;
		initTime = System.nanoTime();
	}
	
	/**
	 * Constructor  .
	 * @since 1.0
	 * */
	public Triangulation() {
		triangles = new ArrayList<>();
	}
	public double getDuration(){
		return  ((endTime - initTime)/1.0E9) ;
	}
	/**
	 * Set the model
	 *  @param model2d geometric model.
	 * 
	 * */
	public void setFaces(List<Face2D> face2ds){
		this.faces = face2ds;
	}
	/**
	 * Return the geometric model
	 * @return 
	 * */
	public List<Face2D> getFaces(){
		return this.faces;
	}
	
	/**
	 * Return the triangle list
	 * @return element list
	 * @since 1.0
	 * */
	public List<Triangle2D> getTriangles() {
		return triangles;
	}
	
	/**
	 * Return the conectivity list of a  model discretization.
	 * @return conectivity list
	 * @since 1.0
	 * */
	public ConectivityList getConectivityList(){
		ConectivityList list = new ConectivityList();
		
		for (Triangle2D element: getTriangles()) {		
			element.clockwiseOrientation();
			list.addNode(element.p0);	
		    list.addNode(element.p1);	
			list.addNode(element.p2);
			list.addElement(element);
			for (Face2D face : getFaces()) {
				if(face.getTypeFace() == Face2D.NORMAL){
						Point2D centroid = element.centroid();
						if(face.getPolygon().contains(centroid)){
							element.setMaterial(face.getMaterial());
							element.setPML(face.getPML());
							element.spaceDensityFunction = face.spaceDensityFunction;
							if(face.isPermanentFieldRegion()){
								element.setPermanentField(face.getPermanentField());
							}
						}
				}
				
				
			}
			if(element.getMaterial() == null){
				for (Face2D face2d : faces) {
					if(face2d.getTypeFace() == Face2D.EXTERNAL){
						element.setMaterial(face2d.getMaterial());
						element.setPML(face2d.getPML());
						element.spaceDensityFunction= face2d.spaceDensityFunction;
					}
				}
				
			}
			
			
		}	
	
		return list;
	}
	/**
	 * Clear the triangle list.
	 * @since 1.0
	 * */
	public void clear(){
		triangles.clear();
	}
	/**
	 * Remove a specified element of the triangle list 
	 * @param triangle is an element.
	 * @since 1.0
	 * */
	public void removeTriangle(Triangle2D triangle){
		triangles.remove(triangle);
	}
	/**
	 * Add a specified element to conectivity list 
	 * @param triangle is an element to be inserted.
	 * @since 1.0
	 * */
	 public void addTriangle(Triangle2D triangle){
		 triangle.p0.setIndex(-1);
		 triangle.p1.setIndex(-1);
		 triangle.p2.setIndex(-1);
		
		 triangles.add(triangle);
	}
	 /**
	  * Add a point to the current discretization.
	  * @param ponto2d is an point to be inserted.
	  * @since 1.0
	  * */
	protected void  addPoint(Point2D ponto2d,Triangle2D triangle2d){
		/*	CASO O PONTO SEJA NULO OU JA TENHA SIDO INSERIDO O M�TODO � ENCERRADO */
		if((ponto2d == null) ||(ponto2d.wasIntserted)) {
			return;
		}
		Triangle2D trianguloEncontrado;
		if(triangle2d == null){
			trianguloEncontrado = findTriangleWithPointInside(ponto2d);
		}else{
			trianguloEncontrado = triangle2d;
		}
			if(trianguloEncontrado == null) return;
		
			/*	CASO O PONTO ESTEJA DENTRO DO TRI�NGULO	*/
			if(trianguloEncontrado.isInsideTriangle( ponto2d)){
				//System.out.println("Triangulo com ponto interno encontrado");
				/*	REMOVE O TRIANGULO ENCONTRADO	*/
					removeTriangle(trianguloEncontrado);
				
				/*	A PARTIR DO TRIANGULO ENCONTRADO E O PONTO CRIA-SE TR�S NOVOS TRIANGULOS	*/
					Triangle2D t1 = new Triangle2D(trianguloEncontrado.p0,trianguloEncontrado.p1 , ponto2d);
					Triangle2D t2 = new Triangle2D(trianguloEncontrado.p1,trianguloEncontrado.p2 , ponto2d);
					Triangle2D t3 = new Triangle2D(trianguloEncontrado.p2,trianguloEncontrado.p0 , ponto2d);
				
				/*	ADICIONA OS ELEMENTOS LIGADOS AOS PONTOS	*/
					ponto2d.addLinkedTriangle(t1);
					ponto2d.addLinkedTriangle(t2);
					ponto2d.addLinkedTriangle(t3);
				
					trianguloEncontrado.p0.addLinkedTriangle(t1);
					trianguloEncontrado.p0.addLinkedTriangle(t3);
				
					trianguloEncontrado.p1.addLinkedTriangle(t1);
					trianguloEncontrado.p1.addLinkedTriangle(t2);
				
					trianguloEncontrado.p2.addLinkedTriangle(t2);
					trianguloEncontrado.p2.addLinkedTriangle(t3);
				
				/*	REMOVE O TRIANGULO ENCONTRADO DA LISTA */
					trianguloEncontrado.p0.removeLinkedTriangle(trianguloEncontrado);
					trianguloEncontrado.p1.removeLinkedTriangle(trianguloEncontrado);
					trianguloEncontrado.p2.removeLinkedTriangle(trianguloEncontrado);
				
				/*	ADICIONA OS NOVOS TRI�NGULO NA LISTA*/
					addTriangle(t1);
					addTriangle(t2);
					addTriangle(t3);
			
				/*	REGULARIZA OS NOVOS TRI�NGULOS	*/
					regularizeEdge(t1, t1.getOpposedEdge(ponto2d), ponto2d);
					regularizeEdge(t2, t2.getOpposedEdge(ponto2d), ponto2d);
					regularizeEdge(t3, t3.getOpposedEdge(ponto2d), ponto2d);
					
				/*	MARCA O PONTO COMO INSERIDO	*/
					ponto2d.wasIntserted = true;
					
			/*	CASO O PONTO SOBRE ALGUMA ARESTA DO TRI�NGULO	*/
			}else if(trianguloEncontrado.isOnTriangle(ponto2d)){
				System.out.println("Triangulo com  ponto sobre ele encontrado");
				
				/*	PEGA A ARESTA QUE CONT�M O PONTO	*/
				
				Line2D edge = trianguloEncontrado.getEdgeWithPoint(ponto2d);
				/*	VERIFICA SE A ARESTA E N�O NULA E N�O � FIXA	*/
				if(edge != null && !edge.isFixedEdge()){
					
					Point2D vertexOpo1 = trianguloEncontrado.getOpposedVertex(edge);
					Triangle2D triWithEdgeIntersection = findLinkedTriangle(edge, trianguloEncontrado);
					if(triWithEdgeIntersection != null){
						Point2D vertexOpo2 = triWithEdgeIntersection.getOpposedVertex(edge);
						
						Triangle2D t1 = new Triangle2D(ponto2d, edge.getP0(), vertexOpo1);
						Triangle2D t2 = new Triangle2D(ponto2d, edge.getP1(), vertexOpo1);
						Triangle2D t3 = new Triangle2D(ponto2d, edge.getP0(), vertexOpo2);
						Triangle2D t4 = new Triangle2D(ponto2d, edge.getP1(), vertexOpo2);
						
						addTriangle(t1);
						addTriangle(t2);
						addTriangle(t3);
						addTriangle(t4);
						
						removeTriangle(trianguloEncontrado);
						removeTriangle(triWithEdgeIntersection);
						
						//	ADICIONA TRIANGULOS LIGADOS
						ponto2d.addLinkedTriangle(t1);
						ponto2d.addLinkedTriangle(t2);
						ponto2d.addLinkedTriangle(t3);
						ponto2d.addLinkedTriangle(t4);
						
						edge.getP0().addLinkedTriangle(t1);
						edge.getP0().addLinkedTriangle(t3);
						
						edge.getP1().addLinkedTriangle(t2);
						edge.getP1().addLinkedTriangle(t4);
						
						vertexOpo1.addLinkedTriangle(t1);
						vertexOpo1.addLinkedTriangle(t2);
						
						vertexOpo2.addLinkedTriangle(t3);
						vertexOpo2.addLinkedTriangle(t4);
						
						//	REMOVE TRIANGULOS LIGADOS	
						vertexOpo1.removeLinkedTriangle(trianguloEncontrado);
						vertexOpo2.removeLinkedTriangle(triWithEdgeIntersection);
						
						edge.getP0().removeLinkedTriangle(trianguloEncontrado);
						edge.getP1().removeLinkedTriangle(trianguloEncontrado);
						
						edge.getP0().removeLinkedTriangle(triWithEdgeIntersection);
						edge.getP1().removeLinkedTriangle(triWithEdgeIntersection);
						
						regularizeEdge(t1, t1.getOpposedEdge(ponto2d), ponto2d);
						regularizeEdge(t2, t2.getOpposedEdge(ponto2d), ponto2d);
						regularizeEdge(t3, t3.getOpposedEdge(ponto2d), ponto2d);
						regularizeEdge(t4, t4.getOpposedEdge(ponto2d), ponto2d);
						
						ponto2d.wasIntserted = true;
					}

				}
				
			}
		}

	 /**
	  * Return the orientation of three points.
	  * @param ponto2d is an point to be inserted.
	  * @param u first point
	  * @param v second point
	  * @param w thrid point
	  * @return Value > 0 , then is clockwise.
	  * Value < 0, then is anticlockwise.
	  * Value = 0 , then is Collinear. 
	  * @since 1.0 
	  * */
	protected float orient(Point2D u,Point2D v,Point2D w){
			float a11 = (float) (u.getX() - w.getX());
			float a12 = (float) (u.getY() - w.getY());
			
			float a21 = (float) (v.getX() - w.getX());
			float a22 = (float) (v.getY() - w.getY());
			return a11*a22 - a12*a21;
		}

	/**
	 * Verify if the edge sastified the delaunay condition, if not, then the sweep are made 
	 * 
	 * */
	protected void regularizeEdge(Triangle2D triangle,Line2D edge,Point2D point){
		/*	CASO A ARESTA FOR FIXA O METODO � ENCERRADO*/
		if(edge.isFixedEdge()) {
			return;
		}
		/*	ENCONTRA O TRIANGULO VISINHO AO TRIANGULO TRIADO A PARTIR DA ARESTA PASSADA */
		Triangle2D trianguloVisinho = findLinkedTriangle(edge, triangle);
		if(trianguloVisinho ==null){ 
			return;
		} 	
		/* � APLICADO A CONDICAO DE DELAUNAY AO TRIANGULO VIZINHO E O PONTO , CASO O PONTO ESTEJA CONTIDO NO CIRCUCIRCULO ENTAO DEVE-SE FAZER FLIP*/
		if(InCircle(trianguloVisinho, point)){
					  /* OS TRIANGULOS IRREGULARES S�O REMOVIDOS*/
					  	 removeTriangle(trianguloVisinho);
					  	 removeTriangle(triangle);
					  	
					 /*	PEGA O VERTICE DO TRIANGULO VIZINHO OPOSTO A ARESTA */
					     Point2D pontoOposto = trianguloVisinho.getOpposedVertex(edge);
					     
					 /*	AQUI � FEITO O FLIP E CRIADO DOIS NOVOS TRIANGULOS*/
					    Triangle2D t1 = new Triangle2D(pontoOposto, edge.getP0(), point);
					    Triangle2D t2 = new Triangle2D(pontoOposto, edge.getP1(), point);

					  /* OS NOVOS TRIANGULOS S�O ADICIONADOS NO VETOR DE TRIANGULOS*/
					    addTriangle(t1);
					    addTriangle(t2);
					  
					  /*	OS TRIANGULOS LIGADO S�O REMOVIDOS*/
					    edge.getP0().removeLinkedTriangle(triangle);
					    edge.getP0().removeLinkedTriangle(trianguloVisinho);
					    
					    edge.getP1().removeLinkedTriangle(triangle);
					    edge.getP1().removeLinkedTriangle(trianguloVisinho);
					    
					    point.removeLinkedTriangle(triangle);
					    pontoOposto.removeLinkedTriangle(trianguloVisinho);
					    
					  /*	OS NOVOS TRIANGULOS S�O LIGADOS	*/
					  edge.getP0().addLinkedTriangle(t1);
					  edge.getP1().addLinkedTriangle(t2);
					  
					  point.addLinkedTriangle(t1);
					  point.addLinkedTriangle(t2);
					  
					  pontoOposto.addLinkedTriangle(t1);
					  pontoOposto.addLinkedTriangle(t2);

					  /*	� REALIZADA A LEGALIZA��O DOS NOVOS TRIANGULOS	*/
					 regularizeEdge(t1, t1.getOpposedEdge(point),point);
					 regularizeEdge(t2, t2.getOpposedEdge(point),point);
					
		}
		
	}
	/**
	 * Return if the 4 points obey the  delaunay criterium, to the maximize the minimium angle.
	 * @param p0 point 1
	 * @param p1 point 2
	 * @param p2 point 3
	 * @param p test point
	 * @return true if the point p is inside the circum-circle formed by the points p0,p1,p2. Otherwise, is false.
	 * @since 1.0
	 * */
	protected  boolean InCircle(Point2D p0,Point2D p1 , Point2D p2,Point2D point) {
			double a11 = p0.getX() - point.getX();
			double a21 = p1.getX() - point.getX();
			double a31 = p2.getX() - point.getX();
			
			double a12 = p0.getY() - point.getY();
			double a22 = p1.getY() - point.getY();
			double a32 = p2.getY() - point.getY();
			
			double a13 = a11 * a11 + a12 * a12;
			double a23 = a21 * a21 + a22 * a22;
			double a33 = a31 * a31 + a32 * a32;
			
			double det = a11 * a22 * a33 + a12 * a23 * a31 + a13 * a21 * a32 -
						 a13 * a22 * a31 - a12 * a21 * a33 - a11 * a23 * a32;
		
			if(orient(p0, p1, p2) > 0 ){
				return det > 0;
			}else {
				return det < 0;
			}

	}
	/**
	 * Return if a triangle and a test point  obey the  delaunay criterium.
	 * @param triangle triangle
	 * @param p test point
	 * @return true if the point p is inside the circum-circle formed by the triangle. Otherwise, is false.
	 * @since 1.0
	 * */
	protected boolean InCircle(Triangle2D triangle,Point2D point){
	    	return InCircle(triangle.p0, triangle.p1, triangle.p2, point);
	}
	
	private void removeSuperPoint(Point2D superPoint){
		List<Triangle2D> list = superPoint.getLinkedTriangles();
		while (list.iterator().hasNext()) {
			Triangle2D triangle2d = list.iterator().next();
			triangle2d.p0.removeLinkedTriangle(triangle2d);
			triangle2d.p1.removeLinkedTriangle(triangle2d);
			triangle2d.p2.removeLinkedTriangle(triangle2d);
			triangles.remove(triangle2d);
		}
		
	}
	protected void removeTrianglesOutsideDomain(){
		List<Triangle2D> trianglesToRemove = new ArrayList<>();
		for (Triangle2D triangle2d : triangles) {
			for (Face2D face2d : faces) {
				switch (face2d.getTypeFace()) {
					case Face2D.EXTERNAL:
						if(!face2d.getPolygon().contains(triangle2d.centroid())){
							triangle2d.p0.removeLinkedTriangle(triangle2d);
							triangle2d.p1.removeLinkedTriangle(triangle2d);
							triangle2d.p2.removeLinkedTriangle(triangle2d);
							trianglesToRemove.add(triangle2d);
					    }
						break;
					case Face2D.HOLE:
						if(face2d.getPolygon().contains(triangle2d.centroid())){
							triangle2d.p0.removeLinkedTriangle(triangle2d);
							triangle2d.p1.removeLinkedTriangle(triangle2d);
							triangle2d.p2.removeLinkedTriangle(triangle2d);
							trianglesToRemove.add(triangle2d);
					    }
						break;
					default:
						break;
				}
			}
		}
		triangles.removeAll(trianglesToRemove);
		
	}
	protected void removeTrianglesWithSuperPoints(Triangle2D superTriangle ){
		Point2D sp0 = superTriangle.p0,sp1 = superTriangle.p1, sp2 = superTriangle.p2;
		removeSuperPoint(sp0);
		removeSuperPoint(sp1);
		removeSuperPoint(sp2);
	}
	protected Triangle2D findLinkedTriangle(Line2D edgeSahred,Triangle2D neighbor){
		List<Triangle2D> linkedList1 = edgeSahred.getP0().getLinkedTriangles();
		for (Triangle2D tl1 : linkedList1) {
				if(tl1.hasEdge(edgeSahred) && tl1 != neighbor){
					return tl1;
				}
		}
		
		return null;
	}
	public static Triangle2D findTriangleWithPointInside(List<Triangle2D> triangles,Point2D ponto){
		Triangle2D triangulo2d  = null;
		for (int i = (triangles.size()-1); i >=0  ; i--) {
			triangulo2d = triangles.get(i);
				if((triangulo2d.isInsideTriangle( ponto) || triangulo2d.isOnTriangle(ponto) || triangulo2d.hasVertex(ponto)) && !triangulo2d.isOutSideTriangle(ponto)){
					return triangulo2d;
				}
			
		}
		return null;
	}

	protected Triangle2D findTriangleWithPointInside(Point2D ponto){
		Triangle2D triangulo2d  = null;
		for (int i = (getTriangles().size()-1); i >=0  ; i--) {
			triangulo2d = getTriangles().get(i);
			if(triangulo2d.isInsideTriangle( ponto) || triangulo2d.isOnTriangle(ponto)){
				return triangulo2d;
			}
		}
		return triangulo2d;
}
	abstract public void initTriangulation();

	
	protected Triangle2D findTriangleByWalkthrough(Triangle2D triangle2d,Point2D circumcenter){
		// verifica se o triangulo for nulo
		if(triangle2d == null) {
			return null;
		
		}
		
		// Verifica se o ponto est� dentro ou sobre o triangulo e se pertencer ao dominio geometrico 
		if(triangle2d.isInsideTriangle(circumcenter) || triangle2d.isOnTriangle(circumcenter)){
			if(Project.isInsideDomain(circumcenter)){
				return triangle2d;
			}else{
				return null;
			}
		}
		
		//Cria uma linha entre o centroid e o ponto passado
		Line2D l0 = new Line2D(triangle2d.centroid(), circumcenter);
		Line2D interEdge = null;
		if(l0.intersect(triangle2d.getEdge0())){
			interEdge = triangle2d.getEdge0();
		}
		if(l0.intersect(triangle2d.getEdge1())){
			interEdge = triangle2d.getEdge1();
		}
		if(l0.intersect(triangle2d.getEdge2())){
			interEdge = triangle2d.getEdge2();
		}
		if(interEdge == null) {
			if(l0.inLine(triangle2d.p0)){
				interEdge = triangle2d.getEdge0();
			}
			if(l0.inLine(triangle2d.p1)){
				interEdge = triangle2d.getEdge1();
			}
			if(l0.inLine(triangle2d.p2)){
				interEdge = triangle2d.getEdge2();
			}
			return null ;
		}
		
		Triangle2D t = findLinkedTriangle(interEdge, triangle2d);
		if(t == null) return null;
		if(t.isInsideTriangle(circumcenter) || t.isOnTriangle(circumcenter)){
			if(Project.isInsideDomain(circumcenter)){
				return t;
			}else{
				return null;
			}
		}else{
			return findTriangleByWalkthrough(t, circumcenter);
		}
			
	
}
	
}
