package ch.epfl.cs107.icmon.area;

import ch.epfl.cs107.icmon.handler.ICMonInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.area.AreaBehavior;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.window.Window;

/**
 * Represents the behavior of an area within the ICMon game, specifying how actors can interact with the area's cells.
 */
public final class ICMonBehavior extends AreaBehavior {
    /**
     * Constructs the ICMonBehavior with a specific window and name, initializing the cells of the behavior grid.
     *
     * @param window (Window), the display context, not null
     * @param name   (String), the name of the behavior, not null
     */
    public ICMonBehavior(Window window, String name) {
        super(window, name);
        int height = getHeight();
        int width = getWidth();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                ICMonCellType color = ICMonCellType.toType(getRGB(height - 1 - y, x));
                setCell(x, y, new ICMonCell(x, y, color));
            }
        }
    }

    /**
     * Enumeration of allowed walking types on cells.
     */
    public enum AllowedWalkingType {
        NONE , // None
        SURF , // Only with surf
        FEET , // Only with feet
        ALL // All previous
    }

    /**
     * Enumeration for identifying cell types based on color codes and their walking type.
     */
    public enum ICMonCellType {
        //https://stackoverflow.com/questions/25761438/understanding-bufferedimage-getrgb-output-values
        // Each cell type corresponds to a particular color and walking type.
        NULL(0, AllowedWalkingType.NONE),
        WALL ( -16777216 , AllowedWalkingType.NONE),
        BUILDING ( -8750470 , AllowedWalkingType.NONE),
        INTERACT (-256, AllowedWalkingType.NONE),
        INTERACTION ( -195580 , AllowedWalkingType.ALL),
        INDOOR_WALKABLE (-1, AllowedWalkingType.FEET),
        OUTDOOR_WALKABLE ( -14112955 , AllowedWalkingType.FEET),
        WATER ( -16776961 , AllowedWalkingType.SURF),
        GRASS ( -16743680 , AllowedWalkingType.FEET),
        ;

        final int type;
        final AllowedWalkingType walkingType;

        /**
         * Constructor for cell types, associating a color code to a walking type.
         *
         * @param type        The color code for the cell type.
         * @param walkingType The allowed walking type on this cell.
         */
        ICMonCellType(int type, AllowedWalkingType walkingType) {
            this.type = type;
            this.walkingType = walkingType;
        }

        /**
         * Converts an integer color code to a corresponding cell type.
         *
         * @param type The integer color code.
         * @return The corresponding ICMonCellType.
         */
        public static ICMonCellType toType(int type) {
            for (ICMonCellType ict : ICMonCellType.values()) {
                if (ict.type == type)
                    return ict;
            }
            // When you add a new color, you can print the int value here before assign it to a type
            System.out.println(type);
            return NULL;
        }
    }

    /**
     * Represents a cell in the ICMon game, defining its interaction and movement capabilities.
     */
    public class ICMonCell extends Cell {
        /// Type of the cell following the enum
        private final ICMonCellType type;

        /**
         * Default constructor for an ICMonCell.
         *
         * @param x    The x coordinate of the cell
         * @param y    The y coordinate of the cell
         * @param type The type of the cell, not null
         */
        public ICMonCell(int x, int y, ICMonCellType type) {
            super(x, y);
            this.type = type;
        }

        /**
         * Returns the type of the cell.
         *
         * @return The type of the cell.
         */
        public ICMonCellType getType(){return type;}

        /**
         * Checks if an entity can leave this cell.
         *
         * @param entity The entity trying to leave.
         * @return true if the entity can leave.
         */
        @Override
        protected boolean canLeave(Interactable entity) {
            return true;
        }

        /**
         * Checks if an entity can enter this cell.
         *
         * @param entity The entity trying to enter.
         * @return true if the entity can enter.
         */
        @Override
        protected boolean canEnter(Interactable entity) { // voir good notes pour commenter après
            if (!entity.takeCellSpace()){
                return this.type.walkingType != AllowedWalkingType.NONE;
            }
            for(Interactable ent : entities){
                if(ent.takeCellSpace()){
                    return false;
                }
            }
            return this.type.walkingType != AllowedWalkingType.NONE;
        }

        /**
         * Indicates that the cell is interactable (contact interaction).
         *
         * @return true as all cells are interactable.
         */
        @Override
        public boolean isCellInteractable() {
            return true;
        }

        /**
         * Indicates that the cell is not interactable from the view (distance interaction).
         *
         * @return false as cells are not interactable from the view.
         */
        @Override
        public boolean isViewInteractable() {
            return false;
        }

        /**
         * Handles the interaction with this cell, delegating to the ICMonInteractionVisitor.
         *
         * @param v                  the visitor interacting with the cell.
         * @param isCellInteraction Indicates if the interaction is at the cell level.
         */
        @Override
        public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
            ((ICMonInteractionVisitor) v).interactWith(this, isCellInteraction);
        }

    }
}