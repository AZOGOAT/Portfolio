package ch.epfl.cs107.icmon.gamelogic.events;

import ch.epfl.cs107.icmon.ICMon;
import ch.epfl.cs107.icmon.actor.ICMonPlayer;
import ch.epfl.cs107.icmon.gamelogic.actions.CompleteEventAction;
import ch.epfl.cs107.icmon.gamelogic.actions.StartEventAction;

/**
 * Represents a chained event in the ICMon game, where the completion of one event triggers the start of another.
 * This allows for a sequence of events to unfold as part of the game's narrative.
 */
public class ICMonChainedEvent extends ICMonEvent{

    /**
     * Constructs a new chained event sequence, where each event in the sequence is triggered after the previous one completes.
     * The sequence starts with an initial event and continues through the provided chain of events. The entire chained event
     * completes once the last event in the chain is completed.
     *
     * @param player       The player character involved in the events, not null. They are the primary actor in all events.
     * @param managerBoss  The manager responsible for handling the lifecycle of game events, not null.
     * @param initialEvent The first event in the chain, initiating the sequence of events, not null.
     * @param chain        The subsequent events in the sequence, not null. These events are triggered one after the other.
     */
    public ICMonChainedEvent(ICMonPlayer player, ICMon.ICMonManager managerBoss, ICMonEvent initialEvent, ICMonEvent ... chain) {
        super(player, managerBoss);
        // Start the initial event upon the start of this chained event.
        onStart(new StartEventAction(initialEvent));
        // Start the first chained event upon the completion of the initial event.
        initialEvent.onComplete(new StartEventAction(chain[0]));
        // Chain each event to start the next one upon its completion.
        for(int i=0; i<chain.length-1 ; ++i){
            chain[i].onComplete(new StartEventAction(chain[i+1]));
        }
        // When the last event in the chain is completed, complete this chained event.
        chain[chain.length-1].onComplete(new CompleteEventAction(this));
    }

    /**
     * Updates the chained event as time progresses.
     * @param deltaTime The time elapsed since the last update.
     */
    @Override
    public void update(float deltaTime) {
        // The class ICMonChainedEvent is not updatable
    }
}