package com.femsfe.GUIDialogBox;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

import javafx.scene.control.Label;


import java.util.ArrayList;


import com.femsfe.Geometrias.Line2D;
import com.femsfe.Geometrias.Point2D;
import com.femsfe.Geometrias.Triangle2D;
import com.femsfe.Triangulacoes.ConectivityList;
import com.femsfe.Triangulacoes.Triangulation;
import com.femsfe.NumberTextField;
import javafx.beans.value.ObservableValue;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;

import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;


import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class LinePlotResultDialog extends Dialog<LineChart<Number, Number>>{
	private VBox main;
	private NumberTextField x0,y0,x1,y1,numeroDivisao;
	private ConectivityList mesh;
	private ArrayList<Point2D> pts;
	private ArrayList<Double > values;
	@SuppressWarnings("static-access")
	public LinePlotResultDialog(ConectivityList mesh) {
		this.mesh  = mesh;
		this.setResizable(true);
		this.setTitle("Plotando resultado sobre uma linha");
		Stage stage = (Stage) this.getDialogPane().getScene().getWindow();
		stage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/icon/line_chart_32x32.png")));
		
		
		
		
	
		 main  = new VBox(5);
		GridPane grid = new GridPane();
		grid.setVgap(5);grid.setHgap(5);
		
		Label pontos = new Label("Pontos");
		Label x = new Label("X");
		Label y = new Label("Y");
		
		grid.add(pontos, 0, 0);
		grid.add(x, 1, 0);
		grid.add(y, 2, 0);
		
		grid.setHalignment(pontos, HPos.CENTER);
		grid.setHalignment(x, HPos.CENTER);
		grid.setHalignment(y, HPos.CENTER);
		
		ButtonType plotButtonType = new ButtonType("Plot", ButtonData.OK_DONE);
		this.getDialogPane().getButtonTypes().addAll(plotButtonType, ButtonType.CANCEL);
		
		
		Node nodeBtn = getDialogPane().lookupButton(plotButtonType);
		 x0 = new NumberTextField(0.0);
		 x0.textProperty().addListener(new NumericChangeListener(nodeBtn));
		 y0 = new NumberTextField(0.0);
		 y0.textProperty().addListener(new NumericChangeListener(nodeBtn));
		 x1 = new NumberTextField(0.0);
		 x1.textProperty().addListener(new NumericChangeListener(nodeBtn));
		 y1 = new NumberTextField(0.0);
		 y1.textProperty().addListener(new NumericChangeListener(nodeBtn));
		
		grid.add(new Label("Inicio"), 0, 1);
		grid.add(x0, 1, 1);
		grid.add(y0, 2, 1);
		
		grid.add(new Label("Fim"), 0, 2);
		grid.add(x1, 1, 2);
		grid.add(y1, 2, 2);
		
		grid.add(new Label("N� Div"), 0, 3);
		numeroDivisao = new NumberTextField(25);
		numeroDivisao.textProperty().addListener(new NumericChangeListener(nodeBtn));
		
		grid.add(numeroDivisao, 1, 3);
	
		main.getChildren().add(grid);
		
		
		this.getDialogPane().setContent(main);
		
		this.setResultConverter(dialogButton ->{
			if(dialogButton == plotButtonType ){
				Line2D line = getLine();
				 pts = line.divisionLineInPoint(getNumDiv());
				return getLineChart(pts);
			}
			return null;
		});
	
		
	}
	private Line2D getLine(){
		float x0 = this.x0.getFloatValue();
		float y0 = this.y0.getFloatValue();
		float x1 = this.x1.getFloatValue();
		float y1 = this.y1.getFloatValue();
		return new Line2D(x0, y0, x1, y1);
	}
	private int getNumDiv(){
		return (int) this.numeroDivisao.getFloatValue();
	}
	
	public ArrayList<Point2D> getPoints(){
		return pts;
	}
	public ArrayList<Double> getValues(){
		return values;
	}

	private ArrayList<Double> getValuesOnMesh(ArrayList<Point2D> pts){	
		Triangle2D triangle2d = null;
		double x,y;
		
		ArrayList<Double> values = new ArrayList<>(pts.size());
		
		for (Point2D point2d : pts) {
			triangle2d = Triangulation.findTriangleWithPointInside(mesh.getElements(), point2d);
			if(triangle2d != null){
				x = point2d.getX(); 
				y = point2d.getY();
				double value  = triangle2d.getValueInsideElement(x,y);
				values.add(value);
			}else{
				values.add(Double.NaN);
			}
		}
		return values;
		
	}
	private LineChart<Number,Number> getLineChart(ArrayList<Point2D> pts){
		 values = getValuesOnMesh(pts);
		
		final NumberAxis xAxis  = new NumberAxis("Comprimento Param�trico", 0, 1, 0.2);
		final NumberAxis yAxis  = new NumberAxis();
		yAxis.setAutoRanging(true);
		yAxis.setLabel("Valor");
		yAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(yAxis){
			@Override
			public String toString(Number object) {
				return String.format("%7.3e", object);
			}
		});
		
		LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
		
	
		Point2D firstPoint = pts.get(0);
		Point2D lastPoint  = pts.get(pts.size()-1);
		double d = lastPoint.distanceTo(firstPoint);
		
		Series<Number, Number> series = new Series<>();
		for (int i = 0; i < pts.size(); i++) {
			if(values.get(i).isNaN()){
				continue;
			}else{
				Double xV = pts.get(i).distanceTo(firstPoint)/d;
				Double yV = values.get(i);
				series.getData().add(new Data<Number,Number>(xV,yV));
			}
		}
		series.setName("Valores Num�ricos ");

		lineChart.getData().add(series);
		
		return lineChart;
		
		
	}
	
	public float getX0(){
		return this.x0.getFloatValue();
	}
	public float getX1(){
		return this.x1.getFloatValue();
	}
	public float getY0(){
		return this.y0.getFloatValue();
	}
	public float getY1(){
		return this.y1.getFloatValue();
	}
	public int getNumDivision(){
		return (int) this.numeroDivisao.getFloatValue();
	}
	

	

}


class NumericChangeListener implements javafx.beans.value.ChangeListener<String>{
	Node node;
	public NumericChangeListener(Node node) {
		this.node = node;
	}
	@Override
	public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
		// TODO Auto-generated method stub
		if(newValue.isEmpty()){
			node.setDisable(true);
		}else{
			if(newValue.matches("^[0-9]{0,2}.[0-9]{0,5}")){
				node.setDisable(false);
			}else{
				node.setDisable(true);
			}
		}
	}
	
}


