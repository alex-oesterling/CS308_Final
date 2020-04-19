package ooga.util;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Array;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.SortedMap;
import java.util.regex.Pattern;
import javax.swing.text.html.parser.Entity;
import ooga.controller.Controller;
import ooga.controller.EntityWrapper;
import ooga.model.actions.Action;
import ooga.model.actions.actionExceptions.InvalidActionException;
import ooga.model.controlschemes.controlSchemeExceptions.InvalidControlSchemeException;
import ooga.model.levels.Level;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class GameParser {

  private String myFileName;
  private static final String TXT_FILEPATH = "src/resources/";
  private static final String IMG_FILEPATH = "resources/";
  private static final String PACKAGE_PREFIX_NAME = "ooga.model.";
  private static final String LEVELS_PREFIX = PACKAGE_PREFIX_NAME + "levels.";
  private Controller mainController;
  private int playerNumber;
  private List<EntityWrapper> playerList;


  private JSONObject jsonObject;

  public GameParser(String fileName, Controller controller) {
    mainController = controller;
    myFileName = TXT_FILEPATH + "properties/" + fileName + ".json";
    jsonObject = (JSONObject) readJsonFile();
    playerNumber = Integer.parseInt(jsonObject.get("players").toString());
    playerList = parsePlayerEntities();
  }

  //FIXME add error handling
  public Object readJsonFile() {
    try {
      FileReader reader = new FileReader(myFileName);
      JSONParser jsonParser = new JSONParser();
      return jsonParser.parse(reader);
    } catch (IOException | ParseException e) {
      throw new InvalidControlSchemeException(e);
    }
  }

  public List<EntityWrapper> getPlayerList(){
    return playerList;
  }

  public void updateJSONValue(String key, String newValue){
    JSONObject root = jsonObject;
    String new_val = newValue;
    String old_val = root.get(key).toString();

    if(!new_val.equals(old_val))
    {
      root.put(key,new_val);

      try (FileWriter file = new FileWriter("src/resources/properties/MarioGame.json", false)) //FIXME: MULT FILES
      {
        file.write(root.toString());
        System.out.println("Successfully updated json object to file");
      } catch (IOException e) {
        e.printStackTrace();//FIXME: TO AVOID FAILING CLASS
      }
    }
  }

  private List<String> sortLevelKeySet(Set keySet){
    List<String> sortedKeys = new ArrayList<>();
    for(Object key : keySet){
      sortedKeys.add(key.toString());
    }
    Collections.sort(sortedKeys);
    return sortedKeys;
  }


  public List<Level> parseLevels() {
    List<Level> levelList = new ArrayList<>();
    JSONArray levelArrangement = (JSONArray) jsonObject.get("levelArrangement");
    JSONObject levels = (JSONObject) levelArrangement.get(0);
    List<String> sortedLevelKeys = sortLevelKeySet(levels.keySet());


    for(String levelNumber : sortedLevelKeys){
      LevelParser parsedLevel = new LevelParser(levels.get(levelNumber).toString(), mainController);
      String levelType = parsedLevel.readLevelType();
      List<EntityWrapper> tiles = parsedLevel.parseTileEntities();
      List<EntityWrapper> enemies = parsedLevel.parseEnemyEntities();
      try {
        Level newLevel = (Level) Class.forName(LEVELS_PREFIX + levelType).getDeclaredConstructor(List.class, List.class, List.class).newInstance(tiles, playerList, enemies);

        levelList.add(newLevel);
      } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
          throw new InvalidActionException("Level could not be found."); //TODO: change exception heading
      }
    }

    return levelList;
  }




  private List<EntityWrapper> parsePlayerEntities() {
    JSONArray playerArrangement = (JSONArray) jsonObject.get("playerArrangement");
    List<EntityWrapper> playerEntityArray = new ArrayList<EntityWrapper>();

    for(int i = 0; i < playerArrangement.size(); i++){
      JSONObject playerInfo = (JSONObject) playerArrangement.get(i);
      String entityName = playerInfo.get("EntityName").toString();
      JSONObject playerLocation = (JSONObject) playerInfo.get("Arrangement");

      EntityWrapper newPlayer = new EntityWrapper(entityName, mainController);
      newPlayer.getModel().setX(Double.parseDouble(playerLocation.get("X").toString()));
      newPlayer.getModel().setY(Double.parseDouble(playerLocation.get("Y").toString()));
      playerEntityArray.add(newPlayer);
    }


//    if(playerNumber == 1){ //FIXME: MAGIC NUMBER
//      System.out.println("REMOVE");
//      playerEntityArray.remove(playerEntityArray.size()-1);
//    }
    return playerEntityArray;
  }

  public PhysicsProfile parsePhysicsProfile() {
    JSONArray physicsArray = (JSONArray) jsonObject.get("physicsProfile");
    JSONObject physicsConstants = (JSONObject) physicsArray.get(0);
    PhysicsProfile gamePhysics = new PhysicsProfile(physicsConstants);
    return gamePhysics;
  }

}