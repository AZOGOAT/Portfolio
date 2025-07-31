package ch.epfl.cs107.icmon.actor.pokemon.actions;

import ch.epfl.cs107.icmon.actor.pokemon.Pokemon;
import ch.epfl.cs107.icmon.gamelogic.fights.ICMonFightAction;

public class Attack implements ICMonFightAction {
    Pokemon pokemonPlayer;
    public Attack(Pokemon source){
        pokemonPlayer = source;

    }
    @Override
    public void perform() {

    }

    @Override
    public String name() {
        return "Attack";
    }

    @Override
    public boolean doAction(Pokemon target) {
        target.getDamage(pokemonPlayer.properties().damage());
        return true;
    }
}
