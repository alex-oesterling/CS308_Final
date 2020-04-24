package ooga.model.levels;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import ooga.controller.EntityWrapper;
import ooga.controller.ViewManager;
import ooga.model.EntityModel;
import ooga.util.GameStatusProfile;

public class LevelSelector {

  private List<Level> parsedLevels;
  private Level activeLevel;


  private int spawningInterval;
  private GameStatusProfile gameStatusProfile;


  public LevelSelector(List<Level> levelList, GameStatusProfile gameProfile){
    parsedLevels = levelList;
    gameStatusProfile = gameProfile;
    spawningInterval = gameStatusProfile.readSpawningInterval();
    activeLevel = parsedLevels.get(0);
  }

  public void updateCurrentLevel(List<EntityWrapper> currentEntityList, ViewManager viewManager, int nextLevel) {
//    System.out.println(currentEntityList.size());
    if (currentEntityList.get(0).getModel().getLevelAdvancementStatus()) {
      currentEntityList.get(0).getModel().setLevelAdvancementStatus(false);
      switchLevel(nextLevel);
      activeLevel.setCurrentPlayerInterval(Math.abs(calculatePlayerInterval(currentEntityList.get(0))));
    }
    activeLevel.spawnEntities(currentEntityList, viewManager);
    this.despawnEntities(currentEntityList, viewManager);
  }

  private void switchLevel(int levelIndex){
    activeLevel = parsedLevels.get(levelIndex);
  }

  private int calculatePlayerInterval(EntityWrapper player) {
    return (int) Math.abs((player.getModel().getX() * gameStatusProfile.readScrollingStatusX() + player.getModel().getY() * gameStatusProfile.readScrollingStatusY())/spawningInterval);
  }

//TODO: decide whether or not to have despawning done by level selector or levels (actually has
// nothing pertaining to individual levels.
  private void despawnEntities(List<EntityWrapper> currentEntityList, ViewManager viewManager){
      List<EntityWrapper> entitiesToDespawn = new ArrayList<>();
      for (EntityWrapper targetEntity : currentEntityList) {
        if (!currentEntityList.get(0).equals(targetEntity) && !isInRange(currentEntityList.get(0).getModel(), targetEntity.getModel())) {
          entitiesToDespawn.add(targetEntity);
        }
      }
      for(EntityWrapper despawnedEntity : entitiesToDespawn){
        currentEntityList.remove(despawnedEntity);
        viewManager.removeEntity(despawnedEntity.getRender());
      }
    }

  private boolean isInRange(EntityModel subjectEntity, EntityModel targetEntity){
    if(Math.sqrt(Math.pow(subjectEntity.getX() - targetEntity.getX(), 2) + Math.pow(subjectEntity.getY() - targetEntity.getY(), 2)) < 1500){
      return true;
    }
    return false;
  }

  public List<Level> getLevelsToPlay() {
    return parsedLevels.subList(parsedLevels.indexOf(activeLevel), parsedLevels.size());
  }

  public Level getActiveLevel() {
    return activeLevel;
  }
}
