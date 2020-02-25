package com.femsfe.BoundaryCondition.expressions;

import com.femsfe.enums.BoundaryConditionType;
import com.femsfe.ComplexNumber.ComplexNumber;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class DirichletConditionExpression extends ConditionExpression {

    private String expression = "";

    public DirichletConditionExpression(String expression) {
        // TODO Auto-generated constructor stub
        setType(BoundaryConditionType.DIRICHLET);
        this.expression = expression;

    }

    public DirichletConditionExpression() {
        // TODO Auto-generated constructor stub
        setType(BoundaryConditionType.DIRICHLET);
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getExpression() {
        return this.expression;
    }

    public ComplexNumber getValue(double x, double y) {
        String[] components = ComplexNumber.getComplexComponents(expression);
        Expression real = new ExpressionBuilder(components[0])
                .variables("x", "y")
                .build()
                .setVariable("x", x)
                .setVariable("y", y);
        Expression img = new ExpressionBuilder(components[1])
                .variables("x", "y")
                .build()
                .setVariable("x", x)
                .setVariable("y", y);
        return new ComplexNumber((float) real.evaluate(), (float) img.evaluate());
    }

}
