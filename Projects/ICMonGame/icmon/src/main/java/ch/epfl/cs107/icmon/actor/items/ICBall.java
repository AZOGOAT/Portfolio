package ch.epfl.cs107.icmon.actor.items;

import ch.epfl.cs107.icmon.handler.ICMonInteractionVisitor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

import java.util.Collections;
import java.util.List;

/**
 * The ICBall class, representing an interactable item within the game world.
 * It extends ICMonItem, implying it is a specific type of item in the ICMon game.
 */
public class ICBall extends ICMonItem {
    /**
     * Constructor for the ICBall class. Creates an ICBall object at a specific position within an area.
     *
     * @param area The game area where the ICBall will be located.
     * @param position The discrete coordinates indicating the ICBall's position in the area.
     */
    public ICBall(Area area, DiscreteCoordinates position){
        super(area, position, "items/icball");
    }

    /**
     * Returns the list of current cells that this ICBall occupies.
     *
     * @return A singleton list containing the main coordinates of this ICBall.
     */
    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    /**
     * Indicates if the ICBall is interactable from the game's view (as opposed to at a cell level).
     *
     * @return true, indicating that the ICBall accept "distance interaction".
     */
    @Override
    public boolean isViewInteractable(){return true;}

    /**
     * Handles interactions with this ICBall. It delegates the interaction to the ICMonInteractionVisitor.
     *
     * @param v The visitor that is trying to interact with this ICBall.
     * @param isCellInteraction Indicates if the interaction is at a cell level.
     */
    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICMonInteractionVisitor) v).interactWith(this, isCellInteraction);
    }

}