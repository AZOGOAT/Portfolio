package ch.epfl.cs107.icmon.gamelogic.fights;

import ch.epfl.cs107.icmon.actor.ICMonActor;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.engine.actor.Actor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
//Class pour plus tard en cas d'ajout d'un dresseur pokemon a combattre

public abstract class ICMonFightableActor extends ICMonActor implements Interactable {


    /**
     * ???
     *
     * @param owner       ???
     * @param orientation ???
     * @param coordinates ???
     */
    public ICMonFightableActor(Area owner, Orientation orientation, DiscreteCoordinates coordinates) {
        super(owner, orientation, coordinates);
    }

}
