package ch.epfl.cs107.icmon.area.maps;

import ch.epfl.cs107.icmon.actor.Door;
import ch.epfl.cs107.icmon.actor.pokemon.Bulbizarre;
import ch.epfl.cs107.icmon.area.ICMonArea;
import ch.epfl.cs107.play.engine.actor.Background;
import ch.epfl.cs107.play.engine.actor.Foreground;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;

/**
 * Represents the Arena map in the game, a specialized area for Pokémon battles and encounters.
 */
public class Arena extends ICMonArea {

    /**
     * Retrieves the player's spawn position in the arena.
     *
     * @return (DiscreteCoordinates): The spawn position of the player.
     */
    @Override
    public DiscreteCoordinates getPlayerSpawnPosition() {
        return new DiscreteCoordinates(6,2);
    }

    /**
     * Returns the title of the area, here being "arena."
     *
     * @return (String): The title of the area.
     */
    @Override
    public String getTitle() {
        return "arena";
    }

    /**
     * Creates all the actors within the Arena area, like backgrounds, doors, and Pokémon.
     * It sets up the scene for this particular area in the game.
     */
    @Override
    protected void createArea() {
        // Sets up the background and foreground for the arena.
        registerActor(new Background(this));
        registerActor(new Foreground(this));
        // Creates a door connecting the arena to another area named "Town".
        registerActor(new Door(this,"Town", new DiscreteCoordinates(20, 15),
                new DiscreteCoordinates(4, 1), new DiscreteCoordinates(5, 1)));
        // Adds a Bulbizarre Pokémon to the arena.
        registerActor(new Bulbizarre(this, Orientation.DOWN, new DiscreteCoordinates(6, 6)));
    }
}