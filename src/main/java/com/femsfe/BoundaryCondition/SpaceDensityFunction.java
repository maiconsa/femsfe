package com.femsfe.BoundaryCondition;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class SpaceDensityFunction {

	private String function;
	public SpaceDensityFunction(String function) {
		this.function = function;
	}
	public double getValue(double x,double y){
		if(function.isEmpty() || function == null) return Double.NaN;
		 Expression ex = new ExpressionBuilder(function).variables("x", "y").build();
		 	ex.setVariable("x", x);
		 	ex.setVariable("y", y);
		 	return ex.evaluate();
	}

}
