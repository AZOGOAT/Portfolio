package ch.epfl.cs107.icmon.actor.npc;

import ch.epfl.cs107.icmon.handler.ICMonInteractionVisitor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;

/**
 * Represents a shop assistant character in the IC game.
 */
public class ICShopAssistant extends NPCActor {

    /**
     * Initializes a new ICShopAssistant.
     *
     * @param owner       the area to which this shop assistant belongs
     * @param orientation the initial orientation of the shop assistant in the area
     * @param coordinates the initial position of the shop assistant in the area
     */
    public ICShopAssistant(Area owner, Orientation orientation, DiscreteCoordinates coordinates) {
        super(owner, orientation, coordinates, "actors/assistant");
    }

    /**
     * Handles the interaction of the shop assistant with other game entities. It delegates the interaction to the ICMonInteractionVisitor.
     *
     * @param v                 The visitor that is trying to interact with this ICShopAssistant
     * @param isCellInteraction indicates if the interaction is at the cell level
     */
    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICMonInteractionVisitor) v).interactWith(this, isCellInteraction);
    }

}