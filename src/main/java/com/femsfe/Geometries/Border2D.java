package com.femsfe.Geometries;
import com.femsfe.enums.PortType;
import java.util.ArrayList;
import java.util.List;

import com.femsfe.enums.BoundaryConditionType;
import com.femsfe.BoundaryCondition.expressions.ConditionExpression;


public  abstract class Border2D extends Geometry2D {
	private BoundaryConditionType boundaryConditionType = BoundaryConditionType.NONE;
	private PortType portType = PortType.NONE;
	private ConditionExpression conditionExpression;
	protected List<Point2D> chewPoints;
	public int numberLinkedFaces = 0;
	public int indexForSave = -1;
	
	public void addChewPoint(Point2D point2d){
		chewPoints.add(point2d);
	}

	public Border2D() {
		chewPoints = new ArrayList<>(0);
	}
	public boolean isSBC(){
		return boundaryConditionType == BoundaryConditionType.SBC;
	}
	public boolean isPort(){
		if(isSBC()){
			return portType != PortType.NONE;
		}else{
			return false;
		}
	}

	public boolean isInputPort(){
		if(isPort()){
			return portType == PortType.INPUT;
		}else{
			return false;
		}
	}
	public boolean isOutputPort(){
		if(isPort()){
			return portType == PortType.OUTPUT;
		}else{
			return false;
		}
	}
	public void setPortType(PortType portType){
		this.portType = portType;
	}
	public PortType getPortType(){
		return portType;
	}
	public List<Point2D> getChewPoints(){
		return chewPoints;
	}
	public BoundaryConditionType getBoundaryConditionType(){
		return boundaryConditionType;
	}
	public boolean isBoundaryCondition() {
		return boundaryConditionType != BoundaryConditionType.NONE;
	}

	public void setBoundaryConditionType(BoundaryConditionType boundaryConditionType) {
		this.boundaryConditionType = boundaryConditionType;
	}
	public boolean isDirichletCondition(){
		return boundaryConditionType == BoundaryConditionType.DIRICHLET;
	}
	public boolean isRobinCondition(){
		return boundaryConditionType == BoundaryConditionType.ROBIN;
	}

	public void setConditionExpression(ConditionExpression conditionExpression){
		this.conditionExpression = conditionExpression;
	}
	public ConditionExpression getConditionExpression(){
		return this.conditionExpression;
	}
	
	public void linkBorderToPoint(){
		Point2D[] endEpoint = getEndpoints();
		endEpoint[0].linkedBorders.add(this);
		endEpoint[1].linkedBorders.add(this);
	}
	public	List<Border2D> getLinkedBorders( ){
		Point2D[] endEpoint = getEndpoints();
		System.out.println("p0->borderSize->"+endEpoint[0].linkedBorders.size());
		System.out.println("p1->borderSize->"+endEpoint[1].linkedBorders.size());
		ArrayList<Border2D> borders = new ArrayList<Border2D>(); 
		for (Border2D border2d : endEpoint[0].linkedBorders) {
			borders.add(border2d);
		}
		
		borders.remove(this);
		for (Border2D border2d : endEpoint[1].linkedBorders) {
			borders.add(border2d);
		}
		borders.remove(this);
		return borders;
	}
	
	public boolean checkLinked(){
		if(getLinkedBorders().size()  == 2){
			return true;
		}else{
			return false;
		}
	}
	@Override
	public void setVisible(boolean status) {
		// TODO Auto-generated method stub
		super.setVisible(status);
		Point2D[] endEpoint = getEndpoints();
		endEpoint[0].setVisible(status);
		endEpoint[1].setVisible(status);
	}
	
	public void clearLinkedBorders(){
		Point2D[] endEpoint = getEndpoints();
		endEpoint[0].linkedBorders.clear();
		endEpoint[1].linkedBorders.clear();;
	}

	
	public abstract List<Line2D> chewSegmentation(double hmin );
	public abstract Point2D[] getEndpoints();
	public abstract double getLength();

}
