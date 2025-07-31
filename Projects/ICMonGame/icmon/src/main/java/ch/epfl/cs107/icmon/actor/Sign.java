package ch.epfl.cs107.icmon.actor;

import ch.epfl.cs107.icmon.handler.ICMonInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Canvas;

import java.util.ArrayList;
import java.util.List;

public class Sign extends AreaEntity {

    String pathSign ;
    List<DiscreteCoordinates> position = new ArrayList<>();

    /**
     * Default AreaEntity constructor
     *
     * @param area        (Area): Owner area. Not null
     * @param position    (DiscreteCoordinate): Initial position of the entity in the Area. Not null
     */
    public Sign(Area area,String path, DiscreteCoordinates position) {
        super(area,Orientation.DOWN, position);
        this.position.add(position);
        pathSign = path;

    }

    /**
     * Get this Interactor's current occupying cells coordinates
     *
     * @return (List of DiscreteCoordinates). May be empty but not null
     */
    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return position;
    }

    /**
     * Indicate if the current Interactable take the whole cell space or not
     * i.e. only one Interactable which takeCellSpace can be in a cell
     * (how many Interactable which don't takeCellSpace can also be in the same cell)
     *
     * @return (boolean)
     */
    @Override
    public boolean takeCellSpace() {
        return false;
    }

    /**
     * @return (boolean): true if this is able to have cell interactions
     */
    @Override
    public boolean isCellInteractable() {
        return false;
    }

    /**
     * @return (boolean): true if this is able to have view interactions
     */
    @Override
    public boolean isViewInteractable() {
        return true;
    }

    /**
     * Call directly the interaction on this if accepted
     *
     * @param v                 (AreaInteractionVisitor) : the visitor
     * @param isCellInteraction
     */
    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICMonInteractionVisitor) v).interactWith(this, isCellInteraction);

    }

    /**
     * Renders itself on specified canvas.
     *
     * @param canvas target, not null
     */
    @Override
    public void draw(Canvas canvas) {

    }

}
