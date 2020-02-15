package com.femsfe.BoundaryCondition;

import com.femsfe.ComplexNumber.ComplexNumber;

public class RobinConditionValue  extends ConditionValue{
	//INDICES DO PONTO NA MALHA
	private int pointIndex0 = -1;
	private int pointIndex1 = -1;
	

	private ComplexNumber qValue;
	private ComplexNumber yValue;
	
	public RobinConditionValue(int pointIndex0,int pointIndex1,ComplexNumber qValue,ComplexNumber yValue) {
		setType(BoundaryConditionType.ROBIN);
		setPointIndex0(pointIndex0);
		setPointIndex1(pointIndex1);
		setqValue(qValue);
		setyValue(yValue);
	}
	
	public int getPointIndex0() {
		return pointIndex0;
	}

	public void setPointIndex0(int pointIndex0) {
		this.pointIndex0 = pointIndex0;
	}

	public int getPointIndex1() {
		return pointIndex1;
	}

	public void setPointIndex1(int pointIndex1) {
		this.pointIndex1 = pointIndex1;
	}

	public ComplexNumber getqValue() {
		return qValue;
	}

	public void setqValue(ComplexNumber qValue) {
		this.qValue = qValue;
	}

	public ComplexNumber getyValue() {
		return yValue;
	}

	public void setyValue(ComplexNumber yValue) {
		this.yValue = yValue;
	}


}
