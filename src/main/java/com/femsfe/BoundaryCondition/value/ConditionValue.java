package com.femsfe.BoundaryCondition.value;

import com.femsfe.enums.BoundaryConditionType;

public abstract class ConditionValue {

    private BoundaryConditionType type;

    public void setType(BoundaryConditionType type) {
        this.type = type;
    }

    public BoundaryConditionType getType() {
        return type;
    }

}
