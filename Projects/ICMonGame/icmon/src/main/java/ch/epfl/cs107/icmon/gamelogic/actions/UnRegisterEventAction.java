package ch.epfl.cs107.icmon.gamelogic.actions;

import ch.epfl.cs107.icmon.ICMon;
import ch.epfl.cs107.icmon.gamelogic.events.ICMonEvent;

/**
 * Represents an action to unregister an event from the ICMon game's event management system.
 * This action is used to remove an event from the game's event manager, typically when the event is completed or no longer needed.
 */
public class UnRegisterEventAction  implements Action {
    /** The ICMonEvent to be unregistered. */
    private ICMonEvent eventLocal ;
    /** The manager responsible for handling game events. */
    private ICMon.ICMonManager managing;


    /**
     * Constructs a new action to unregister the given event from the specified game manager.
     *
     * @param event         The ICMonEvent to be unregistered, not null.
     * @param managerLocal  The manager responsible for event unregistration, not null.
     */
    public UnRegisterEventAction(ICMonEvent event, ICMon.ICMonManager managerLocal){
        eventLocal = event;
        managing = managerLocal;
    }

    /**
     * Executes the action of unregistering the event.
     * It invokes the unregisterEvent method of the manager, effectively removing the event from the manager's list of handled events.
     */
    @Override
    public void perform() {
        managing.unregisterEvent(eventLocal);
    }
}
