package ch.epfl.cs107.icmon.gamelogic.actions;

import ch.epfl.cs107.icmon.ICMon;

/**
 * Represents an action to request the game to resume from a paused state.
 * This action signals the game state to end the pause menu and continue the game.
 */
public class RequestResumeAction implements Action {
    /** The current game state that will be affected by the resume request. */
    private ICMon.ICMonGameState game;

    /**
     * Constructs a new action to request resuming the game from the specified state.
     *
     * @param state The current game state, not null.
     */
    public RequestResumeAction(ICMon.ICMonGameState state){
        game= state;
    }

    /**
     * Executes the action of resuming the game.
     * It invokes the EndPauseMenu method of the game state, effectively resuming the game.
     */
    @Override
    public void perform() {
        game.EndPauseMenu();
    }

}
