package ch.epfl.cs107.icmon.actor.pokemon;

import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;

/**
 * Represents the Pokémon Nidoqueen in the game.
 */
public class Nidoqueen extends Pokemon {

    /**
     * Creates a Nidoqueen.
     *
     * @param owner       (Area): The area to which Nidoqueen belongs.
     * @param orientation (Orientation): The initial orientation of Nidoqueen in the area.
     * @param coordinates (DiscreteCoordinates): The initial position of Nidoqueen in the area.
     */
    public Nidoqueen(Area owner, Orientation orientation, DiscreteCoordinates coordinates) {
        super(owner, orientation, coordinates, "nidoqueen", 10, 1);
    }



}