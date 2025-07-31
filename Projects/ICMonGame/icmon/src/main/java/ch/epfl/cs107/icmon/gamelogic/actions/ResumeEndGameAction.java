package ch.epfl.cs107.icmon.gamelogic.actions;

import ch.epfl.cs107.icmon.ICMon;

/**
 * Represents an action to resume all game events, typically used when ending the game or transitioning to a new game state.
 * This action signals the game's event manager to resume handling all paused or pending events.
 */
public class ResumeEndGameAction implements Action {
    /** The game's event manager that will handle the resumption of events. */
    private ICMon.ICMonManager managerBoss;

    /**
     * Constructs a new action to resume all events managed by the specified ICMonManager.
     *
     * @param manager The manager responsible for handling game events, not null.
     */
    public ResumeEndGameAction(ICMon.ICMonManager manager){
        managerBoss = manager;
    }

    /**
     * Executes the action of resuming all events.
     * It invokes the resumeAllEvents method of the manager, effectively resuming all paused or pending events.
     */
    @Override
    public void perform() {
        managerBoss.resumeAllEvents();

    }
}
