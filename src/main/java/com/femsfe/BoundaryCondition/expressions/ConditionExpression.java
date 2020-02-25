package com.femsfe.BoundaryCondition.expressions;

import com.femsfe.enums.BoundaryConditionType;

public abstract class ConditionExpression {

    private BoundaryConditionType type;

    public void setType(BoundaryConditionType type) {
        this.type = type;
    }

    public BoundaryConditionType getType() {
        return this.type;
    }
}
