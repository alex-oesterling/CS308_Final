package ooga.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import ooga.model.CollisionEngine;
import ooga.model.PhysicsEngine;
import ooga.model.levels.InfiniteLevel;
import ooga.model.levels.InfiniteLevelBuilder;
import ooga.model.levels.Level;
import ooga.model.levels.LevelSelecter;
import ooga.util.LevelParser;
import ooga.view.application.Camera;
import ooga.view.gui.managers.StageManager;

import java.util.ArrayList;
import java.util.List;


public class InfiniteLevelController implements Controller {


  private PhysicsEngine physicsEngine;
  private CollisionEngine collisionEngine;
  private EntityWrapper entityWrapper;
  private List<EntityWrapper> entityList;
  private List<EntityWrapper> entityBuffer;
  private static final int FRAMES_PER_SECOND = 60;
  private static final int MILLISECOND_DELAY = 1000 / FRAMES_PER_SECOND;
  private static final double SECOND_DELAY = 1.0 / FRAMES_PER_SECOND;

  private int escCounter = 0;
  private Timeline animation;
  private InfiniteLevelBuilder builder;
  private Camera camera;
  private Pane level;
  private ViewManager myViewManager;
  private LevelSelecter levelSelecter;


  public InfiniteLevelController(StageManager stageManager) { //FIXME add exception stuff

    //TODO: Quick and dirty nodes for testing purpose -- replace with Entity stuff
    builder = new InfiniteLevelBuilder(this);
    level = builder.generateLevel();

    entityList = new ArrayList<>();
    entityBuffer = new ArrayList<>();
    entityList.add(new EntityWrapper("Flappy_Bird", this));

    myViewManager = new ViewManager(stageManager, builder, entityList.get(0).getRender()); //FIXME to be more generalized and done instantly

    entityWrapper = entityList.get(0);
    myViewManager.updateEntityGroup(entityWrapper.getRender());


    physicsEngine = new PhysicsEngine("dummyString");
    collisionEngine = new CollisionEngine();

    myViewManager.getTestScene().setOnKeyPressed(e -> {

      myViewManager.handlePressInput(e.getCode());
      for (EntityWrapper entity : entityList) {
        entity.handleKeyInput(e.getCode().toString());//FIXME i would like to
      }
    });
    myViewManager.getTestScene().setOnKeyReleased(e -> {
      for (EntityWrapper entity : entityList) {
        entity.handleKeyReleased(e.getCode().toString());//FIXME i would like to
      }
    });

    setUpTimeline();
    LevelParser parser1 = new LevelParser("Pipe1", this);
    LevelParser parser2 = new LevelParser("Pipe2", this);
    LevelParser parser3 = new LevelParser("Pipe3", this);
    LevelParser parser4 = new LevelParser("Pipe4", this);
    LevelParser parser5 = new LevelParser("Pipe5", this);

    List<EntityWrapper> player = new ArrayList<>();
    player.add(entityWrapper);
    Level level1 = new InfiniteLevel(parser1.parseTileEntities(), player, parser1.parseEnemyEntities());
    Level level2 = new InfiniteLevel(parser2.parseTileEntities(), player, parser2.parseEnemyEntities());
    Level level3 = new InfiniteLevel(parser3.parseTileEntities(), player, parser3.parseEnemyEntities());
    Level level4 = new InfiniteLevel(parser4.parseTileEntities(), player, parser4.parseEnemyEntities());
    Level level5 = new InfiniteLevel(parser5.parseTileEntities(), player, parser5.parseEnemyEntities());

    List<Level> levels = new ArrayList<>();
    levels.add(level1);
    levels.add(level2);
    levels.add(level3);
    levels.add(level4);
    levels.add(level5);

    levelSelecter = new LevelSelecter(levels);


  }

  private void setUpTimeline() {
    //TODO: Timeline Code -- don't remove
    KeyFrame frame = new KeyFrame(Duration.millis(MILLISECOND_DELAY), e -> step(SECOND_DELAY));
    animation = new Timeline();
    animation.setCycleCount(Timeline.INDEFINITE);
    animation.getKeyFrames().add(frame);
    animation.play();
  }

  private void step(double elapsedTime) {
//    System.out.println(entityList.size());
    if (!myViewManager.getIsGamePaused()) {
      levelSelecter.updateCurrentLevel(entityList, myViewManager);
      myViewManager.updateValues();
      for (EntityWrapper subjectEntity : entityList) {
        for (EntityWrapper targetEntity : entityList) {
          collisionEngine
              .produceCollisionActions(subjectEntity.getModel(), targetEntity.getModel());
        }
        subjectEntity.update(elapsedTime);
        physicsEngine.applyForces(subjectEntity.getModel());
      }
    }
    entityList.addAll(entityBuffer);
    entityBuffer = new ArrayList<>();
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

  @Override
  public void removeEntity(EntityWrapper node) {

  }

}