package ch.epfl.cs107.icmon.gamelogic.actions;

import ch.epfl.cs107.icmon.actor.ICMonPlayer;
import ch.epfl.cs107.play.engine.actor.Dialog;

/**
 * Represents an action that initiates a dialog interaction in the game.
 * This action encapsulates the behavior necessary to start a conversation or display information to the player.
 */
public class StartDialogAction implements Action{

    /** The dialog to be displayed. */
    private Dialog dialog;
    /** The player character who will engage in the dialog. */
    private ICMonPlayer player;

    /**
     * Constructs a new StartDialogAction with the specified dialog and player.
     *
     * @param dialog The dialog to start, not null.
     * @param player The player character who will engage in the dialog, not null.
     */
    public StartDialogAction(Dialog dialog, ICMonPlayer player){
        this.dialog = dialog;
        this.player = player;
    }

    /**
     * Executes the action of starting the dialog.
     * It invokes the openDialog method on the player, effectively opening the specified dialog.
     */
    @Override
    public void perform() {
        player.openDialog(dialog);
    }
}