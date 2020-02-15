package com.femsfe.GUIDialogBox;

import com.femsfe.Analysis.ProblemType;
import com.femsfe.Project;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;

public class DefineBCPotentialDialog extends TextInputDialog {

	public DefineBCPotentialDialog(ProblemType type) {
		this.setTitle("Dialog for Assign Boundary Condition ");
		this.getEditor().setText("0.0");
		switch (Project.getProblemType()) {
			case ELETROSTATIC:
				this.setHeaderText("Assign Eletric Potential,\n\u03D5= V(x,y)  ");
				this.setContentText("Please enter your boundary value(V):");
				break;
			case MAGNETOSTATIC_VECTOR_POTENTIAL:
				this.setHeaderText("Assign Component Z of Magnetic Vector Potential,\n\u03D5= Az(x,y)  ");
				this.setContentText("Please enter your boundary value(Wb/m):");
				break;
			case MAGNETOSTATIC_SCALAR_POTENTIAL:
				this.setHeaderText("Assign Magnetic Scalar Potential ,\n\u03D5= Vm(x,y)  ");
				this.setContentText("Please enter your boundary value(A):");
				break;
			case HELMHOLTZ_TE_MODE:
				this.setHeaderText("Assign Component Z of Eletric Field,\n\u03D5= Ez(x,y)  ");
				break;
			case HELMHOLTZ_TM_MODE:
				this.setHeaderText("Assign Component Z of Magnetic Field,\n\u03D5= Hz(x,y)  ");
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
