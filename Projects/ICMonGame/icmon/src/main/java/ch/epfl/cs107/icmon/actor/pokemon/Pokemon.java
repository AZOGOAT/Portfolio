package ch.epfl.cs107.icmon.actor.pokemon;

import ch.epfl.cs107.icmon.gamelogic.fights.ICMonFightAction;
import ch.epfl.cs107.icmon.gamelogic.fights.ICMonFightableActor;
import ch.epfl.cs107.icmon.handler.ICMonInteractionVisitor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.RPGSprite;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Canvas;

import java.util.ArrayList;
import java.util.List;


/**
 * Abstract class representing a Pokémon, an interactable and fightable entity in the game.
 * It serves as a base for all specific Pokémon instances.
 */
public abstract class Pokemon extends ICMonFightableActor {
    /** Sprite representing the visual appearance of the Pokémon. */
    private final Sprite spritePokemon;
    /** Name of the Pokémon. */
    private String pokemonName;
    /** Current health points of the Pokémon. */
    private int pokemonHp;
    /** Maximum health points specific to each Pokémon. */
    private static int MAX_HP; //Constante propre à chaque pokemon
    /** Maximum damage this Pokémon can inflict. */
    private static int MAX_DAMAGE;
    /** Indicator of whether the Pokémon is alive. */
    private boolean alive;
    /** List of fight actions available to the Pokémon. */
    private List<ICMonFightAction> fightActions = new ArrayList();


    /**
     * Creates a new Pokémon with specified properties.
     *
     * @param owner       The area the Pokémon will inhabit.
     * @param orientation The initial orientation of the Pokémon.
     * @param coordinates The initial position of the Pokémon.
     * @param name        The name of the Pokémon.
     * @param hpMax       The maximum health points of the Pokémon.
     * @param damage      The maximum damage the Pokémon can inflict.
     */
    public Pokemon(Area owner, Orientation orientation, DiscreteCoordinates coordinates, String name,int hpMax, int damage) {
        super(owner, orientation, coordinates);
        pokemonName = name;
        MAX_HP = hpMax;
        MAX_DAMAGE = damage;

        pokemonHp = MAX_HP;
        alive = true;
        spritePokemon = new RPGSprite("pokemon/" + pokemonName, 1, 1, this);
    }

    /**
     * Draws the Pokémon on the given canvas.
     *
     * @param canvas The canvas to draw the Pokémon on.
     */
    @Override
    public void draw(Canvas canvas) {
        spritePokemon.draw(canvas);    }

    /**
     * Indicates whether the Pokémon occupies cell space.
     *
     * @return false, as Pokémon typically do not block cell space.
     */
    @Override
    public boolean takeCellSpace() {
        return false;
    }

    /**
     * Adds a fight action to the Pokémon's list of available actions.
     *
     * @param fightActions The fight action to add.
     */
    public void setFightAction(ICMonFightAction fightActions) {
        this.fightActions.add(fightActions);
    }

    /**
     * Gets the list of fight actions available to the Pokémon.
     *
     * @return The list of available fight actions.
     */
    public List<ICMonFightAction> getFightActions() {
        return fightActions;
    }

    /**
     * Applies damage to the Pokémon, reducing its health points.
     *
     * @param damage The amount of damage to apply.
     */
    public void getDamage(int damage ) {
        if(pokemonHp >0) {
            pokemonHp -= damage;
        }
        if (pokemonHp <0){
            pokemonHp =0;
        }
    }
    /**
     * Checks if the Pokémon is alive based on its health points.
     *
     * @return true if the Pokémon's health points are above 0, false otherwise.
     */
    public boolean isAlive(){
        return ( pokemonHp >0 );
    }

    /**
     * Provides access to the Pokémon's properties through the inner class.
     *
     * @return An instance of PokemonProperties containing the Pokémon's properties.
     */
    public PokemonProperties properties(){
        return new PokemonProperties();
    }


    /**
     * Inner class encapsulating properties of the Pokémon for external access.
     */
    public final class PokemonProperties {

        public String name(){
            return pokemonName;
        }

        public float hp(){
            return pokemonHp;
        }

        public float maxHp(){
            return MAX_HP;
        }

        public int damage(){
            return MAX_DAMAGE;
        }

    }

    /**
     * Handles interactions with this Pokémon when another actor attempts to interact.
     *
     * @param v                    The visitor attempting to interact with this Pokémon.
     * @param wantsContactInteraction Indicates if the interaction is close contact or not.
     */
    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean wantsContactInteraction) {
        ((ICMonInteractionVisitor) v).interactWith(this , wantsContactInteraction);
    }
}
