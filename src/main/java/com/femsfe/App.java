package com.femsfe;

import java.net.URL;

import com.femsfe.ComplexNumber.ComplexNumber;
import com.femsfe.Geometrias.Material;
import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class App extends Application{

    private static final FXGLPanel fxglPanel = new FXGLPanel();
    public static Parent root;
    public static Scene scene;
    public static TreeView<GeometryLabelItem> treeView = new TreeViewProject();
    private static final PromptComannd prompt = new PromptComannd();
    private static final TextArea infoArea = new TextArea();
    private static Stage primaryStage;
    public static TabPane centralTopTabPane;

    public static ButtonsID lastButtonID;

    @Override
    public void start(Stage primaryStage) {

        App.primaryStage = primaryStage;
        primaryStage.setTitle("Finite Element Methods for  Static Field in Eletromagnetism - New Project.jfem*");
        URL url = getClass().getResource("icon/icon_surface.jpg");

        primaryStage.getIcons().add(new Image(url.toExternalForm()));
        primaryStage.setOnCloseRequest((WindowEvent event) -> {
            Platform.exit();
            System.exit(0);
        });

        try {
            root = FXMLLoader.load(App.class.getResource("fxml/Main.fxml"));
            scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("styles/application.css").toExternalForm());

            primaryStage.setScene(scene);
            primaryStage.show();

            Project.treeView = (TreeViewProject) treeView;

            RepositoryMaterials.addMaterial(new Material("Air", new ComplexNumber(1, 0), new ComplexNumber(1, 0)));
            RepositoryMaterials.addMaterial(new Material("Silicon", new ComplexNumber(12, 0), new ComplexNumber(1, 0)));

            ComboBox<String> cbMaterial = (ComboBox<String>) root.lookup("#comboBoxMaterial");
            RepositoryMaterials.getList().forEach((material) -> {
                cbMaterial.getItems().add(material.getName());
            });
            cbMaterial.getSelectionModel().select(0);

            fxglPanel.setScene(scene);
            fxglPanel.setCursor(null);

            SplitPane sPaneLeft = (SplitPane) root.lookup("#splitPaneLeft");
            sPaneLeft.getItems().remove(0);
            sPaneLeft.getItems().add(0, treeView);

            SplitPane sPane = (SplitPane) root.lookup("#splitPaneCenter");

            TabPane tabPanePrompt = (TabPane) sPane.lookup("#tabPanePrompt");
            tabPanePrompt.getTabs().get(0).setGraphic(new ImageView(new Image(getClass().getResource("icon/prompt_16x16.png").toExternalForm())));
            tabPanePrompt.getTabs().get(0).setContent(prompt);

            tabPanePrompt.getTabs().add(new Tab("Info"));
            tabPanePrompt.getTabs().get(1).setGraphic(new ImageView(new Image(getClass().getResourceAsStream("icon/info_16x16.png"))));
            tabPanePrompt.getTabs().get(1).setContent(infoArea);
            infoArea.setEditable(false);

            sPane.getItems().clear();

            Tab graphWindow = new Tab("Graphic Window", fxglPanel);
            graphWindow.setClosable(false);

            centralTopTabPane = new TabPane(graphWindow);

            sPane.getItems().addAll(centralTopTabPane, tabPanePrompt);

            infoArea.appendText(System.getProperty("sun.arch.data.model") + "Bits Java ");
            infoArea.appendText("Total Memory:" + Runtime.getRuntime().totalMemory() / 1024000 + "MB\n");
            infoArea.appendText("Max Memory:" + Runtime.getRuntime().maxMemory() / 1024000 + "MB\n");
            infoArea.appendText("Free Memory:" + Runtime.getRuntime().freeMemory() / 1024000 + "MB\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static FXGLPanel getGraphicPanel() {
        return App.fxglPanel;
    }

    public static void setTitle(String title) {
        primaryStage.setTitle(title);
    }

    public static String getTitle() {
        return primaryStage.getTitle();
    }

    public static void setPathProject(String path) {
        Label label = (Label) root.lookup("#labelPathProject");
        label.setText("Path Project: " + path);
    }

    public ToggleButton getToggleButtonByName(String name) {
        return (ToggleButton) root.lookup("#" + name);
    }

    public static PromptComannd getPromptComannd() {
        return prompt;
    }

    public static TextArea getInfoArea() {
        return infoArea;
    }

    public static void focusInfoArea() {
        SplitPane sPane = (SplitPane) root.lookup("#splitPaneCenter");

        TabPane tabPanePrompt = (TabPane) sPane.lookup("#tabPanePrompt");
        tabPanePrompt.getSelectionModel().select(1);
    }

    public static void main(String[] args) {
        launch();

    }

}
