package ch.epfl.cs107.icmon.area.maps;

import ch.epfl.cs107.icmon.actor.Door;
import ch.epfl.cs107.icmon.area.ICMonArea;
import ch.epfl.cs107.play.engine.actor.Background;
import ch.epfl.cs107.play.engine.actor.Foreground;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

/**
 * Represents the House map in the game, a private area typically for rest and character interactions.
 */
public class House extends ICMonArea {

    /**
     * Creates all the actors within the House area, like backgrounds and doors.
     * It sets up the scene for this particular area in the game.
     */
    @Override
    protected void createArea() {
        // Sets up the background and foreground for the house.
        registerActor(new Background(this));
        registerActor(new Foreground(this));
        // Creates a door connecting the house to another area named "Town".
        registerActor(new Door(this,"Town", new DiscreteCoordinates(7, 26),
                new DiscreteCoordinates(3, 1), new DiscreteCoordinates(4, 1)));
    }

    /**
     * Retrieves the player's spawn position in the house.
     *
     * @return (DiscreteCoordinates): The spawn position of the player.
     */
    @Override
    public DiscreteCoordinates getPlayerSpawnPosition() {
        return new DiscreteCoordinates(6,2);
    }

    /**
     * Returns the title of the area, here being "house."
     *
     * @return (String): The title of the area.
     */
    @Override
    public String getTitle() { return "house"; }
}