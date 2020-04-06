package ooga.model.controlschemes;

import java.util.Map;
import javafx.scene.input.KeyEvent;
import ooga.model.actions.Action;

public class Keybindings extends ControlScheme {

  public Keybindings(Map<String, Action> bindings){
    super(bindings);
  }

  @Override
  public Action getCurrentAction() {
    return currentAction;
  }

  @Override
  public void handleKeyInput(KeyEvent keyEvent) {
    currentAction = actionMap.get(keyEvent.getText());
  }
}
