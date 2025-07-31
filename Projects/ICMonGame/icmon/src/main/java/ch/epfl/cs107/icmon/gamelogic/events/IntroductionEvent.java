package ch.epfl.cs107.icmon.gamelogic.events;

import ch.epfl.cs107.icmon.ICMon;
import ch.epfl.cs107.icmon.actor.ICMonPlayer;
import ch.epfl.cs107.icmon.gamelogic.actions.StartDialogAction;
import ch.epfl.cs107.play.engine.actor.Dialog;
import ch.epfl.cs107.play.engine.actor.TextGraphics;

import java.awt.*;


/**
 * Represents the introduction event at the beginning of the ICMon game.
 * This event typically displays the introductory dialog and sets up initial game narrative or context.
 */
public class IntroductionEvent extends ICMonEvent {
    /** The dialog to be displayed as part of the introduction. */
    private Dialog welcomeDialog;
    /** The player character involved in the event. */
    private ICMonPlayer player;
    /** TextGraphics to display introductory text. */
    private TextGraphics forFont = new TextGraphics("", 0.6f, new Color(0, 0, 0)) ;

    /**
     * Constructs a new IntroductionEvent with the specified player and game manager.
     * Initializes the event with a welcome dialog and the initial narrative text.
     *
     * @param player      The player character, not null.
     * @param managerBoss The manager responsible for handling the event's lifecycle, not null.
     */
    public IntroductionEvent(ICMonPlayer player, ICMon.ICMonManager managerBoss) {
        super(player, managerBoss);
        welcomeDialog = new Dialog("welcome_to_icmon");
        forFont.setText("Once Upon a time...");
        this.player = player;
        // Schedule the start dialog action to initiate the welcome dialog at the start of the event.
        onStart(new StartDialogAction(welcomeDialog, player));

    }

    /**
     * Updates the introduction event as time progresses.
     * Completes the event when the welcome dialog is completed, marking the end of the introduction sequence.
     *
     * @param deltaTime The time elapsed since the last update.
     */
    @Override
    public void update(float deltaTime) {
        if (welcomeDialog.isCompleted()) {
            this.complete();
        }
    }
}