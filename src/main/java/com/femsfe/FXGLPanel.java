package com.femsfe;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputListener;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2GL3;

import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.gl2.GLUT;

import com.femsfe.Analysis.FiniteAnalysis;
import com.femsfe.Geometrias.*;
import com.femsfe.Triangulacoes.ConectivityList;

import javafx.embed.swing.SwingNode;
import javafx.scene.Scene;
import javafx.scene.control.ToggleButton;


public class FXGLPanel  extends SwingNode implements GLEventListener,MouseInputListener{
	public GLJPanel glPanel;
	private FPSAnimator animator;
	private GLU glu;
	private GLUT glut;
	private Scene scene;
	private static GL2 gl;
	/* *
	 * Mouse Location
	 */
	private  Point deviceMouseLocation  = null;
	protected Point deviceMouseClickLocation = null;
	protected double[] worldMouseLocation = null;
	protected double[] initialZoomPoint = null,endZoomPoint = null;

	protected Point pointPan1 = null,pointPan2 = null;
	private int width,height;
	private int[] VIEWPORT;
	
	public Point2D center = new Point2D(0, 0);
	public float left = -0.5f,right = 0.5f,bottom = -0.5f,top = 0.5f; 
	
	/**
	 * Flags
	 */	
	private boolean isInside = false;
	private boolean isLeftButtonPressed = false;
	
	
	public FXGLPanel( ) {
		glPanel = new GLJPanel();
		init();
		createSwingNode();
		
	}
	private void createSwingNode(){
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				setContent(glPanel);
			}
		});
	}
	public void setScene(Scene scene){
		this.scene = scene;
	}
	
	public void setOrtho(float left,float right,float bottom,float top){
		this.left = left;
		this.right = right;
		this.bottom = bottom;
		this.top = top;	
	}
	
	public void init(){
		//GLProfile profile = GLProfile.getDefault();
		//GLCapabilities capabilities = new GLCapabilities(profile);	
		glPanel.addGLEventListener(this);
		glPanel.addMouseListener(this);
		glPanel.addMouseMotionListener(this);	
		 glu = new GLU();
		 glut = new GLUT();
		 animator = new FPSAnimator(glPanel, 60);
		start();	
	}
	
	public void stop(){
		animator.stop();
	}
	public void pause(){
		animator.pause();
	}
	public void  start(){
		animator.start();
	}
	public void resume(){
		animator.resume();
	}
	public boolean isAnimating(){
		return animator.isAnimating();
	}
	
	@Override
	public void display(GLAutoDrawable draw) {
		gl = draw.getGL().getGL2();
		
		//LIMPA O BUFFER
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
			
		//DEFINE A COR DE FUNDO
		gl.glClearColor(0.f, 0.f, 0.f, 0.f);
		//gl.glClearColor(1.f, 1.f, 1.f, 1.f);
		myReshape();
		
		gl.glColor4d(0.99f, 0.99f, 0.99f, 0.99f);
		gl.glBegin(GL2.GL_POLYGON);
			gl.glVertex2d(left, bottom);
			gl.glVertex2d(right, bottom);
			gl.glVertex2d(right, top);
			gl.glVertex2d(left, top);
		gl.glEnd();
		
		drawGrid(20);

		if(deviceMouseLocation != null){
			worldMouseLocation = deviceCoord2worldCoord(deviceMouseLocation.x, deviceMouseLocation.y, 0);
			
			if(SelectGeometry.started() && SelectGeometry.getGeometryType() == GeometryType.FACE){
				boolean isOverOther = false;
				for (Face2D face2d : Project.getFaceList()) {
					if(face2d.getTypeFace() != Face2D.HOLE ){
						boolean contains = face2d.getPolygon().contains(worldMouseLocation[0], worldMouseLocation[1]);
						face2d.isOver = contains;
						if(contains == true && face2d.getTypeFace() != Face2D.EXTERNAL){
							isOverOther = true;
						}
					}
				}
				
				if(Project.getFaceList().get(0).getTypeFace() == Face2D.EXTERNAL ){
					if(Project.getFaceList().get(0).isOver == true && isOverOther == true){
						Project.getFaceList().get(0).isOver = false;
					}
				}	
			}
			
			double x = worldMouseLocation[0], y = worldMouseLocation[1];
			if(x > left && x < right && y > bottom && y < top){
				isInside = true;
			}else{
				isInside = false;
			}
		}
		
		drawAllGeometries();
		createGeometry();
		selectGeometry();
		drawModel(Project.getMesh());

		zoom();
		pan();
		drawCoordinates();
		drawArrowCoors();
		
		 drawMaterialFaces();
		
		// drawTicksLabel(10, true);
		gl.glFlush();
		
	}

	@Override
	public void dispose(GLAutoDrawable draw) {	
	}

	@Override
	public void init(GLAutoDrawable draw) {
		 gl = (GL2) draw.getGL();
		 width = glPanel.getWidth();
		 height = glPanel.getHeight();
		 VIEWPORT = getViewportByAspectRatio();
		 myReshape();
	}
	
	public int[] getViewportByAspectRatio(){
		double  aspect = width/height;
		double R = (right - left)/(top-bottom);
		System.out.println(R);
		int newVH = height,newVW = width;
		if(R > aspect){
			
			newVH = (int) (width/R);
		}else if(R <aspect){
			newVW = (int) (height*R);
		}else{
			newVW = width;
			newVH = height;
		}
		
		int x = (width-newVW)/2;
		int y = (height-newVH)/2;
		
		int[] VP = {x,y,newVW,newVH};
		return  VP;
	}
	
	public void myReshape(){

			// VIEWPORT
			gl.glMatrixMode(GL2.GL_VIEWPORT);
			gl.glLoadIdentity();
			gl.glViewport(VIEWPORT[0], VIEWPORT[1], VIEWPORT[2], VIEWPORT[3]);
			
			// PROJECTION
			gl.glMatrixMode(GL2.GL_PROJECTION);
			gl.glLoadIdentity();
			glu.gluOrtho2D(left,right,bottom, top);
			
			//MODELVIEW
			gl.glMatrixMode(GL2.GL_MODELVIEW);
			gl.glLoadIdentity();	
	}

	public void reshape(GLAutoDrawable draw, int x, int y, int w, int h) {
		gl = (GL2) draw.getGL(); 
		width = w;
		height  = h;
		
		VIEWPORT = getViewportByAspectRatio();
		myReshape();

	}
	/*	CONVERTE A COORDENADA DE DISPOSITIVO PARA O 3D WORLD	*/
	public  double[] deviceCoord2worldCoord(int x,int y,int z){
		double[] pontoMundo = new double[4];
		int[]  viewport = getViewport();
		int realY = viewport[3] - (int) y - 1;
		glu.gluUnProject(x,realY,z, getModelViewMatrix(),0,getProjectionMatrix(),0,viewport,0,pontoMundo,0); 
		return pontoMundo;
	}
	/*	RETORNA A MATRIX PROJE��O	*/
	public  double[] getProjectionMatrix(){
		double[] proj = new double[16];
		((GL2GL3) gl).glGetDoublev(GL2.GL_PROJECTION_MATRIX, proj,0);
		return proj;
	}
	/*	RETORNA A MATRIX MODEL_VIEW	*/
	public  double[] getModelViewMatrix(){
		double[] mv = new double[16];
		((GL2GL3) gl).glGetDoublev(GL2.GL_MODELVIEW_MATRIX, mv,0);
		return mv;
	}
	/*	RETORNA O VETOR VIEW_PORT	*/
	public static int[] getViewport(){
		int[] viewport = new int[4];
		gl.glGetIntegerv(GL.GL_VIEWPORT, viewport,0);
		return viewport;
	}

	private void drawPoint2D(Point2D point){
		 gl.glEnable(GL2.GL_POINT_SMOOTH );
		 gl.glEnable(GL.GL_BLEND );
		 gl.glBlendFunc(GL.GL_SRC_ALPHA,GL.GL_ONE_MINUS_SRC_ALPHA );
		gl.glPointSize(6.0f);
		gl.glBegin(GL2.GL_POINTS);
			gl.glVertex2d(point.getX(), point.getY());
		gl.glEnd();
		gl.glDisable(GL2.GL_POINT_SMOOTH );
		gl.glDisable(GL.GL_BLEND );
		gl.glPointSize(1.0f);
	}
	private void drawLine2D(Line2D line2d){
		gl.glEnable(GL2.GL_LINE_SMOOTH );
		gl.glEnable(GL.GL_BLEND );
		gl.glBlendFunc(GL.GL_SRC_ALPHA,GL.GL_ONE_MINUS_SRC_ALPHA );
		gl.glLineWidth(1.5f);
		gl.glBegin(GL2.GL_LINES);
			gl.glVertex2d(line2d.getP0().getX(), line2d.getP0().getY());
			gl.glVertex2d(line2d.getP1().getX(), line2d.getP1().getY());
		gl.glEnd();
		gl.glDisable(GL2.GL_LINE_SMOOTH );
		gl.glDisable(GL.GL_BLEND );
	}
	private void drawLine2D(Line2D line2d,float lineWidth,double r,double g,double b){
		if(line2d == null) return;
		gl.glEnable(GL2.GL_LINE_SMOOTH );
		gl.glEnable(GL.GL_BLEND );
		gl.glBlendFunc(GL.GL_SRC_ALPHA,GL.GL_ONE_MINUS_SRC_ALPHA );
		gl.glLineWidth(lineWidth);
		gl.glColor3d(r, g, b);
		gl.glBegin(GL2.GL_LINES);
			gl.glVertex2d(line2d.getP0().getX(), line2d.getP0().getY());
			gl.glVertex2d(line2d.getP1().getX(), line2d.getP1().getY());
		gl.glEnd();
		gl.glDisable(GL2.GL_LINE_SMOOTH );
		gl.glDisable(GL.GL_BLEND );
	}
	private void drawArc2D(Arc2D arc2d){
		
		Point2D center = arc2d.getCenterPoint();
		double radius = arc2d.getRadius();
		double angStart = arc2d.getStartAngle();
		double angEnd = arc2d.getEndAngle();
		if(angStart == angEnd) return;
		
		Point2D pStart= arc2d.getStartPoint();
		Point2D pEnd = arc2d.getEndPoint();
		
		
		angStart = Math.toDegrees(Math.atan2(pStart.getY() - center.getY(), pStart.getX() - center.getX()));
		angEnd = Math.toDegrees(Math.atan2(pEnd.getY() - center.getY(), pEnd.getX() - center.getX()));
		gl.glEnable(GL2.GL_LINE_SMOOTH );
		gl.glEnable(GL.GL_BLEND );
		gl.glBlendFunc(GL.GL_SRC_ALPHA,GL.GL_ONE_MINUS_SRC_ALPHA );
		gl.glLineWidth(1.5f);
		double angle = angStart;
		gl.glBegin(GL.GL_LINE_STRIP);
		gl.glVertex2d(pStart.getX(),pStart.getY());
		double factor = 0.1;
		if(angEnd < angStart){ angEnd+=360;}
		while(Math.abs(angle - angEnd) > factor){
			gl.glVertex2d(center.getX() + radius*Math.cos(Math.toRadians(angle)), center.getY() + radius*Math.sin(Math.toRadians(angle)));
			angle+=factor;
		}
		gl.glVertex2d(pEnd.getX(),pEnd.getY());
		gl.glEnd();
		gl.glDisable(GL2.GL_LINE_SMOOTH );
		gl.glDisable(GL.GL_BLEND );
		
	
		
	}
	private void drawCircle2D(Circle2D circle2d){
		if(circle2d.getCenterPoint() != null){
		int lineAmount = 100;
		Point2D center = circle2d.getCenterPoint();

		double twicePi = 2.0f *Math.PI;
		gl.glEnable(GL2.GL_LINE_SMOOTH );
		gl.glEnable(GL.GL_BLEND );
		gl.glBlendFunc(GL.GL_SRC_ALPHA,GL.GL_ONE_MINUS_SRC_ALPHA );
		gl.glLineWidth(1.5f);
		gl.glBegin(GL2.GL_LINE_LOOP);
			for(int i = 0; i <= lineAmount;i++) { 
				gl.glVertex2d(
				    center.getX() + (circle2d.getRadius()* Math.cos(i *  twicePi / lineAmount)), 
				    center.getY() + (circle2d.getRadius()* Math.sin(i * twicePi / lineAmount))
				);
			}
		gl.glEnd();
		gl.glDisable(GL2.GL_LINE_SMOOTH );
		gl.glDisable(GL.GL_BLEND );

		}
	}
	private void drawPolygon2D(Polygon2D polygon){
			if(polygon.isSelected()){
				gl.glColor3d(1.0f, 0.0f, 0.0f);
			}else{
				gl.glColor3d(0.0f, 0.0f, 1.0f);
			}
			gl.glEnable(GL2.GL_LINE_SMOOTH );
			gl.glEnable(GL.GL_BLEND );
			gl.glBlendFunc(GL.GL_SRC_ALPHA,GL.GL_ONE_MINUS_SRC_ALPHA );
			gl.glLineWidth(1.5f);
			List<Point2D> points = polygon.getPointList();
			gl.glBegin(GL2.GL_LINE_STRIP);
				for (Point2D ponto2d : points) {
					gl.glVertex2d(ponto2d.getX(), ponto2d.getY());
				}
			gl.glEnd();
			gl.glDisable(GL2.GL_LINE_SMOOTH );
			gl.glDisable(GL.GL_BLEND );
		
	}
	@SuppressWarnings("unused")
	private void drawPolygon2D(Polygon2D polygon,float lineWidth,float r,float g,float b){
		if(polygon == null) return;
		gl.glColor3d(r,g, b);
		gl.glEnable(GL2.GL_LINE_SMOOTH );
		gl.glEnable(GL.GL_BLEND );
		gl.glBlendFunc(GL.GL_SRC_ALPHA,GL.GL_ONE_MINUS_SRC_ALPHA );
		gl.glLineWidth(lineWidth);
		List<Point2D> points = polygon.getPointList();
		gl.glBegin(GL2.GL_LINE_STRIP);
			for (Point2D ponto2d : points) {
				gl.glVertex2d(ponto2d.getX(), ponto2d.getY());
			}
		gl.glEnd();
		gl.glDisable(GL2.GL_LINE_SMOOTH );
		gl.glDisable(GL.GL_BLEND );
	
}
	private void drawPolyline2D(Polyline2D polyline2d){
		gl.glEnable(GL2.GL_LINE_SMOOTH );
		gl.glEnable(GL.GL_BLEND );
		gl.glBlendFunc(GL.GL_SRC_ALPHA,GL.GL_ONE_MINUS_SRC_ALPHA );
		gl.glLineWidth(1.5f);
		List<Point2D> points = polyline2d.getListPoint();
		gl.glBegin(GL2.GL_LINE_STRIP);
			for (Point2D ponto2d : points) {
				gl.glVertex2d(ponto2d.getX(), ponto2d.getY());
			}
		gl.glEnd();
		gl.glDisable(GL2.GL_LINE_SMOOTH );
		gl.glDisable(GL.GL_BLEND );

	}
	private void drawPointList(List<Point2D> pointList){
		gl.glEnable(GL2.GL_POINT_SMOOTH );
		gl.glEnable(GL.GL_BLEND );
		gl.glBlendFunc(GL.GL_SRC_ALPHA,GL.GL_ONE_MINUS_SRC_ALPHA );
		gl.glPointSize(5.0f);
		gl.glColor3d(0.0f, 0.0f, 1.0f);
		gl.glBegin(GL2.GL_POINTS);
			for (int i = 0; i < pointList.size(); i++) {
				gl.glVertex2d(pointList.get(i).getX(), pointList.get(i).getY());
			}
		gl.glEnd();
		gl.glPointSize(1.0f);
		gl.glDisable(GL2.GL_POINT_SMOOTH );
		gl.glDisable(GL.GL_BLEND );
	}
	
	private void drawModel(ConectivityList mesh){
		if(mesh == null) return;

		drawMesh(mesh);
		drawResultColorMap(mesh);
		drawArrowsMap(mesh);
		drawContoursMap(mesh);
		
		if(mesh.isArrowsVisible() || mesh.isColorMapVisible() || mesh.isIsolinesVisible()){
			gl.glColor3f(0.0f, 0.0f, 0.0f);
			gl.glLineWidth(1.5f);
			for (Border2D border2d : Project.getBordersFromFaces()) {
				switch (border2d.getType()) {
					case LINE:
						drawLine2D((Line2D) border2d);
						break;
					case ARC:
						Arc2D arc = (Arc2D) border2d;
						drawPolyline2D(new Polyline2D(arc.getChewPoints()));
						break;
					case BEZIER:
						BezierCurve2D bezier = (BezierCurve2D) border2d;
						drawPolyline2D(new Polyline2D(bezier.getChewPoints()));
						break;
					default:
						break;
				}
			}	
		}
		
		if(mesh.isColorMapVisible()){
			FiniteAnalysis FEMA = Project.getFiniteAnalysis();
			drawColorBar(FEMA.minReal,FEMA.maxReal,5);	
		}
		if(mesh.isMeshQualityVisible()){
			drawColorBar(0,1,5);	
		}
		
		
		
		
		
	}
	private void drawMesh(ConectivityList mesh) {
			if(mesh == null) return ;
					double color[];
					
					for (Triangle2D triangle2d : mesh.getElements()) {
				
						if(mesh.isMeshQualityVisible()){
							color = getColdToHotColor(triangle2d.qualityFactor());
							gl.glColor3d(color[0], color[1], color[2]);
							gl.glEnable(GL2.GL_POLYGON_SMOOTH);
							gl.glEnable(GL.GL_BLEND );
							gl.glBlendFunc(GL.GL_SRC_ALPHA,GL.GL_ONE_MINUS_SRC_ALPHA );
							gl.glBegin(GL2.GL_POLYGON);
								gl.glVertex2d(triangle2d.p0.getX(), triangle2d.p0.getY());
								gl.glVertex2d(triangle2d.p1.getX(), triangle2d.p1.getY());
								gl.glVertex2d(triangle2d.p2.getX(), triangle2d.p2.getY());
							
							gl.glEnd();	
							gl.glDisable(GL2.GL_POLYGON_SMOOTH);
							gl.glDisable(GL.GL_BLEND );
						}
						if(mesh.isMeshVisible()){
							gl.glEnable(GL2.GL_LINE_SMOOTH);
							gl.glEnable(GL.GL_BLEND );
							gl.glBlendFunc(GL.GL_SRC_ALPHA,GL.GL_ONE_MINUS_SRC_ALPHA );
							Line2D e0 = triangle2d.getEdge0();
							
							if(!mesh.isMeshQualityVisible()){
								drawFillTriangle(triangle2d, 0.75, 0.75, 0.75, 0.75);
							}
							if(e0.isFixedEdge() == true){
									gl.glLineWidth(1.75f);
									
										gl.glColor3f(0,0,0);
								}else{
									gl.glLineWidth(.8f);
									gl.glColor3f(0,0,0);
								
								}
							gl.glBegin(GL2.GL_LINES);
								gl.glVertex2d(e0.getX0(), e0.getY0());
								gl.glVertex2d(e0.getX1(), e0.getY1());
							gl.glEnd();
						
							Line2D e1 = triangle2d.getEdge1();
							if(e1.isFixedEdge() == true){
									gl.glLineWidth(1.75f);
									gl.glColor3f(0,0,0);
								}else{
									gl.glLineWidth(0.8f);
									gl.glColor3f(0,0,0);
								}
							gl.glBegin(GL2.GL_LINES);
								gl.glVertex2d(e1.getX0(), e1.getY0());
								gl.glVertex2d(e1.getX1(), e1.getY1());
							gl.glEnd();
					
							Line2D e2 = triangle2d.getEdge2();
							if(e2.isFixedEdge() == true){
									gl.glLineWidth(1.75f);
									gl.glColor3f(0,0,0);
								}else{
									gl.glLineWidth(.8f);
									gl.glColor3f(0,0,0);
								}
							gl.glBegin(GL2.GL_LINES);
								gl.glVertex2d(e2.getX0(), e2.getY0());
								gl.glVertex2d(e2.getX1(), e2.getY1());
							gl.glEnd();
						}
					
						gl.glDisable(GL2.GL_LINE_SMOOTH);
						gl.glDisable(GL.GL_BLEND );
	
					}
			
			
	}
	private void drawArrowsMap(ConectivityList mesh){
		if(mesh == null || mesh.isArrowsVisible() == false) return;
		for (Triangle2D triangle2d : mesh.getElements()) {
			Line2D arrow = triangle2d.getArrow(Project.problemType);
			double length = arrow.getLength();
			gl.glEnable(GL2.GL_LINE_SMOOTH);
			gl.glEnable(GL.GL_BLEND );
			gl.glBlendFunc(GL.GL_SRC_ALPHA,GL.GL_ONE_MINUS_SRC_ALPHA );
			gl.glColor3d(0, 0, 0);
			gl.glLineWidth(1.0f);
			gl.glBegin(GL2.GL_LINES);
				gl.glVertex2d(arrow.getX0(), arrow.getY0());
				gl.glVertex2d(arrow.getX1(), arrow.getY1());
			gl.glEnd();
			gl.glDisable(GL2.GL_LINE_SMOOTH);
			gl.glDisable(GL.GL_BLEND );
			
			double cx0 = arrow.getX1();
			double cy0 = arrow.getY1();
			double cx1 = arrow.getX0();
			double cy1 = arrow.getY0();
			
		
			double dist = length;
			// Find a and h.
			double a = 0.35*length;
			double r0 = a / Math.cos(Math.toRadians(30));
			double h = Math.sqrt(r0 * r0 - a * a);
			// Find P2.
			double cx2 = cx0 + a * (cx1 - cx0) / dist;
			double cy2 = cy0 + a * (cy1 - cy0) / dist;
		    // Get the points P3.
			double x3 = cx2 + h * (cy1 - cy0) / dist;
			double y3 = cy2 - h * (cx1 - cx0) / dist;
			
			double x4 = cx2 - h * (cy1 - cy0) / dist;
			double y4 = cy2 + h * (cx1 - cx0) / dist;
		
			gl.glEnable(GL2.GL_POLYGON_SMOOTH);
			gl.glEnable(GL.GL_BLEND );
			gl.glBlendFunc(GL.GL_SRC_ALPHA,GL.GL_ONE_MINUS_SRC_ALPHA );
			gl.glBegin(GL2.GL_POLYGON);
				gl.glVertex2d(arrow.getX1(), arrow.getY1());
				gl.glVertex2d(x3, y3);
				gl.glVertex2d(x4, y4);
			gl.glEnd();
			gl.glDisable(GL2.GL_POLYGON_SMOOTH);
			gl.glDisable(GL.GL_BLEND );
		}
	}
	private void drawContoursMap(ConectivityList mesh){
		if(mesh == null || !mesh.isIsolinesVisible()){ return;	}
		if(mesh.getElements().isEmpty()) return;
		double maxValue = Project.getFiniteAnalysis().maxReal;
		double minValue = Project.getFiniteAnalysis().minReal;
		double delta = (maxValue - minValue);
	
		for (Triangle2D triangle2d :mesh.getElements()) {
			for (double i = 1; i < mesh.getContourNumber(); i++) {
				double factor =  i/mesh.getContourNumber();
				drawLine2D(triangle2d.getIsoLine(minValue + factor*delta),1.1f,0,0,0);
			
			}	
		}
	}
	private void drawResultColorMap(ConectivityList  mesh){
		if(mesh == null || mesh.isColorMapVisible() == false) return;	
		FiniteAnalysis FEMA = Project.getFiniteAnalysis();
		double min = FEMA.minReal;
		double max = FEMA.maxReal;
		double delta = max - min;
		if(delta == 0 ) {return;}


		double[] color;
		for (Triangle2D triangle2d: mesh.getElements()) {
				gl.glBegin(GL2.GL_POLYGON);
					double i0 = (triangle2d.p0.getValue().getReal() -min) /delta;
					color = getColdToHotColor(i0);
					gl.glColor3d(color[0], color[1], color[2]);
			
					gl.glVertex2d(triangle2d.p0.getX(),triangle2d.p0.getY() );
					
					double i1 = (triangle2d.p1.getValue().getReal() - min)/delta;
					color = getColdToHotColor(i1);
					gl.glColor3d(color[0], color[1], color[2]);
					gl.glVertex2d(triangle2d.p1.getX(),triangle2d.p1.getY() );

			
					double i2 = (triangle2d.p2.getValue().getReal() - min)/delta;
					color = getColdToHotColor(i2);
					gl.glColor3d(color[0], color[1], color[2]);
					gl.glVertex2d(triangle2d.p2.getX(),triangle2d.p2.getY() );
				gl.glEnd();
						
		}

	}

	private void drawBezierCurve(BezierCurve2D bezier){	
		
		if(bezier.pointCtrlVisible){
			drawPointList(bezier.getControlPoints());
		}
		if(bezier.polygonBezierVisible && bezier.sizePointList() >=2){
			gl.glEnable(GL.GL_BLEND);
			gl.glDepthMask(true);
			gl.glBlendFunc(GL.GL_SRC_ALPHA,GL.GL_ONE_MINUS_SRC_ALPHA);
			gl.glLineWidth(1.25f);
			gl.glBegin(GL.GL_LINE_STRIP);
				gl.glColor4f(0.5f, 0.5f, 0.5f, 0.5f);
				List<Point2D> list = bezier.getControlPoints();
				for (int i = 0; i < list.size(); i++) {
					gl.glVertex2d(list.get(i).getX(), list.get(i).getY());
				}
			gl.glEnd();
			gl.glDisable(GL.GL_BLEND);
			gl.glDepthMask(false);	
		}
		
		if(bezier.sizePointList() >= 2){
			List<Point2D> list = bezier.computeBezierCurve(100);
			if(bezier.isSelected()){
				gl.glColor3f(1.0f, 0.0f, 0.0f);
			}else{
				gl.glColor3f(0.0f, 0.0f, 1.0f);
			}
			gl.glEnable(GL2.GL_LINE_SMOOTH );
			gl.glEnable(GL.GL_BLEND );
			gl.glBlendFunc(GL.GL_SRC_ALPHA,GL.GL_ONE_MINUS_SRC_ALPHA );
			gl.glLineWidth(1.5f);
			gl.glBegin(GL2.GL_LINE_STRIP);
				for (Point2D ponto2d : list) {
					gl.glVertex2d(ponto2d.getX(), ponto2d.getY());	
				}
			gl.glEnd();
			gl.glDisable(GL2.GL_LINE_SMOOTH );
			gl.glDisable(GL.GL_BLEND );
		}
	}
	private void drawRectangle(Rectangle2D rect){
			gl.glEnable(GL2.GL_LINE_SMOOTH );
			gl.glEnable(GL.GL_BLEND );
			gl.glBlendFunc(GL.GL_SRC_ALPHA,GL.GL_ONE_MINUS_SRC_ALPHA );
			gl.glLineWidth(1.5f);
			gl.glBegin(GL2.GL_LINE_STRIP);
				gl.glVertex2d(rect.getP0().getX(), rect.getP0().getY());
				gl.glVertex2d(rect.getQ0().getX(), rect.getQ0().getY());
				gl.glVertex2d(rect.getP1().getX(), rect.getP1().getY());
				gl.glVertex2d(rect.getQ1().getX(), rect.getQ1().getY());
				gl.glVertex2d(rect.getP0().getX(), rect.getP0().getY());
			gl.glEnd();
			gl.glDisable(GL2.GL_LINE_SMOOTH );
			gl.glDisable(GL.GL_BLEND );
			drawPoint2D(rect.getP0());
			drawPoint2D(rect.getP1());
			drawPoint2D(rect.getQ0());
			drawPoint2D(rect.getQ1());
	
	}
	private void drawGeometry(Geometry2D geometry2d){

		if(geometry2d == null || !geometry2d.isVisible()){return;}
		if(geometry2d instanceof Point2D ){
			drawPoint2D((Point2D) geometry2d);
		}else if(geometry2d instanceof Line2D){
			drawLine2D((Line2D) geometry2d);
		}else if(geometry2d instanceof Circle2D){
			drawCircle2D((Circle2D) geometry2d);
		}else if(geometry2d instanceof Polygon2D){
			drawPolygon2D((Polygon2D)geometry2d);
		}else if(geometry2d instanceof BezierCurve2D){
			drawBezierCurve((BezierCurve2D) geometry2d);
		}else if(geometry2d instanceof Rectangle2D){
			drawRectangle((Rectangle2D) geometry2d);
		}else if(geometry2d instanceof Arc2D){
			drawArc2D((Arc2D) geometry2d);
		}else if(geometry2d instanceof Polyline2D){
			drawPolyline2D((Polyline2D) geometry2d);
		}else if(geometry2d instanceof Face2D){
			Face2D face2d = (Face2D) geometry2d;
			gl.glRasterPos2d(face2d.center.getX(),face2d.center.getY());
			String str = null; 
			
			double r = 1,g = 1,b = 1;
			switch (face2d.getTypeFace()) {
				case Face2D.HOLE:
					str = "HF_"+ face2d.getID();
					r = 1;
					g = 1;
					b = 1;
					break;
				case Face2D.NORMAL:
					str = "NF_"+ face2d.getID();
					r = 0.75;
					g = 0.75;
					b = 0.75;
					break;
				case Face2D.EXTERNAL:
					str = "EF_"+ face2d.getID();
					r = 0.75;
					g = 0.75;
					b = 0.75;
					break;
	
				default:
					break;
			}
			if(face2d.isSelected() || face2d.isOver){
				r = b = g = 0.5;
			}
			if(Project.isMaterialVisible()){
				if(face2d.getMaterial() != null && face2d.getTypeFace() != Face2D.HOLE){
					Material material = face2d.getMaterial();
					float[] color = material.getColor();
					r = color[0];
					g = color[1];
					b = color[2];
				}else{
					r = g = b = 1;
				}
			}
			
			
			for (Triangle2D triangle2d : face2d.trianglesForDrawMaterial) {
				drawFillTriangle(triangle2d, r, g, b,0.25);
			}
			glut.glutBitmapString(GLUT.BITMAP_TIMES_ROMAN_10, str);
		}
	}
	private void drawFillTriangle(Triangle2D triangle2d,double r,double g,double b,double alpha){
		gl.glColor4d(r, g, b,alpha);
		gl.glBegin(GL2.GL_POLYGON);
			gl.glVertex2d(triangle2d.p0.getX(), triangle2d.p0.getY());
			gl.glVertex2d(triangle2d.p1.getX(), triangle2d.p1.getY());
			gl.glVertex2d(triangle2d.p2.getX(), triangle2d.p2.getY());
		gl.glEnd();
	}
	private void drawGeometryList(List<? extends Geometry2D> geometryList){
		for (Geometry2D geometry2d : geometryList) {
			if(geometry2d.isSelected()){
				gl.glColor3d(1.0, 0, 0);
			}else{
				gl.glColor3d(0.0, 0.0, 1.0);
			}
			
			drawGeometry(geometry2d);
			gl.glColor3d(0.0, 0.0, 1.0);
			gl.glLineWidth(1.25f);
		}
	}
	private void drawAllGeometries( ){
		drawGeometryList(Project.getGeometryList());
	}

	private void createGeometry(){
		if(CreateGeometry.started() && isInside){
			if(deviceMouseClickLocation != null){ 
				double click[] =  deviceCoord2worldCoord(deviceMouseClickLocation.x, deviceMouseClickLocation.y, 0);	
				CreateGeometry.addPoint((float)click[0], (float)click[1]);
				deviceMouseClickLocation = null;
			}
			if(deviceMouseLocation != null && !CreateGeometry.finished()){
				double location[] = deviceCoord2worldCoord(deviceMouseLocation.x, deviceMouseLocation.y, 0);
				double x =  location[0];
				double y =  location[1];
				gl.glColor3d(0.0, 0.0, 1.0);
				switch (CreateGeometry.getType()) {
				case POINT:
					drawPoint2D(new Point2D(x,y));
					break;
				case LINE:
					Line2D line = (Line2D) CreateGeometry.getGeometry();
					line.setP1(x, y);
					drawPoint2D(line.getP0());
					drawPoint2D(line.getP1());
					drawLine2D(line);
					
					line.setP1(null);
					break;
				case POLYLINE:
					Polyline2D polyline = (Polyline2D) CreateGeometry.getGeometry();
					polyline.addPoint(x,y);
					drawPointList(polyline.getListPoint());
					drawPolyline2D(polyline);
					polyline.remove(polyline.size()-1);
					break;
				case RECTANGLE:
					Rectangle2D rect = (Rectangle2D) CreateGeometry.getGeometry();
					rect.setP1(x, y);
					drawPoint2D(rect.getP0());
					drawPoint2D(rect.getP1());
					drawPoint2D(rect.getQ0());
					drawPoint2D(rect.getQ1());
					drawRectangle(rect);
					rect.setP1(null);
					break;
				case POLYGON:
					Polygon2D poly = (Polygon2D) CreateGeometry.getGeometry();
					drawPolygon2D(poly);
					drawPointList(poly.getPointList());
					drawPoint2D( new Point2D(x, y));
					drawLine2D(new Line2D(poly.getLastPoint(), new Point2D(x,y)));
					break;
				case CIRCLE:
					Circle2D circle2d = (Circle2D) CreateGeometry.getGeometry();
					circle2d.setRadius(circle2d.getCenterPoint().distanceTo(x, y));
					drawCircle2D(circle2d);
					drawLine2D(new Line2D(circle2d.getCenterPoint(), new Point2D(x, y)));
					circle2d.setRadius(-1);
					break;
				case BEZIER:
					BezierCurve2D bezier = (BezierCurve2D) CreateGeometry.getGeometry();
					bezier.addPoint(x, y);
					drawBezierCurve(bezier);
					bezier.removePoint(bezier.sizePointList()-1);
					break;
				case ARC:
					Arc2D arc = (Arc2D) CreateGeometry.getGeometry();
					if(arc.getRadius() == -1){
						gl.glColor4f(0.5f, 0.5f, 0.5f, 0.5f);
						drawLine2D(new Line2D(arc.getCenterPoint().getX(),arc.getCenterPoint().getY() ,x,y));
						
					}else{
						arc.setEndPoint(x, y);
						double xx = arc.getCenterPoint().getX() + arc.getRadius()*Math.cos(Math.toRadians(arc.getEndAngle()));
						double yy = arc.getCenterPoint().getY() + arc.getRadius()*Math.sin(Math.toRadians(arc.getEndAngle()));
						arc.setEndPoint((float)xx, (float)yy);
						drawLine2D(new Line2D(arc.getCenterPoint(), arc.getStartPoint()));
						drawLine2D(new Line2D(arc.getCenterPoint(), arc.getEndPoint()));
						drawArc2D(arc);
						arc.setEndAngle(-1);
					}
					break;
				default:
					break;
				}
			}
		}
		
		
	}

	private void pan(){
		if(getToggleButtonByName("TGLBTN_PAN") == null) return;
			if(getToggleButtonByName("TGLBTN_PAN").isSelected() && isLeftButtonPressed && pointPan1 != null && pointPan2 != null){
				double p0[] = deviceCoord2worldCoord(pointPan1.x, pointPan1.y, 0);
				double p1[] = deviceCoord2worldCoord(pointPan2.x, pointPan2.y, 0);
			
				double dx = p0[0] - p1[0];
				double dy = p0[1] - p1[1];
				left += dx;
				right += dx;
				bottom += dy;
				top += dy;
				
				pointPan1 = pointPan2;
			}
	}
	private void zoom(){
		if(getToggleButtonByName("TGLBTN_ZOOM_IN").isSelected() || getToggleButtonByName("TGLBTN_ZOOM_OUT").isSelected()){

			if(initialZoomPoint == null && deviceMouseClickLocation != null){
				initialZoomPoint = deviceCoord2worldCoord(deviceMouseClickLocation.x, deviceMouseClickLocation.y, -1);
				deviceMouseClickLocation = null;
			}else{
				if(endZoomPoint == null &&  deviceMouseClickLocation != null){
					endZoomPoint = deviceCoord2worldCoord(deviceMouseClickLocation.x, deviceMouseClickLocation.y, 0);
				
					double xMin = Math.min(initialZoomPoint[0], endZoomPoint[0]);
					double xMax = Math.max(initialZoomPoint[0], endZoomPoint[0]);
					double yMin = Math.min(initialZoomPoint[1], endZoomPoint[1]);
					double yMax = Math.max(initialZoomPoint[1], endZoomPoint[1]);
					double w  = Math.abs(xMax - xMin);
					double h  = Math.abs(yMax - yMin);
					
					double xMed = (xMax + xMin)/2, yMed = (yMax + yMin)/2;
					double rectArea = w*h;
					double l = Math.sqrt(rectArea);
					
					if(getToggleButtonByName("TGLBTN_ZOOM_IN").isSelected()){
					
						left = (float) (xMed-(l/2));
						right = (float) (xMed+(l/2));
						bottom = (float) (yMed-(l/2));
						top = (float) (yMed+(l/2));
						

					}
					if(getToggleButtonByName("TGLBTN_ZOOM_OUT").isSelected()){
					
						left-=(l/2);
						right+=(l/2);
						bottom-=(l/2);
						top+=(l/2);
						
					}	
					
					Geometry2D.EPSILON = (float) (w*0.05);
					initialZoomPoint = null;
					endZoomPoint = null;
					deviceMouseClickLocation = null;
				}
			
			}
			
			if(initialZoomPoint!= null && endZoomPoint == null && deviceMouseLocation != null){
					double[] cur = deviceCoord2worldCoord(deviceMouseLocation.x, deviceMouseLocation.y,0);
					gl.glColor3f(0.0f, 0.0f, 0.0f);
					gl.glLineWidth(2.0f);
					gl.glBegin(GL2.GL_LINE_LOOP);
						gl.glVertex2d(initialZoomPoint[0], initialZoomPoint[1]);
						gl.glVertex2d(cur[0], initialZoomPoint[1]);
						gl.glVertex2d(cur[0], cur[1]);
						gl.glVertex2d(initialZoomPoint[0], cur[1]);
					gl.glEnd();
			}
				
				
		}
		
	}

	
	private void selectGeometry() {
		if(SelectGeometry.started()){
			if(pointPan2 == null){
				if(deviceMouseClickLocation == null) return;
				double click[]  = deviceCoord2worldCoord(deviceMouseClickLocation.x, deviceMouseClickLocation.y, 0);
				deviceMouseClickLocation = null;
				SelectGeometry.selectGeometryNear((float)click[0], (float)click[1]);
				
			}else{
				if(initialZoomPoint == null && pointPan1 != null){
					initialZoomPoint = deviceCoord2worldCoord(pointPan1.x, pointPan1.y, 0);
				}
				if(pointPan1 != null){
					endZoomPoint = deviceCoord2worldCoord(pointPan2.x, pointPan2.y, 0);
					gl.glColor3f(0.0f, 1.0f, 0.0f);
					gl.glLineWidth(2.0f);
					gl.glBegin(GL2.GL_LINE_LOOP);
						gl.glVertex2d(initialZoomPoint[0], initialZoomPoint[1]);
						gl.glVertex2d(endZoomPoint[0], initialZoomPoint[1]);
						gl.glVertex2d(endZoomPoint[0], endZoomPoint[1]);
						gl.glVertex2d(initialZoomPoint[0], endZoomPoint[1]);
					gl.glEnd();
				}
			}
			
		}	
	}

	public void mouseReleased(MouseEvent e) {
		switch (e.getButton()) {
			case MouseEvent.BUTTON1:
				if(SelectGeometry.started()){
						deviceMouseClickLocation = e.getPoint();
						if(pointPan2 != null){
							Point2D p0 = new Point2D((float)initialZoomPoint[0],(float) initialZoomPoint[1]);
							Point2D p1 = new Point2D((float)endZoomPoint[0], (float)endZoomPoint[1]);
							SelectGeometry.selectByClipArea(new Rectangle2D(p0, p1));
							pointPan1 = null;
							pointPan2 = null;
							initialZoomPoint = null;
							endZoomPoint = null;
							deviceMouseClickLocation =null;
						}
				}
				if(CreateGeometry.started()){
					deviceMouseClickLocation = e.getPoint();
				}
				if(IntersectGeometry.started()){
					deviceMouseClickLocation = e.getPoint();
				}	
				if(getToggleButtonByName("TGLBTN_PAN").isSelected() && isLeftButtonPressed ){
					pointPan1 = null;
					pointPan2 = null;
					deviceMouseClickLocation = null;
				}	
				if(getToggleButtonByName("TGLBTN_ZOOM_IN").isSelected() || getToggleButtonByName("TGLBTN_ZOOM_OUT").isSelected() ){
					deviceMouseClickLocation = e.getPoint();
				}

				isLeftButtonPressed = false;
			break;
		case MouseEvent.BUTTON2:
			 deviceMouseClickLocation = null;

			 initialZoomPoint = null;endZoomPoint = null;
			 pointPan1 = null;pointPan2 = null;
 
			break;

		default:
			break;
		}
	}
	public void mouseEntered(MouseEvent e) { 
		
		}
	public void mouseExited(MouseEvent e) { deviceMouseClickLocation = null; deviceMouseLocation = null;}
	public void mouseMoved(MouseEvent e) {
		deviceMouseLocation = e.getPoint();
		}
	public void mouseClicked(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {
		if(!isInside) return;
		if(e.getButton() == MouseEvent.BUTTON1){
			 if(getToggleButtonByName("TGLBTN_PAN").isSelected()){
				 	pointPan1 = e.getPoint();
				 	pointPan2 = e.getPoint();
				 	isLeftButtonPressed = true;
			 }
			 if(SelectGeometry.started()){
				 pointPan1 = e.getPoint();
				 pointPan2 = null;
				 endZoomPoint = null;
				 initialZoomPoint = null;
				 isLeftButtonPressed = true;
			 }
		 }
	}
	
	public void mouseDragged(MouseEvent e) { 
		if(!isInside) return;
			if( getToggleButtonByName("TGLBTN_PAN").isSelected() && isLeftButtonPressed){
					pointPan2 = e.getPoint();
			}
				 if(pointPan1 != null && SelectGeometry.started() && isLeftButtonPressed ){
					 int dx = e.getX() - pointPan1.x;
					 int dy = e.getY() - pointPan1.y;
					 int dip= (int) Math.sqrt(dx*dx+dy*dy);
					 if( dip >=10 ){
						 pointPan2 = e.getPoint();
					 }
				 }
			
			
		}
			
		
	public ToggleButton getToggleButtonByName(String name){
		return (ToggleButton) scene.getRoot().lookup("#" + name);
	}
	
	public double[] getGrayScaleColor(double factor){	
		double[] color = {factor,factor,factor};
		return color;
	}
	public double[] getRainbowColor(double factor){
		
		double red = 0;
		double green = 0;
		double blue = 0;

		double color[] = {red,green,blue};
		return color;
	}
 	public double[] getColdToHotColor(double factor){
		double x = factor;
		double red = 0;
		if(x >= 0.75){
			red = 1;
		}else if(x >= 0.5 && x <= 0.75){
			red = (x-0.5)/0.25;
		}else if(x >= 0 && x <= 0.5){
			red = 0;
		}
		
		double green = 0;
		if(x >=0 &&  x<=0.25){
			green = (x/0.25);
		}else if(x >= 0.25  && x <= 0.75 ){
			green = 1;
		}else if(x >= 0.75 && x <= 1){
			green =  (1 - 4*(x - 0.75));
		}
		
		double blue = 0;
		if(x >=0 &&  x <= 0.25){
			blue  = 1;
		}else if(x >=0.25 && x <= 0.5){
				blue = 1 - 4*(x-0.25);
			
		}else if(x >= 0.5 && x <= 1 ){
			blue = 0;
		}
		
		double[] color = {red,green,blue};
		return color;
	}


	public void drawColorBar(double min,double max,int tickNumber){
		
		gl.glMatrixMode(GL2.GL_VIEWPORT);
		gl.glLoadIdentity();
		//gl.glViewport(width-98 - VIEWPORT[0], 80, 50, height-80- VIEWPORT[1]);
		gl.glViewport(width-98, 80, 50, height-80);
		
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		glu.gluOrtho2D(0, 1, 0, 1);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
		
		double[] wPoint1 = {0,0};
		double[] wPoint2 = {0.5,0.7};
		double factor = (wPoint2[1] - wPoint1[1])/4;
		
		double color[] = getColdToHotColor(0);

		gl.glBegin(GL2.GL_QUADS);
		
			//first
			gl.glColor3d(color[0], color[1], color[2]);
			gl.glVertex2d(wPoint1[0], wPoint1[1]);
			gl.glVertex2d(wPoint2[0], wPoint1[1]);
			
			color = getColdToHotColor(0.25);
			gl.glColor3d(color[0], color[1], color[2]);
			gl.glVertex2d(wPoint2[0],  wPoint1[1]+factor);
			gl.glVertex2d(wPoint1[0],   wPoint1[1]+factor);	
			
			//second
			gl.glColor3d(color[0], color[1], color[2]);
			gl.glVertex2d(wPoint1[0], wPoint1[1]+ factor);
			gl.glVertex2d(wPoint2[0], wPoint1[1]+ factor);
		
			color = getColdToHotColor(0.5);
			gl.glColor3d(color[0], color[1], color[2]);
			gl.glVertex2d(wPoint2[0], wPoint1[1]+ 2*factor);
			gl.glVertex2d(wPoint1[0], wPoint1[1]+ 2*factor);	
			
			
			//thrid
			gl.glColor3d(color[0], color[1], color[2]);
			gl.glVertex2d(wPoint1[0], wPoint1[1]+ 2*factor);
			gl.glVertex2d(wPoint2[0], wPoint1[1]+ 2*factor);
		
			color = getColdToHotColor(0.75);
			gl.glColor3d(color[0], color[1], color[2]);
			gl.glVertex2d(wPoint2[0], wPoint1[1]+ 3*factor);
			gl.glVertex2d(wPoint1[0], wPoint1[1]+ 3*factor);
			
			
			//fourth
			gl.glColor3d(color[0], color[1], color[2]);
			gl.glVertex2d(wPoint1[0], wPoint1[1]+ 3*factor);
			gl.glVertex2d(wPoint2[0], wPoint1[1]+ 3*factor);
		
			color = getColdToHotColor(1);
			gl.glColor3d(color[0], color[1], color[2]);
			gl.glVertex2d(wPoint2[0], wPoint2[1]);
			gl.glVertex2d(wPoint1[0], wPoint2[1]);
			
		gl.glEnd();
		double r = 1,g = 1,b = 1;

		if(VIEWPORT[2] == width && VIEWPORT[3] == height){
			r = g = b = 0;
		}
		
		gl.glColor3d(r, g, b);
		gl.glLineWidth(1.25f);
		gl.glBegin(GL2.GL_LINE_STRIP);
			gl.glVertex2d(wPoint1[0], wPoint1[1]);
			gl.glVertex2d(wPoint2[0], wPoint1[1]);
			gl.glVertex2d(wPoint2[0], wPoint2[1]);
			gl.glVertex2d(wPoint1[0], wPoint2[1]);
			gl.glVertex2d(wPoint1[0], wPoint1[1]);
		gl.glEnd();
		
		double dx = (wPoint2[0] - wPoint1[0])*0.25;
		double dy = (wPoint2[1] - wPoint1[1])/tickNumber;

		
		
		for (int i = 1; i < tickNumber; i++) {
			drawLine2D(new Line2D((float)wPoint1[0],(float) (wPoint1[1]+i*dy),(float)( wPoint1[0] + dx),(float) (wPoint1[1]+i*dy)), 1.25f, 0, 0, 0);
			drawLine2D(new Line2D((float)wPoint2[0], (float)(wPoint1[1]+i*dy), (float)(wPoint2[0] - dx), (float)(wPoint1[1]+i*dy)), 1.25f, 0, 0, 0);
			
			gl.glColor3d(r, g, b);
			gl.glRasterPos2d(0.6, wPoint1[1]+i*dy);
			glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12,String.format("%.3E",min + ((double)i/tickNumber)*(max - min)));		
		}

		gl.glColor3d(r, g, b);
		gl.glRasterPos2d(0.6, wPoint1[1]);
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12,String.format("%.3E",min));
		
		gl.glColor3d(r, g, b);
		gl.glRasterPos2d(0.61, wPoint2[1]);
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12,String.format("%.3E",max));
		
		myReshape();
	}

	
	private void drawArrowCoors(){
		
		gl.glMatrixMode(GL2.GL_VIEWPORT);
		gl.glLoadIdentity();
		gl.glViewport(0,0, 50, 50);

	
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		glu.gluOrtho2D(0, 1, 0, 1);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
			
		double[] origin = {0.2,0.2};
		double[] lastVPoint = {0.2,0.8};
		double[] lastHPoint = {0.8,0.2};
		
		gl.glColor3d(1, 0, 0);
		gl.glRasterPos2d(0.65, 0.35);
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12,"X");
		
		gl.glColor3d(0, 1, 0);
		gl.glRasterPos2d(0.3,0.70);
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12,"Y");
		
		gl.glEnable(GL2.GL_LINE_SMOOTH );
		gl.glEnable(GL.GL_BLEND );
		gl.glBlendFunc(GL.GL_SRC_ALPHA,GL.GL_ONE_MINUS_SRC_ALPHA );
		gl.glLineWidth(1.5f);
		gl.glBegin(GL2.GL_LINES);
			gl.glColor3d(1, 0, 0);
			gl.glVertex2d(origin[0], origin[1]);
			gl.glVertex2d(lastHPoint[0], lastHPoint[1]);

			gl.glColor3d(0, 1, 0);
			gl.glVertex2d(origin[0], origin[1]);
			gl.glVertex2d(lastVPoint[0], lastVPoint[1]);
			
		gl.glEnd();
		gl.glLineWidth(1.0f);
		gl.glDisable(GL2.GL_LINE_SMOOTH );
		gl.glDisable(GL.GL_BLEND );
		
		gl.glEnable(GL2.GL_POLYGON_SMOOTH );
		gl.glEnable(GL.GL_BLEND );
		gl.glBlendFunc(GL.GL_SRC_ALPHA,GL.GL_ONE_MINUS_SRC_ALPHA );
		gl.glColor3d(1, 0, 0);
		gl.glBegin(GL2.GL_POLYGON);
			gl.glVertex2d(0.85, 0.2);
			gl.glVertex2d(0.6, 0.3);
			gl.glVertex2d(0.6, 0.1);
		
		gl.glEnd();
		gl.glColor3d(0, 1, 0);
		gl.glBegin(GL2.GL_POLYGON);
			gl.glVertex2d(0.2,0.85 );
			gl.glVertex2d( 0.3,0.6);
			gl.glVertex2d(0.1,0.6);
		gl.glEnd();
		gl.glColor3d(0, 0, 0);
		gl.glDisable(GL2.GL_POLYGON_SMOOTH );
		gl.glDisable(GL.GL_BLEND );
		
		myReshape();
	

	}
	
	public void drawCoordinates(){
		
		if(deviceMouseLocation != null && isInside){
			worldMouseLocation = deviceCoord2worldCoord(deviceMouseLocation.x, deviceMouseLocation.y, 0);
			
			gl.glMatrixMode(GL2.GL_VIEWPORT);
			gl.glLoadIdentity();
			//gl.glViewport(VIEWPORT[0], height-50 - VIEWPORT[1], 50, 50);
			gl.glViewport(0, height-50, 50, 50);
			gl.glMatrixMode(GL2.GL_PROJECTION);
			gl.glLoadIdentity();
			glu.gluOrtho2D(0, 1, 0, 1);
			gl.glMatrixMode(GL2.GL_MODELVIEW);
			gl.glLoadIdentity();
			
			gl.glColor3f(1, 0, 0);
			gl.glRasterPos2d(0.15, 0.75);
			
			glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, "X:" + String.format("%.5f",worldMouseLocation[0]));
			gl.glColor3f(0, 1, 0);
			gl.glRasterPos2d(0.15, 0.50);
		
			glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, "Y:" + String.format("%.5f", worldMouseLocation[1]));
			gl.glColor3f(0, 0, 1);
		}else{
			gl.glMatrixMode(GL2.GL_VIEWPORT);
			gl.glLoadIdentity();
			//gl.glViewport(VIEWPORT[0], height-50 - VIEWPORT[1], 50, 50);
			gl.glViewport(0, height-50, 50, 50);

			gl.glMatrixMode(GL2.GL_PROJECTION);
			gl.glLoadIdentity();
			glu.gluOrtho2D(0, 1, 0, 1);
			gl.glMatrixMode(GL2.GL_MODELVIEW);
			gl.glLoadIdentity();
			
			gl.glColor3f(1, 0, 0);
			gl.glRasterPos2d(0.2, 0.75);
			glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, "X:OutsideDomain");
			gl.glColor3f(0, 1, 0);
			gl.glRasterPos2d(0.2, 0.50);
			glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, "Y:OutSideDomain ");
			gl.glColor3f(0, 0, 1);
		}
		
		myReshape();
	
	}
	
	private void drawFillRect(double x,double y,double w,double h,double r,double g,double b ){
		gl.glColor3d(r, g, b);
		
		gl.glBegin(GL2.GL_POLYGON);
			gl.glVertex2d(x, y);
			gl.glVertex2d(x+w, y);
			gl.glVertex2d(x+w, y+h);
			gl.glVertex2d(x, y+h);
		gl.glEnd();
	}
	
	private void drawMaterialFaces(){
		
		if(!Project.isMaterialVisible()) return;
		gl.glMatrixMode(GL2.GL_VIEWPORT);
		gl.glLoadIdentity();
		//gl.glViewport(VIEWPORT[0], height-50 - VIEWPORT[1], 50, 50);
		gl.glViewport(0, height/2, 50, 50);
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		glu.gluOrtho2D(0, 1, 0, 1);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
		
		double x = 0.1,  y = 0.0;
		double L =0.15 ;
		float[] color;
		
		List<Face2D> faces = Project.getFaceList();
		List<Material> materials = new ArrayList<>();
		for (int i = 0; i < faces.size(); i++) {
			if(i == 5) break;
			Face2D face2d  = faces.get(i);
			if(face2d.getMaterial() != null && face2d.getTypeFace() != Face2D.HOLE && !materials.contains(face2d.getMaterial())){
				color = face2d.getMaterial().getColor();
				drawFillRect(x, y, L, L,color[0] , color[1], color[2]);
				if(VIEWPORT[2] == width && VIEWPORT[3] == height){
					gl.glColor3f(0, 0, 0);
				}else{
					gl.glColor3f(1, 1, 1);
				}
				gl.glRasterPos2d(L+0.15, y);
				glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, face2d.getMaterial().getName());
				y+=(L+0.05);
				materials.add(face2d.getMaterial());
			}
		}
		gl.glMatrixMode(GL2.GL_VIEWPORT);
		gl.glLoadIdentity();
		//gl.glViewport(VIEWPORT[0], height-50 - VIEWPORT[1], 50, 50);
		gl.glViewport(51, height/2, 50, 50);
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		glu.gluOrtho2D(0, 1, 0, 1);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
		
		x = 0.1;
		y = 0.0;
		for (int i = 4; i < faces.size(); i++) {
			Face2D face2d  = faces.get(i);
			if(face2d.getMaterial() != null && face2d.getTypeFace() != Face2D.HOLE && !materials.contains(face2d.getMaterial())){
				color = face2d.getMaterial().getColor();
				drawFillRect(x, y, L, L,color[0] , color[1], color[2]);
				if(VIEWPORT[0] == width && VIEWPORT[1] == height){
					gl.glColor3f(0, 0, 0);
				}else{
					gl.glColor3f(1, 1, 1);
				}
				gl.glRasterPos2d(L+0.1, y);
				glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, face2d.getMaterial().getName());
				y+=(L+0.05);
				materials.add(face2d.getMaterial());
			}
		}
		
		myReshape();
		
	}

	
	private void drawGrid(int n){
			double dx = (right-left)/n;
			double dy = (top-bottom)/n;
		
			double curY = bottom+dy;
			double curX = left+dx;
			
			gl.glEnable(GL.GL_BLEND);
			gl.glDepthMask(true);
			gl.glBlendFunc(GL.GL_SRC_ALPHA,GL.GL_ONE_MINUS_SRC_ALPHA);
			gl.glLineWidth(0.25f);
			gl.glEnable(GL2.GL_LINE_STIPPLE);
			gl.glLineStipple(1, (short) 0xAAAA);
			gl.glColor3d(0.5, 0.5, 0.5);
			gl.glBegin(GL2.GL_LINES);
			for (int i = 1; i < n; i++) {
				gl.glColor3d(0.5, 0.5, 0.5);
				gl.glVertex2d(left, curY);
				gl.glVertex2d(right, curY);
				gl.glVertex2d(curX,bottom);
				gl.glVertex2d(curX, top);
				
				
				
				curY+=dy;
				curX+=dx;
			}
			gl.glEnd();
			gl.glDisable(GL.GL_BLEND);
			gl.glDisable(GL2.GL_LINE_STIPPLE);
			gl.glDepthMask(false);
			
			
	} 



	
}
