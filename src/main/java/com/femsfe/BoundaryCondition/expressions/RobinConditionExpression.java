package com.femsfe.BoundaryCondition.expressions;

import com.femsfe.enums.BoundaryConditionType;
import com.femsfe.ComplexNumber.ComplexNumber;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class RobinConditionExpression extends ConditionExpression {

    private String qExpression = "";
    private String yExpression = " ";

    public RobinConditionExpression() {
        // TODO Auto-generated constructor stub
        setType(BoundaryConditionType.ROBIN);
    }

    public RobinConditionExpression(String qExpression, String yExpression) {
        // TODO Auto-generated constructor stub
        setType(BoundaryConditionType.ROBIN);
        this.qExpression = qExpression;
        this.yExpression = yExpression;
    }

    public void setQExpression(String qExpression) {
        this.qExpression = qExpression;
    }

    public void setYExpression(String gamaEpresion) {
        this.yExpression = gamaEpresion;
    }

    public String getQExpression() {
        return this.qExpression;
    }

    public String getYExpression() {
        return this.yExpression;
    }

    public ComplexNumber getQValue(double x, double y) {
        String[] complexComponents = ComplexNumber.getComplexComponents(getQExpression());
        Expression r = new ExpressionBuilder(complexComponents[0]).variables("x", "y").build();
        r.setVariable("x", x);
        r.setVariable("y", y);
        Expression i = new ExpressionBuilder(complexComponents[1]).variables("x", "y").build();
        i.setVariable("x", x);
        i.setVariable("y", y);
        return new ComplexNumber((float) r.evaluate(), (float) i.evaluate());
    }

    public ComplexNumber getYValue(double x, double y) {
        String[] complexComponents = ComplexNumber.getComplexComponents(getYExpression());
        Expression r = new ExpressionBuilder(complexComponents[0]).variables("x", "y").build();
        r.setVariable("x", x);
        r.setVariable("y", y);
        Expression i = new ExpressionBuilder(complexComponents[1]).variables("x", "y").build();
        i.setVariable("x", x);
        i.setVariable("y", y);
        return new ComplexNumber((float) r.evaluate(), (float) i.evaluate());
    }

}
