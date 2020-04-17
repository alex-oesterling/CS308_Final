package ooga.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import ooga.model.CollisionEngine;
import ooga.model.PhysicsEngine;
import ooga.model.levels.InfiniteLevelBuilder;
import ooga.model.levels.FiniteLevel;
import ooga.model.levels.Level;
import ooga.model.levels.LevelSelecter;
import ooga.util.LevelParser;
import ooga.view.gui.managers.StageManager;

import java.util.ArrayList;
import java.util.List;

public class FiniteLevelController implements Controller {

  private PhysicsEngine physicsEngine;
  private CollisionEngine collisionEngine;
  private EntityWrapper entityWrapper;
  private List<EntityWrapper> entityList;
  private List<EntityWrapper> entityBrickList;
  private List<EntityWrapper> entityBuffer;
  private static final int FRAMES_PER_SECOND = 60;
  private static final int MILLISECOND_DELAY = 1000 / FRAMES_PER_SECOND;
  private static final double SECOND_DELAY = 1.0 / FRAMES_PER_SECOND;

  private Timeline animation;
  private InfiniteLevelBuilder builder;
  private ViewManager myViewManager;
  private LevelSelecter levelSelecter;




  public FiniteLevelController(StageManager stageManager) { //FIXME add exception stuff

    //TODO: Quick and dirty nodes for testing purpose -- replace with Entity stuff
    builder = new InfiniteLevelBuilder(this);


    myViewManager = new ViewManager(stageManager, builder, null);

    entityList = new ArrayList<>();
    entityBrickList = new ArrayList<>();
    entityBuffer = new ArrayList<>();
    //myViewManager.setUpCamera(entityList.get(0).getRender());

    physicsEngine = new PhysicsEngine("dummyString");
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

    setUpTimeline();

    LevelParser parser = new LevelParser("SampleLevel", this);
    LevelParser p2 = new LevelParser("Level2", this);

    List<EntityWrapper> tiles = parser.parseTileEntities();
    List<EntityWrapper> player = parser.parsePlayerEntities();
    List<EntityWrapper> enemy = parser.parseEnemyEntities();
    for(EntityWrapper k : player){
      entityList.add(k);
      myViewManager.updateEntityGroup(k.getRender());
    }
    myViewManager.setUpCamera(entityList.get(0).getRender()); //FIXME to be more generalized and done instantly
    Level t1 = new FiniteLevel(tiles, player, enemy);
    Level t2 = new FiniteLevel(p2.parseTileEntities(), p2.parsePlayerEntities(), p2.parseEnemyEntities());

    List<Level> levelList = new ArrayList<>();
    levelList.add(t1);
    levelList.add(t2);
    levelSelecter = new LevelSelecter(levelList);

  }

  private void setUpTimeline() {
    //TODO: Timeline Code -- don't remove
    KeyFrame frame = new KeyFrame(Duration.millis(MILLISECOND_DELAY), e -> step(SECOND_DELAY));
    animation = new Timeline();
    animation.setCycleCount(Timeline.INDEFINITE);
    animation.getKeyFrames().add(frame);
    animation.play();
  }

  private void step (double elapsedTime) {
    if (!myViewManager.getIsGamePaused()) {
      levelSelecter.updateCurrentLevel(entityList, myViewManager);
      myViewManager.updateValues();
      //TODO: Consider making one method in Level.java as updateLevel() for the methods above^, although I concern about whether or not spawnEntities would get an up-to-date EntityList
      for (EntityWrapper subjectEntity : entityList) {
        for (EntityWrapper targetEntity : entityList) {
          collisionEngine.produceCollisionActions(subjectEntity.getModel(), targetEntity.getModel());
        }
        subjectEntity.update(elapsedTime);
        physicsEngine.applyForces(subjectEntity.getModel());
      }
      entityList.addAll(entityBuffer);
      entityBuffer = new ArrayList<>();
    }
    entityList.addAll(entityBuffer);
    entityBuffer = new ArrayList<>();
  }

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

