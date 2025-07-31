package ch.epfl.cs107.icmon.actor.npc;

import ch.epfl.cs107.icmon.actor.ICMonActor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.engine.actor.RPGSprite;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.window.Canvas;

import java.util.List;

/**
 * Represents a non-playable character (NPC) in the game.
 */
public abstract class NPCActor extends ICMonActor {
    /** Sprite representing the visual appearance of the NPC. */
    private final Sprite sprite;

    /**
     * Initializes a new NPCActor.
     *
     * @param owner       the area to which this NPC belongs
     * @param orientation the initial orientation of the NPC in the area
     * @param coordinates the initial position of the NPC in the area
     * @param spriteName  the name of the sprite image used to represent the NPC
     */
    public NPCActor(Area owner, Orientation orientation, DiscreteCoordinates coordinates, String spriteName) {
        super(owner, orientation, coordinates);
        sprite = new RPGSprite(spriteName , 1, 1.3125f, this , new RegionOfInterest(0, 0, 16,
                21));
    }

    /**
     * Draws the NPC on the canvas.
     *
     * @param canvas The canvas to draw the NPC on
     */
    @Override
    public void draw(Canvas canvas) {
        sprite.draw(canvas);
    }

    /**
     * Indicates that the NPC takes up cell space.
     *
     * @return true, as NPCs typically occupy a cell space
     */
    @Override
    public boolean takeCellSpace(){return true;}

    /**
     * Returns the list of coordinates representing the cells occupied by the NPC.
     *
     * @return The list of occupied coordinates
     */
    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return super.getCurrentCells();
    }

    /**
     * Indicates that the NPC is interactable from the view (typically, the game's camera or player's perspective).
     *
     * @return true, as NPCs are usually interactable and accept "distance interaction"
     */
    @Override
    public boolean isViewInteractable() {
        return true;
    }
}