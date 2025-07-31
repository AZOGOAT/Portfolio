package ch.epfl.cs107.icmon.gamelogic.actions;

import ch.epfl.cs107.icmon.actor.items.ICMonItem;
import ch.epfl.cs107.play.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.engine.actor.Actor;

public class UnRegisterAreaAction implements Action {
    private Area area;

    private Actor thingToArea;
    public void perform(){
        area.unregisterActor(thingToArea);

    }
    public UnRegisterAreaAction (Area area, Actor entity){
        this.area = area;
        thingToArea = entity;
    }
}

