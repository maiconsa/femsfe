package com.femsfe.BoundaryCondition.value;

import com.femsfe.enums.BoundaryConditionType;
import com.femsfe.ComplexNumber.ComplexNumber;

public class DirichletConditionValue extends ConditionValue {

    private ComplexNumber value;
    private int pointIndex = -1;

    public DirichletConditionValue(int pointIndex, ComplexNumber value) {
        setType(BoundaryConditionType.DIRICHLET);
        setPointIndex(pointIndex);
        setValue(value);
    }

    public void setValue(ComplexNumber value) {
        this.value = value;
    }

    public ComplexNumber getValue() {
        return this.value;
    }

    public void setPointIndex(int index) {
        this.pointIndex = index;
    }

    public int getPointIndex() {
        return pointIndex;
    }

}
