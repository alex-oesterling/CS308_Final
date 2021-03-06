package ooga.model.actions;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Stack;
import ooga.model.EntityModel;
import ooga.model.actions.actionExceptions.InvalidActionException;

public class ActionFactory {

  private Map<String, String> myActions = new HashMap<>();
  private static final String PACKAGE_PREFIX_NAME = "ooga.model.";
  private static final String ACTIONS_PREFIX = PACKAGE_PREFIX_NAME + "actions.";

  public ActionFactory() {
    setGeneralCommands();
  }

  private void setGeneralCommands() {
    var actionBundle = ResourceBundle.getBundle(ActionFactory.class.getPackageName() + ".resources." + "actions");
    for(String tag : actionBundle.keySet()) {
      myActions.put(tag, actionBundle.getString(tag));
    }
  }

  public Action makeAction(String action, Class<?>[] classes, Object[] params) throws InvalidActionException {
    String formalAction = validateAction(action); //TODO: check if action is valid
    return buildAction(formalAction, classes, params);
  }

  private String validateAction(String action)
      throws InvalidActionException {
    if(myActions.containsKey(action)) {
      return action;
    }
    throw new InvalidActionException(action + " doesnt exist"); //TODO: fix exception handling
  }

  private Action buildAction(String formalAction, Class<?> classTypes[], Object param[]) {
    try {
      return (Action) Class.forName(ACTIONS_PREFIX + formalAction).getDeclaredConstructor(classTypes).newInstance(param);
    }
    catch
    (ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      throw new InvalidActionException("Action " + formalAction + " could not be found with the following constructors:\n" + classTypes + "\n" + param);
    }
  }
}




