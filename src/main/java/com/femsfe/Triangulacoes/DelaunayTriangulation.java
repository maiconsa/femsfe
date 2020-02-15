package com.femsfe.Triangulacoes;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.femsfe.Geometrias.Point2D;
import com.femsfe.Geometrias.Triangle2D;


public class DelaunayTriangulation extends Triangulation {
	private List<Point2D> pointList;

	public DelaunayTriangulation(List<Point2D> pointList){
		 super();
		this.pointList = pointList;
	}
	public DelaunayTriangulation() {
		 super();
	}
	public void randomizedPoint(){
		Collections.shuffle(pointList);
	}
	private Triangle2D superTriangle(){
		double M = 0;
		if(pointList != null  && pointList.size() >=3){
			Iterator<Point2D> iterator = pointList.iterator();
			Point2D ponto2d;
			while (iterator.hasNext()) {
				 ponto2d = (Point2D) iterator.next();
				 if(Math.max(ponto2d.getX(), ponto2d.getY()) > M){
					 M  = Math.max(ponto2d.getX(), ponto2d.getY());	
				 }
			}
			M *=16;
		}
		return new Triangle2D(new Point2D(3*M, 0), new Point2D(0, 3*M), new Point2D(-3*M, -3*M));		
	}
	public void initTriangulation(){
		/*	ENCONTRADO SUPER TRIANGULO QUE ENVOLVE TODOS OS PONTOS	*/
		Triangle2D superTriangulo = superTriangle();
		System.out.println("Delaunay Triangulation of a Set of Point With "+pointList.size() + " Points Started......");
		/*	ADICIONA SUPER TRIANGULO	*/
		addTriangle(superTriangulo);
		
		/*	DISTRIBUI OS PONTOS ALEATORIAMENTE	*/
		randomizedPoint();
		int index = 0;
		/*	INICIA LOOP	*/
		for (Point2D ponto2d : pointList) {	
				ponto2d.setIndex(index);
				addPoint(ponto2d,null);
				index++;
		}//FIM WHILE LOOP

		removeTrianglesWithSuperPoints(superTriangulo);
	}





	
}
