package ch.epfl.cs107.icmon.gamelogic.messaging;

import ch.epfl.cs107.icmon.ICMon;
import ch.epfl.cs107.icmon.actor.Door;

/**
 * The PassDoorMessage class represents a specific gameplay message in the ICMon game.
 * It is used to trigger the action of passing through a door, resulting in a change of game state.
 * This class implements the GamePlayMessage interface and provides the necessary logic to process the message.
 *
 * When processed, this message triggers the switching of the game area to a new area specified by the door.
 */
public class PassDoorMessage implements GamePlayMessage {
    /** The door through which the player will pass. */
    private Door doorToPass;
    /** The game state of ICMon. */
    private ICMon.ICMonGameState state;

    /**
     * Constructs a new PassDoorMessage with the specified door and game state.
     *
     * @param door The door through which the player will pass.
     * @param state The game state of ICMon.
     */
    public PassDoorMessage(Door door, ICMon.ICMonGameState state){
        doorToPass=door;
        this.state = state;
    }

    /**
     * Processes the PassDoorMessage.
     * When processed, this message triggers the switching of the game area to a new area specified by the door.
     */
    public void process(){
        state.switchingArea(doorToPass.getKey(), doorToPass.getPlayerCoordinates());
    }

}