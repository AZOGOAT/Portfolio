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

/**
 * Represents a door entity in the game which connects different areas.
 */
public class Door extends AreaEntity {
    /** Key for the area to which this door leads. */
    private String areaKey;
    /** List of coordinates representing the door's position. */
    private List<DiscreteCoordinates> doorCoordinates= new ArrayList<>();
    /** Coordinates where the player should appear after passing through the door. */
    private DiscreteCoordinates playerCoordinates;

    /**
     * Gets the area key associated with this door.
     *
     * @return the key representing the area connected by this door.
     */
    public String getKey() {
        return areaKey;
    }

    /**
     * Gets the coordinates where the player will appear after using the door.
     *
     * @return the player's landing coordinates.
     */
    public DiscreteCoordinates getPlayerCoordinates() {
        return playerCoordinates;
    }


    /**
     * Initializes a new Door.
     *
     * @param area the area where the door is located.
     * @param key the unique identifier for the area this door connects to.
     * @param playerCoordinates coordinates where the player should appear after passing through the door.
     * @param doorCoordinatesPrincipal the main coordinates of the door in the area.
     * @param DoorCoordinates additional coordinates that the door covers.
     */
    public Door(Area area, String key, DiscreteCoordinates playerCoordinates, DiscreteCoordinates doorCoordinatesPrincipal, DiscreteCoordinates ... DoorCoordinates){
        super(area, Orientation.UP,doorCoordinatesPrincipal );
        this.playerCoordinates = playerCoordinates;
        doorCoordinates.add(doorCoordinatesPrincipal);
        for (DiscreteCoordinates coordinates : DoorCoordinates){
            doorCoordinates.add(coordinates);
        }
        areaKey = key;

    }


    @Override
    public void update(float deltaTime) {
        // Door is not updatable
    }

    /**
     * Returns the list of coordinates representing the cells occupied by the door.
     *
     * @return The list of occupied coordinates.
     */
    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return doorCoordinates;
    }

    /**
     * Indicates that the door does not block the cell space, allowing entities to pass through.
     *
     * @return false, as doors typically do not block the passage.
     */
    @Override
    public boolean takeCellSpace() {
        return false;
    }

    /**
     * Indicates that the door can be interacted with at the cell level.
     *
     * @return true, as doors are usually interactable.
     */
    @Override
    public boolean isCellInteractable() {
        return true;
    }

    /**
     * Indicates that the door can be interacted with from the view (typically, the game's camera or player's perspective).
     *
     * @return true, as doors are usually interactable and accept "distance interaction".
     */
    @Override
    public boolean isViewInteractable() {
        return true;
    }

    /**
     * Handles the interaction between the door and other entities, specifically handling transitions between areas.
     *
     * @param v the visitor that is interacting with the door.
     * @param isCellInteraction indicates if the interaction is at the cell level.
     */
    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICMonInteractionVisitor) v).interactWith(this , isCellInteraction);

    }

    /**
     * Handles the drawing of the door on the canvas, if any specific representation is needed.
     *
     * @param canvas The canvas on which to draw the door.
     */
    @Override
    public void draw(Canvas canvas) {
        // Door is not drawable
    }
}