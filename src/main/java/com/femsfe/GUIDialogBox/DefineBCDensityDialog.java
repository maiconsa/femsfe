package com.femsfe.GUIDialogBox;

import com.femsfe.Analysis.ProblemType;
import com.femsfe.Project;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class DefineBCDensityDialog extends TextInputDialog {

	public DefineBCDensityDialog(ProblemType type) {
		this.setTitle("Dialog for Assign Boundary Condition ");
		this.getEditor().setText("0.0");
		switch (Project.getProblemType()) {
			case ELETROSTATIC:
				this.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/resources/icon/BC_surface_charge_density.png"))));
				this.setHeaderText("Assign Charge Density ,\n\u03C3(x,y)  ");
				this.setContentText("Please enter your  charge density value(C/m^2):");
				
				break;
			case MAGNETOSTATIC_VECTOR_POTENTIAL:
				this.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/resources/icon/BC_surface_current_density.png"))));
				this.setHeaderText("Assign Current,Kz(x,y)  ");
				this.setContentText("Please enter your current value(A/m):");
				break;
			case MAGNETOSTATIC_SCALAR_POTENTIAL:
				this.setHeaderText("Assign Magnetic Scalar Potential ,\n\u03D5= Vm(x,y)  ");
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
