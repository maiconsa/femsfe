module femsfe {
    /*requires java.desktop;*/
    requires jblas;
    requires exp4j;

    requires jogl.all;
 
    requires javafx.controls;
    requires javafx.swing;
    requires javafx.fxml;
    requires javafx.graphics;
    opens com.femsfe to javafx.fxml;
    
    opens com.femsfe.controllers;
    exports com.femsfe;
}
