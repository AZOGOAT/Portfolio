package ch.epfl.cs107.icmon.actor.items;

import ch.epfl.cs107.play.areagame.actor.CollectableAreaEntity;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.engine.actor.RPGSprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Canvas;

/**
 * Abstract class representing a collectable item within the ICMon game world.
 * This class defines the common properties and behaviors of items that can be collected by the player.
 */
public abstract class ICMonItem extends CollectableAreaEntity implements Interactable {
    /** The visual representation of the item in the game. */
    private RPGSprite item;
    /**
     * Constructs an ICMonItem with a specified area, position, and sprite.
     * Sets the orientation of the item to DOWN by default.
     *
     * @param area       The game area where the item will be located.
     * @param position   The discrete coordinates indicating the item's position in the area.
     * @param spriteName The name of the sprite image to use for this item.
     */
    public ICMonItem(Area area, DiscreteCoordinates position, String spriteName) {
        super(area, Orientation.DOWN, position);
        item = new RPGSprite(spriteName, 1f, 1f, this);
    }

    /**
     * Indicates whether the item occupies space on the game's grid.
     *
     * @return true, because items are considered to take up cell space.
     */
    @Override
    public boolean takeCellSpace() {return true;}

    /**
     * Determines if the item can be interacted with at the cell level.
     *
     * @return true, because items are able to have cell interactable.
     */
    @Override
    public boolean isCellInteractable() {return true;}

    /**
     * Renders itself on specified canvas.
     *
     * @param canvas The canvas on which to draw the item.
     */
    @Override
    public void draw(Canvas canvas) {
        item.draw(canvas);
    }
}