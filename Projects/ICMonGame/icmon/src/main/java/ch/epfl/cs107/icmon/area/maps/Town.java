package ch.epfl.cs107.icmon.area.maps;

import ch.epfl.cs107.icmon.actor.Door;
import ch.epfl.cs107.icmon.actor.Sign;
import ch.epfl.cs107.icmon.actor.npc.ICShopAssistant;
import ch.epfl.cs107.icmon.area.ICMonArea;
import ch.epfl.cs107.play.engine.actor.Background;
import ch.epfl.cs107.play.engine.actor.Foreground;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;

/**
 * Represents the Town area in the game, a central hub connecting various buildings and locations.
 */
public final class Town extends ICMonArea {

    /**
     * Retrieves the player's spawn position when entering the town.
     *
     * @return (DiscreteCoordinates): The spawn position of the player in the town.
     */
    @Override
    public DiscreteCoordinates getPlayerSpawnPosition() {
        return new DiscreteCoordinates(5, 5);
    }

    /**
     * Creates all the actors within the Town area, like backgrounds, doors, signs, and NPCs.
     * It sets up the scene for this particular area in the game.
     */
    @Override
    protected void createArea() {
        // Sets up the background and foreground for the town.
        registerActor(new Background(this));
        registerActor(new Foreground(this));
        // Registers various doors connecting the town to other areas like lab, arena, house, and shop.
        registerActor ( new Door(this, "lab", new DiscreteCoordinates(6, 2), new DiscreteCoordinates(15, 24)));
        registerActor ( new Door(this, "arena", new DiscreteCoordinates(4, 2), new DiscreteCoordinates(20, 16)));
        registerActor ( new Door(this,"house", new DiscreteCoordinates(2,2), new DiscreteCoordinates(7,27)));
        registerActor ( new Door(this,"shop", new DiscreteCoordinates(3,2), new DiscreteCoordinates(25,20)));
        // Registers an NPC, ICShopAssistant, for interactions within the town.
        registerActor(new ICShopAssistant(this, Orientation.DOWN,new DiscreteCoordinates(8,8)));
        // Registers a sign
        registerActor ( new Sign(this,"sign_lab",new DiscreteCoordinates(17,16)));

    }

    /**
     * Returns the title of the area, here being "Town."
     *
     * @return (String): The title of the area.
     */
    @Override
    public String getTitle() {
        return "Town";
    }

    /**
     * Performs time-based updates for the town if needed.
     *
     * @param deltaTime The time elapsed since the last update.
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
    }

}
