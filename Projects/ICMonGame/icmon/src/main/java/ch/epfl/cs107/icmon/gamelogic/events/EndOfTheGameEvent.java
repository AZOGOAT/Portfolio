package ch.epfl.cs107.icmon.gamelogic.events;

import ch.epfl.cs107.icmon.ICMon;
import ch.epfl.cs107.icmon.actor.ICMonPlayer;
import ch.epfl.cs107.icmon.actor.npc.ICShopAssistant;
import ch.epfl.cs107.play.engine.actor.Dialog;

/**
 * Represents the event marking the end of the game within the ICMon game.
 * It encapsulates the behavior and interactions required to conclude the game's narrative or gameplay.
 */
public class EndOfTheGameEvent extends ICMonEvent{
    /** The player involved in the event. */
    private ICMonPlayer player;

    /**
     * Constructs a new EndOfTheGameEvent for the specified player.
     *
     * @param player  The player character, not null.
     * @param manage  The manager responsible for handling game events, not null.
     */
    public EndOfTheGameEvent(ICMonPlayer player, ICMon.ICMonManager manage) {
        super(player,manage);
        this.player = player;
    }

    /**
     * Handles interaction with an ICShopAssistant as part of concluding the game.
     *
     * @param other             The ICShopAssistant involved in the interaction, not null.
     * @param isCellInteraction True if the interaction is at the cell level.
     */
    @Override
    public void interactWith(ICShopAssistant other, boolean isCellInteraction) {
        player.openDialog(new Dialog("end_of_game_event_interaction_with_icshopassistant"));
        System.out.println("I heard that you were able to implement this step successfully. Congrats !");

    }

    /**
     * Updates the event as time progresses. Typically, additional logic for concluding the game would be implemented here.
     *
     * @param deltaTime The time elapsed since the last update.
     */
    @Override
    public void update(float deltaTime) {
        // This event is not updatable
    }
}
