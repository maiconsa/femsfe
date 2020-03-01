package com.femsfe.controllers;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import com.femsfe.enums.ProblemType;
import com.femsfe.App;
import com.femsfe.enums.BoundaryConditionType;
import com.femsfe.BoundaryCondition.expressions.DirichletConditionExpression;
import com.femsfe.BoundaryCondition.PermanentField;
import com.femsfe.BoundaryCondition.expressions.RobinConditionExpression;
import com.femsfe.BoundaryCondition.SpaceDensityFunction;
import com.femsfe.ComplexNumber.ComplexNumber;
import com.femsfe.CreateGeometry;
import com.femsfe.dialogs.DefineBCDensityDialog;
import com.femsfe.dialogs.DefineBCPotentialDialog;
import com.femsfe.dialogs.DefineBCSpaceDensityDialog;
import com.femsfe.dialogs.DefinePermanentFieldDialog;
import com.femsfe.dialogs.DefineProblemTypeDialog;
import com.femsfe.dialogs.LinePlotResultDialog;
import com.femsfe.dialogs.ProgressMeshDialog;
import com.femsfe.Geometries.Arc2D;
import com.femsfe.Geometries.BezierCurve2D;
import com.femsfe.Geometries.Border2D;
import com.femsfe.Geometries.Circle2D;
import com.femsfe.Geometries.Face2D;
import com.femsfe.Geometries.Geometry2D;
import com.femsfe.enums.GeometryType;
import com.femsfe.Geometries.Line2D;
import com.femsfe.Geometries.Material;
import com.femsfe.Geometries.Point2D;
import com.femsfe.Geometries.Polygon2D;
import com.femsfe.Geometries.Polyline2D;
import com.femsfe.Geometries.Triangle2D;
import com.femsfe.IntersectGeometry;
import com.femsfe.components.NumberTextField;
import com.femsfe.enums.Units;
import com.femsfe.PML.PMLRegions;
import com.femsfe.Project;
import com.femsfe.RepositoryMaterials;
import com.femsfe.SelectGeometry;
import com.femsfe.Triangulation.ConectivityList;
import com.femsfe.Triangulation.ConstrainedDelaunayTriangulation;
import com.femsfe.Wavelenght;
import com.femsfe.dialogs.ExceptionDialog;
import com.femsfe.enums.ButtonsID;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;

import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MainController implements Initializable {

    private ButtonsID lastButtonID;
    @FXML
    private Button BTN_CPOINT, BTN_CLINE, BTN_CPOLYLINE, BTN_CRECTANGLE, BTN_CPOLYGON, BTN_CCIRCLE, BTN_CARC, BTN_CBEZIER, BTN_CNFACE, BTN_CBFACE, BTN_CEFACE;

    @FXML
    private Button BTN_LINE_DIVISION, BTN_CIRCLE_DIVISION, BTN_POINTS_DISTANCE;

    @FXML
    private Button BTN_LINES_INTER, BTN_LINES_CIRCLE_INTER, BTN_LINES_ARC_INTER, BTN_CIRCLES_INTER;

    @FXML
    private Button BTN_BC_POTENTIAL, BTN_BC_DENSITY, BTN_BC_SPACE_DENSITY, BTN_BC_PERM_FIELD;
    @FXML
    private CheckBox checkMaterialVisibility;

    @FXML
    private Button btnNewMaterial, btnExportMesh, btnAssignPermanentField;

    @FXML
    private ToggleGroup viewGroup;

    @FXML
    private CheckBox checkViewMesh, checkViewMeshQuality;

    @FXML
    private ComboBox<String> CB_MATERIAL;

    @FXML
    private ToggleButton TGLBTN_ZOOM_IN, TGLBTN_ZOOM_OUT, TGLBTN_PAN, TGLBTN_ISOLINE, TGLBTN_COLORMAP, TGLBTN_ARROW, tglBtnCreatePort;

    @FXML
    private javafx.scene.control.MenuItem menuItemOpenProject, menuItemNewProject, menuItemSaveProject, menuItemSaveProjectAs, menuItemExit, menuItemAssignWavelength;
    @FXML
    private javafx.scene.control.MenuItem menuItemAssignProbType, menuItemDefineUnit;

    public MainController() {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        menuItemOpenProject.setGraphic(new ImageView(new Image(App.class.getResourceAsStream("icon/open_file_16x16.png"))));
        menuItemNewProject.setGraphic(new ImageView(new Image(App.class.getResourceAsStream("icon/new_icon_16x16.png"))));
        menuItemSaveProject.setGraphic(new ImageView(new Image(App.class.getResourceAsStream("icon/save_icon_16x16.png"))));
        menuItemSaveProjectAs.setGraphic(new ImageView(new Image(App.class.getResourceAsStream("icon/save_as_icon_16x16.png"))));
        menuItemExit.setGraphic(new ImageView(new Image(App.class.getResourceAsStream("icon/off_16x16.png"))));

    }

    private void clearViewGroup() {
        if (viewGroup.getSelectedToggle() != null) {
            viewGroup.getSelectedToggle().setSelected(false);
        }
    }

    public void onRemoveGeometry(Event event) {
        CreateGeometry.end();
        javafx.scene.control.MenuItem item = (javafx.scene.control.MenuItem) event.getSource();
        ButtonsID btnID = ButtonsID.valueOf(item.getId());
        lastButtonID = btnID;
        switch (btnID) {
            case BTN_REMOVE_POINTS:
                SelectGeometry.begin(GeometryType.POINT);
                break;
            case BTN_REMOVE_LINES:
                SelectGeometry.begin(GeometryType.LINE);
                break;
            case BTN_REMOVE_ARCS:
                SelectGeometry.begin(GeometryType.ARC);
                break;
            case BTN_REMOVE_CIRCLES:
                SelectGeometry.begin(GeometryType.CIRCLE);
                break;
            case BTN_REMOVE_BEZIER:
                SelectGeometry.begin(GeometryType.BEZIER);
                break;
            case BTN_REMOVE_FACES:
                SelectGeometry.begin(GeometryType.FACE);
                break;
            default:
                break;
        }
        SelectGeometry.toRemove = true;
    }

    public void onLineDivision() {
        resetViewMesh();
        resetViewResult();
        checkMaterialVisibility.setSelected(false);
        Project.setIsMaterialVisible(false);

        CreateGeometry.end();
        IntersectGeometry.end();
        lastButtonID = ButtonsID.BTN_LINE_DIVISION;
        SelectGeometry.begin(GeometryType.LINE);

    }

    public void onCircleDivision() {
        resetViewMesh();
        resetViewResult();
        checkMaterialVisibility.setSelected(false);
        Project.setIsMaterialVisible(false);

        CreateGeometry.end();
        IntersectGeometry.end();
        lastButtonID = ButtonsID.BTN_CIRCLE_DIVISION;
        SelectGeometry.begin(GeometryType.CIRCLE);
    }

    public void onZoomIn() {
        IntersectGeometry.end();
        SelectGeometry.end();
        CreateGeometry.end();
        lastButtonID = null;
    }

    public void onZoomOut() {
        CreateGeometry.end();
        IntersectGeometry.end();
        SelectGeometry.end();
        CreateGeometry.end();
        lastButtonID = null;
    }

    public void onPan() {
        CreateGeometry.end();
        IntersectGeometry.end();
        SelectGeometry.end();
        CreateGeometry.end();
        lastButtonID = null;
    }

    public void onExpand() {
        float[] ortho = Project.getOrtho();
        if (ortho != null) {
            App.getGraphicPanel().setOrtho(ortho[0], ortho[1], ortho[2], ortho[3]);
        }
    }

    public void onNewMaterial() {
        Dialog<String> dialog = new Dialog<>();
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();

        stage.getIcons().add(new Image(App.class.getResourceAsStream("/icon/new_material_32x32.png")));
        dialog.setTitle("New Material Dialog");
        dialog.setHeaderText("Create New Material");
        GridPane grid = new GridPane();
        ColumnConstraints column0 = new ColumnConstraints(100, 100, Double.MAX_VALUE);
        ColumnConstraints column1 = new ColumnConstraints(80, 80, Double.MAX_VALUE);
        ColumnConstraints column2 = new ColumnConstraints(80, 80, Double.MAX_VALUE);
        grid.getColumnConstraints().addAll(column0, column1, column2);
        grid.setHgap(5);
        grid.setVgap(5);
        grid.add(new Label("Name"), 0, 0);
        grid.add(new Label("Parameter"), 0, 1);
        grid.add(new Label("R. Permittivity"), 0, 2);
        grid.add(new Label("R. Permeability"), 0, 3);

        TextField name = new TextField();
        name.setPromptText("Enter material name");
        grid.add(name, 1, 0);
        GridPane.setColumnSpan(name, 2);

        grid.add(new Label("Re"), 1, 1);
        grid.add(new Label("Img"), 2, 1);

        NumberTextField realPermittivity = new NumberTextField(0.0);
        NumberTextField imgPermittivity = new NumberTextField(0.0);
        grid.add(realPermittivity, 1, 2);
        grid.add(imgPermittivity, 2, 2);

        NumberTextField realPermeability = new NumberTextField(0.0);
        NumberTextField imgPermeability = new NumberTextField(0.0);
        grid.add(realPermeability, 1, 3);
        grid.add(imgPermeability, 2, 3);
        dialog.getDialogPane().setContent(grid);

        ButtonType okButtonType = new ButtonType("OK", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);
        Node okButton = dialog.getDialogPane().lookupButton(okButtonType);
        realPermittivity.textProperty().addListener((observable, oldValue, newValue) -> {
            if (imgPermittivity.getText().trim().isEmpty()
                    || realPermeability.getText().trim().isEmpty()
                    || imgPermeability.getText().trim().isEmpty()
                    || name.getText().trim().isEmpty()
                    || newValue.trim().isEmpty()) {
                okButton.setDisable(true);
            } else {
                okButton.setDisable(false);
            }
        });
        imgPermittivity.textProperty().addListener((observable, oldValue, newValue) -> {
            if (realPermittivity.getText().trim().isEmpty()
                    || realPermeability.getText().trim().isEmpty()
                    || imgPermeability.getText().trim().isEmpty()
                    || name.getText().trim().isEmpty()
                    || newValue.trim().isEmpty()) {
                okButton.setDisable(true);
            } else {
                okButton.setDisable(false);
            }
        });
        name.textProperty().addListener((observable, oldValue, newValue) -> {
            if (realPermittivity.getText().trim().isEmpty()
                    || realPermeability.getText().trim().isEmpty()
                    || imgPermeability.getText().trim().isEmpty()
                    || imgPermittivity.getText().trim().isEmpty()
                    || newValue.trim().isEmpty()) {
                okButton.setDisable(true);
            } else {
                okButton.setDisable(false);
            }
        });

        realPermeability.textProperty().addListener((observable, oldValue, newValue) -> {
            if (realPermittivity.getText().trim().isEmpty()
                    || name.getText().trim().isEmpty()
                    || imgPermeability.getText().trim().isEmpty()
                    || imgPermittivity.getText().trim().isEmpty()
                    || newValue.trim().isEmpty()) {
                okButton.setDisable(true);
            } else {
                okButton.setDisable(false);
            }
        });

        imgPermeability.textProperty().addListener((observable, oldValue, newValue) -> {
            if (realPermittivity.getText().trim().isEmpty()
                    || name.getText().trim().isEmpty()
                    || realPermeability.getText().trim().isEmpty()
                    || imgPermittivity.getText().trim().isEmpty()
                    || newValue.trim().isEmpty()) {
                okButton.setDisable(true);
            } else {
                okButton.setDisable(false);
            }
        });

        CheckBox checkPML = new CheckBox("PML");
        checkPML.setSelected(false);
        ComboBox<PMLRegions> cbPMLRegions = new ComboBox<>();
        cbPMLRegions.setDisable(true);
        PMLRegions[] regions = PMLRegions.values();
        for (int i = 0; i < regions.length; i++) {
            cbPMLRegions.getItems().add(regions[i]);
        }
        cbPMLRegions.getSelectionModel().select(0);
        grid.add(checkPML, 0, 4);
        grid.add(cbPMLRegions, 1, 4);
        GridPane.setColumnSpan(cbPMLRegions, 2);

        checkPML.setOnAction(event -> {
            if (checkPML.isSelected()) {
                cbPMLRegions.setDisable(false);
            } else {
                cbPMLRegions.setDisable(true);
            }
        });

        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(dialogButton -> {
            ComplexNumber permittivity = new ComplexNumber(realPermittivity.getFloatValue(), imgPermittivity.getFloatValue());
            ComplexNumber permeabilityy = new ComplexNumber(realPermeability.getFloatValue(), imgPermeability.getFloatValue());
            if (dialogButton == okButtonType) {
                Material newMaterial = new Material(name.getText(), permittivity, permeabilityy);
                if (checkPML.isSelected()) {

                    //newMaterial.setPML(new PML(cbPMLRegions.getSelectionModel().getSelectedItem()));
                    newMaterial.setPMLRegion(cbPMLRegions.getSelectionModel().getSelectedItem());
                } else {
                    //newMaterial.setPML(null);		
                    newMaterial.setPMLRegion(null);
                }
                RepositoryMaterials.addMaterial(newMaterial);
                updateComboBoxMaterial();
                return "OK";
            }
            return null;
        });

        /*Optional<String> result  =*/ dialog.showAndWait();

    }

    public void onEditMaterial() {
        Dialog<String> dialog = new Dialog<>();
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();

        stage.getIcons().add(new Image(App.class.getResourceAsStream("icon/edit_material_32x32.png")));
        dialog.setTitle("Edit Material Dialog");
        dialog.setHeaderText("Edit Material");
        GridPane grid = new GridPane();
        ColumnConstraints column0 = new ColumnConstraints(100, 100, Double.MAX_VALUE);
        ColumnConstraints column1 = new ColumnConstraints(80, 80, Double.MAX_VALUE);
        ColumnConstraints column2 = new ColumnConstraints(80, 80, Double.MAX_VALUE);
        grid.getColumnConstraints().addAll(column0, column1, column2);
        grid.setHgap(5);
        grid.setVgap(5);
        grid.add(new Label("Name"), 0, 0);
        grid.add(new Label("Parameter"), 0, 1);
        grid.add(new Label("R. Permittivity"), 0, 2);
        grid.add(new Label("R. Permeability"), 0, 3);

        int index = CB_MATERIAL.getSelectionModel().getSelectedIndex();
        if (index == -1) {
            return;
        }
        Material material = RepositoryMaterials.getMaterial(index);

        TextField name = new TextField(material.getName());
        name.setPromptText("Enter material name");
        grid.add(name, 1, 0);
        GridPane.setColumnSpan(name, 2);

        grid.add(new Label("Re"), 1, 1);
        grid.add(new Label("Img"), 2, 1);

        NumberTextField realPermittivity = new NumberTextField(material.getRelativePermittivity().getReal());
        NumberTextField imgPermittivity = new NumberTextField(material.getRelativePermittivity().getImg());
        grid.add(realPermittivity, 1, 2);
        grid.add(imgPermittivity, 2, 2);

        NumberTextField realPermeability = new NumberTextField(material.getRelativePermeability().getReal());
        NumberTextField imgPermeability = new NumberTextField(material.getRelativePermeability().getImg());
        grid.add(realPermeability, 1, 3);
        grid.add(imgPermeability, 2, 3);
        dialog.getDialogPane().setContent(grid);

        ButtonType okButtonType = new ButtonType("OK", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);
        Node okButton = dialog.getDialogPane().lookupButton(okButtonType);
        realPermittivity.textProperty().addListener((observable, oldValue, newValue) -> {
            if (imgPermittivity.getText().trim().isEmpty()
                    || realPermeability.getText().trim().isEmpty()
                    || imgPermeability.getText().trim().isEmpty()
                    || name.getText().trim().isEmpty()
                    || newValue.trim().isEmpty()) {
                okButton.setDisable(true);
            } else {
                okButton.setDisable(false);
            }
        });
        imgPermittivity.textProperty().addListener((observable, oldValue, newValue) -> {
            if (realPermittivity.getText().trim().isEmpty()
                    || realPermeability.getText().trim().isEmpty()
                    || imgPermeability.getText().trim().isEmpty()
                    || name.getText().trim().isEmpty()
                    || newValue.trim().isEmpty()) {
                okButton.setDisable(true);
            } else {
                okButton.setDisable(false);
            }
        });
        name.textProperty().addListener((observable, oldValue, newValue) -> {
            if (realPermittivity.getText().trim().isEmpty()
                    || realPermeability.getText().trim().isEmpty()
                    || imgPermeability.getText().trim().isEmpty()
                    || imgPermittivity.getText().trim().isEmpty()
                    || newValue.trim().isEmpty()) {
                okButton.setDisable(true);
            } else {
                okButton.setDisable(false);
            }
        });

        realPermeability.textProperty().addListener((observable, oldValue, newValue) -> {
            if (realPermittivity.getText().trim().isEmpty()
                    || name.getText().trim().isEmpty()
                    || imgPermeability.getText().trim().isEmpty()
                    || imgPermittivity.getText().trim().isEmpty()
                    || newValue.trim().isEmpty()) {
                okButton.setDisable(true);
            } else {
                okButton.setDisable(false);
            }
        });

        imgPermeability.textProperty().addListener((observable, oldValue, newValue) -> {
            if (realPermittivity.getText().trim().isEmpty()
                    || name.getText().trim().isEmpty()
                    || realPermeability.getText().trim().isEmpty()
                    || imgPermittivity.getText().trim().isEmpty()
                    || newValue.trim().isEmpty()) {
                okButton.setDisable(true);
            } else {
                okButton.setDisable(false);
            }
        });

        CheckBox checkPML = new CheckBox("PML");
        checkPML.setSelected(material.isPML());
        ComboBox<PMLRegions> cbPMLRegions = new ComboBox<>();
        cbPMLRegions.setDisable(!material.isPML());
        PMLRegions[] regions = PMLRegions.values();
        for (int i = 0; i < regions.length; i++) {
            cbPMLRegions.getItems().add(regions[i]);
        }
        cbPMLRegions.getSelectionModel().select(material.getPMLRegion());;
        grid.add(checkPML, 0, 4);
        grid.add(cbPMLRegions, 1, 4);
        GridPane.setColumnSpan(cbPMLRegions, 2);

        checkPML.setOnAction(event -> {
            if (checkPML.isSelected()) {
                cbPMLRegions.setDisable(false);
            } else {
                cbPMLRegions.setDisable(true);
            }
        });

        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(dialogButton -> {
            ComplexNumber permittivity = new ComplexNumber(realPermittivity.getFloatValue(), imgPermittivity.getFloatValue());
            ComplexNumber permeabilityy = new ComplexNumber(realPermeability.getFloatValue(), imgPermeability.getFloatValue());
            if (dialogButton == okButtonType) {
                material.setName(name.getText());
                material.setRelativePermittivity(permittivity);
                material.setRelativePermeability(permeabilityy);
                if (checkPML.isSelected()) {
                    material.setPMLRegion(cbPMLRegions.getSelectionModel().getSelectedItem());
                } else {
                    material.setPMLRegion(null);
                }
                updateComboBoxMaterial();
                return "OK";
            }
            return null;
        });

        /*Optional<String> result  =*/ dialog.showAndWait();

    }

    public void onDeleteMaterial() {
        int index = CB_MATERIAL.getSelectionModel().getSelectedIndex();
        if (index == -1) {
            return;
        }
        RepositoryMaterials.removeMaterial(index);
        updateComboBoxMaterial();

    }

    public void onDiscretizeModel() {
        resetViewMesh();
        resetViewResult();
        checkMaterialVisibility.setSelected(false);
        Project.setIsMaterialVisible(false);

        lastButtonID = null;
        SelectGeometry.end();
        CreateGeometry.end();
        IntersectGeometry.end();
        boolean hasExternalFace = (Project.getExternalFace() != null);

        /*for (Face2D face2d : Project.getFaceList()) {
			if(face2d.getTypeFace() == Face2D.EXTERNAL){
				hasExternalFace = true;
				break;
			}
		}*/
        if (hasExternalFace) {
            if (!Project.allModelHasMaterial()) {
                Alert alert = new Alert(AlertType.WARNING);
                Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                stage.getIcons().add(new Image(App.class.getResourceAsStream("icon/meshing_32x32.png")));

                alert.setTitle("Material Not Applied");
                alert.setHeaderText("Please add material to domain");
                alert.setContentText("Please, you have to add material  to  model domain before mesh build");
                alert.showAndWait();
                return;
            }
            TextInputDialog inputDialog = new TextInputDialog("0.25");
            inputDialog.getEditor().textProperty().addListener(new ChangeListener<String>() {

                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    Node okBtn = inputDialog.getDialogPane().lookupButton(ButtonType.OK);
                    if (newValue.matches("^[0-9]{1,2}.[0-9]{1,5}$") && !newValue.isEmpty() && !newValue.equals("0.0")) {
                        okBtn.setDisable(false);
                    } else {
                        okBtn.setDisable(true);
                    }
                }
            });
            inputDialog.setTitle("Discretize domain");
            Project.setModelVisibility(true);
            Optional<String> result = inputDialog.showAndWait();
            if (result.isPresent()) {

                ConstrainedDelaunayTriangulation CDT = new ConstrainedDelaunayTriangulation(Project.getFaceList());
                CDT.init(Float.valueOf(result.get()));
                ProgressMeshDialog pmd = new ProgressMeshDialog(CDT);
                pmd.start();
                pmd.show();

                Project.setMesh(CDT.getConectivityList());
                ConectivityList mesh = Project.getMesh();
                Project.setModelVisibility(false);
                mesh.setMeshVisible(true);
                checkViewMesh.setSelected(true);

                App.getInfoArea().appendText("--------MESH INFO--------\n");
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                java.util.Date hora = Calendar.getInstance().getTime();

                App.getInfoArea().appendText("Hour: " + sdf.format(hora) + "\n");
                App.getInfoArea().appendText("Number Faces: " + Project.faceSize() + "\n");
                App.getInfoArea().appendText("Number Elements: " + mesh.elementsSize() + "\n");
                App.getInfoArea().appendText("Number Nodes: " + mesh.nodeSize() + "\n");
                App.getInfoArea().appendText("Time: " + CDT.getDuration() + " seconds\n\n");

                App.focusInfoArea();

            }

        } else {
            Alert alert = new Alert(AlertType.WARNING);
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();

            stage.getIcons().add(new Image(App.class.getResourceAsStream("icon/external_face_32x32.png")));
            alert.setTitle("External Face Not Detect");
            alert.setHeaderText("Please define the external face of domain");
            alert.setContentText("Please, you have to created external face for the domain");
            alert.showAndWait();

        }

    }

    public void onClearMesh() {
        checkMaterialVisibility.setSelected(false);
        Project.setIsMaterialVisible(false);

        Project.setMesh(null);
        Project.setModelVisibility(true);
        resetViewMesh();
        resetViewResult();

    }

    public void onCheckViewMesh() {
        ConectivityList mesh = Project.getMesh();
        if (mesh == null) {
            Alert alert = new Alert(AlertType.WARNING);
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image(App.class.getResourceAsStream("icon/meshing_32x32.png")));

            alert.setTitle("Mesh Not Detect");
            alert.setHeaderText("Please discretize the domain");
            alert.setContentText("Please, you have to discretize the domain before compute quality histogram");
            alert.showAndWait();

            checkViewMesh.setSelected(false);
            return;
        }

        checkMaterialVisibility.setSelected(false);
        Project.setIsMaterialVisible(false);

        mesh.setMeshVisible(checkViewMesh.isSelected());
        if (mesh.isMeshQualityVisible() || mesh.isMeshVisible()) {
            Project.setModelVisibility(false);
        } else {
            Project.setModelVisibility(true);
        }

        resetViewResult();

    }

    public void onCheckPlotColorMap() {
        checkMaterialVisibility.setSelected(false);
        Project.setIsMaterialVisible(false);

        ConectivityList mesh = Project.getMesh();
        if (mesh == null) {
            alertMeshNull();
            TGLBTN_COLORMAP.setSelected(false);
            return;
        }
        if (!mesh.simulationMade()) {
            alertSimulationNull();
            TGLBTN_COLORMAP.setSelected(false);
            return;
        }
        resetViewMesh();
        mesh.setColorMapVisible(TGLBTN_COLORMAP.isSelected());

        if (!TGLBTN_COLORMAP.isSelected() && !TGLBTN_ARROW.isSelected() && !TGLBTN_ISOLINE.isSelected()) {
            Project.setModelVisibility(true);
        } else {
            Project.setModelVisibility(false);
        }

    }

    public void onExecute() {
        resetViewMesh();
        resetViewResult();
        checkMaterialVisibility.setSelected(false);
        Project.setIsMaterialVisible(false);

        ConectivityList mesh = Project.getMesh();
        if (mesh == null) {
            alertMeshNull();
            return;
        }

        try {
            Project.computeAnalysis();
            mesh.setColorMapVisible(true);
            TGLBTN_COLORMAP.setSelected(true);
            mesh.setSimulationStatus(true);

        } catch (OutOfMemoryError e) {
            Alert alert = new Alert(AlertType.WARNING);
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image(App.class.getResourceAsStream("icon/memory_32x32.png")));
            alert.setGraphic(new ImageView(new Image(App.class.getResourceAsStream("icon/memory_32x32.png"))));

            alert.setTitle("Withou Memoryt");
            alert.setHeaderText("Memory Heap Full");
            alert.setContentText("Please, try again  with less element!!!");
            alert.showAndWait();

            Project.getFiniteAnalysis().clear();
        }

    }

    public void onCreateGeometry(Event event) {
        Project.setModelVisibility(true);
        resetViewMesh();
        resetViewResult();

        checkMaterialVisibility.setSelected(false);
        Project.setIsMaterialVisible(false);

        Button btn = (Button) event.getSource();

        if (!App.getTitle().contains("*")) {
            App.setTitle(App.getTitle() + "*");
        }

        SelectGeometry.end();
        clearViewGroup();

        ButtonsID btnID = ButtonsID.valueOf(btn.getId());
        lastButtonID = btnID;
        switch (btnID) {
            case BTN_CPOINT:
                CreateGeometry.begin(GeometryType.POINT);
                break;
            case BTN_CLINE:
                CreateGeometry.begin(GeometryType.LINE);
                break;
            case BTN_CPOLYLINE:
                CreateGeometry.begin(GeometryType.POLYLINE);
                break;
            case BTN_CRECTANGLE:
                CreateGeometry.begin(GeometryType.RECTANGLE);
                break;
            case BTN_CPOLYGON:
                CreateGeometry.begin(GeometryType.POLYGON);
                break;
            case BTN_CCIRCLE:
                CreateGeometry.begin(GeometryType.CIRCLE);
                break;
            case BTN_CARC:
                CreateGeometry.begin(GeometryType.ARC);
                break;
            case BTN_CBEZIER:
                CreateGeometry.begin(GeometryType.BEZIER);
                break;
            case BTN_CNFACE:
                CreateGeometry.end();
                SelectGeometry.begin(GeometryType.BORDER);
                break;
            case BTN_CBFACE:
                CreateGeometry.end();
                SelectGeometry.begin(GeometryType.BORDER);
                break;
            case BTN_CEFACE:
                CreateGeometry.end();
                SelectGeometry.begin(GeometryType.BORDER);
                break;
            default:
                break;
        }

    }

    public void onIntersections(Event event) {
        resetViewMesh();
        resetViewResult();

        checkMaterialVisibility.setSelected(false);
        Project.setIsMaterialVisible(false);

        CreateGeometry.end();
        SelectGeometry.end();

        Button btn = (Button) event.getSource();
        ButtonsID btnID = ButtonsID.valueOf(btn.getId());

        lastButtonID = btnID;
        switch (btnID) {
            case BTN_LINES_INTER:
                IntersectGeometry.begin(IntersectGeometry.LINES);
                SelectGeometry.begin(GeometryType.LINE);
                break;
            case BTN_LINES_CIRCLE_INTER:
                IntersectGeometry.begin(IntersectGeometry.LINES_CIRCLE);
                SelectGeometry.begin(GeometryType.BORDER);
                break;
            case BTN_LINES_ARC_INTER:
                IntersectGeometry.begin(IntersectGeometry.LINES_ARC);
                SelectGeometry.begin(GeometryType.BORDER);
                break;
            case BTN_CIRCLES_INTER:
                IntersectGeometry.begin(IntersectGeometry.CIRCLE_CIRCLE);
                SelectGeometry.begin(GeometryType.CIRCLE);
                break;
            default:
                break;
        }
    }

    public void updateComboBoxMaterial() {
        CB_MATERIAL.getItems().clear();
        for (Material material : RepositoryMaterials.getList()) {
            CB_MATERIAL.getItems().addAll(material.getName());
        }
        CB_MATERIAL.getSelectionModel().select(0);
    }

    public void onAssignProbType() {
        DefineProblemTypeDialog dialog = new DefineProblemTypeDialog();
        Optional<ProblemType> result = dialog.showAndWait();
        if (result.isPresent()) {
            Project.problemType = result.get();
            switch (result.get()) {
                case ELETROSTATIC:
                    BTN_BC_SPACE_DENSITY.setDisable(false);
                    BTN_BC_POTENTIAL.setText("Eletric Potential");
                    BTN_BC_DENSITY.setText("Surface Charge Density");
                    BTN_BC_SPACE_DENSITY.setText("Space Charge Density");
                    BTN_BC_PERM_FIELD.setText("Polarization");
                    menuItemAssignWavelength.setDisable(true);
                    break;
                case MAGNETOSTATIC_VECTOR_POTENTIAL:
                    BTN_BC_SPACE_DENSITY.setDisable(false);
                    BTN_BC_POTENTIAL.setText("Mag Vector Potential");
                    BTN_BC_DENSITY.setText("Surface Current Density");
                    BTN_BC_SPACE_DENSITY.setText("Space Current Density");
                    BTN_BC_PERM_FIELD.setText("Permanent M.Field");
                    menuItemAssignWavelength.setDisable(true);
                    break;
                case MAGNETOSTATIC_SCALAR_POTENTIAL:
                    BTN_BC_SPACE_DENSITY.setDisable(true);
                    menuItemAssignWavelength.setDisable(true);
                    BTN_BC_POTENTIAL.setText("Mag Scalar Potential");
                    BTN_BC_DENSITY.setText("Surface Current Density");
                    BTN_BC_SPACE_DENSITY.setText("Space Current Density");
                    BTN_BC_PERM_FIELD.setText("Permanent M.Field");
                    break;
                default:
                    menuItemAssignWavelength.setDisable(false);

                    break;
            }
        }

        Project.treeView.updateProbleType();

    }

    public void onAssignWavelength() {
        Dialog<Float> dialog = new Dialog<>();
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();

        stage.getIcons().add(new Image(App.class.getResourceAsStream("icon/new_material_32x32.png")));
        dialog.setTitle("Wavelength ");
        dialog.setHeaderText("Assign Wavelength to Project");
        GridPane grid = new GridPane();
        grid.setHgap(5);
        grid.setVgap(5);
        grid.add(new Label("Wavelength Type "), 0, 0);
        grid.add(new Label("Unit"), 0, 1);

        NumberTextField wavelength = new NumberTextField();
        wavelength.setText(String.valueOf(Project.getWavelenght().getValue()));
        grid.add(wavelength, 1, 0);
        ComboBox<Units> comboUnits = new ComboBox<>();

        comboUnits.setPrefWidth(200);
        Units[] units = Units.values();
        for (int i = 0; i < units.length; i++) {
            comboUnits.getItems().add(units[i]);
        }
        comboUnits.getSelectionModel().select(Project.wavelenght.getUnit());

        grid.add(comboUnits, 1, 1);

        dialog.getDialogPane().setContent(grid);

        ButtonType okButtonType = new ButtonType("OK", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                return wavelength.getFloatValue();
            }
            return Float.NaN;
        });
        Optional<Float> result = dialog.showAndWait();
        if (result.isPresent()) {
            Project.wavelenght = new Wavelenght(wavelength.getFloatValue(), comboUnits.getSelectionModel().getSelectedItem());

        }
        Project.treeView.updateProbleType();
    }

    public void onPointDistance() {
        CreateGeometry.end();
        IntersectGeometry.end();
        SelectGeometry.begin(GeometryType.POINT);
    }

    public void onCheckPlotArrow() {
        checkMaterialVisibility.setSelected(false);
        Project.setIsMaterialVisible(false);

        ConectivityList mesh = Project.getMesh();
        if (mesh == null) {
            alertMeshNull();
            TGLBTN_ARROW.setSelected(false);
            return;
        }
        if (!mesh.simulationMade()) {
            alertSimulationNull();
            TGLBTN_ARROW.setSelected(false);
            return;
        }
        resetViewMesh();
        mesh.setArrowsVisible(TGLBTN_ARROW.isSelected());

        if (!TGLBTN_COLORMAP.isSelected() && !TGLBTN_ARROW.isSelected() && !TGLBTN_ISOLINE.isSelected()) {
            Project.setModelVisibility(true);
        } else {
            Project.setModelVisibility(false);
        }
    }

    public void onLaplacianSmooth() {

        if (Project.getMesh() == null) {
            Alert alert = new Alert(AlertType.WARNING);
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();

            stage.getIcons().add(new Image(App.class.getResourceAsStream("icon/meshing_32x32.png")));
            alert.setTitle("Mesh Not Detect");
            alert.setHeaderText("Please discretize the domain");
            alert.setContentText("Please, you have to discretize the domain before compute analysis");
            alert.showAndWait();

            return;
        }

        ChoiceDialog<Integer> numberContourDialog = new ChoiceDialog<Integer>(5);
        numberContourDialog.setTitle("Choose Dialog for the interaction Number ");
        numberContourDialog.setHeaderText("Select the interaction number below");
        numberContourDialog.setContentText("Interaction Number:");
        numberContourDialog.setGraphic(new ImageView(App.class.getResource("icon/laplacian_smooth_32x32.png").toString()));

        Stage stage = (Stage) numberContourDialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(App.class.getResource("icon/laplacian_smooth_24x24.png").toString()));

        for (int i = 1; i <= 30; i++) {
            numberContourDialog.getItems().add(i);
        }
        Optional<Integer> result = numberContourDialog.showAndWait();
        if (result.isPresent()) {
            for (int i = 0; i < result.get().intValue(); i++) {
                for (Point2D node : Project.getMesh().getNodes()) {
                    node.laplacianSmooth();
                }
            }

        }
    }

    public void onCheckMeshQuality() {
        ConectivityList mesh = Project.getMesh();
        if (mesh == null) {
            Alert alert = new Alert(AlertType.WARNING);
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image(App.class.getResourceAsStream("icon/meshing_32x32.png")));

            alert.setTitle("Mesh Not Detect");
            alert.setHeaderText("Please discretize the domain");
            alert.setContentText("Please, you have to discretize the domain before plot quality histogram");
            alert.showAndWait();

            checkViewMeshQuality.setSelected(false);
            return;
        }
        checkMaterialVisibility.setSelected(false);
        Project.setIsMaterialVisible(false);

        mesh.setMeshQualityVisible(checkViewMeshQuality.isSelected());
        if (mesh.isMeshQualityVisible() || mesh.isMeshVisible()) {
            Project.setModelVisibility(false);
        } else {
            Project.setModelVisibility(true);
        }

        resetViewResult();

    }

    public void onCheckIsolines() {
        checkMaterialVisibility.setSelected(false);
        Project.setIsMaterialVisible(false);

        ConectivityList mesh = Project.getMesh();
        if (mesh == null) {
            alertMeshNull();
            TGLBTN_ISOLINE.setSelected(false);
            return;
        }
        if (!mesh.simulationMade()) {
            alertSimulationNull();
            TGLBTN_ISOLINE.setSelected(false);
            return;
        }

        resetViewMesh();

        if (!TGLBTN_COLORMAP.isSelected() && !TGLBTN_ARROW.isSelected() && !TGLBTN_ISOLINE.isSelected()) {
            Project.setModelVisibility(true);
        } else {
            Project.setModelVisibility(false);
        }

        if (TGLBTN_ISOLINE.isSelected()) {
            ChoiceDialog<Integer> numberContourDialog = new ChoiceDialog<Integer>(5);
            numberContourDialog.setTitle("Choose Dialog for the contour Number ");
            numberContourDialog.setHeaderText("Select the contour number below");
            numberContourDialog.setContentText("Contour Number:");
            numberContourDialog.setGraphic(new ImageView(App.class.getResource("icon/isolines_32x32.png").toString()));

            Stage stage = (Stage) numberContourDialog.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image(App.class.getResource("icon/isolines_32x32.png").toString()));

            for (int i = 1; i <= 30; i++) {
                numberContourDialog.getItems().add(i);
            }
            Optional<Integer> result = numberContourDialog.showAndWait();
            if (result.isPresent()) {
                mesh.setContourNumber(result.get().intValue());
                mesh.setIsolinesVisible(true);
            } else {
                mesh.setIsolinesVisible(false);
                TGLBTN_ISOLINE.setSelected(false);
                for (Face2D face2d : Project.getFaceList()) {
                    face2d.setVisible(true);
                }
            }
        } else {
            mesh.setIsolinesVisible(false);
        }

    }

    public void onDefineUnit() {
        ChoiceDialog<Units> choiceUnit = new ChoiceDialog<>();
        Units[] units = Units.values();
        for (int i = 0; i < units.length; i++) {
            choiceUnit.getItems().add(units[i]);
        }
        choiceUnit.setSelectedItem(Geometry2D.getUnit());
        choiceUnit.setTitle("Choice Units Dialog");
        choiceUnit.setHeaderText("You can choose a unit below!!!");
        choiceUnit.setContentText("Please, choose your unit:");
        choiceUnit.setGraphic(new ImageView(new Image(App.class.getResourceAsStream("icon/unit_16x16.png"))));

        Stage stage = (Stage) choiceUnit.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image("icon/unit_16x16.png"));

        Optional<Units> result = choiceUnit.showAndWait();
        if (result.isPresent()) {
            if (result.get() != Geometry2D.getUnit()) {
                Geometry2D.setUnit(result.get());

            }
        }
    }

    public void onClearBC() {
        for (Border2D border2d : Project.getBordersList()) {
            border2d.setConditionExpression(null);
            Point2D[] endpoints = border2d.getEndpoints();
            endpoints[0].setValue(null);
            endpoints[1].setValue(null);
            border2d.setBoundaryConditionType(BoundaryConditionType.NONE);
        }
    }

    public void onClearDomainBC() {
        for (Face2D face2d : Project.getFaceList()) {
            face2d.spaceDensityFunction = new SpaceDensityFunction("0");
            face2d.setPermanentField(null);
        }
    }

    public void onCreatePort() {
        SelectGeometry.end();
        clearViewGroup();

        if (tglBtnCreatePort.isSelected()) {
            SelectGeometry.begin(GeometryType.LINE);
        } else {
            SelectGeometry.end();
        }
    }

    public void onNewProject() {
        App.setTitle("Finite Element Methods for Eletromagnetic Problems - New Project.jfem*");
        App.setPathProject("Project Not Saved!!");

        BTN_CEFACE.setDisable(false);

        App.getPromptComannd().clear();
        App.getPromptComannd().appendText(">>");
        App.getInfoArea().clear();

        RepositoryMaterials.clear();
        RepositoryMaterials.addMaterial(new Material("Air", new ComplexNumber(1, 0), new ComplexNumber(1, 0)));
        Project.setProjectPath(null);
        Project.clearAll();

        updateComboBoxMaterial();

    }

    public void onOpenProject() throws IOException {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select your project");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JFEM", "*.jfem"));
        File proj = fileChooser.showOpenDialog(App.scene.getWindow());
        if (proj == null) {
            return;
        }

        App.setTitle("Finite Element Methods for Eletromagnetic Problems - " + proj.getName());
        String projPath = proj.getAbsolutePath();
        App.setPathProject(projPath);
        Project.setProjectPath(projPath);
        if (projPath.endsWith(".jfem") && proj.isFile()) {
            RepositoryMaterials.clear();
            Project.clearAll();

            App.getInfoArea().clear();
            App.getPromptComannd().clear();

            List<Material> materialsFromFile = new ArrayList<>();
            List<Point2D> pointsFromFile = new ArrayList<>();
            List<Line2D> linesFromFile = new ArrayList<>();
            List<Arc2D> arcsFromFile = new ArrayList<>();
            List<BezierCurve2D> bezierFromFile = new ArrayList<>();
            List<Face2D> facesFromFile = new ArrayList<>();

            try (BufferedReader br = Files.newBufferedReader(Paths.get(projPath))) {
                GeometryType geoType = GeometryType.NULL;
                boolean isMatList = false;
                String line;
                String coords[];

                while ((line = br.readLine()) != null) {
                    if (line.contains("PROBLEM_TYPE")) {
                        Project.setProblemType(ProblemType.valueOf(line.split(":")[1]));
                    }
                    if (line.contains("UNITS")) {
                        Geometry2D.setUnit(Units.valueOf(line.split(":")[1]));
                    }
                    if (line.contains("WAVELENGHT")) {
                        String[] split = line.split(":");
                        String wave[] = split[1].split(",");
                        Project.setWavelenght(new Wavelenght(Float.valueOf(wave[0]), Units.valueOf(wave[1])));
                    }
                    if (line.contains("END")) {
                        geoType = GeometryType.NULL;
                        if (isMatList) {
                            isMatList = false;
                        }
                    }

                    if (isMatList) {
                        String[] infoMaterial = line.split(",");
                        String name = infoMaterial[0];
                        ComplexNumber permittivity = new ComplexNumber(Float.valueOf(infoMaterial[1]), Float.valueOf(infoMaterial[2]));
                        ComplexNumber permeability = new ComplexNumber(Float.valueOf(infoMaterial[3]), Float.valueOf(infoMaterial[4]));
                        Material material = new Material(name, permittivity, permeability);
                        material.setColor(RepositoryMaterials.getColor(materialsFromFile.size()));
                        materialsFromFile.add(material);

                    }

                    switch (geoType) {
                        case POINT:
                            coords = line.split(",");
                            pointsFromFile.add(new Point2D(Double.valueOf(coords[0]), Double.valueOf(coords[1])));
                            break;
                        case LINE:
                            coords = line.split(" ");
                            linesFromFile.add(new Line2D(pointsFromFile.get(Integer.valueOf(coords[0])), pointsFromFile.get(Integer.valueOf(coords[1]))));
                            break;
                        case ARC:
                            coords = line.split(" ");
                            Point2D center = pointsFromFile.get(Integer.valueOf(coords[0]));
                            Point2D start = pointsFromFile.get(Integer.valueOf(coords[1]));
                            Point2D end = pointsFromFile.get(Integer.valueOf(coords[2]));
                            arcsFromFile.add(new Arc2D(center, start, end));
                            break;
                        case BEZIER:
                            coords = line.split(" ");
                            BezierCurve2D bezier = new BezierCurve2D();
                            for (int i = 0; i < coords.length; i++) {
                                bezier.addPoint(pointsFromFile.get(Integer.valueOf(coords[i])));
                            }
                            bezierFromFile.add(bezier);
                            break;
                        case FACE:
                            coords = line.trim().split(",");
                            Face2D face = new Face2D();
                            int id = -1;
                            GeometryType type = null;
                            face.setTypeFace(Integer.valueOf(coords[0]));
                            int indexMaterial = Integer.valueOf(coords[1]);
                            if (indexMaterial == -1) {
                                face.setMaterial(null);
                            } else {
                                face.setMaterial(materialsFromFile.get(indexMaterial));
                            }
                            if (face.getTypeFace() == Face2D.EXTERNAL) {
                                BTN_CEFACE.setDisable(true);
                            }
                            for (int i = 2; i < coords.length; i++) {
                                String[] infoBorder = coords[i].split("-");
                                id = Integer.valueOf(infoBorder[0]);
                                type = GeometryType.valueOf(infoBorder[1]);
                                switch (type) {
                                    case LINE:
                                        face.addBorder(linesFromFile.get(id));
                                        break;
                                    case ARC:
                                        face.addBorder(arcsFromFile.get(id));
                                        break;
                                    case BEZIER:
                                        face.addBorder(bezierFromFile.get(id));
                                        break;
                                    default:
                                        break;
                                }

                            }
                            face.findPolygon();
                            ConstrainedDelaunayTriangulation C = new ConstrainedDelaunayTriangulation();
                            C.discretizeFace(face);
                            facesFromFile.add(face);
                            break;
                        default:
                            break;
                    }

                    if (line.contains("BEGIN")) {
                        if (!line.trim().contains("MATERIALS")) {
                            geoType = GeometryType.valueOf(line.split(":")[1]);
                        } else {
                            isMatList = true;
                        }
                    }
                }
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            RepositoryMaterials.addAll(materialsFromFile);
            Project.addAllGeometry(pointsFromFile);
            Project.addAllGeometry(linesFromFile);
            Project.addAllGeometry(arcsFromFile);
            Project.addAllGeometry(bezierFromFile);
            Project.addAllGeometry(facesFromFile);

            switch (Project.getProblemType()) {
                case ELETROSTATIC:;
                    menuItemAssignWavelength.setDisable(true);
                    break;
                case MAGNETOSTATIC_VECTOR_POTENTIAL:
                    menuItemAssignWavelength.setDisable(true);
                    break;
                case MAGNETOSTATIC_SCALAR_POTENTIAL:
                    menuItemAssignWavelength.setDisable(true);
                    break;
                case HELMHOLTZ_TE_MODE:
                    menuItemAssignWavelength.setDisable(false);

                    break;
                case HELMHOLTZ_TM_MODE:
                    tglBtnCreatePort.setDisable(false);
                    menuItemAssignWavelength.setDisable(false);
                    break;
                default:
                    break;
            }

            Project.treeView.update();
            updateComboBoxMaterial();
            App.getPromptComannd().clear();
            App.getPromptComannd().appendText(">>");

        };
    }

    public void onSaveProjectAs() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Project As..");
        fileChooser.setInitialFileName("New Project");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JFEM", "*.jfem"));

        File proj = fileChooser.showSaveDialog(App.scene.getWindow());
        if (proj == null) {
            return;
        }

        saveProject(proj.getAbsolutePath());
        App.setTitle("Finite Element Methods for Eletromagnetic Problems - " + proj.getName());
        App.setPathProject(proj.getAbsolutePath());
        Project.setProjectPath(proj.getAbsolutePath());
    }

    public void onSaveProject() {
        if (Project.getProjectPath() == null) {
            App.getInfoArea().clear();
            App.getPromptComannd().clear();
            onSaveProjectAs();
        } else {
            App.getInfoArea().clear();
            App.getPromptComannd().clear();
            saveProject(Project.getProjectPath());
            App.setTitle(App.getTitle().replace("*", ""));
        }
    }

    public void saveProject(String projPath) {

        try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(projPath), StandardCharsets.UTF_8)) {
            //Salva o tipo de problema
            bw.append("PROBLEM_TYPE:" + Project.getProblemType().toString());

            if (Project.getProblemType() == ProblemType.HELMHOLTZ_TE_MODE || Project.getProblemType() == ProblemType.HELMHOLTZ_TM_MODE) {
                float value = Project.getWavelenght().getValue();
                Units unit = Project.getWavelenght().getUnit();
                bw.newLine();
                bw.append("WAVELENGHT:" + value + "," + unit.toString());
            }
            bw.newLine();

            //Salva a unidade do problema
            bw.append("UNITS:" + Geometry2D.getUnit().toString());
            bw.newLine();

            //Sala os materiais
            bw.append("BEGIN:MATERIALS");
            bw.newLine();
            for (Material material : RepositoryMaterials.getList()) {
                ComplexNumber permability = material.getRelativePermeability();
                ComplexNumber permittivity = material.getRelativePermittivity();
                bw.append(material.getName() + "," + permittivity.getReal() + "," + permittivity.getImg() + "," + permability.getReal() + "," + permability.getImg());
                bw.newLine();
            }
            bw.append("END");
            bw.newLine();

            //Salvando Pontos
            if (Project.pointSize() > 0) {
                bw.append("BEGIN:POINT");
                bw.newLine();
                int i = 0;
                for (Point2D point2d : Project.getPointList()) {
                    bw.append(Double.toString(point2d.getX()));
                    bw.append(",");
                    bw.append(Double.toString(point2d.getY()));
                    bw.newLine();
                    point2d.indexForSave = i;
                    i++;
                }
                bw.append("END");
                bw.newLine();
            }

            //Salvando Linhas
            if (Project.lineSize() > 0) {
                bw.append("BEGIN:LINE");
                bw.newLine();
                int i = 0;
                for (Line2D line2d : Project.getLineList()) {
                    bw.append(String.valueOf(line2d.getP0().indexForSave));
                    bw.append(" ");
                    bw.append(String.valueOf(line2d.getP1().indexForSave));
                    bw.newLine();
                    line2d.indexForSave = i;
                    i++;
                }
                bw.append("END");
                bw.newLine();
            }

            //Salvando Arcos
            if (Project.arcSize() > 0) {
                bw.append("BEGIN:ARC");
                bw.newLine();
                int i = 0;
                for (Arc2D arc2d : Project.getArcList()) {
                    bw.append(String.valueOf(arc2d.getCenterPoint().indexForSave));
                    bw.append(" ");
                    bw.append(String.valueOf(arc2d.getStartPoint().indexForSave));
                    bw.append(" ");
                    bw.append(String.valueOf(arc2d.getEndPoint().indexForSave));
                    bw.newLine();

                    arc2d.indexForSave = i;
                    i++;
                }
                bw.append("END");
                bw.newLine();
            }

            //Salvando Curvas Bezier
            if (Project.bezierSize() > 0) {
                bw.append("BEGIN:BEZIER");
                bw.newLine();
                int i = 0;
                for (BezierCurve2D bezierCurve2D : Project.getBezierList()) {
                    for (Point2D point2d : bezierCurve2D.getControlPoints()) {
                        bw.append(String.valueOf(point2d.indexForSave));
                        bw.append(" ");
                    }
                    bw.newLine();
                    bezierCurve2D.indexForSave = i;
                    i++;
                }
                bw.append("END");
                bw.newLine();
            }

            //Salvando faces
            if (Project.faceSize() > 0) {
                bw.append("BEGIN:FACE");
                bw.newLine();

                for (Face2D face2d : Project.getFaceList()) {
                    bw.append(Integer.toString(face2d.getTypeFace()));
                    bw.append("," + RepositoryMaterials.getIndex(face2d.getMaterial()) + ",");
                    for (Border2D border2d : face2d.getBoundaries()) {
                        bw.append(String.valueOf(border2d.indexForSave) + "-" + border2d.getType().toString());
                        bw.append(",");
                    }
                    bw.newLine();
                }
                bw.append("END");
                bw.newLine();
            }

            bw.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked", "static-access"})
    public void onPlotHistogram() {
        if (Project.getMesh() == null) {
            Alert alert = new Alert(AlertType.WARNING);
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image(App.class.getResourceAsStream("icon/meshing_32x32.png")));

            alert.setTitle("Mesh Not Detect");
            alert.setHeaderText("Please discretize the domain");
            alert.setContentText("Please, you have to discretize the domain before compute quality histogram");
            alert.showAndWait();
        }

        Dialog<Boolean> histogram = new Dialog<>();
        histogram.setTitle("Hitogram quality mesh");
        Stage stage = (Stage) histogram.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(App.class.getResourceAsStream("icon/histogram_32x32.png")));

        ButtonType okButtonType = new ButtonType("OK", ButtonData.OK_DONE);
        histogram.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Quality Factor");
        yAxis.setLabel("Number of Element");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setCategoryGap(0);
        barChart.setBarGap(0);

        int[] group = new int[10];
        for (int i = 0; i < group.length; i++) {
            group[i] = 0;
        }
        float quality = 0;
        for (Triangle2D element : Project.getMesh().getElements()) {
            quality = element.qualityFactor();
            if (quality <= 0.1) {
                group[0]++;
            } else if (quality <= 0.2) {
                group[1]++;
            } else if (quality <= 0.3) {
                group[2]++;
            } else if (quality <= 0.4) {
                group[3]++;
            } else if (quality <= 0.5) {
                group[4]++;
            } else if (quality <= 0.6) {
                group[5]++;
            } else if (quality <= 0.7) {
                group[6]++;
            } else if (quality <= 0.8) {
                group[7]++;
            } else if (quality <= 0.9) {
                group[8]++;
            } else if (quality <= 1) {
                group[9]++;
            }
        }
        XYChart.Series series1 = new XYChart.Series();
        series1.setName("Hisgrama de  Qualidade");

        series1.getData().add(new XYChart.Data("0-0.1", group[0]));
        series1.getData().add(new XYChart.Data("0.1-0.2", group[1]));
        series1.getData().add(new XYChart.Data("0.2-0.3", group[2]));
        series1.getData().add(new XYChart.Data("0.3-0.4", group[3]));
        series1.getData().add(new XYChart.Data("0.4-0.5", group[4]));
        series1.getData().add(new XYChart.Data("0.5-0.6", group[5]));
        series1.getData().add(new XYChart.Data("0.6-0.7", group[6]));
        series1.getData().add(new XYChart.Data("0.7-0.8", group[7]));
        series1.getData().add(new XYChart.Data("0.8-0.9", group[8]));
        series1.getData().add(new XYChart.Data("0.9-1.0", group[9]));

        barChart.getData().addAll(series1);

        VBox vBox = new VBox(5);
        GridPane meshInfo = new GridPane();
        meshInfo.setVgap(5);
        meshInfo.setHgap(5);
        meshInfo.setAlignment(Pos.CENTER);
        Label labelMeshInfo = new Label("Informa��es da Malha");
        labelMeshInfo.setFont(Font.font(labelMeshInfo.getFont().getFamily(), FontWeight.BOLD, labelMeshInfo.getFont().getSize()));
        meshInfo.setColumnSpan(labelMeshInfo, 2);
        meshInfo.setHalignment(labelMeshInfo, HPos.CENTER);
        meshInfo.setValignment(labelMeshInfo, VPos.CENTER);

        meshInfo.add(labelMeshInfo, 0, 0);
        meshInfo.add(new Label("N�mero de Elementos:"), 0, 1);
        meshInfo.add(new Label(String.valueOf(Project.getMesh().elementsSize())), 1, 1);
        meshInfo.add(new Label("Num�ro de n�s:"), 0, 2);
        meshInfo.add(new Label(String.valueOf(Project.getMesh().nodeSize())), 1, 2);

        Button saveHistogram = new Button();
        saveHistogram.setOnAction((ActionEvent event) -> {
            // TODO Auto-generated method stub
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("JPG", "*.jpg"), new FileChooser.ExtensionFilter("png", "*.png"));
            File output = fileChooser.showSaveDialog(histogram.getOwner());
            WritableImage wi = barChart.snapshot(new SnapshotParameters(), null);
            try {
                if (output != null) {
                    BufferedImage image = SwingFXUtils.fromFXImage(wi, null);
                    BufferedImage imageRGB = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.OPAQUE);
                    
                    Graphics2D graphics = imageRGB.createGraphics();
                    graphics.drawImage(image, 0, 0, null);
                    ImageIO.write(imageRGB, fileChooser.getSelectedExtensionFilter().getDescription(), output);
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        });
        saveHistogram.setGraphic(new ImageView(new Image(App.class.getResourceAsStream("icon/camera_32x32.png"))));
        labelMeshInfo = new Label("Save Image");
        labelMeshInfo.setFont(Font.font(labelMeshInfo.getFont().getFamily(), FontWeight.BOLD, labelMeshInfo.getFont().getSize()));

        meshInfo.add(labelMeshInfo, 4, 0);
        meshInfo.add(saveHistogram, 4, 1);
        meshInfo.setRowSpan(saveHistogram, 2);

        GridPane histInfo = new GridPane();
        histInfo.setVgap(5);
        histInfo.setHgap(5);
        Label label = new Label("Range: ");
        label.setFont(Font.font(labelMeshInfo.getFont().getFamily(), FontWeight.BOLD, labelMeshInfo.getFont().getSize()));
        histInfo.add(label, 0, 0);

        label = new Label("Number of Element: ");
        label.setFont(Font.font(labelMeshInfo.getFont().getFamily(), FontWeight.BOLD, labelMeshInfo.getFont().getSize()));
        histInfo.add(label, 0, 1);

        histInfo.setAlignment(Pos.CENTER);
        histInfo.addRow(0, new Label("0-0.1"), new Label("0.1-0.2"), new Label("0.2-0.3"), new Label("0.3-0.4"), new Label("0.4-0.5"), new Label("0.5-0.6"), new Label("0.6-0.7"), new Label("0.7-0.8"), new Label("0.8-0.9"), new Label("0.9-1.0"));

        for (int i = 0; i < group.length; i++) {
            label = new Label(String.valueOf(group[i]));
            histInfo.add(label, i + 1, 1);
            histInfo.setHalignment(label, HPos.CENTER);
            histInfo.setValignment(label, VPos.CENTER);
        }
        vBox.getChildren().addAll(meshInfo, barChart, histInfo);

        DialogPane root = histogram.getDialogPane();
        root.setContent(vBox);
        histogram.showAndWait();

    }

    public void onPlotLineResult() {
        LinePlotResultDialog lpr = new LinePlotResultDialog(Project.getMesh());
        Optional<LineChart<Number, Number>> result = lpr.showAndWait();
        if (result.isPresent()) {
            VBox vBox = new VBox(5);
            LineChart<Number, Number> lineChart = lpr.getResult();
            Button saveGraph = new Button();
            Button exportData = new Button();
            ToolBar toolbar = new ToolBar(saveGraph, exportData);
            exportData.setTooltip(new Tooltip("Export Data"));
            exportData.setGraphic(new ImageView(new Image(App.class.getResourceAsStream("icon/export_16x16.png"))));
            exportData.setOnAction((ActionEvent event) -> {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Export Data Line..");
                fileChooser.setInitialFileName("PlotLine2D");
                fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("txt", "*.txt"), new FileChooser.ExtensionFilter("dat", "*.dat"));
                File output = fileChooser.showSaveDialog(lpr.getOwner());
                try {
                    if (output == null) {
                        return;
                    }
                    BufferedWriter bw = Files.newBufferedWriter(Paths.get(output.getAbsolutePath()), StandardCharsets.UTF_8);
                    ArrayList<Point2D> pts = lpr.getPoints();
                    ArrayList<Double> values = lpr.getValues();
                    bw.append("X\tY\tVALUE");
                    bw.newLine();
                    for (int i = 0; i < pts.size(); i++) {
                        bw.append(pts.get(i).getX() + " \t" + pts.get(i).getY() + " \t" + values.get(i));
                        bw.newLine();
                    }
                    bw.close();
                    
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    ExceptionDialog exceptionDialog = new ExceptionDialog(e);
                }
            });

            saveGraph.setTooltip(new Tooltip("Save Line Graph 2D"));
            saveGraph.setGraphic(new ImageView(new Image(App.class.getResourceAsStream("icon/camera_16x16.png"))));
            saveGraph.setOnAction((ActionEvent event) -> {
                FileChooser fileChooser = new FileChooser();
                fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("JPG", "*.jpg"), new FileChooser.ExtensionFilter("png", "*.png"));
                File output = fileChooser.showSaveDialog(lpr.getOwner());
                WritableImage wi = lineChart.snapshot(new SnapshotParameters(), null);
                try {
                    if (output != null) {
                        BufferedImage image = SwingFXUtils.fromFXImage(wi, null);
                        
                        BufferedImage imageRGB = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.OPAQUE);
                        Graphics2D graphics = (Graphics2D) imageRGB.getGraphics();
                        graphics.drawImage(image, 0, 0, null);
                        ImageIO.write(imageRGB, fileChooser.getSelectedExtensionFilter().getDescription(), output);
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    ExceptionDialog exceptionDialog = new ExceptionDialog(e);
                }
            });

            vBox.getChildren().addAll(toolbar, lineChart);
            TabPane CTTB = App.centralTopTabPane;
            CTTB.getTabs().add(new Tab("Plot Line 2D - " + CTTB.getTabs().size(), vBox));
        }
    }

    public void onExportMesh() {

    }

    public void onAssignBC(Event event) {
        resetViewMesh();
        resetViewResult();
        checkMaterialVisibility.setSelected(false);
        Project.setIsMaterialVisible(false);
        Project.setModelVisibility(true);

        CreateGeometry.end();
        IntersectGeometry.end();
        SelectGeometry.begin(GeometryType.BORDER);
        Button btn = (Button) event.getSource();
        ButtonsID btnID = ButtonsID.valueOf(btn.getId());
        lastButtonID = btnID;
    }

    public void onAssignDomainBC(Event event) {
        resetViewMesh();
        resetViewResult();
        Project.setModelVisibility(true);

        CreateGeometry.end();
        IntersectGeometry.end();
        SelectGeometry.begin(GeometryType.FACE);
        Button btn = (Button) event.getSource();
        ButtonsID btnID = ButtonsID.valueOf(btn.getId());
        lastButtonID = btnID;
    }

    public void onKeyPressedRoot(javafx.scene.input.KeyEvent keyEvent) {
        switch (keyEvent.getCode()) {
            case ESCAPE:
                if (lastButtonID == null) {
                    return;
                }
                switch (lastButtonID.getOperationType()) {
                    case CREATE:
                        finishCreateByEscape();
                        createFaceByEscape();
                        break;
                    case SELECT:
                        switch (lastButtonID) {
                            case BTN_LINE_DIVISION:
                                divideLineByEscape();
                                break;
                            case BTN_CIRCLE_DIVISION:
                                divideCircleByEscape();
                                break;
                            case BTN_BC_POTENTIAL:
                                assignBCPotentialByEscape();
                                break;
                            case BTN_BC_DENSITY:
                                assignBCDensityByEscape();
                                break;
                            case BTN_BC_SPACE_DENSITY:
                                assignBCSpaceDensityByEscape();
                                break;
                            case BTN_BC_PERM_FIELD:
                                assignPermanentFieldByEscape();
                                break;
                            case BTN_ASSIGN_MATERIAL:
                                assignMaterialByEscape();
                                break;
                            default:
                                if (lastButtonID.name().trim().contains("INTER")) {
                                    intersectByEscape();
                                }
                                if (SelectGeometry.started() && SelectGeometry.toRemove) {
                                    if (SelectGeometry.getGeometryType() == GeometryType.FACE) {
                                        for (Face2D face2d : SelectGeometry.getFacesSelected()) {
                                            if (face2d.getTypeFace() == Face2D.EXTERNAL) {
                                                BTN_CEFACE.setDisable(false);
                                                break;
                                            }
                                        }
                                    }
                                    Project.removeAllGeometry(SelectGeometry.getSelection());
                                    SelectGeometry.clear();
                                    SelectGeometry.end();
                                    lastButtonID = null;
                                }
                                break;
                        }
                        break;
                    default:
                        break;
                }
                break;

            default:
                break;
        }
    }

    private void createFaceByEscape() {
        if (SelectGeometry.started() && SelectGeometry.isEmpty()) {
            CreateGeometry.end();
            SelectGeometry.end();

            resetViewMesh();
            resetViewResult();

            lastButtonID = null;
        } else if (SelectGeometry.started() && !SelectGeometry.isEmpty()) {
            if (!lastButtonID.name().trim().contains("FACE")) {
                return;
            }
            List<Border2D> bordes = Face2D.organizeBorders(SelectGeometry.getBordersSelected());

            if (bordes != null) {
                Face2D face = new Face2D();

                for (Border2D border2d : bordes) {
                    face.addBorder(border2d);
                }
                face.findPolygon();
                ConstrainedDelaunayTriangulation C = new ConstrainedDelaunayTriangulation();
                C.discretizeFace(face);
                Alert alert = new Alert(AlertType.INFORMATION);
                Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                alert.setTitle("Face Created");
                alert.setHeaderText(null);
                switch (lastButtonID) {
                    case BTN_CNFACE:
                        face.setTypeFace(Face2D.NORMAL);
                        alert.setContentText("Normal Face Created!!!!");
                        alert.setGraphic(new ImageView(new Image(App.class.getResourceAsStream("icon/normal_face_32x32.png"))));
                        stage.getIcons().add(new Image(App.class.getResourceAsStream("icon/normal_face_32x32.png")));
                        break;
                    case BTN_CBFACE:
                        face.setTypeFace(Face2D.HOLE);
                        face.setMaterial(null);
                        alert.setContentText("Hole Face Created!!!!");
                        alert.setGraphic(new ImageView(new Image(App.class.getResourceAsStream("icon/hole_face_32x32.png"))));
                        stage.getIcons().add(new Image(App.class.getResourceAsStream("icon/hole_face_32x32.png")));
                        break;
                    case BTN_CEFACE:
                        face.setTypeFace(Face2D.EXTERNAL);
                        alert.setContentText("External Face Created!!!!");
                        alert.setGraphic(new ImageView(new Image(App.class.getResourceAsStream("icon/external_face_32x32.png"))));
                        stage.getIcons().add(new Image(App.class.getResourceAsStream("icon/external_face_32x32.png")));
                        BTN_CEFACE.setDisable(true);
                        break;
                    default:
                        break;
                }

                Project.addGeometry(face);
                SelectGeometry.clear();
                alert.showAndWait();

            } else {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Create Face Error");
                alert.setHeaderText(null);
                alert.setContentText("Face must be closed!!!!");
                alert.showAndWait();
                SelectGeometry.clear();
            }
        }
    }

    private void finishCreateByEscape() {
        if (CreateGeometry.started() && CreateGeometry.isReseted()) {
            SelectGeometry.end();
            CreateGeometry.end();
            lastButtonID = null;

            resetViewMesh();
            resetViewResult();

        } else if (CreateGeometry.started() && !CreateGeometry.isReseted()) {
            if (lastButtonID == ButtonsID.BTN_CPOLYLINE) {
                Polyline2D polyline = (Polyline2D) CreateGeometry.getGeometry();
                Project.addAllGeometry(polyline.edges());
                CreateGeometry.reset();
            } else if (lastButtonID == ButtonsID.BTN_CPOLYGON) {
                Polygon2D poly = (Polygon2D) CreateGeometry.getGeometry();
                if (!poly.isClosed() && poly.getSize() >= 3) {
                    poly.addPoint(poly.getFirstPoint());
                    if (poly.isClosed()) {
                        Project.addGeometry(poly);
                        CreateGeometry.reset();
                    }
                }
            } else if (lastButtonID == ButtonsID.BTN_CBEZIER) {
                BezierCurve2D bezier = (BezierCurve2D) CreateGeometry.getGeometry();

                Project.addGeometry(bezier.copy());
                CreateGeometry.reset();
            }
        }
    }

    public void divideCircleByEscape() {
        if (SelectGeometry.started() && SelectGeometry.isEmpty()) {
            SelectGeometry.end();
            lastButtonID = null;

            resetViewMesh();
            resetViewResult();
            return;
        }
        List<Circle2D> circles = SelectGeometry.getCirclesSelected();
        if (!circles.isEmpty()) {
            TextInputDialog textInput = new TextInputDialog("2");
            textInput.setTitle("Circle Division");
            textInput.setHeaderText("Enter with the division number");
            textInput.setContentText("Division Number:");
            textInput.setGraphic(new ImageView(new Image(App.class.getResourceAsStream("icon/circle_division_64x64.png"))));
            Optional<String> result = textInput.showAndWait();
            if (result.isPresent()) {
                int n = Integer.parseInt(result.get());
                for (Circle2D circle2d : circles) {
                    Project.addAllGeometry(circle2d.division(n));
                    Project.removeGeometry(circle2d);;
                }
                lastButtonID = null;

            }
            SelectGeometry.clear();
        }
    }

    public void divideLineByEscape() {
        if (SelectGeometry.started() && SelectGeometry.isEmpty()) {
            SelectGeometry.end();
            lastButtonID = null;

            resetViewMesh();
            resetViewResult();
            return;
        }
        List<Line2D> lines = SelectGeometry.getLinesSelected();
        if (!lines.isEmpty()) {
            TextInputDialog textInput = new TextInputDialog("2");
            textInput.setTitle("Line Division");
            textInput.setHeaderText("Enter with the division number");
            textInput.setContentText("Division Number:");
            textInput.setGraphic(new ImageView(new Image(App.class.getResourceAsStream("icon/line_division_64x64.png"))));
            textInput.getEditor().textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                if (newValue.trim().isEmpty()) {
                    Node okBtn = (Node) textInput.getDialogPane().lookupButton(ButtonType.OK);
                    okBtn.setDisable(true);
                }
            });

            Optional<String> result = textInput.showAndWait();
            if (result.isPresent()) {
                int n = Integer.parseInt(result.get());
                for (Line2D line2d : lines) {
                    Project.addAllGeometry(line2d.division(n));
                    Project.removeGeometry(line2d);;

                }
                lastButtonID = null;

            }
            SelectGeometry.clear();
        }
    }

    public void intersectByEscape() {
        if (IntersectGeometry.started() && SelectGeometry.started() && SelectGeometry.isEmpty()) {
            IntersectGeometry.end();
            SelectGeometry.end();
            lastButtonID = null;

            resetViewMesh();
            resetViewResult();
            return;
        }
        IntersectGeometry.compute();
        IntersectGeometry.reset();
    }

    public void assignBCPotentialByEscape() {
        if (SelectGeometry.started() && SelectGeometry.isEmpty()) {
            SelectGeometry.end();
            lastButtonID = null;

            resetViewMesh();
            resetViewResult();

            return;
        }
        List<Border2D> borders = SelectGeometry.getBordersSelected();
        if (!borders.isEmpty()) {
            DefineBCPotentialDialog bcPotential = new DefineBCPotentialDialog(Project.getProblemType());
            Optional<String> result = bcPotential.showAndWait();

            if (result.isPresent()) {
                for (Border2D border : borders) {
                    border.setConditionExpression(new DirichletConditionExpression(result.get()));
                    border.setBoundaryConditionType(BoundaryConditionType.DIRICHLET);
                };

            }
            SelectGeometry.clear();
        }
    }

    public void assignBCDensityByEscape() {
        if (SelectGeometry.started() && SelectGeometry.isEmpty()) {
            SelectGeometry.end();
            lastButtonID = null;

            resetViewMesh();
            resetViewResult();
            return;
        }
        List<Border2D> borders = SelectGeometry.getBordersSelected();
        if (!borders.isEmpty()) {
            DefineBCDensityDialog bcPotential = new DefineBCDensityDialog(Project.getProblemType());
            Optional<String> result = bcPotential.showAndWait();

            if (result.isPresent()) {
                for (Border2D border : borders) {
                    border.setConditionExpression(new RobinConditionExpression(result.get(), "0"));
                    border.setBoundaryConditionType(BoundaryConditionType.ROBIN);
                };

            }
            SelectGeometry.clear();
        }
    }

    public void assignBCSpaceDensityByEscape() {
        if (SelectGeometry.started() && SelectGeometry.isEmpty()) {
            SelectGeometry.end();
            lastButtonID = null;
            resetViewMesh();
            resetViewResult();
            return;
        }
        List<Face2D> faces = SelectGeometry.getFacesSelected();
        if (!faces.isEmpty()) {
            DefineBCSpaceDensityDialog bcPotential = new DefineBCSpaceDensityDialog(Project.getProblemType());
            Optional<String> result = bcPotential.showAndWait();

            if (result.isPresent()) {
                faces.forEach((face) -> {
                    face.spaceDensityFunction = new SpaceDensityFunction(result.get());
                });
;
            }
            SelectGeometry.clear();
        }
    }

    public void assignPermanentFieldByEscape() {
        if (SelectGeometry.started() && SelectGeometry.isEmpty()) {
            SelectGeometry.end();
            lastButtonID = null;
            resetViewMesh();
            resetViewResult();
            return;
        }
        List<Face2D> faces = SelectGeometry.getFacesSelected();
        if (!faces.isEmpty()) {
            DefinePermanentFieldDialog bcPotential = new DefinePermanentFieldDialog(Project.getProblemType());
            Optional<PermanentField> result = bcPotential.showAndWait();

            if (result.isPresent()) {
                for (Face2D face : faces) {
                    face.setPermanentField(result.get());
                };
            }
            SelectGeometry.clear();
        }
    }

    public void onAssignMaterial(Event event) {
        Project.setModelVisibility(true);
        checkMaterialVisibility.setSelected(false);
        Project.setIsMaterialVisible(false);

        resetViewMesh();
        resetViewResult();

        CreateGeometry.end();
        IntersectGeometry.end();
        SelectGeometry.begin(GeometryType.FACE);
        Button btn = (Button) event.getSource();
        ButtonsID btnID = ButtonsID.valueOf(btn.getId());
        lastButtonID = btnID;
    }

    public void assignMaterialByEscape() {
        if (SelectGeometry.started() && SelectGeometry.isEmpty()) {
            SelectGeometry.end();
            lastButtonID = null;
            resetViewMesh();
            resetViewResult();

            return;
        }
        List<Face2D> faces = SelectGeometry.getFacesSelected();
        if (!faces.isEmpty()) {

            int index = CB_MATERIAL.getSelectionModel().getSelectedIndex();
            if (index == -1) {
                SelectGeometry.clear();
                return;
            }
            for (Face2D face : faces) {
                if (face.getTypeFace() == Face2D.HOLE) {
                    face.setMaterial(null);
                } else {
                    face.setMaterial(RepositoryMaterials.getMaterial(index));
                }
            };
            SelectGeometry.clear();
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Material inserido");
            alert.setHeaderText("O material foi adicionado com sucesso");
            alert.setContentText("O material " + CB_MATERIAL.getSelectionModel().getSelectedItem() + " foi inserido nas faces selecionadas");
            alert.showAndWait();
            Project.treeView.updateFacesItems();
        }
    }

    public void onCheckMaterialVisibility(Event event) {
        Project.setModelVisibility(true);
        CheckBox check = (CheckBox) event.getSource();
        Project.setIsMaterialVisible(check.isSelected());
    }

    private void resetViewMesh() {
        checkViewMesh.setSelected(false);
        checkViewMeshQuality.setSelected(false);
        ConectivityList mesh = Project.getMesh();
        if (mesh != null) {
            mesh.setMeshQualityVisible(false);
            mesh.setMeshVisible(false);
        }
    }

    private void resetViewResult() {
        TGLBTN_COLORMAP.setSelected(false);
        TGLBTN_ISOLINE.setSelected(false);
        TGLBTN_ARROW.setSelected(false);
        ConectivityList mesh = Project.getMesh();
        if (mesh != null) {
            mesh.setColorMapVisible(false);
            mesh.setArrowsVisible(false);
            mesh.setIsolinesVisible(false);
        }

    }

    private void alertMeshNull() {
        Alert alert = new Alert(AlertType.WARNING);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();

        stage.getIcons().add(new Image(App.class.getResourceAsStream("icon/meshing_32x32.png")));
        alert.setTitle("Mesh Not Detect");
        alert.setHeaderText("Please discretize the domain");
        alert.setContentText("Please, you have to discretize the domain before compute analysis");
        alert.showAndWait();
    }

    private void alertSimulationNull() {
        Alert alert = new Alert(AlertType.WARNING);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();

        stage.getIcons().add(new Image(App.class.getResourceAsStream("icon/meshing_32x32.png")));
        alert.setTitle("Simulation not detected");
        alert.setHeaderText("Please compute FEM process");
        alert.setContentText("Please, you have to compute analysis before see results");
        alert.showAndWait();
    }

    public void onExit() {
        Platform.exit();
        System.exit(0);
    }

}
