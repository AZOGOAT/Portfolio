package ch.epfl.cs107.icmon.gamelogic.actions;

import ch.epfl.cs107.icmon.gamelogic.events.ICMonEvent;

/**
 * Represents an action to complete a specific event in the ICMon game.
 * This action encapsulates the logic necessary to mark an event as complete.
 */
public class CompleteEventAction implements Action {

    /** The event to be completed. */
    private ICMonEvent event;

    /**
     * Creates a new action to complete the given event.
     *
     * @param event The ICMonEvent that is to be completed, not null.
     */
    public CompleteEventAction(ICMonEvent event){
        this.event = event;
    }

    /**
     * Performs the completion of the event.
     * This is typically called to update the event's state to reflect that it has been completed.
     */
    @Override
    public void perform() {
        event.complete();
    }
}