package ch.epfl.cs107.icmon.gamelogic.actions;

import ch.epfl.cs107.icmon.ICMon;

/**
 * Represents an action that resumes all the events managed by the ICMon's game manager.
 * It is used when it's necessary to continue the execution of all paused or pending events.
 */
public class ResumeEventAction implements Action {
    /** The manager responsible for handling the game's events. */
    private ICMon.ICMonManager managerBoss;

    /**
     * Constructs a new ResumeEventAction with the specified game manager.
     *
     * @param manager The ICMon game manager responsible for handling events, not null.
     */
    public ResumeEventAction(ICMon.ICMonManager manager){
        managerBoss = manager;
    }

    /**
     * Executes the action of resuming all events.
     * It invokes the resumeAllEvents method of the manager, effectively unpausing or continuing all managed events.
     */
    @Override
    public void perform() {
        managerBoss.resumeAllEvents();
    }
}