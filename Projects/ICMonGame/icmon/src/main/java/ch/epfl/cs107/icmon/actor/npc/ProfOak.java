package ch.epfl.cs107.icmon.actor.npc;

import ch.epfl.cs107.icmon.handler.ICMonInteractionVisitor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;

/**
 * Represents Prof Oak, a character in the game known for guiding players through the world of Pokemon.
 */
public class ProfOak extends NPCActor{
    /**
     * Initializes a new instance of Prof Oak.
     *
     * @param owner       the area to which Prof Oak belongs
     * @param orientation the initial orientation of Prof Oak in the area
     * @param coordinates the initial position of Prof Oak in the area
     */
    public ProfOak(Area owner, Orientation orientation, DiscreteCoordinates coordinates) {
        super(owner, orientation, coordinates, "actors/prof-oak");
    }

    /**
     * Handles the interaction of Prof Oak with other game entities. It delegates the interaction to the ICMonInteractionVisitor.
     *
     * @param v                 the visitor that is trying to interact with Prof Oak
     * @param isCellInteraction indicates if the interaction is at the cell level
     */
    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICMonInteractionVisitor) v).interactWith(this, isCellInteraction);
    }
}