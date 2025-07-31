package ch.epfl.cs107.icmon.area.maps;

import ch.epfl.cs107.icmon.actor.Door;
import ch.epfl.cs107.icmon.actor.npc.ICShopAssistant;
import ch.epfl.cs107.icmon.area.ICMonArea;
import ch.epfl.cs107.play.engine.actor.Background;
import ch.epfl.cs107.play.engine.actor.Foreground;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;

/**
 * Represents the Shop map in the game, a "commercial area" for interacting with shop assistants.
 */
public class Shop extends ICMonArea {

    /**
     * Retrieves the player's spawn position in the shop.
     *
     * @return (DiscreteCoordinates): The spawn position of the player.
     */
    @Override
    public DiscreteCoordinates getPlayerSpawnPosition() {
        return new DiscreteCoordinates(6, 2);
    }

    /**
     * Returns the title of the area, here being "shop."
     *
     * @return (String): The title of the area.
     */
    @Override
    public String getTitle() {
        return "shop";
    }

    /**
     * Creates all the actors within the Shop area, such as backgrounds, doors, and NPCs like ICShopAssistant.
     * It sets up the scene for this particular area in the game.
     */
    @Override
    protected void createArea() {
        // Sets up the background and foreground for the shop.
        registerActor(new Background(this));
        registerActor(new Foreground(this));
        // Creates a door connecting the shop to another area named "Town".
        registerActor(new Door(this, "Town", new DiscreteCoordinates(25, 19),
                new DiscreteCoordinates(3, 1), new DiscreteCoordinates(4, 1)));
        // Adds an ICShopAssistant to the shop for interactions and purchasing items.
        registerActor(new ICShopAssistant(this, Orientation.DOWN,new DiscreteCoordinates(4,6)));
    }
}