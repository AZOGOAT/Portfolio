package ch.epfl.cs107.icmon.actor.pokemon;

import ch.epfl.cs107.icmon.actor.pokemon.actions.Attack;
import ch.epfl.cs107.icmon.actor.pokemon.actions.RunAway;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.engine.actor.RPGSprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;

/**
 * Represents the Pokémon Bulbizarre in the game.
 */
public class Bulbizarre extends Pokemon {

    /**
     * Creates a Bulbizarre.
     *
     * @param owner       (Area): The area to which Bulbizarre belongs.
     * @param orientation (Orientation): The initial orientation of Bulbizarre in the area.
     * @param coordinates (DiscreteCoordinates): The initial position of Bulbizarre in the area.
     */
    public Bulbizarre(Area owner, Orientation orientation, DiscreteCoordinates coordinates) {
        super(owner, orientation, coordinates, "bulbizarre", 10, 1);
        setFightAction(new Attack(this));
        setFightAction(new RunAway());
    }



}