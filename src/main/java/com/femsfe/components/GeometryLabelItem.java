package com.femsfe.components;

import com.femsfe.Geometries.Geometry2D;
import javafx.scene.control.Label;

public class GeometryLabelItem extends Label {

    private Geometry2D geometry2d;

    public GeometryLabelItem(String name) {
         this.setText(name);
    }

    public GeometryLabelItem(Geometry2D geometry, String name) {
        this.geometry2d = geometry;
        this.setText(name);
    }

    public Geometry2D getGeometry() {
        return geometry2d;
    }

}
