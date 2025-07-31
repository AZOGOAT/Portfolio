package ch.epfl.cs107.icmon.area.maps;

import ch.epfl.cs107.icmon.actor.Door;
import ch.epfl.cs107.icmon.actor.npc.ProfOak;
import ch.epfl.cs107.icmon.area.ICMonArea;
import ch.epfl.cs107.play.engine.actor.Background;
import ch.epfl.cs107.play.engine.actor.Foreground;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;

/**
 * Represents the Laboratory map in the game, a place for scientific research and character interaction, especially with ProfOak.
 */
public class Lab extends ICMonArea {

    /**
     * Retrieves the player's spawn position in the laboratory.
     *
     * @return (DiscreteCoordinates): The spawn position of the player.
     */
    @Override
    public DiscreteCoordinates getPlayerSpawnPosition() {
        return new DiscreteCoordinates(6,2);
    }

    /**
     * Returns the title of the area, here being "lab."
     *
     * @return (String): The title of the area.
     */
    @Override
    public String getTitle() {
        return "lab";
    }

    /**
     * Creates all the actors within the Lab area, such as backgrounds, doors, and NPCs like ProfOak.
     * It sets up the scene for this particular area in the game.
     */
    @Override
    protected void createArea() {
        // Sets up the background and foreground for the lab.
        registerActor(new Background(this));
        registerActor(new Foreground(this));
        // Creates a door connecting the lab to another area named "Town".
        registerActor(new Door(this,"Town", new DiscreteCoordinates(15, 23),
                new DiscreteCoordinates(6, 1), new DiscreteCoordinates(7, 1)));
        // Adds ProfOak to the lab for interactions and storyline progression.
        registerActor(new ProfOak(this, Orientation.DOWN,new DiscreteCoordinates(11,7)));
    }
}