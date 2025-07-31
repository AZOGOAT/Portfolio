package ch.epfl.cs107.play.tuto1;

import ch.epfl.cs107.play.areagame.AreaGame;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.tuto1.actor.SimpleGhost;
import ch.epfl.cs107.play.tuto1.area.maps.Ferme;
import ch.epfl.cs107.play.tuto1.area.maps.Village;
import ch.epfl.cs107.play.window.Button;
import ch.epfl.cs107.play.window.Keyboard;
import ch.epfl.cs107.play.window.Window;

/**
 * Playable element Tuto1
 */
public final class Tuto1 extends AreaGame {

    /** Camera scale's constant */
    public final static float CAMERA_SCALE_FACTOR = 10f;
    /** Step to move of the player  */
    private final static float STEP = .05f;
    /** Areas of the game Tuto1 */
    private final String[] areas = {"zelda/Ferme", "zelda/Village"};
    /** Player of our Tuto1 */
    private SimpleGhost player;
    /** Index of the area to choose (to display)*/
    private int areaIndex;

    /**
     * Enables to add areas that we want to the game
     */
    private void createAreas() {
        addArea(new Ferme());
        addArea(new Village());
    }

    /**
     * Enables the Tuto1 playable element to start, it launches the game.
     *
     * @param window     (Window): display context. Not null
     * @param fileSystem (FileSystem): given file system. Not null
     * @return (boolean): true if game has well started or false in the opposite
     */
    @Override
    public boolean begin(Window window, FileSystem fileSystem) {
        if (super.begin(window, fileSystem)) {
            createAreas();
            areaIndex = 0;
            Area area = setCurrentArea(areas[areaIndex], true);
            player = new SimpleGhost(new Vector(18, 7), "ghost.1");
            area.registerActor(player);
            area.setViewCandidate(player);
            return true;
        }
        return false;
    }

    /**
     * Enables the Tuto1 playable element to be refreshed each deltaTime ,
     * and in particular to make the player move
     * @param deltaTime elapsed time since last update, in seconds, non-negative
     */
    @Override
    public void update(float deltaTime) {
        if (player.isWeak())
            switchArea();
        Keyboard keyboard = getWindow().getKeyboard();
        Button key = keyboard.get(Keyboard.UP);
        if (key.isDown())
            player.moveUp(STEP);
        key = keyboard.get(Keyboard.DOWN);
        if (key.isDown())
            player.moveDown(STEP);
        key = keyboard.get(Keyboard.LEFT);
        if (key.isDown())
            player.moveLeft(STEP);
        key = keyboard.get(Keyboard.RIGHT);
        if (key.isDown())
            player.moveRight(STEP);
        super.update(deltaTime);
    }

    /**
     * (Empty method) from super class AreaGame, overwrote
     */
    @Override
    public void end() {

    }

    /**
     * Represents the Playable Element's title
     *
     * @return String containing the name we want
     */
    @Override
    public String getTitle() {
        return "Tuto1";
    }

    /**
     * Enables character to switch from an area to another
     */
    private void switchArea() {
        Area currentArea = getCurrentArea();
        currentArea.unregisterActor(player);
        areaIndex = (areaIndex == 0) ? 1 : 0;
        currentArea = setCurrentArea(areas[areaIndex], false);
        currentArea.registerActor(player);
        currentArea.setViewCandidate(player);
        player.strengthen();
    }

}