package ooga.model.actions;

import ooga.model.EntityModel;
import ooga.view.gui.managers.AudioVideoManager;

/**
 * Abstract Action superclass which defines the functionality of each individual Action
 * subclass. ALl actions have execute methods, and take in a String as a constructor:
 *
 * Example Usage:
 *
 * Action myAction = New MoveX("10"0;
 * myAction.execute(myEntity);
 *
 * Dependencies: ooga.model.EntityModel class which all actions operate on.
 *
 * @author Alex Oesterling, axo
 */
public abstract class Action {
  protected String param;
  protected AudioVideoManager soundBoard;

  /**
   * Constructor method for action. Takes in a string
   * representing the parameter for use in the
   * abstract execute method by each child Action class.
   * @param parameter - A string representing the parameter to be parsed
   *                  and used in each Action's respective execute() method.
   */
  public Action(String parameter){
    param = parameter;
    soundBoard = new AudioVideoManager();
  }

  /**
   * Key method for Action classes. All actions have an execute method which operates in a novel way
   * on EntityModels, such as moving them, spawning new ones, killing off old ones, and more.
   *
   * Each execute method generally uses the protected instance variable param in a way to determine
   * how its execute method uniquely operates.
   *
   * Example syntax:
   *
   * public class MoveX extends Action(){
   *
   * public void execute(EntityModel e){e.moveX(param);}
   *
   * }
   */
  public abstract void execute(EntityModel entity);

  /**
   * Overriden equals method. Checks not only that the entity classtypes are equal
   * but that their unique parameters are equal as well.
   * @param o - the object to compare with the action
   * @return - a boolean representing whether or not the object equals this Action class
   */
  @Override
  public boolean equals(Object o){
    return(o.getClass()==this.getClass() && o.getClass().toString().equals(this.getClass().toString()) && ((Action)o).getParam().equals(this.param));
  }

  /**
   * Method which allows an action to display what it's parameter is
   * @return a string representing the parameter stored inside of it.
   */
  public String getParam(){return param;}
}
