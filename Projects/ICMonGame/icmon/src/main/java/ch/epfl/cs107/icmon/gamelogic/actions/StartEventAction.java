package ch.epfl.cs107.icmon.gamelogic.actions;

import ch.epfl.cs107.icmon.gamelogic.events.ICMonEvent;

/**
 * Represents an action that initiates an ICMon event in the game.
 * This action is responsible for starting a specific event, changing the game's state or triggering specific behaviors.
 */
public class StartEventAction implements Action {
    /** The event to be started. */
    private ICMonEvent event;

    /**
     * Constructs a new StartEventAction for the specified event.
     *
     * @param event The ICMonEvent to be started, not null.
     */
    public StartEventAction(ICMonEvent event){
        this.event = event;
    }

    /**
     * Executes the action of starting the event.
     * It invokes the start method of the event, effectively beginning its sequence or effects.
     */
    @Override
    public void perform() {
        event.start();
    }
}
