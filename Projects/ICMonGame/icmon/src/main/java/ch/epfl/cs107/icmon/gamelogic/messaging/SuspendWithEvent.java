package ch.epfl.cs107.icmon.gamelogic.messaging;

import ch.epfl.cs107.icmon.ICMon;
import ch.epfl.cs107.icmon.gamelogic.events.PokemonFightEvent;
import ch.epfl.cs107.icmon.gamelogic.fights.ICMonFightableActor;

/**
 * Represents a gameplay message that handles the suspension of the game with a specific event.
 * This message is typically used to pause the game or an ongoing event and manage its state.
 */
public class SuspendWithEvent implements GamePlayMessage{
    /** The Pokemon actor related to the suspension. */
    private ICMonFightableActor hasPokemon;
    /** The current state of the game. */
    private ICMon.ICMonGameState state;
    /** The specific Pokemon fight event that triggers the suspension. */
    private PokemonFightEvent eventCreatesSuspend;
    /** The manager responsible for handling game events and their lifecycle. */
    private ICMon.ICMonManager manager;

    /**
     * Constructs a new SuspendWithEvent message with the specified game state, Pokemon actor, event, and manager.
     *
     * @param state            The current state of the game, not null.
     * @param hasPokemon       The Pokemon actor related to the suspension, not null.
     * @param event            The specific Pokemon fight event that triggers the suspension, not null.
     * @param manage           The manager responsible for handling game events, not null.
     */
    public SuspendWithEvent(ICMon.ICMonGameState state, ICMonFightableActor hasPokemon, PokemonFightEvent event, ICMon.ICMonManager manage){
        this.state = state;
        this.hasPokemon = hasPokemon;
        eventCreatesSuspend = event;
        manager = manage;
    }

    /**
     * Gets the specific Pokemon fight event that triggers the suspension.
     *
     * @return The PokemonFightEvent that triggers the suspension.
     */
    public PokemonFightEvent getEventCreatesSuspend() {
        return eventCreatesSuspend;
    }

    /**
     * Processes the suspension message, handling the pause or resumption of the game or the specified event.
     * This typically involves invoking the suspend or resume functionalities within the game state.
     */
    @Override
    public void process() {
        //Pause menu // resume menu // pause game menu
        state.suspendMessage(eventCreatesSuspend,manager);
    }
}
