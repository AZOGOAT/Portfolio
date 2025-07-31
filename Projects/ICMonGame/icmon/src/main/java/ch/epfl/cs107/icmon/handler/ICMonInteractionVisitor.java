package ch.epfl.cs107.icmon.handler;

import ch.epfl.cs107.icmon.actor.Door;
import ch.epfl.cs107.icmon.actor.ICMonPlayer;
import ch.epfl.cs107.icmon.actor.Sign;
import ch.epfl.cs107.icmon.actor.items.ICBall;
import ch.epfl.cs107.icmon.actor.npc.ICShopAssistant;
import ch.epfl.cs107.icmon.actor.npc.ProfOak;
import ch.epfl.cs107.icmon.area.ICMonBehavior.ICMonCell;
import ch.epfl.cs107.icmon.gamelogic.fights.ICMonFightableActor;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;

public interface ICMonInteractionVisitor extends AreaInteractionVisitor {
    default void interactWith(ICMonCell other, boolean isCellInteraction) {
    }
    default void interactWith(ICMonPlayer other, boolean isCellInteraction) {
    }
    default void interactWith(ICBall other, boolean isCellInteraction) {
    }
    default void interactWith(ICShopAssistant other, boolean isCellInteraction) {
    }
    default void interactWith(ProfOak other, boolean isCellInteraction) {
    }
    default void interactWith(Door door, boolean isCellInteraction){}

    default void interactWith(ICMonFightableActor hasPokemon,boolean wantsContact){}

    default void interactWith(Sign sign, boolean isCellInteraction) {}

    }
