package com.femsfe.dialogs;

import com.femsfe.enums.ProblemType;
import com.femsfe.App;
import com.femsfe.Project;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class DefineProblemTypeDialog extends Dialog<ProblemType> {

	public DefineProblemTypeDialog() {
		Stage stage = (Stage) this.getDialogPane().getScene().getWindow();
		
		stage.getIcons().add(new Image(App.class.getResourceAsStream("icon/new_material_32x32.png")));
		this.setTitle("Problem Type");
		this.setHeaderText("Assign Problem Type to Project");
		GridPane grid = new GridPane();
		grid.setHgap(5);
		grid.setVgap(5);
		grid.add(new Label("Problem Type"), 0, 0);
		
	
		ComboBox<ProblemType> comboProbType = new ComboBox<>();
		comboProbType.setPrefWidth(200);
		ProblemType[] types = ProblemType.values();
		for (int i = 0; i < types.length; i++) {
			comboProbType.getItems().add(types[i]);
		}
		comboProbType.getSelectionModel().select(Project.problemType);
		grid.add(comboProbType, 1, 0);

		this.getDialogPane().setContent(grid);
		
		ButtonType okButtonType = new ButtonType("OK", ButtonData.OK_DONE);
		this.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);
		
		this.setResultConverter(dialogButton -> {
		    if (dialogButton == okButtonType) {
		    	return comboProbType.getValue();
		    }
		    return null;
		});
	}

}
