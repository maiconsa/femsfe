package com.femsfe;


import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;

import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

public class RobinBoundaryConditionDialog  extends Dialog<Pair<String, String>>{

	public RobinBoundaryConditionDialog() {

		this.setTitle("Thrid Kind Boundary Dialog");
		this.setHeaderText("Assig Thrid Kind Boundary Condition ,\n \u2207\u03D5 = q(x,y) - \u03B3(x,y) \u03D5");
		//this.setGraphic(new ImageView(this.getClass().getResource("login.png").toString()));	
		ButtonType applyButtonType = new ButtonType("Apply", ButtonData.OK_DONE);
		this.getDialogPane().getButtonTypes().addAll(applyButtonType, ButtonType.CANCEL);
		
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));
		
		TextField q = new TextField();
		q.setPromptText("0.0");
		TextField gama = new TextField();
		gama.setPromptText("0.0");

		grid.add(new Label("q(x,y):"), 0, 0);
		grid.add(q, 1, 0);
		grid.add(new Label("\u03B3(x,y):"), 0, 1);
		grid.add(gama, 1, 1);

		
		Node applyButton = this.getDialogPane().lookupButton(applyButtonType);
		
		q.textProperty().addListener((observable, oldValue, newValue) -> {
			if(gama.getText().trim().isEmpty() || newValue.trim().isEmpty() ){
				applyButton.setDisable(true);
			}else{
				applyButton.setDisable(false);
			}
		});
		gama.textProperty().addListener((observable, oldValue, newValue) -> {
			if(q.getText().trim().isEmpty() || newValue.trim().isEmpty() ){
				applyButton.setDisable(true);
			}else{
				applyButton.setDisable(false);
			}
		});

		
		

		this.getDialogPane().setContent(grid);

		// Request focus on the username field by default.
		Platform.runLater(() -> q.requestFocus());

		// Convert the result to a username-password-pair when the login button is clicked.
		this.setResultConverter(dialogButton -> {
		    if (dialogButton == applyButtonType) {
		        return new Pair<>(q.getText(), gama.getText());
		    }
		    return null;
		});
	}
	


}
