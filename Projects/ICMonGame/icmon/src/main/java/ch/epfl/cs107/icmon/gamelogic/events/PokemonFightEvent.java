package ch.epfl.cs107.icmon.gamelogic.events;

import ch.epfl.cs107.icmon.ICMon;
import ch.epfl.cs107.icmon.actor.ICMonPlayer;
import ch.epfl.cs107.icmon.gamelogic.fights.ICMonFight;
import ch.epfl.cs107.icmon.gamelogic.fights.ICMonFightableActor;

/**
 * The PokemonFightEvent class represents an event in the ICMon game where a fight is taking place between Pokemon.
 * It extends the ICMonEvent class and manages the lifecycle of a Pokemon fight event, including starting, updating, and completing the event.
 */
public class PokemonFightEvent extends ICMonEvent {

    /**
     * The pause menu ICMonFight associated with this event.
     * This is typically used to display fight-related information and controls during the event.
     */
    private ICMonFight pauseMenuICMonFight;

    /**
     * The ICMonManager for managing the state and progression of the event.
     * This manager is typically responsible for coordinating various events and actions within the game.
     */
    private ICMon.ICMonManager manager;

    /**
     * The opponent Pokemon for the fight event.
     * This actor represents the Pokemon that the player's Pokemon is fighting against.
     */
    private ICMonFightableActor pokemonOppenent;

    /**
     * Constructs a new PokemonFightEvent. This constructor initializes all the necessary attributes for the fight event,
     * including the participating player, the opponent Pokemon, the player's Pokemon, the game manager, and the associated pause menu.
     *
     * @param player The ICMonPlayer participating in the fight. This is the player's character in the game.
     * @param pokemonOppenent The opponent Pokemon in the fight. This is the enemy that the player's Pokemon will fight.
     * @param pokemonPlayer The player's Pokemon in the fight. This parameter is passed but not stored or used in the current implementation.
     * @param managerBoss The ICMonManager for managing the game's events and actions.
     * @param menu The pause menu ICMonFight associated with this event. This menu is displayed during the fight event.
     */
    public PokemonFightEvent(ICMonPlayer player, ICMonFightableActor pokemonOppenent, ICMonFightableActor pokemonPlayer, ICMon.ICMonManager managerBoss, ICMonFight menu) {
        super(player, managerBoss);
        pauseMenuICMonFight =menu;
        manager = managerBoss;
        this.pokemonOppenent = pokemonOppenent;
    }

    /**
     * Gets the pause menu ICMonFight associated with this event.
     * This menu is typically used to provide the player with fight controls and information.
     *
     * @return The pause menu ICMonFight.
     */
    public ICMonFight getPauseMenuICMonFight() {
        return pauseMenuICMonFight;
    }

    /**
     * Simulates a single time step for the PokemonFightEvent. This method is called on every frame to update
     * the event's state. It checks if the fight is finished and, if so, marks the event as completed.
     *
     * @param deltaTime Elapsed time since the last update, in seconds (non-negative). This is used to control the timing and progression of the fight.
     */
    @Override
    public void update(float deltaTime) {
        // Check if the fight is over and complete the event if it is.
        if (getPauseMenuICMonFight().getIsReallyFinish() ){
            System.out.println("event completed");
            this.complete();
        }
    }
}
