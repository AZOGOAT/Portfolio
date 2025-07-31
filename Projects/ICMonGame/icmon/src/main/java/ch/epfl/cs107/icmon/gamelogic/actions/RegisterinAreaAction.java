package ch.epfl.cs107.icmon.gamelogic.actions;

import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.engine.actor.Actor;

/**
 * Represents an action that registers an Actor to an Area within the game.
 * This action encapsulates the behavior necessary to add an actor to a specified area.
 */
public class RegisterinAreaAction implements Action {
    /** The Area to which the Actor will be registered. */
    private Area area;
    /** The Actor to be registered in the Area. */
    private Actor thingToArea;


    /**
     * Constructs a new action to register an Actor to a specified Area.
     *
     * @param area   The Area where the Actor will be registered, not null.
     * @param entity The Actor to register to the Area, not null.
     */
    public RegisterinAreaAction (Area area, Actor entity){
        this.area = area;
        thingToArea = entity;
    }

    /**
     * Executes the action of registering the Actor to the Area.
     * It invokes the area's registerActor method, effectively adding the actor to the area.
     */
    @Override
    public void perform(){
        area.registerActor(thingToArea);
    }
}
