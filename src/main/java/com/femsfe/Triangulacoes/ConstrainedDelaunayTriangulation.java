package com.femsfe.Triangulacoes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.femsfe.Geometrias.Border2D;
import com.femsfe.Geometrias.Face2D;
import com.femsfe.Geometrias.Line2D;
import com.femsfe.Geometrias.OrderByAngle;
import com.femsfe.Geometrias.Point2D;
import com.femsfe.Geometrias.Triangle2D;
import com.femsfe.Project;

/**
 * A subclass of <code>Triangulation</code> used for domain discretization of a model 2d .
 * @author Maicon Alc�ntara
 * @since 1.0
 * */
public class ConstrainedDelaunayTriangulation extends Triangulation   {	
	

	List<Line2D> segments;
	Triangle2D ST;
	/**
	 * An empty constructor.
	 * */
	public ConstrainedDelaunayTriangulation(){
		super();
	}

	public ConstrainedDelaunayTriangulation(List<Face2D> faces) {
			super();
			this.faces = faces;
	
	}
	/**
	 * Start the discretization based on a value required.
	 * @param hmin determine the element size 
	 * @since 1.0
	 * */
	public void init(float hmin)  {
		start();
			discretizeModel((float) hmin);
			chewRefine(hmin);
		stop();
	}
	
	public  void discretizeFace(Face2D face2d){
		double length = Double.POSITIVE_INFINITY; 
		for (Border2D border2d : face2d.getBoundaries()) {
			length = Math.min(length, border2d.getLength());
		}
		float hmin = (float) (length/20);
		double M = face2d.getMaxCoordinate();
		M *= 16;
		Triangle2D superTriangle = new Triangle2D(new Point2D((float) (3*M), 0), new Point2D(0, (float) (3*M)), new Point2D((float)(-3*M),(float) (-3*M)));	
			clear();
			addTriangle(superTriangle);
		List<Line2D> listLine = face2d.chewSegmentation(hmin);
			for (Line2D line2d :listLine  ) {
				addPoint(line2d.getP0(),null);
				addPoint(line2d.getP1(),null);
				
			}
			for (Border2D border2d : face2d.getBoundaries()) {
				for (Point2D point2d : border2d.getChewPoints()) {
					if(!point2d.wasIntserted){
						addPoint(point2d, null);
					}
					
				}
			}
			for (Line2D line2d : listLine) {
				addEdgeCDT(line2d);
			}
			
		removeTrianglesWithSuperPoints(superTriangle);
		List<Triangle2D> trianglesToRemove = new ArrayList<>();
		for (Triangle2D triangle2d : triangles) {

						if(!face2d.getPolygon().contains(triangle2d.centroid())){
							triangle2d.p0.removeLinkedTriangle(triangle2d);
							triangle2d.p1.removeLinkedTriangle(triangle2d);
							triangle2d.p2.removeLinkedTriangle(triangle2d);
							trianglesToRemove.add(triangle2d);
					    }
		}
		triangles.removeAll(trianglesToRemove);
		
		face2d.trianglesForDrawMaterial = new ArrayList<>();
		for (Triangle2D triangle2d : getTriangles()) {
			triangle2d.p0.clearLinkedTriangles();
			triangle2d.p1.clearLinkedTriangles();
			triangle2d.p2.clearLinkedTriangles();
			face2d.trianglesForDrawMaterial.add(triangle2d);
		}
		clear();
	}

	
	/**
	 * Return  the element  that has the point v as vertex and it intersects by the cutEdge. This methos is used  in addEdgeCDT method.
	 * The searching occur in conectivity list that  belong to model.
	 * @param v a 
	 * @param cutEdge is a edge
	 * @since 1.0
	 */ 
	private Triangle2D findTriangle(Point2D v ,Line2D cutEdge){
		for (Triangle2D triangle2d : v.getLinkedTriangles()) {
			if(triangle2d.intersect(cutEdge)){ 
				return triangle2d;
				}
		}
		return null;
	}
	/**
	 * Return the opposed element based on a triangle t and a point v. This method is required for the intersection searching used in addEdgeCDT method.
	 * @param t element 
	 * @param v vertex
	 * @since 1.0
	 * */
	private Triangle2D opposedTriangle(Triangle2D t,Point2D v){
		Line2D opposedEdge = t.getOpposedEdge(v);
		if(opposedEdge == null){ return null;}
		return findLinkedTriangle(opposedEdge, t);
	}

	private boolean TriangleListHasEdge(Line2D edge){
		Point2D p0 = edge.getP0();
		Point2D p1 = edge.getP1();
		List<Triangle2D> linkedList1 = p0.getLinkedTriangles();
		List<Triangle2D> linkedList2 = p1.getLinkedTriangles();
		for (Triangle2D tl1 : linkedList1) {
				for (Triangle2D tl2 : linkedList2) {
						if(tl1.equals(tl2)){
							return true;
						}
				}
		}
		return false;	
	}

	/**
	 * Return the triangle that wrapped the model.
	 * */
	private Triangle2D superTriangle( ){
			double M = 0;
			for (Face2D face : getFaces()) {
					M =  Math.max(M, face.getMaxCoordinate());
				
			}	  
		M *=16;
		return new Triangle2D(new Point2D((float) (3*M), 0), new Point2D(0, (float) (3*M)), new Point2D((float)(-3*M),(float) (-3*M)));		
	}
	

	/**
	 * Add the constrained edge to the discretization.
	 * @param edge to be inserted.
	 * @since 1.0
	 * */

	private void addEdgeCDT(Line2D edge){
			if(TriangleListHasEdge(edge)) return;
			Point2D a = edge.getP0(),b = edge.getP1();
			Point2D v;
			Triangle2D t = findTriangle(a,edge);
			if(t == null){
				return;
			}
			Set<Point2D> PU = new HashSet<>();	Set<Point2D> PL = new HashSet<>();
			Triangle2D tOpo;
			Line2D edgeShared = null;
			Point2D vOpo;
			 v = a;
			while(!t.hasVertex(b)){
				tOpo = opposedTriangle(t, v);
				if(tOpo == null) return;
				edgeShared = t.getOpposedEdge(v);
				vOpo = tOpo.getOpposedVertex(edgeShared);
				double ori  = Triangle2D.area2(a, b, vOpo);
				if(ori > 0){
					PU.add(vOpo);
					if(Triangle2D.area2(a, b,edgeShared.getP0()) > 0){
						v = edgeShared.getP0();PU.add(v);
					}
					if(Triangle2D.area2(a, b,edgeShared.getP1()) > 0){
						v = edgeShared.getP1();PU.add(v);
					}
					
				}else if(ori < 0){
					PL.add(vOpo);
					if(Triangle2D.area2(a, b,edgeShared.getP0()) < 0){
						v = edgeShared.getP0();PL.add(v);
					}
					if(Triangle2D.area2(a, b,edgeShared.getP1()) < 0){
						v = edgeShared.getP1();PL.add(v);
					}
					/*	QUANDO O VERTICE OPOSTO � O OUTRO V�RTICE DA ARESTA DE INTERSECAO*/
				}else{
					if(Triangle2D.area2(a, b,edgeShared.getP0()) < 0){
						v = edgeShared.getP0();PL.add(v);
					}
					if(Triangle2D.area2(a, b,edgeShared.getP1()) < 0){
						v = edgeShared.getP1();PL.add(v);
					}
					PU.add(vOpo);
					if(Triangle2D.area2(a, b,edgeShared.getP0()) > 0){
						v = edgeShared.getP0();PU.add(v);
					}
					if(Triangle2D.area2(a, b,edgeShared.getP1()) > 0){
						v = edgeShared.getP1();PU.add(v);
					}
				}
				t.p0.removeLinkedTriangle(t);
				t.p1.removeLinkedTriangle(t);
				t.p2.removeLinkedTriangle(t);
				removeTriangle(t);
				t = tOpo;
				if(t.hasVertex(b)){
					removeTriangle(t);
					t.p0.removeLinkedTriangle(t);
					t.p1.removeLinkedTriangle(t);
					t.p2.removeLinkedTriangle(t);
				}
			}
			PU.remove(a);PU.remove(b);
			PL.remove(a);PL.remove(b);
			recursiveTriangulation(new ArrayList<>(PU), edge);
			recursiveTriangulation(new ArrayList<>(PL), edge);
		
		
	}
	/**
	 * Discretize the model based on a value hmin.
	 * @param hmin determine the element size 
	 * @since 1.0
	 * */
	private void discretizeModel(float hmin){
		 ST = superTriangle();
			clear();
			addTriangle(ST);
		segments = Project.chewSegmentation(hmin);
			for (Line2D line2d :segments  ) {
				addPoint(line2d.getP0(),null);
				addPoint(line2d.getP1(),null);
				
			}
			for (Border2D border2d : Project.getBordersFromFaces()) {
				for (Point2D point2d : border2d.getChewPoints()) {
					if(!point2d.wasIntserted){
						addPoint(point2d, null);
					}
					
				}
			}
			for (Line2D line2d : segments) {
				addEdgeCDT(line2d);
			}
			
		removeTrianglesWithSuperPoints(ST);
		removeTrianglesOutsideDomain();
	}
			
	private void recursiveTriangulation(List<Point2D> P,Line2D ab){
		Point2D c = null;
		if(!P.isEmpty()){
			c = P.get(0);
		}
		if(P.size() >1){
				Point2D pontoMedia = ab.midPoint();
				Point2D a = ab.getP0();
				Point2D b = ab.getP1();
			
				Collections.sort(P,new OrderByAngle(pontoMedia));
				for (Point2D ponto2d : P) {
					if(InCircle(ab.getP0(), c, ab.getP1(), ponto2d)){
						c = ponto2d;
						break;
					}
				}
				ArrayList<Point2D> PL = new ArrayList<>();
				ArrayList<Point2D> PR = new ArrayList<>();
				for (Point2D ponto2d : P) {
					if(orient(pontoMedia, c, ponto2d) > 0){
						PL.add(ponto2d);
					}else if(orient(pontoMedia, c, ponto2d) < 0){
						PR.add(ponto2d);
					}
				}
				Point2D aux = null;
				if(orient(pontoMedia, c, a) < 0){
					aux = a;
					a = b;
					b = aux;
				}
				recursiveTriangulation(PL, new Line2D(a,c));
				recursiveTriangulation(PR, new Line2D(b,c));
		}
		if(!P.isEmpty()){
			Triangle2D newTriangle = new Triangle2D(ab.getP0(), ab.getP1(), c);
			ab.getP0().addLinkedTriangle(newTriangle);
			ab.getP1().addLinkedTriangle(newTriangle);
			c.addLinkedTriangle(newTriangle);
			addTriangle(new Triangle2D(ab.getP0(), ab.getP1(), c));
		}
	}

	/* **************************************************************************************************
	 * 				MESH REFYNEMENT - CHEW ALGORITHM
	 ********************************************************************************************************/
	public void chewRefine(double hmin){
				Triangle2D invalid = findTriangleWithInvalidCircunradius(hmin);
				while (invalid != null && invalid.circumradius() > hmin){
				
					Point2D p = invalid.circumcenter();
					Triangle2D t = findTriangleByWalkthrough(invalid, p);
					//Triangle2D t = findTriangleWithPointInside(p);
					if(t != null){
						addPoint(p,t);
					}
					invalid = findTriangleWithInvalidCircunradius(hmin);
				}			
	}
	
	
	private Triangle2D findTriangleWithInvalidCircunradius(double hmin){
		for (int i = getTriangles().size()-1; i >= 0 ;i--) {
			Triangle2D triangle2d = getTriangles().get(i);
			if(triangle2d.circumradius() > hmin && !triangle2d.marked ){
				triangle2d.marked  = true;
				return triangle2d;
			}
		}
	
		return null;
	}

	@Override
	public void initTriangulation() {
		// TODO Auto-generated method stub
		
	}


	

	

	

}



