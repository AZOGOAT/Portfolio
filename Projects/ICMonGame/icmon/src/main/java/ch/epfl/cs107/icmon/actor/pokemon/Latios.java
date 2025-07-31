package ch.epfl.cs107.icmon.actor.pokemon;

import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;

/**
 * Represents the Pokémon Latios in the game.
 */
public class Latios extends Pokemon {

    /**
     * Creates a Latios.
     *
     * @param owner       (Area): The area to which Latios belongs.
     * @param orientation (Orientation): The initial orientation of Latios in the area.
     * @param coordinates (DiscreteCoordinates): The initial position of Latios in the area.
     */
    public Latios(Area owner, Orientation orientation, DiscreteCoordinates coordinates) {
        super(owner, orientation, coordinates, "latios", 10, 1);
    }





}