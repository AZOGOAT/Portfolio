package ch.epfl.cs107.icmon.area;

import ch.epfl.cs107.icmon.ICMon;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Window;

/**
 * Abstract class representing a specific area in the ICMon game, defining the common behaviors and properties of all game areas.
 */
public abstract class ICMonArea extends Area {

    /**
     * Abstract method to be implemented by specific areas for creating all the actors and setting up the scene.
     */

    protected abstract void createArea();

    /**
     * Abstract method to retrieve the player's spawn position when entering the area.
     *
     * @return (DiscreteCoordinates): The spawn position of the player in this area.
     */
    public abstract DiscreteCoordinates getPlayerSpawnPosition();

    /**
     * Initializes the area, setting up its behavior and the actors within it. Overrides the Area's begin method to include ICMon specific setup.
     *
     * @param window     (Window): Display context. Not null
     * @param fileSystem (FileSystem): Given file system. Not null
     * @return (boolean): true if the area initialization is successful, otherwise false.
     */
    @Override
    public boolean begin(Window window, FileSystem fileSystem) {
        if (super.begin(window, fileSystem)) {
            setBehavior(new ICMonBehavior(window, getTitle()));
            createArea();
            return true;
        }
        return false;
    }

    /**
     * Provides the scale factor for the camera specific to the ICMon game. Overrides the default to provide a game-specific scale factor.
     *
     * @return (float): The camera scale factor for the ICMon game.
     */
    @Override
    public final float getCameraScaleFactor() {
        return ICMon.CAMERA_SCALE_FACTOR;
    }

}