package com.femsfe;

import javafx.beans.property.SimpleStringProperty;

public class GeometricProperties {

    private final SimpleStringProperty propertie;
    private final SimpleStringProperty value;

    public GeometricProperties(String propertie, String value) {
        this.propertie = new SimpleStringProperty(propertie);
        this.value = new SimpleStringProperty(value);

    }

    public String getPropertie() {
        return propertie.get();
    }

    public String getValue() {
        return value.get();
    }

    public void setPropertie(String propertie) {
        this.propertie.set(propertie);
    }

    public void setValue(String value) {
        this.value.set(value);
    }

}
