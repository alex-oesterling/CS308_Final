package ooga.view.application;

import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import ooga.controller.TestController;

import java.io.File;

public class TestSandboxBlue {
    private static final int SCENE_WIDTH = 1280;
    private static final int SCENE_HEIGHT = 720;
    private Scene myScene;
    private Pane myBackgroundPane;
    private Stage currentStage;
    private Group group;
    private TestController testController;
    private Scene oldScene;


    public TestSandboxBlue(Stage stage) {
        this.currentStage = stage;
        oldScene = currentStage.getScene();
        initModel();
        initView();
        initStage(stage);
        initController();
    }

    private void initModel() {

    }

    private void initView() {
        myBackgroundPane = new Pane();
        BackgroundFill commandBackground = new BackgroundFill(Color.BLUE, CornerRadii.EMPTY, Insets.EMPTY);
        myBackgroundPane.setBackground(new Background(commandBackground));
    }

    private void initController() { //FIXME ADD ERROR HANDLING
        testController = new TestController(myBackgroundPane, myScene, currentStage, oldScene);
    }

    private void initStage(Stage primaryStage) {
        myScene = new Scene(myBackgroundPane, SCENE_WIDTH, SCENE_HEIGHT);
        primaryStage.setScene(myScene);
        primaryStage.show();
    }
}