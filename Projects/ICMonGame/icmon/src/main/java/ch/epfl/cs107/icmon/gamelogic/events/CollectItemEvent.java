package ch.epfl.cs107.icmon.gamelogic.events;

import ch.epfl.cs107.icmon.ICMon;
import ch.epfl.cs107.icmon.actor.ICMonPlayer;
import ch.epfl.cs107.icmon.actor.items.ICBall;
import ch.epfl.cs107.icmon.actor.items.ICMonItem;
import ch.epfl.cs107.icmon.actor.npc.ICShopAssistant;
import ch.epfl.cs107.play.engine.actor.Dialog;

/**
 * Represents an event where a player collects an item within the ICMon game.
 * It tracks the collection status and interactions associated with the item being collected.
 */
public class CollectItemEvent extends ICMonEvent{
    /** The item to be collected. */
    private final ICMonItem item;
    /** The player involved in the event. */
    private final ICMonPlayer player;

    /**
     * Constructs a new CollectItemEvent involving a player and an item.
     *
     * @param player The player character, not null.
     * @param item   The item to be collected, not null.
     * @param manage The manager responsible for handling game events, not null.
     */
    public CollectItemEvent (ICMonPlayer player, ICMonItem item, ICMon.ICMonManager manage){
        super(player,manage);
        this.item = item;
        this.player = player;

    }

    /**
     * Updates the event as time progresses.
     * Completes the event when the item is collected.
     *
     * @param deltaTime The time elapsed since the last update.
     */
    @Override
    public void update(float deltaTime) {
        if (item.isCollected()){
            complete();
        }
    }

    /**
     * Handles interaction with a ball as part of the event.
     *
     * @param ball              The ball involved in the interaction, not null.
     * @param isCellInteraction True if the interaction is at the cell level.
     */
    @Override
    public void interactWith(ICBall ball, boolean isCellInteraction) {
        if (isCellInteraction) {
            ball.collect();
            System.out.println("Player is interacting with Ball");
        }
    }

    /**
     * Handles interaction with an ICShopAssistant as part of the event.
     *
     * @param other             The ICShopAssistant involved in the interaction, not null.
     * @param isCellInteraction True if the interaction is at the cell level.
     */
    @Override
    public void interactWith(ICShopAssistant other, boolean isCellInteraction) {
        if (!isCellInteraction){
            player.openDialog(new Dialog("collect_item_event_interaction_with_icshopassistant"));
            System.out.println("This is an interaction between the player and ICShopAssistant based on events !");
        }
    }

}
