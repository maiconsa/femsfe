package com.femsfe.GUIDialogBox;


import com.femsfe.Triangulacoes.Triangulation;
import javafx.concurrent.Task;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class ProgressMeshDialog extends Dialog<Boolean> {
	ProgressIndicator progressIndicator;
	Triangulation triangulation;
	Task<Boolean> task;
	
	public ProgressMeshDialog(Triangulation triangulation) {
		progressIndicator = new ProgressIndicator();
		this.triangulation = triangulation;
		
		this.setTitle("Progress Mesh Generation");
		Stage stage = (Stage) this.getDialogPane().getScene().getWindow();
		stage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/icon/meshing_32x32.png")));
		
		ButtonType okBtn = new ButtonType("OK", ButtonData.OK_DONE);
		this.getDialogPane().getButtonTypes().addAll(okBtn, ButtonType.CANCEL);
		
		this.getDialogPane().setContent(progressIndicator);
		
		
		
		this.setResultConverter( btn -> {
			if(btn == okBtn){
				return true;
			}else{
				triangulation.stop();
				stop();
			}
			return null;
		});
		
	}
	

	
	public void start(){
		task  = createTask();
		
		progressIndicator.progressProperty().unbind();
		progressIndicator.progressProperty().bind(task.progressProperty());
		
			Thread taskThread = new Thread(task);
			taskThread.start();
		
	}
	private void stop(){
		if(!triangulation.isEnd()){
			triangulation.stop();
			triangulation.clear();
		}
		progressIndicator.progressProperty().unbind();
		progressIndicator.setProgress(10);
		
		task.cancel(true);
	}
	
	private Task<Boolean> createTask(){
		return new Task<Boolean>() {

			@Override
			protected Boolean call() throws Exception {

				if(triangulation.isEnd() == true){
					
					updateProgress(1, 1);
					updateMessage("Gera��o de Malha Conclu�da!!!!");
				}
				
				return true;
			}
			
		};
	}
	

}