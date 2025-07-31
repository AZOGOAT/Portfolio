package ch.epfl.cs107.icmon.gamelogic.actions;

/**
 * Represents an action that logs a message in the terminal, primarily for debugging or informational purposes within the game.
 */
public class LogAction implements Action{
    /** The message to be logged when the action is performed. */
    private String message;

    /**
     * Constructs a LogAction with a specific message to log.
     *
     * @param message The message to log, not null.
     */
    public LogAction(String message){
        this.message = message;
    }

    /**
     * Performs the logging action by printing the message to the system's console.
     */
    @Override
    public void perform(){
        System.out.println(message);
    }
}
