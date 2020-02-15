package com.femsfe;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.femsfe.Geometrias.*;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import javafx.util.Pair;

public final class TreeViewProject extends TreeView<GeometryLabelItem> {
	private TreeItem<GeometryLabelItem> root ;
	private TreeItem<GeometryLabelItem> rootProbType;
	private TreeItem<GeometryLabelItem> rootPoints;
	private TreeItem<GeometryLabelItem> rootLines;
	private TreeItem<GeometryLabelItem> rootArcs;
	private TreeItem<GeometryLabelItem> rootBeziers;
	private TreeItem<GeometryLabelItem> rootFaces;
	private TreeItem<GeometryLabelItem> rootCircles;


	@SuppressWarnings("unchecked")
	public TreeViewProject( ) {

	root = new TreeItem<>(new GeometryLabelItem(null, "Project Hierarchy"),new ImageView(new Image(getClass().getResource("icon/repository_20x20.png").toExternalForm())) );
	rootProbType = new TreeItem<>(new GeometryLabelItem(null, "Problem Type"),new ImageView(new Image(getClass().getResource("icon/prob_type_16x16.png").toExternalForm())));
	rootPoints = new TreeItem<>(new GeometryLabelItem(null, "Points"),new ImageView(new Image(getClass().getResource("icon/geometry_16x16.png").toExternalForm())));
	rootLines = new TreeItem<>(new GeometryLabelItem(null, "Lines"),new ImageView(new Image(getClass().getResource("icon/geometry_16x16.png").toExternalForm())));
	rootArcs = new TreeItem<>(new GeometryLabelItem(null, "Arcs"),new ImageView(new Image(getClass().getResource("icon/geometry_16x16.png").toExternalForm())));
	rootBeziers = new TreeItem<>(new GeometryLabelItem(null, "Bezier"),new ImageView(new Image(getClass().getResource("icon/geometry_16x16.png").toExternalForm())));
	rootFaces = new TreeItem<>(new GeometryLabelItem(null, "Faces"),new ImageView(new Image(getClass().getResource("icon/geometry_16x16.png").toExternalForm())));
	rootCircles = new TreeItem<>(new GeometryLabelItem(null,  "Circles"),new ImageView(new Image(getClass().getResource("icon/geometry_16x16.png").toExternalForm())));
	

		
		this.setRoot(root);
		root.setExpanded(true);
		root.getChildren().addAll(rootProbType,rootPoints,rootLines,rootArcs,rootBeziers,rootCircles,rootFaces);
		
		this.getSelectionModel().selectedItemProperty().addListener(new TreeViewChangeListener());
		
		
		updateProbleType();
		updatePointsItems();
		updateLinesItems();
		updateArcsItems();
		updateCirclesItems();
		updateBezierItems();
		updateFacesItems();

		
	}
	public void update(){
		updateProbleType();
		updatePointsItems();
		updateLinesItems();
		updateArcsItems();
		updateCirclesItems();
		updateFacesItems();
		updateBezierItems();
	}
	public void updateProbleType(){
		TreeItem<GeometryLabelItem> type = new TreeItem<GeometryLabelItem>();
		TreeItem<GeometryLabelItem> variable = new TreeItem<GeometryLabelItem>();
		List<TreeItem<GeometryLabelItem>> items = new ArrayList<>();
		URL url;
		items.add(type);
		items.add(variable);
		type.setValue(new GeometryLabelItem(null, "Type: "+ Project.getProblemType().toString()));
		switch (Project.getProblemType()) {
			case ELETROSTATIC:
				url = getClass().getResource("icon/eletrostatic_16x16.png");
				type.setGraphic(new ImageView(new Image(url.toExternalForm())));
				variable.setValue(new GeometryLabelItem(null, "Variable: Eletric Potencial(V)"));
				break;
			case MAGNETOSTATIC_VECTOR_POTENTIAL:
				url = getClass().getResource("/resources/icon/magnetostatic_16x16.png");
				type.setGraphic(new ImageView(new Image(url.toExternalForm())));
				variable.setValue(new GeometryLabelItem(null, "Variable: Magnetic Vector Potencial(A)"));

				break;
			case MAGNETOSTATIC_SCALAR_POTENTIAL:
				url = getClass().getResource("/resources/icon/magnetostatic_16x16.png");
				type.setGraphic(new ImageView(new Image(url.toExternalForm())));
				variable.setValue(new GeometryLabelItem(null, "Variable: Magnetic Scalar Potencial(A)"));

				break;
			case HELMHOLTZ_TE_MODE:
				url = getClass().getResource("/resources/icon/wave_16x16.png");
				type.setGraphic(new ImageView(new Image(url.toExternalForm())));
				variable.setValue(new GeometryLabelItem(null, "Variable: Eletric Field (Ez)" ));
				
				TreeItem<GeometryLabelItem> wavelenght1 = new TreeItem<GeometryLabelItem>();
				Wavelenght wave1 = Project.getWavelenght();
				wavelenght1.setValue(new GeometryLabelItem(null,"Wavelegnth: "+wave1.getValue()+wave1.getUnit().getNotation()));
				items.add(wavelenght1);
				
				break;
			case HELMHOLTZ_TM_MODE:
				url = getClass().getResource("/resources/icon/wave_16x16.png");
				type.setGraphic(new ImageView(new Image(url.toExternalForm())));
				variable.setValue(new GeometryLabelItem(null, "Variable: Magnetic Field (Hz)" ));
				
				TreeItem<GeometryLabelItem> wavelenght2 = new TreeItem<GeometryLabelItem>();
				Wavelenght wave2 = Project.getWavelenght();
				wavelenght2.setValue(new GeometryLabelItem(null,"Wavelegnth: "+wave2.getValue()+wave2.getUnit().getNotation()));
				items.add(wavelenght2);
				break;
			default:
				
				break;
		}
		
		
		
	
		rootProbType.getChildren().setAll(items);
	
		
		
	}
	
	public void updatePointsItems(){
		TreeItem<GeometryLabelItem> treeItem = new TreeItem<GeometryLabelItem>();
		List<Point2D> points = Project.getPointList();
		List<TreeItem<GeometryLabelItem>> items = new ArrayList<>();
		for (int i = 0; i < points.size(); i++) {
			points.get(i).setID(i);
			treeItem  = new TreeItem<GeometryLabelItem>();
			treeItem.setValue(new GeometryLabelItem(points.get(i)," Point " + i + ": ("+ points.get(i).getX() + "," + points.get(i).getY() + ")"));
			treeItem.setGraphic(new ImageView(new Image(getClass().getResource("/resources/icon/point_16x16.png").toExternalForm())));
			items.add(treeItem);
		}
		rootPoints.getChildren().setAll(items);
	}
	public void updateLinesItems(){
			TreeItem<GeometryLabelItem> lineItem,pointItem;
			List<Line2D> lines = Project.getLineList();
			List<TreeItem<GeometryLabelItem>> items = new ArrayList<>();
			for (int i = 0; i < lines.size(); i++) {
				Line2D line = lines.get(i);
				line.setID(i);
			lineItem  = new TreeItem<GeometryLabelItem>(new GeometryLabelItem(line, "Line " + i),new ImageView(new Image(getClass().getResource("/resources/icon/line_16x16.png").toExternalForm())) );
				pointItem  = new TreeItem<GeometryLabelItem>(new GeometryLabelItem(line.getP0()," Point " + 0 + ": ("+ line.getP0().getX() + "," + line.getP0().getY() + ")"),
						new ImageView(new Image(getClass().getResource("/resources/icon/point_16x16.png").toExternalForm())) );
				lineItem.getChildren().add(pointItem);
				pointItem  = new TreeItem<GeometryLabelItem>(new GeometryLabelItem(line.getP1()," Point " + 1 + ": ("+ line.getP1().getX() + "," + line.getP1().getY() + ")"),
						new ImageView(new Image(getClass().getResource("/resources/icon/point_16x16.png").toExternalForm())) );
				lineItem.getChildren().add(pointItem);
				items.add(lineItem);
			}
			rootLines.getChildren().setAll(items);
	
		
	}
	public void updateArcsItems(){

		TreeItem<GeometryLabelItem> arcItem,pointItem;
		List<Arc2D> arcs = Project.getArcList();
		List<TreeItem<GeometryLabelItem>> items = new ArrayList<>();
		
		for (int i = 0; i < arcs.size(); i++) {
			Arc2D arc = arcs.get(i);
			arc.setID(i);
			arcItem  = new TreeItem<GeometryLabelItem>(new GeometryLabelItem(arc," Arc "+ i) ,new ImageView(new Image(getClass().getResourceAsStream("/resources/icon/arc_16x16.png"))) );
			pointItem  = new TreeItem<GeometryLabelItem>(new GeometryLabelItem(arc.getStartPoint()," Start Point: ("+ arc.getStartPoint().getX() + "," + arc.getStartPoint().getY() + ")"),
								new ImageView(new Image(getClass().getResource("/resources/icon/point_16x16.png").toExternalForm())));
			arcItem.getChildren().add(pointItem);
			pointItem  = new TreeItem<GeometryLabelItem>(new GeometryLabelItem(arc.getEndPoint()," End Point: ("+ arc.getEndPoint().getX() + "," + arc.getEndPoint().getY() + ")"),
					new ImageView(new Image(getClass().getResource("/resources/icon/point_16x16.png").toExternalForm())));
			arcItem.getChildren().add(pointItem);
			items.add(arcItem);
		}
		rootArcs.getChildren().setAll(items);
	}
	public void updateBezierItems(){
		TreeItem<GeometryLabelItem> bezierItem,pointItem;
		List<TreeItem<GeometryLabelItem>> items = new ArrayList<>();
		List<BezierCurve2D> beziers = Project.getBezierList();
		for (int i = 0; i < beziers.size(); i++) {
			BezierCurve2D bezier = beziers.get(i);
			bezier.setID(i);
			bezierItem  = new TreeItem<GeometryLabelItem>(new GeometryLabelItem(bezier," Bezier "+ i) ,new ImageView(new Image(getClass().getResourceAsStream("/resources/icon/bezier_curve_16x16.png"))) );
			for (Point2D point2d : bezier.getControlPoints()) {
				pointItem = new TreeItem<GeometryLabelItem>(
						new GeometryLabelItem(point2d," Control Point : ("+ point2d.getX() + "," + point2d.getY() + ")"),
						new ImageView(new Image(getClass().getResource("/resources/icon/point_16x16.png").toExternalForm()))
						);
				bezierItem.getChildren().add(pointItem);
			}
			items.add(bezierItem);
		}
		rootBeziers.getChildren().setAll(items);
	}
	public void updateCirclesItems(){
		TreeItem<GeometryLabelItem> circleItem,item;
		List<Circle2D> circles = Project.getCircleList();
		List<TreeItem<GeometryLabelItem>> items = new ArrayList<>();
		for (int i = 0; i < circles.size(); i++) {
			Circle2D circle = circles.get(i);
			circle.setID(i);
			circleItem  = new TreeItem<GeometryLabelItem>(new GeometryLabelItem(circle," Circle " + i),new ImageView(new Image(getClass().getResourceAsStream("/resources/icon/circle_16x16.png"))) );
			item  = new TreeItem<GeometryLabelItem>(new GeometryLabelItem(null," Radius:" + circle.getRadius()));
			circleItem.getChildren().add(item);
			item  = new TreeItem<GeometryLabelItem>(new GeometryLabelItem(null," Center Point : ("+ circle.getCenterPoint().getX() + "," +circle.getCenterPoint().getY() + ")"),new ImageView(new Image(getClass().getResourceAsStream("/resources/icon/point_16x16.png"))) );
			circleItem.getChildren().add(item);
			
			items.add(circleItem);
			
		}
		rootCircles.getChildren().setAll(items);
	}
	public void updateFacesItems(){
		TreeItem<GeometryLabelItem> faceItem,materialItem;
		List<Face2D> faces = Project.getFaceList();
		List<TreeItem<GeometryLabelItem>> items = new ArrayList<>();
		
		for (int i = 0; i < faces.size(); i++) {
			Face2D face = faces.get(i);
			face.setID(i);
			String type = "";
			switch (face.getTypeFace()) {
				case Face2D.NORMAL:
					type = "NORMAL";
					break;
				case Face2D.HOLE:
					type = "HOLE";
					break;
				case Face2D.EXTERNAL:
					type = "EXTERNAL";
					break;
				default:
					break;
			}
			faceItem  = new TreeItem<GeometryLabelItem>(new GeometryLabelItem(face," Face " + i + "  - Type: " + type) ,new ImageView(new Image(getClass().getResourceAsStream("/resources/icon/face_16x16.png"))) );
			if(face.getMaterial() != null){
				materialItem = new TreeItem<GeometryLabelItem>(new GeometryLabelItem(null,"Material: " + face.getMaterial().getName()));
			}else{
				materialItem = new TreeItem<GeometryLabelItem>(new GeometryLabelItem(null,"Material: Not Assign"));
			}
				faceItem.getChildren().add(materialItem);
				items.add(faceItem);
			
		}
		rootFaces.getChildren().setAll(items);
	}

	 class TreeViewChangeListener implements ChangeListener<TreeItem<GeometryLabelItem>>{

		@SuppressWarnings("unchecked")
		@Override
		public void changed(ObservableValue<? extends TreeItem<GeometryLabelItem>> observable,
				TreeItem<GeometryLabelItem> oldValue, TreeItem<GeometryLabelItem> newValue) {
			if(oldValue != null){
				Geometry2D old = (Geometry2D) oldValue.getValue().getGeometry();
				if(old != null){
					old.setSelected(false);
				}
			}
			if(newValue == null) return;
			Geometry2D newG =(Geometry2D) newValue.getValue().getGeometry();
			if(newG != null){
				newG.setSelected(true);
			}
			
			@SuppressWarnings("rawtypes")
			TableView table =   (TableView) App.scene.getRoot().lookup("#propertiesTable");
			
			@SuppressWarnings("rawtypes")
			TableColumn propCol =  (TableColumn) table.getColumns().get(0);
						propCol.setEditable(false);
						propCol.setSortable(false);	
						propCol.setMinWidth(100);
				
			@SuppressWarnings("rawtypes")
			TableColumn valCol = (TableColumn) table.getColumns().get(1);
						valCol.setMinWidth(100);
						valCol.setSortable(false);	
			propCol.setCellValueFactory(new PairKeyFactory());
			valCol.setCellValueFactory(new PairValueFactory());	
			valCol.setCellFactory(new Callback<TableColumn<Pair<String, Object>, Object>, TableCell<Pair<String, Object>, Object>>() {
		            @Override
		            public TableCell<Pair<String, Object>, Object> call(TableColumn<Pair<String, Object>, Object> column) {
		                return new PairValueCell();
		            }
		        });
			
			
			List<Pair<String, Object>> list = new ArrayList<>();;
			  ObservableList<Pair<String, Object>> data;
			   	
		    	if(newValue == root){
		    		list.add(new Pair<String, Object>("Point Size", Project.pointSize()));
		    		list.add(new Pair<String, Object>("Line Size", Project.lineSize()));
		    		list.add(new Pair<String, Object>("Arc Size", Project.arcSize()));
		    		list.add(new Pair<String, Object>("Circle Size", Project.circleSize()));
		    		list.add(new Pair<String, Object>("Face Size", Project.faceSize()));
		    		data = FXCollections.observableArrayList(list);
					
					table.setItems(data);;
		    	} 
		    	if(newG == null) return; 
		    	CheckBox checkVisibility = new CheckBox();
			    if(newG.isVisible()){
			    	checkVisibility.setText("True");
			    }else{
			    	checkVisibility.setText("False");
			    }
			    checkVisibility.setSelected(newG.isVisible());
			    checkVisibility.setOnMouseClicked(new EventHandler<MouseEvent>() {

					@Override
					public void handle(MouseEvent event) {
						newG.setVisible(checkVisibility.isSelected());
						if(checkVisibility.isSelected()){
							checkVisibility.setText("True");
						}else{
							checkVisibility.setText("False");
						}
					}
				});
			
			
			  
			  
			
			switch (newG.getType()) {
				case POINT:
					Point2D point = (Point2D) newG;
					list.clear();
					list.add(new Pair<String, Object>("Geometry Type", "POINT"));
					list.add(new Pair<String, Object>("Geometry ID", newG.getID()));
					list.add(new Pair<String, Object>("X", point.getX() ));
					list.add(new Pair<String, Object>("Y", point.getY() ));
					checkVisibility.setSelected(newG.isVisible());
					list.add(new Pair<String, Object>("Visible",checkVisibility));
					
					data = FXCollections.observableArrayList(list);
					
					table.setItems(data);;
					
				
					break;
				case LINE:
					Line2D line = (Line2D) newG;
					list.clear();
					list.add(new Pair<String, Object>("Geometry Type", "LINE"));
					list.add(new Pair<String, Object>("Geometry ID", newG.getID()));
					list.add(new Pair<String, Object>("X0", line.getX0()));
					list.add(new Pair<String, Object>("Y0", line.getY0()));
					list.add(new Pair<String, Object>("X1", line.getX1()));
					list.add(new Pair<String, Object>("Y1", line.getY1()) );
					list.add(new Pair<String, Object>("Visible",checkVisibility));
					list.add(new Pair<String, Object>("Lenght", line.getLength()));
					data = FXCollections.observableArrayList(list);
					table.setItems(data);;
					break;
				case CIRCLE:
					Circle2D circle2d = (Circle2D) newG;
					list.clear();
					list.add(new Pair<String, Object>("Geometry Type", "CIRCLE"));
					list.add(new Pair<String, Object>("Geometry ID", newG.getID()));
					list.add(new Pair<String, Object>("Center Point", null));
					list.add(new Pair<String, Object>("X0", circle2d.getX0()));
					list.add(new Pair<String, Object>("Y0", circle2d.getY0()));
					list.add(new Pair<String, Object>("Radius ", circle2d.getRadius()));
					list.add(new Pair<String, Object>("Visible",checkVisibility));
					 data = FXCollections.observableArrayList(list);
					
					table.setItems(data);;
					break;
				case ARC:
					Arc2D arc = (Arc2D) newG;
					list.clear();
					list.add(new Pair<String, Object>("Geometry Type", "CIRCLE"));
					list.add(new Pair<String, Object>("Geometry ID", newG.getID()));
					list.add(new Pair<String, Object>("X0", arc.getCenterPoint().getX()));
					list.add(new Pair<String, Object>("Y0", arc.getCenterPoint().getY()));
					list.add(new Pair<String, Object>("Start Angle", arc.getStartAngle()));
					list.add(new Pair<String, Object>("End Angle", arc.getEndAngle()));
					list.add(new Pair<String, Object>("Radius ", arc.getRadius()));
					list.add(new Pair<String, Object>("Visible",checkVisibility));
					 data = FXCollections.observableArrayList(list);
					
					table.setItems(data);;
					break;
				case FACE:
					Face2D face = (Face2D) newG;
					ObservableList<String > obList = FXCollections.observableArrayList(new String("EXTERNAL"),new String("NORMAL"),new String("HOLE"));
					ComboBox<String> cb = new ComboBox<>(obList);
					
					
					cb.prefWidthProperty().bind(valCol.widthProperty());
					cb.getSelectionModel().select(face.getTypeFace());
					if(face.getTypeFace() == Face2D.EXTERNAL){
						cb.setDisable(true);
					}
					cb.setOnAction(new EventHandler<ActionEvent>() {

						@Override
						public void handle(ActionEvent event) {
							face.setTypeFace(cb.getSelectionModel().getSelectedIndex());
							int index = cb.getSelectionModel().getSelectedIndex();
							if(index == 1){
								face.setMaterial(null);
							}
							updateFacesItems();
							
						}
					});
					
					ObservableList<Material> materialList = FXCollections.observableArrayList(RepositoryMaterials.getList());
					ComboBox<Material> cbMaterial = new ComboBox<>(materialList);
					cbMaterial.prefWidthProperty().bind(valCol.widthProperty());
					cbMaterial.setCellFactory(new Callback<ListView<Material>, ListCell<Material>>() {
						@Override
						public ListCell<Material> call(ListView<Material> param) {
							return new ListCell<Material>(){
								
								@Override
								protected void updateItem(Material item, boolean empty) {
									super.updateItem(item, empty);
									if(item == null) return;
										setText(item.getName());
									
								}	
							};
						}
					});
					
					cbMaterial.getSelectionModel().select(face.getMaterial());
					cbMaterial.setOnAction(new EventHandler<ActionEvent>() {

						@Override
						public void handle(ActionEvent event) {
							face.setMaterial(cbMaterial.getSelectionModel().getSelectedItem());
							updateFacesItems();
							
						}
					});
					cbMaterial.setButtonCell(new ListCell<Material>() {
			          @Override
			          protected void updateItem(Material item, boolean empty) {
			        	// TODO Auto-generated method stub
			        	super.updateItem(item, empty);
			        	if(item == null) return;
						setText(item.getName());
					
			        }
			        });

					
					list = new ArrayList<>();
					list.add(new Pair<String, Object>("Geometry Type", "FACE"));
					list.add(new Pair<String, Object>("Geometry ID", newG.getID()));
					list.add(new Pair<String, Object>("Face Type", cb));
					list.add(new Pair<String, Object>("Material", cbMaterial));
					list.add(new Pair<String, Object>("Visible",checkVisibility));
					 data = FXCollections.observableArrayList(list);
					
					table.setItems(data);;
					break;
				default:
						break;
			}
		}
		
		
	}


}







