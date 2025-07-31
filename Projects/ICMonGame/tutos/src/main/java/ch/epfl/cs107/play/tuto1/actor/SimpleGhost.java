package ch.epfl.cs107.play.tuto1.actor;

import ch.epfl.cs107.play.engine.actor.Entity;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.engine.actor.TextGraphics;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

import java.awt.*;

/**
 * Represents the Ghost character at its Simple state (tuto1's state)
 */
public final class SimpleGhost extends Entity {
    /** Represents the character's amount of heart points (HP)  */
    private float hp;
    /** Represents the displayable character's amount of HP   */
    private final TextGraphics hpText;
    /** Attribut for Graphic representation  */
    private final Sprite sprite;

    /**
     * Constructor of the SimpleGhost class
     * @param position initial position of Vector type to set Ghost on
     * @param spriteName Name of the picture's linked to the Ghost to set on Ghost's sprite attribut
     */
    public SimpleGhost(Vector position, String spriteName) {
        super(position);
        this.hp = 10;
        sprite = new Sprite(spriteName, 1f, 1f, this);
        hpText = new TextGraphics(Integer.toString((int) hp), .4f, Color.BLUE);
        hpText.setParent(this);
        hpText.setAnchor(new Vector(-.3f, .1f));
    }

    /**
     * Update method overwrote from Actor's Interface inplemented by super class Entity
     * Enables the character to evolve in the game, in particular here its HP
     * @param deltaTime elapsed time since last update, in seconds, non-negative
     */
    @Override
    public void update(float deltaTime) {
        if (hp > 0f) {
            hp -= deltaTime;
            hpText.setText(Integer.toString((int) hp));
        }
        if (hp < 0f) hp = 0f;
    }

    /**
     * Enables the Ghost to be drawn on the Canva
     * @param canvas target, not null
     */
    @Override
    public void draw(Canvas canvas) {
        sprite.draw(canvas);
        hpText.draw(canvas);
    }

    /**
     * Enables the character to move UP
     * @param delta distance that we want the character to move of
     */
    public void moveUp(float delta) {
        setCurrentPosition(getPosition().add(0f, delta));
    }

    /**
     * Enables the character to move DOWN
     * @param delta distance that we want the character to move of
     */
    public void moveDown(float delta) {
        setCurrentPosition(getPosition().add(0f, -delta));
    }

    /**
     * Enables the character to move LEFT
     * @param delta distance that we want the character to move of
     */
    public void moveLeft(float delta) {
        setCurrentPosition(getPosition().add(-delta, 0f));
    }

    /**
     * Enables the character to move RIGHT
     * @param delta distance that we want the character to move of
     */
    public void moveRight(float delta) {
        setCurrentPosition(getPosition().add(delta, 0f));
    }

    /**
     * Method to know if Ghost still have HP
     * @return True if the character's HP is equal to 0
     */
    public boolean isWeak() {
        return (hp == 0f);
    }

    /**
     * Sets the HP value at a positive given value (here 10)
     */
    public void strengthen() {
        hp = 10;
    }

}