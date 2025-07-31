package ch.epfl.cs107.icmon.gamelogic.actions;

import ch.epfl.cs107.icmon.ICMon;
import ch.epfl.cs107.icmon.gamelogic.events.ICMonEvent;

/**
 * Represents an action to register an event within the ICMon game's event management system.
 * This action encapsulates the logic necessary to add an event to the game's event manager.
 */
public class RegisterEventAction implements Action {
    /** The ICMonEvent to be registered. */
    private ICMonEvent eventLocal ;
    /** The manager responsible for handling game events. */
    private ICMon.ICMonManager managing;

    /**
     * Constructs a new action to register the given event with the specified game manager.
     *
     * @param event         The ICMonEvent to be registered, not null.
     * @param managerLocal  The manager responsible for event registration, not null.
     */
    public RegisterEventAction(ICMonEvent event, ICMon.ICMonManager managerLocal){
        eventLocal = event;
        managing = managerLocal;

    }

    /**
     * Executes the action of registering the event.
     * This is typically called to add the event to the game's event manager for further handling.
     */
    @Override
    public void perform(){
        managing.registerEvent(eventLocal);
    }
}
