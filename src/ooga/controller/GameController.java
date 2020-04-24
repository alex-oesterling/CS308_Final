package ooga.controller;

import com.github.strikerx3.jxinput.exceptions.XInputNotLoadedException;


import java.util.ArrayList;
import java.util.List;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import ooga.model.CollisionEngine;
import ooga.model.PhysicsEngine;
import ooga.util.GamePadListener;
import ooga.model.levels.InfiniteLevelBuilder;


import ooga.model.levels.LevelSelector;
import ooga.util.GameParser;

import ooga.view.gui.managers.StageManager;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class GameController implements Controller {

  private PhysicsEngine physicsEngine;
  private CollisionEngine collisionEngine;
  private EntityWrapper entityWrapper;
  private List<EntityWrapper> entityList;
  private List<EntityWrapper> player;
  private List<EntityWrapper> entityBrickList;
  private List<EntityWrapper> entityBuffer;
  private List<EntityWrapper> entityRemove;
  private static final int FRAMES_PER_SECOND = 60;
  private static final int MILLISECOND_DELAY = 1000 / FRAMES_PER_SECOND;
  private static final double SECOND_DELAY = 1.0 / FRAMES_PER_SECOND;
  private static final String TXT_FILEPATH = "src/resources/";


  private Timeline animation;
  private InfiniteLevelBuilder builder;
  private ViewManager myViewManager;
  private LevelSelector levelSelector;
  private GamePadListener g;
  private GameParser gameParser;
  private ControlSchemeSwitcher myControlSchemeSwitcher;



  public GameController(StageManager stageManager, String gameName, boolean loadedGame) throws XInputNotLoadedException { //FIXME add exception stuff
    builder = new InfiniteLevelBuilder(this);
    g = new GamePadListener();

    gameParser = new GameParser(gameName, this, loadedGame);
    myViewManager = new ViewManager(stageManager, builder, gameParser.getPlayerList());

    entityList = new ArrayList<>();
    entityBuffer = new ArrayList<>();
    entityRemove = new ArrayList<>();
//    EntityWrapper player = new EntityWrapper("Mario_Fire", this);

    for(EntityWrapper player : gameParser.getPlayerList()){
      entityList.add(player);
      myViewManager.updateEntityGroup(player.getRender());
    }

    physicsEngine = new PhysicsEngine(gameParser.parsePhysicsProfile()); //TODO: add PhysicsProfile object
    collisionEngine = new CollisionEngine();
    myViewManager.getTestScene().setOnKeyPressed(e -> {

      myViewManager.handlePressInput(e.getCode());
      for(EntityWrapper entity : entityList){
        entity.handleKeyInput(e.getCode().toString());//FIXME i would like to
      }
    });
    myViewManager.getTestScene().setOnKeyReleased(e-> {
      for(EntityWrapper entity : entityList){
        entity.handleKeyReleased(e.getCode().toString());//FIXME i would like to
      }
    });

    myViewManager.setUpCamera(gameParser.getPlayerList()); //FIXME to be more generalized and done instantly


    levelSelector = new LevelSelector(gameParser.parseLevels());
    setUpTimeline();

  }

  private void setUpTimeline() {
    //TODO: Timeline Code -- don't remove
    KeyFrame frame = new KeyFrame(Duration.millis(MILLISECOND_DELAY), e ->
    {
      try {
        step(SECOND_DELAY);
      } catch (XInputNotLoadedException ex) {
        ex.printStackTrace();
      } catch (Exception exception) {
        exception.printStackTrace(); //FIXME: REPLACE TO AVOID FAIL CASE
      }
    });
    animation = new Timeline();
    animation.setCycleCount(Timeline.INDEFINITE);
    animation.getKeyFrames().add(frame);
    animation.play();

  }

  private void step (double elapsedTime) throws Exception {
    g.update();
    myViewManager.handleMenuInput();
    if (gameParser.getPlayerList().size() > 1) { //FIXME: TESTCODE FOR CONTROLLER EVENTUALLY SUPPORT SIMUL CONTROLSCHEMES
      if (g.getState() != null) {
        if (!g.getState().getPressed()) {
          System.out.println("PRESSED");
          gameParser.getPlayerList().get(1).handleControllerInputPressed(g.getState().getControl());
        } else if (g.getState().getPressed()) {
          System.out.println("RELEASED");
          gameParser.getPlayerList().get(1).handleControllerInputReleased(g.getState().getControl());
        }
      }
    }
    if (!myViewManager.getIsGamePaused()) {
      levelSelector.updateCurrentLevel(entityList, myViewManager);
      handleSaveGame();
      myViewManager.updateValues();
      //TODO: Consider making one method in Level.java as updateLevel() for the methods above^, although I concern about whether or not spawnEntities would get an up-to-date EntityList

      for (EntityWrapper subjectEntity : entityList) {
        for (EntityWrapper targetEntity : entityList) {
          collisionEngine.produceCollisionActions(subjectEntity.getModel(), targetEntity.getModel());
          if(targetEntity.getModel().getIsDead()) {
            myViewManager.removeEntityGroup(targetEntity.getRender()); //TODO: fix so not jut goombas
          }
        }
        subjectEntity.update(elapsedTime);
        physicsEngine.applyForces(subjectEntity.getModel());
      }
      entityList.addAll(entityBuffer);
      entityBuffer = new ArrayList<>();
      if(entityRemove.size() > 0) {
        //System.out.println("Removed: " + entityRemove.get(0).getModel().getEntityID());
      }
      entityList.removeAll(entityRemove);
      entityRemove = new ArrayList<>();

    }
    entityList.addAll(entityBuffer);
    entityBuffer = new ArrayList<>();
  }

  private void handleSaveGame() {
    if(myViewManager.getSaveGame()) {
      JSONArray saveGame = new JSONArray();
      JSONObject obj = new JSONObject();
      for(int i = 0; i < levelSelector.getLevelsToPlay().size(); i++) {
        obj.put("Level_" + (i+1), levelSelector.getLevelsToPlay().get(i).getLevelName());
      }
      saveGame.add(obj);
      gameParser.saveGame("levelArrangement", saveGame);
      myViewManager.setSaveGame();
    }
  }

  @Override
  public void removeEntity(EntityWrapper node) {
    myViewManager.removeEntity(node.getRender());
  }

  @Override
  public void addEntity(EntityWrapper newEntity) {
    entityBuffer.add(newEntity);
    myViewManager.addEntity(newEntity.getRender());
  }


  @Override
  public List<EntityWrapper> getEntityList() {
    return entityList;
  }
}
