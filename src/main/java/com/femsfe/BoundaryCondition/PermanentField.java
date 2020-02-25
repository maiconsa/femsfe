package com.femsfe.BoundaryCondition;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class PermanentField {

    private final String xComponent;
    private final String yComponent;

    public PermanentField(String xComponent, String yComponent) {
        this.xComponent = xComponent;
        this.yComponent = yComponent;
    }

    public double getXValue(double x, double y) {
        if (xComponent.isEmpty() || xComponent == null) {
            return Double.NaN;
        }
        Expression ex = new ExpressionBuilder(xComponent).variables("x", "y").build();
        ex.setVariable("x", x);
        ex.setVariable("y", y);
        return ex.evaluate();
    }

    public double getYValue(double x, double y) {
        if (xComponent.isEmpty() || yComponent == null) {
            return Double.NaN;
        }
        Expression ex = new ExpressionBuilder(yComponent).variables("x", "y").build();
        ex.setVariable("x", x);
        ex.setVariable("y", y);
        return ex.evaluate();
    }

}
