package ooga.view.gui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.util.Duration;
import ooga.exceptions.DisplayExceptions;
import ooga.view.gui.managers.AudioVideoManager;
import ooga.view.gui.managers.StageManager;
import ooga.view.gui.userinterface.GameCabinet;
import ooga.view.gui.userinterface.Welcome;

import java.io.FileNotFoundException;
import java.util.ResourceBundle;

public class ProgramLauncher {
    public static final String UNEXPECTED_ERROR = "Unexpected Error";
    public static final String ERROR_MSG = "Unexpected Error. Please check data files and run again.";
    private final String RESOURCES_PACKAGE = "resources.guiText";
    private ResourceBundle myResources = ResourceBundle.getBundle(RESOURCES_PACKAGE);
    private static final double FRAMES_PER_SECOND = 60;
    private static final double SECOND_DELAY = 1.0 / FRAMES_PER_SECOND;
    private Timeline animation;
    private Welcome welcomeScreen;
    private GameCabinet library;
    private AudioVideoManager audioVideoManager;
    private StageManager stageManager;

    public ProgramLauncher(Stage primaryStage) throws FileNotFoundException {
        this.welcomeScreen = new Welcome();
        this.audioVideoManager = new AudioVideoManager();
        this.stageManager = new StageManager(primaryStage, audioVideoManager);;
    }

    public void start() throws Exception {
        library = new GameCabinet(stageManager, audioVideoManager);
        initBootupScreen();
        startAnimationLoop();
    }

    private void initBootupScreen(){
        stageManager.createAndSwitchScenes(welcomeScreen);
        audioVideoManager.switchMusic(stageManager);
    }

    private void startAnimationLoop() {
        KeyFrame frame = new KeyFrame(Duration.seconds(SECOND_DELAY), e -> {
            try {
                step(); }
            catch (DisplayExceptions ex) {
                animation.pause();
                ex.displayAlert();
                stageManager.reboot();
            } catch(Exception ex) {
                animation.pause();
                createAlert();
                stageManager.reboot();
            }

        });
        animation = new Timeline();
        animation.setCycleCount(Timeline.INDEFINITE);
        animation.getKeyFrames().add(frame);
        animation.play();
    }

    private void step() throws Exception {
        if(welcomeScreen.getPlayPressed()){
            welcomeScreen.setPlayPressedOff();
            stageManager.createAndSwitchScenes(library, myResources.getString("GameSelect"));
            stageManager.updateCurrentScene(myResources.getString("GameSelect"), library.getScene());
        }
        library.updateCurrentGame();
    }

    private void createAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(UNEXPECTED_ERROR);
        alert.setHeaderText(ERROR_MSG);
        alert.show();
    }

}
