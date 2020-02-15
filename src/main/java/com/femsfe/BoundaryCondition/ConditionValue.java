package com.femsfe.BoundaryCondition;

public abstract class ConditionValue {
	private BoundaryConditionType type;
	
	public void setType(BoundaryConditionType type){
		this.type = type;
	}
	
	public BoundaryConditionType getType(){
		return type;
	}

}
