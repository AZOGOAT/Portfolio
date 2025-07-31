package ch.epfl.cs107.icmon.actor;

import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.actor.MovableAreaEntity;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Canvas;

import java.util.Collections;
import java.util.List;

/**
 * Represents an interactive, movable entity within the ICMon game world.
 */
public abstract class ICMonActor extends MovableAreaEntity implements Interactable {

    /**
     * Initializes a new ICMonActor.
     *
     * @param owner        The area to which this actor belongs.
     * @param orientation  The initial orientation of the actor in the area.
     * @param coordinates  The initial position of the actor in the area.
     */
    public ICMonActor(Area owner, Orientation orientation, DiscreteCoordinates coordinates) {
        super(owner, orientation, coordinates);
        resetMotion();
    }

    /**
     * Renders the actor on the canvas.
     *
     * @param canvas The canvas to draw the actor on.
     */
    @Override
    public void draw(Canvas canvas) {}

    /**
     * Indicates whether the actor occupies cell space.
     *
     * @return false, as ICMonActors typically do not block cell space (by default).
     */
    @Override
    public boolean takeCellSpace() {
        return false;
    }

    /**
     * Indicates whether the actor is interactable at the cell level.
     *
     * @return true, as ICMonActors are usually interactable.
     */
    @Override
    public boolean isCellInteractable() {
        return true;
    }

    /**
     * Indicates whether the actor is interactable from a distance (view interaction).
     *
     * @return false, as ICMonActors typically interact at the cell level, not from a distance.
     */
    @Override
    public boolean isViewInteractable() {
        return false;
    }

    /**
     * Returns the list of coordinates representing the cells occupied by the actor.
     *
     * @return A list of occupied coordinates.
     */
    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    /**
     * Handles the interaction of the actor with other entities in the area.
     *
     * @param v                 The visitor that is interacting with the actor.
     * @param isCellInteraction Indicates if the interaction is at the cell level.
     */
    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {

    }

    /**
     * Transfers the actor to a specified area and position.
     *
     * @param area     The area to transfer the actor to.
     * @param position The position in the new area.
     */
    public void enterArea(Area area, DiscreteCoordinates position) {
        area.registerActor(this);
        area.setViewCandidate(this);
        setOwnerArea(area);
        setCurrentPosition(position.toVector());
        resetMotion();
    }

    /**
     * Unregisters the actor from its current area, effectively removing it from that area.
     */
    public void leaveArea() {
        getOwnerArea().unregisterActor(this);
    }

    /**
     * Centers the game's camera on this actor.
     */
    public void centerCamera() {
        getOwnerArea().setViewCandidate(this);
    }
}