package com.femsfe.BoundaryCondition.value;

import com.femsfe.ComplexNumber.ComplexNumber;
import com.femsfe.Geometries.Line2D;
import com.femsfe.enums.PortType;

public class SimpleBCValue extends ConditionValue {

    public int index0 = -1;
    public int index1 = -1;
    public PortType portType;
    public ComplexNumber value0;
    public ComplexNumber value1;

    public SimpleBCValue(int index0, int index1, ComplexNumber incidentValue0, ComplexNumber incidentValue1, PortType portType) {
        this.index0 = index0;
        this.index1 = index1;
        this.value0 = incidentValue0;
        this.value1 = incidentValue1;
        this.portType = portType;
    }

    public void swapByEdge(Line2D line2d) {
        if (line2d.getP0().getIndex() != index0 && line2d.getP1().getIndex() != index1) {
            int auxIndex = index0;
            index0 = index1;
            index1 = auxIndex;

            ComplexNumber auxValue = value0;
            value0 = value1;
            value1 = auxValue;
        }
    }

    public int[] getIndexes() {
        int[] indexes = {index0, index1};
        return indexes;
    }

    public ComplexNumber[] getIncidentValues() {
        ComplexNumber[] values = {value0, value1};
        return values;
    }
}
