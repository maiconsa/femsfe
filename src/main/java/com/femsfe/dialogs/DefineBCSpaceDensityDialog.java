package com.femsfe.dialogs;

import com.femsfe.enums.ProblemType;
import com.femsfe.App;
import com.femsfe.Project;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class DefineBCSpaceDensityDialog extends TextInputDialog {

    public DefineBCSpaceDensityDialog(ProblemType type) {
        this.setTitle("Dialog for Assign Boundary Condition on Domain ");
        this.getEditor().setText("0.0");
        switch (Project.getProblemType()) {
            case ELETROSTATIC:
                this.setGraphic(new ImageView(new Image(App.class.getResourceAsStream("icon/poisson_equation_eletro.png"))));
                this.setHeaderText("Assign  Space Charge Density ,\n\u03C1(x,y)  ");
                this.setContentText("Please enter your  space charge density value(C/m^2):");
                break;
            case MAGNETOSTATIC_VECTOR_POTENTIAL:
                this.setGraphic(new ImageView(new Image(App.class.getResourceAsStream("icon/equation_magne.png"))));
                this.setHeaderText("Assign Component Z \nof Current Density,Jz(x,y)  ");
                this.setContentText("Please enter your current density value(A/m^2):");
                break;
            default:
                break;
        }

        this.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            Node okBtn = (Node) this.getDialogPane().lookupButton(ButtonType.OK);
            okBtn.setDisable(newValue.trim().isEmpty());
        });
    }

}
