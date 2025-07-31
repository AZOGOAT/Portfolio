package ch.epfl.cs107.icmon.gamelogic.actions;

import ch.epfl.cs107.icmon.ICMon;

/**
 * Represents an action to suspend all events within the ICMon game's event management system.
 * This action is used to temporarily halt the execution of game events, such as during a pause or transition.
 */
public class SuspendEventAction implements Action {
    /** The game's event manager that will handle the suspension of events. */
    private ICMon.ICMonManager managerBoss;

    /**
     * Constructs a new action to suspend all events managed by the specified ICMonManager.
     *
     * @param manager The manager responsible for handling game events, not null.
     */
    public SuspendEventAction(ICMon.ICMonManager manager){
        managerBoss = manager;
    }

    /**
     * Executes the action of suspending all events.
     * It invokes the suspendAllEvents method of the manager, effectively pausing all managed events.
     */
    @Override
    public void perform() {
        managerBoss.suspendAllEvents();
    }
}
