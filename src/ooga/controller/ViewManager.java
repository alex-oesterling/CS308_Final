package ooga.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.effect.BoxBlur;
import javafx.scene.input.KeyCode;

import javafx.scene.layout.*;
import javax.swing.text.html.parser.Entity;
import ooga.apis.view.ViewExternalAPI;
import ooga.view.application.Camera;
import ooga.view.application.menu.ConfigurationMenu;
import ooga.view.application.menu.InGameMenu;
import ooga.view.gui.managers.StageManager;

/**
 * This class handles the front end views needed by the controller
 */
public class ViewManager implements ViewExternalAPI {
  private static final String RESOURCES_PACKAGE = "resources.guiText";
  private ResourceBundle myResources = ResourceBundle.getBundle(RESOURCES_PACKAGE);
  private final String DEFAULT_MENU_TEXT = myResources.getString("defaultStatus");
  private BorderPane testPane;
  private Group entityGroup;
  private List<Node> overlay = new ArrayList<>();
  private InGameMenu menu;
  private ConfigurationMenu config;
  private StageManager currentStage;
  private BorderPane level;
  private Camera camera;
  private Scene testScene;
  private int escCounter = 0;
  private int configCounter = 0;
  private boolean saveGame = false;
  private boolean isGamePaused = false;

  /**
   * Construcotr for view managers
   * @param stageManager : stage manager
   * @param playerList : list of players
   */
  public ViewManager(StageManager stageManager, List<EntityWrapper> playerList){
    menu = new InGameMenu();
    config = new ConfigurationMenu(playerList);
    overlay.add(menu);
    overlay.add(config);
    currentStage = stageManager;
    level = new BorderPane();
    testPane = level;
    testScene = currentStage.getCurrentScene();
    testScene.setRoot(testPane);
    entityGroup = new Group();
    level.getChildren().add(entityGroup);
    setUpPane();
    testScene = stageManager.getCurrentScene();
  }



  public void updateCamera() {
    camera.update(overlay);
  }

  public void updateEntityRenders(List<EntityWrapper> currentEntityList, List<EntityWrapper> entitiesToDespawn){
    for(EntityWrapper targetEntity : currentEntityList){
      if(!entityGroup.getChildren().contains(targetEntity.getRender())){
        addEntity(targetEntity.getRender());
      }
    }
    for(EntityWrapper despawnedEntity : entitiesToDespawn){
      removeEntity(despawnedEntity.getRender());
    }
  }




  private void setUpPane(){
    level = new BorderPane();
    testPane = level;
    testScene = currentStage.getCurrentScene();
    testScene.setRoot(testPane);
    entityGroup = new Group();
    level.getChildren().add(entityGroup);
  }


  /**
   * returns level
   * @return level
   */
  public Pane getLevel() {
    return level;
  }

  /**
   * sets up scrolling camera
   * @param node entity list
   * @param scrollStatusX : whether x scroll is enabled
   * @param scrollStatusY : whether y scroll is enabled
   */
  public void setUpCamera(List<EntityWrapper> node, int scrollStatusX, int scrollStatusY) { camera = new Camera(currentStage.getStage(), level, node, scrollStatusX, scrollStatusY); }

  /**
   * returns the scene
   * @return testSCene
   */
  public Scene getTestScene() {
    return testScene;
  }

  /**
   * sets the new level
   * @param levelBuilt new level built
   */
  public void setLevel(BorderPane levelBuilt) {
    this.level = levelBuilt;
  }

  /**
   * updates camera values
   */
  public void updateValues() {
    camera.update(overlay);
  }

  /**
   * remove entity from view
   * @param node - the entity to be removed
   */
  @Override
  public void removeEntity(Node node) {
    entityGroup.getChildren().remove(node);
  }

  /**
   * add entity to view
   * @param node
   */
  @Override
  public void addEntity(Node node) {
    entityGroup.getChildren().add(node);
  }

  /**
   * handle key inputs
   * @param code
   */
  public void handlePressInput (KeyCode code) {
    if (code == KeyCode.ESCAPE && escCounter < 1) {
      pauseGame(); }
    else if (code == KeyCode.Q && escCounter == 1) {
      unPauseGame(); }
    else if (code == KeyCode.H) {
      pauseGame();
      goHome(code.getChar()); }
    else if(code == KeyCode.X) {
      saveGame = true; }
  }

  /**
   * returns if game should be save
   * @return true if game should be played
   */
  public boolean getSaveGame() {
    return saveGame;
  }

  /**
   * handles inputs from pause menu
   * @throws Exception
   */
  public void handleMenuInput() throws Exception {
    resumeGame();
    saveGame();
    exitGame();
    editControls();
    rebootGame();
    editVolume();
  }

  private void editVolume() {
    currentStage.getAvManager().setMusicVolume(config.getMusicVolume());
    currentStage.getAvManager().setFXVolume(config.getFXVolume());
  }

  private void rebootGame() throws Exception {
    if (menu.getRebootPressed()){
      menu.setRebootOff();
      currentStage.reboot(); }
  }

  private void editControls() {
    if (menu.getControlsPressed()){
      menu.setControlsOff();
      if(configCounter < 1 && menu.getStatus().equals(DEFAULT_MENU_TEXT)){
        launchConfigMenu(); } }
    if(config.getExitPressed()){
      config.setExitOff();
      handlePressInput(KeyCode.Q); }
  }

  private void exitGame() {
    if (menu.getExitPressed()) {
      handlePressInput(KeyCode.H);
      menu.setExitOff(); }
  }

  private void saveGame() {
    if (menu.getSavePressed()) {
      saveGame=true;
      unPauseGame();
      menu.setSaveOff(); }
  }

  private void resumeGame() {
    if (menu.getResumePressed()) {
      unPauseGame();
      menu.setResumeOff(); }
  }

  private void goHome(String state){
    currentStage.updateCurrentScene(currentStage.getCurrentTitle(), currentStage.getCurrentScene());
    currentStage.updateCurrentScene(state, currentStage.getPastScene());
    currentStage.switchScenes(myResources.getString("GameSelect"));
  }

  public void endGame() {
    goHome(KeyCode.H.getChar());
  }

  /**
   * pauses the game
   */
  public void pauseGame(){
    BoxBlur bb = new BoxBlur();
    entityGroup.setEffect(bb);
    isGamePaused = true;
    testPane.setLeft(menu);
    escCounter++;
  }

  /**
   * launches config menu for user to change configs
   */
  private void launchConfigMenu(){
    testPane.setCenter(config);
    configCounter++;
  }

  /**
   * unpauses the game
   */
  private void unPauseGame(){
    updateMenu(DEFAULT_MENU_TEXT);
    testPane.getChildren().remove(config);
    testPane.getChildren().remove(menu);
    entityGroup.setEffect(null);
    isGamePaused = false;
    escCounter--;
    configCounter = 0;
  }

  /**
   * updates the menu with results
   * @param text result
   */
  public void updateMenu(String text) {
    menu.updateGameResult(text);
  }

  /**
   * returns if game should be paused
   * @return if game is paused
   */
  public boolean getIsGamePaused() {
    return isGamePaused;
  }

  /**
   * sets game should be paused
   */
  public void setGamePaused() {
    isGamePaused = true;
  }

  /**
   * sets whether game should be saved
   */
  public void setSaveGame() {
    saveGame = !saveGame;
  }

  /**
   * returns camera
   * @return camera
   */
  public Camera getCamera(){
    return camera;
  }

//  /**
//   * sets up level pane
//   */
//  private void setUpPane(){
//    level = new BorderPane();
//    testPane = level;
//    testScene = currentStage.getCurrentScene();
//    testScene.setRoot(testPane);
//    EntityGroup = new Group();
//    level.getChildren().add(EntityGroup);
//  }

}
