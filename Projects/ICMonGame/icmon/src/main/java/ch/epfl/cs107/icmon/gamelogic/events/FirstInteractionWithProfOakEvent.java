package ch.epfl.cs107.icmon.gamelogic.events;

import ch.epfl.cs107.icmon.ICMon;
import ch.epfl.cs107.icmon.actor.ICMonPlayer;
import ch.epfl.cs107.icmon.actor.npc.ICShopAssistant;
import ch.epfl.cs107.icmon.actor.npc.ProfOak;
import ch.epfl.cs107.icmon.actor.pokemon.Bulbizarre;
import ch.epfl.cs107.icmon.area.maps.Arena;
import ch.epfl.cs107.play.engine.actor.Dialog;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;

/**
 * Represents the event of the player's first interaction with Professor Oak in the ICMon game.
 * This event typically initiates the player's journey and involve receiving the first Pokémon.
 */
public class FirstInteractionWithProfOakEvent extends ICMonEvent {
    /** The dialog associated with the interaction with Professor Oak. */
    private Dialog dialogWithOak;
    /** The player involved in the event. */
    private ICMonPlayer player;
    private ICMon.ICMonManager manager;

    /**
     * Constructs a new event for the player's first interaction with Professor Oak.
     *
     * @param player      The player character involved in the interaction, not null.
     * @param managerBoss The manager responsible for handling game events, not null.
     */
    public FirstInteractionWithProfOakEvent(ICMonPlayer player, ICMon.ICMonManager managerBoss) {
        super(player, managerBoss);
        dialogWithOak = new Dialog("first_interaction_with_prof_oak");
        this.player = player;
        manager = managerBoss;
    }

    /**
     * Handles interaction with an ICShopAssistant during the event.
     * Here, it involves opening a dialog.
     *
     * @param madam              The ICShopAssistant involved in the interaction, not null.
     * @param isViewInteraction True if the interaction is at the view level.
     */
    @Override
    public void interactWith(ICShopAssistant madam, boolean isViewInteraction) {
        player.openDialog(new Dialog("first_interaction_with_oak_event_icshopassistant"));
    }

    /**
     * Handles the main interaction with Professor Oak.
     * This includes opening the initial dialog and triggering additional game logic (receiving a Pokémon).
     *
     * @param profOak            Professor Oak character, not null.
     * @param isViewInteraction True if the interaction is at the view level.
     */
    @Override
    public void interactWith(ProfOak profOak, boolean isViewInteraction) {
        player.openDialog(dialogWithOak);
        if (dialogWithOak.isCompleted()) {
            (new Arena()).registerActor(new Bulbizarre(new Arena(), Orientation.DOWN, new DiscreteCoordinates(6, 6)));


            // Ici je dois add le pokémon "Latios" à la collection de pokémon
        }
    }

    /**
     * Updates the event as time progresses.
     * Completes the event when the initial dialog with Professor Oak is completed.
     *
     * @param deltaTime The time elapsed since the last update.
     */
    @Override
    public void update(float deltaTime) {
        if (dialogWithOak.isCompleted()) {
            this.complete();
        }
    }
}
