package ch.epfl.cs107.icmon.gamelogic.actions;

import ch.epfl.cs107.icmon.ICMon;
import ch.epfl.cs107.icmon.actor.ICMonActor;

/**
 * Represents an action within the game logic to make an ICMonActor leave its current area.
 * This class encapsulates the necessary behavior for an actor to exit an area.
 */
public class LeaveAreaAction  implements Action {
    /** The ICMonActor that is to leave the area. */
    private ICMonActor hasPokemonActor;

    /**
     * Constructs a LeaveAreaAction for the specified ICMonActor.
     *
     * @param hasPokemon The ICMonActor that will leave the area, not null.
     * @param state      The current state of the game, not null.
     */
    public LeaveAreaAction(ICMonActor hasPokemon, ICMon.ICMonGameState state){
        hasPokemonActor = hasPokemon;
    }

    /**
     * Executes the action of leaving the area for the specified ICMonActor.
     * It invokes the leaveArea method of the actor, effectively removing it from its current area.
     */
    @Override
    public void perform() {
        hasPokemonActor.leaveArea();
    }

}
