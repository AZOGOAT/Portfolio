package ch.epfl.cs107.icmon.gamelogic.actions;

/**
 * Represents an action within the game logic of ICMon.
 * This interface defines a contract for all classes that will implement actionable behavior.
 */
public interface Action {
     /**
      * Performs the action. The specific behavior of this method is defined in the implementing classes.
      */
     void perform();
}