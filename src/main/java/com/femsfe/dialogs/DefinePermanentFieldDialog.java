package com.femsfe.dialogs;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar.ButtonData;
import com.femsfe.enums.ProblemType;
import com.femsfe.App;
import com.femsfe.BoundaryCondition.PermanentField;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class DefinePermanentFieldDialog extends Dialog<PermanentField> {

	public DefinePermanentFieldDialog(ProblemType problemType) {
		this.setResizable(false);
		this.setTitle("Define Field Component");
		Stage stage = (Stage) this.getDialogPane().getScene().getWindow();
		stage.getIcons().add(new Image(App.class.getResourceAsStream("icon/line_chart_32x32.png")));
		
		ButtonType okBtn = new ButtonType("OK", ButtonData.OK_DONE);
		this.getDialogPane().getButtonTypes().addAll(okBtn, ButtonType.CANCEL);
		
		
		TextField xComponent = new TextField("0.0");
		TextField yComponent = new TextField("0.0");
		
		xComponent.textProperty().addListener((observable, oldValue, newValue) ->{
			Node ok = this.getDialogPane().lookupButton(okBtn);
			if(yComponent.getText().trim().isEmpty() ||
			   newValue.trim().isEmpty() ){
				ok.setDisable(true);
			}else{
				ok.setDisable(false);
			}
		});
		yComponent.textProperty().addListener((observable, oldValue, newValue) ->{
			Node ok = this.getDialogPane().lookupButton(okBtn);
			if(xComponent.getText().trim().isEmpty() ||
			   newValue.trim().isEmpty() ){
				ok.setDisable(true);
			}else{
				ok.setDisable(false);
			}
		});
		
	
		
		VBox vBox = new VBox();
		GridPane grid = new GridPane();
		grid.setHgap(5);
		grid.setVgap(5);
		if(problemType == ProblemType.ELETROSTATIC){
			vBox.getChildren().add(new ImageView(new Image(getClass().getResourceAsStream("/resources/icon/polarization_equation.png"))));
			grid.addRow(0, new Label("P0x"),xComponent,new Label("C/m^2"));
			grid.addRow(1, new Label("P0y"),yComponent,new Label("C/m^2"));
		}else{
			vBox.getChildren().add(new ImageView(new Image(getClass().getResourceAsStream("/resources/icon/magnetic_field_permanent_equation.png"))));
			grid.addRow(0, new Label("B0x"),xComponent,new Label("T"));
			grid.addRow(1, new Label("B0y"),yComponent,new Label("T"));
			
		}
		vBox.getChildren().add(grid);
		this.getDialogPane().setContent(vBox);
		
		
		
		this.setResultConverter( btn -> {
			if(btn == okBtn){
				return new PermanentField(xComponent.getText(), yComponent.getText());
			}
			return null;
		});
		
	}

}
