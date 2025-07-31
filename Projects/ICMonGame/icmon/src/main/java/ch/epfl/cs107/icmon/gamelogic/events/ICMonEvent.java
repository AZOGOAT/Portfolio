package ch.epfl.cs107.icmon.gamelogic.events;

import ch.epfl.cs107.icmon.ICMon;
import ch.epfl.cs107.icmon.actor.ICMonPlayer;
import ch.epfl.cs107.icmon.gamelogic.actions.Action;
import ch.epfl.cs107.icmon.gamelogic.actions.RegisterEventAction;
import ch.epfl.cs107.icmon.gamelogic.actions.UnRegisterEventAction;
import ch.epfl.cs107.icmon.handler.ICMonInteractionVisitor;
import ch.epfl.cs107.play.engine.Updatable;

import java.util.ArrayList;

/**
 * Abstract base class representing a game event in ICMon.
 * It defines common behavior for events, including life cycle methods like start, complete, suspend, and resume.
 */
public abstract class ICMonEvent implements Updatable, ICMonInteractionVisitor {
    private boolean isStarted;
    private boolean isComplete;
    private boolean isSuspended;
    private ICMonPlayer player;
    private ArrayList<Action> starting = new ArrayList<>();
    private ArrayList<Action> ending = new ArrayList<>();
    private ArrayList<Action> suspended = new ArrayList<>();
    private ArrayList<Action> resumed = new ArrayList<>();

    /**
     * Constructs an ICMonEvent with specified player and manager. Registers the event upon start and unregisters upon completion.
     *
     * @param player      The player involved in the event, not null.
     * @param managerBoss The manager responsible for handling the event's lifecycle, not null.
     */
    public ICMonEvent(ICMonPlayer player, ICMon.ICMonManager managerBoss){
        this.player = player;
        onStart(new RegisterEventAction(this,managerBoss));
        onComplete(new UnRegisterEventAction(this,managerBoss));
    }

    /**
     * Starts the event, performing all actions defined for the start of the event.
     * Ensures the event is not already started.
     */
    public final void start(){
        if (!isStarted){
            for( Action action:starting){
                action.perform();
            }
            isStarted = true;
        }
    }

    /**
     * Completes the event, performing all actions defined for the end of the event.
     * Ensures the event is started and not already complete.
     */
    public final void complete(){
        if(!isComplete || isStarted){
            for( Action action:ending){
                action.perform();
            }
            isComplete = true;
            isStarted = false;
        }
    }

    /**
     * Suspends the event, performing all actions defined for suspension.
     * Ensures the event is started, not already complete or suspended.
     */
    public final void suspend(){
        if(!isComplete || !isSuspended || isStarted){
            for( Action action:suspended){
                action.perform();
            }
            isComplete = true;
            isSuspended = true;
            isStarted = false;
        }
    }

    /**
     * Resumes the event from suspension, performing all actions defined for resumption.
     * Ensures the event is suspended and started and not completed.
     */
    public final void resume(){
        if(!isComplete || isSuspended || isStarted){
            for( Action action:resumed){
                action.perform();
            }
            isComplete = true;
            isSuspended = false;
            isStarted = false;
        }
    }

    /**
     * Registers an action to be performed at the start of the event.
     *
     * @param action The action to be performed, not null.
     */
    public final void onStart(Action action){
        starting.add(action);
    }

    /**
     * Registers an action to be performed when the event is completed.
     *
     * @param action The action to be performed, not null.
     */
    public final void onComplete(Action action){
        ending.add(action);
    }

    /**
     * Registers an action to be performed when the event is suspended.
     *
     * @param action The action to be performed, not null.
     */
    public final void onSuspension(Action action){
        suspended.add(action);
    }

    /**
     * Registers an action to be performed when the event is resumed.
     *
     * @param action The action to be performed, not null.
     */
    public final void onResume(Action action){
        resumed.add(action);
    }
}
