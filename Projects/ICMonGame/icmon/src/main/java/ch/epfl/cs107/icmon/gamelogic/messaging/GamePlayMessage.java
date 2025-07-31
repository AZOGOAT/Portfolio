package ch.epfl.cs107.icmon.gamelogic.messaging;

/**
 * The GamePlayMessage interface represents a message that can be processed within the ICMon game's gameplay.
 * Classes implementing this interface define specific messages that can be sent and processed during gameplay.
 *
 * Messages can be used to trigger events or actions within the game when certain conditions are met.
 * Implementing classes should provide the specific logic for processing the message.
 *
 * This interface serves as a contract for creating custom gameplay messages.
 */
public interface GamePlayMessage{

    /**
     * Processes the gameplay message.
     * Implementing classes should define the behavior that occurs when this message is processed.
     */
    public abstract void process();
}