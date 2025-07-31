package ch.epfl.cs107.icmon.actor;


import ch.epfl.cs107.icmon.ICMon;
import ch.epfl.cs107.icmon.actor.items.ICBall;
import ch.epfl.cs107.icmon.actor.pokemon.Bulbizarre;
import ch.epfl.cs107.icmon.actor.pokemon.Latios;
import ch.epfl.cs107.icmon.actor.pokemon.Nidoqueen;
import ch.epfl.cs107.icmon.actor.pokemon.Pokemon;
import ch.epfl.cs107.icmon.area.ICMonBehavior;
import ch.epfl.cs107.icmon.gamelogic.actions.LeaveAreaAction;
import ch.epfl.cs107.icmon.gamelogic.events.PokemonFightEvent;
import ch.epfl.cs107.icmon.gamelogic.fights.ICMonFight;
import ch.epfl.cs107.icmon.gamelogic.fights.ICMonFightableActor;
import ch.epfl.cs107.icmon.gamelogic.messaging.PassDoorMessage;
import ch.epfl.cs107.icmon.gamelogic.messaging.SuspendWithEvent;
import ch.epfl.cs107.icmon.handler.ICMonInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.actor.Interactor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Dialog;
import ch.epfl.cs107.play.engine.actor.OrientedAnimation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Button;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.window.Keyboard;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents the main player's class in the ICMon game, encapsulating the player's attributes and behaviors.
 * It is responsible for player movement, interaction, and participating in various game events like Pokemon fights.
 */
public final class ICMonPlayer extends ICMonActor implements Interactor {

    /** Duration for moving from one cell to another. */
    private final static int MOVE_DURATION = 2;

    /** Duration of animation frames. */
    public final static int ANIMATION_DURATION = 5;
    // Array holding different animations for different actions
    private OrientedAnimation[] animation;
    // Current animation being displayed
    private OrientedAnimation currentAnimation;
    private OrientedAnimation walkingOnGround;
    private OrientedAnimation walkingOnWater;
    private OrientedAnimation isInteracting;
    // Handles interactions of the player with various game elements
    private final ICMonPlayerInteractionHandler handler;
    // Represents the current game state
    private ICMon.ICMonGameState state;
    // Dialog attribute for managing interactions through dialogs
    private Dialog dialogAttribut;
    // Indicates if the player is currently in a dialog
    private boolean isInDialog ;
    // List of Pokemon the player has
    private ArrayList<Pokemon> playerPokemons = new ArrayList<>() ;

    /**
     * Constructor for ICMonPlayer. Initializes the player with animations, interaction handler, and game state.
     * Also sets the initial set of Pokemons available to the player.
     *
     * @param owner       The area the player is currently in, not null.
     * @param orientation The initial orientation of the player, not null.
     * @param coordinates The initial position of the player in the area, not null.
     * @param state       The current game state, not null.
     */
    public ICMonPlayer(Area owner, Orientation orientation, DiscreteCoordinates coordinates, ICMon.ICMonGameState state) {
        super(owner, orientation, coordinates);
        // Initialize different animations for different player states
        animation = new OrientedAnimation[3]; // je peux construire mon tableau ici avec for
        animation[0] = new OrientedAnimation("actors/player", ANIMATION_DURATION/2, orientation, this);
        animation[1] = new OrientedAnimation("actors/player_water", ANIMATION_DURATION/2, orientation, this);
        animation[2] = new OrientedAnimation("actors/player_interact", ANIMATION_DURATION/2, orientation, this);
        walkingOnGround = animation[0];
        walkingOnWater = animation[1];
        isInteracting = animation[2];
        currentAnimation = walkingOnGround; // Default animation
        handler = new ICMonPlayerInteractionHandler();
        this.state = state;
        setPokemons(); // Initialize player's Pokemon list
        resetMotion();
    }

    /**
     * Update the player for each time step: handles movement, interaction, and animation updates.
     *
     * @param deltaTime elapsed time since last update, in seconds, non-negative
     */
    @Override
    public void update(float deltaTime) {
        Keyboard keyboard = getOwnerArea().getKeyboard();
        // Player movement and interaction handling
        if (!isInDialog) {
            moveIfPressed(Orientation.LEFT, keyboard.get(Keyboard.LEFT));
            moveIfPressed(Orientation.UP, keyboard.get(Keyboard.UP));
            moveIfPressed(Orientation.RIGHT, keyboard.get(Keyboard.RIGHT));
            moveIfPressed(Orientation.DOWN, keyboard.get(Keyboard.DOWN));
        }

        else {
            // Handle dialog progression
            if (dialogAttribut!=null && (keyboard.get(Keyboard.SPACE).isPressed())) {
                dialogAttribut.update(deltaTime);
            }
            // End dialog if completed
            if (dialogAttribut.isCompleted()) {
                isInDialog = false;
            }
        }
        // Update current animation based on player movement
        if (isDisplacementOccurs()){
            currentAnimation.update(deltaTime);
        } else{
            currentAnimation.reset();
            resetMotion();
        }
        super.update(deltaTime);
    }

    /**
     * Draws the player on the game's canvas, including any active dialogs.
     *
     * @param canvas target canvas to draw on, not null
     */
    @Override
    public void draw(Canvas canvas) {
        currentAnimation.draw(canvas);
        if (dialogAttribut!=null && isInDialog ) {
            dialogAttribut.draw(canvas);
        }
    }

    /**
     * Indicates that the player occupies cell space, blocking movement through the cell.
     *
     * @return true as player typically blocks cell space
     */
    @Override
    public boolean takeCellSpace() {
        return true;
    }


    /**
     * Returns the coordinates of the cell(s) the player occupies.
     *
     * @return List of occupied cell coordinates
     */
    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return super.getCurrentCells();
    }

    /**
     * Returns the field of view cells of the player, typically the cells in front of the player.
     *
     * @return List of cells in the player's field of view
     */
    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates().jump(getOrientation().toVector()));
    }

    /**
     * Indicates whether the player wants to interact with other interactable entities in the cell space.
     *
     * @return true if the player is ready for cell interaction
     */
    @Override
    public boolean wantsCellInteraction() {
        return true;
    }

    /**
     * Indicates whether the player wants to interact with other interactable entities in the view.
     * Typically used for actions that require player's confirmation or initiation.
     *
     * @return true if the player is ready for view interaction
     */
    @Override
    public boolean wantsViewInteraction() {
        Keyboard keyboard = getOwnerArea().getKeyboard();
        if (!isInDialog) {
            return keyboard.get(Keyboard.L).isPressed();
        }
        return false;
    }

    /**
     * Executes interaction between the player and another interactable entity.
     *
     * @param other             The interactable entity the player is interacting with.
     * @param isCellInteraction True if the interaction is at the cell level.
     */
    @Override
    public void interactWith(Interactable other, boolean isCellInteraction) {
        other.acceptInteraction(handler,isCellInteraction);
        state.acceptInteraction(other,isCellInteraction);

    }


    /**
     * Accepts interaction from another entity, delegating the handling to the interaction handler.
     *
     * @param visitor            The visitor attempting to interact with the player.
     * @param isCellInteraction True if the interaction is at the cell level.
     */
    @Override
    public void acceptInteraction(AreaInteractionVisitor visitor, boolean isCellInteraction) {
        ((ICMonInteractionVisitor) visitor).interactWith(this, isCellInteraction);
    }

    /**
     * Handles player movement based on orientation and button press.
     *
     * @param orientation The desired orientation of the movement.
     * @param button      The button corresponding to the desired movement.
     */
    private void moveIfPressed(Orientation orientation, Button button ) {
        if (button.isDown()) {
            if (!isDisplacementOccurs()) {
                orientate(orientation);
                currentAnimation.orientate(orientation);
                move(MOVE_DURATION);
            }
        }
    }

    /**
     * Registers the player in a new area at the specified position.
     *
     * @param area     The new area the player is entering.
     * @param position The position of the player in the new area.
     */
    public void enterArea(Area area, DiscreteCoordinates position) {
        area.registerActor(this);
        area.setViewCandidate(this);
        setOwnerArea(area);
        setCurrentPosition(position.toVector());
        resetMotion();
    }

    /**
     * Initializes the player's list of Pokemons with predefined species.
     */
    public void setPokemons(){

        playerPokemons.add(new Bulbizarre(getOwnerArea(),Orientation.DOWN,new DiscreteCoordinates(0,0)));
        playerPokemons.add(new Latios(getOwnerArea(),Orientation.DOWN,new DiscreteCoordinates(0,0)));
        playerPokemons.add(new Nidoqueen(getOwnerArea(),Orientation.DOWN,new DiscreteCoordinates(0,0)));

    }
    /**
     * Chooses a Pokemon from the player's collection to participate in fights.
     *
     * @return The chosen Pokemon for the fight.
     */
    public Pokemon choosedPokemon(){
        return playerPokemons.get(0);
    }

    /**
     * Initiates a fight with another ICMonFightableActor.
     *
     * @param hasPokemon The opposing Pokemon in the fight.
     */
    public void fight(ICMonFightableActor hasPokemon){
        //création de l'évènement "combat"
        PokemonFightEvent eventFight = new PokemonFightEvent(this,hasPokemon,choosedPokemon(),
                state.createManager(), new ICMonFight((Pokemon) hasPokemon, choosedPokemon(), state ));
        //enregistrement dans le jeu

        //creation du message suspendWithEvent
        System.out.println("Sending Fight message");
        SuspendWithEvent suspendCausCombat = new SuspendWithEvent(state,hasPokemon,eventFight,state.createManager());
        //envoi au jeu
        state.send(suspendCausCombat);

        // //solution 1
        eventFight.onComplete(new LeaveAreaAction(hasPokemon,state));


    }


    /**
     * Centers the camera on the player, focusing the view on their position.
     */
    public void centerCamera() {
        super.centerCamera();
    }

    /**
     * Inner class responsible for handling interactions between the player and other game entities.
     */
    private class ICMonPlayerInteractionHandler implements ICMonInteractionVisitor{

        /**
         * Handles interaction with a cell of specific type in the game area.
         *
         * @param other             The cell the player is interacting with.
         * @param isCellInteraction True if the interaction is at the cell level.
         */
        @Override
        public void interactWith(ICMonBehavior.ICMonCell other, boolean isCellInteraction) {
            if(isCellInteraction){
                switch (other.getType()) {
                    case WATER -> currentAnimation = walkingOnWater;
                    case INDOOR_WALKABLE, OUTDOOR_WALKABLE,GRASS -> currentAnimation = walkingOnGround;
                    case  INTERACTION-> currentAnimation = isInteracting;
                }
            }
        }

        /**
         * Handles interaction with an ICBall, typically collecting it.
         *
         * @param ball             The ICBall the player is interacting with.
         * @param isCellInteraction True if the interaction is at the cell level.
         */
        @Override
        public void interactWith(ICBall ball, boolean isCellInteraction){
            if (wantsViewInteraction()) {
                ball.collect();
            }
        }

        /**
         * Handles interaction with a Door, typically triggering a transition to another area.
         *
         * @param door             The Door the player is interacting with.
         * @param isCellInteraction True if the interaction is at the cell level.
         */
        @Override
        public void interactWith(Door door, boolean isCellInteraction) {
            if (isCellInteraction){
                System.out.println("Sending Pass_door message");
                PassDoorMessage message = new PassDoorMessage(door, state);
                state.send(message);


            }
        }

        /**
         * Handles interaction with an ICMonFightableActor, typically initiating a fight.
         *
         * @param hasPokemon        The ICMonFightableActor the player is interacting with.
         * @param wantsContactInteraction True if the interaction is a close contact.
         */
        @Override
        public void interactWith(ICMonFightableActor hasPokemon , boolean wantsContactInteraction ){
            if (wantsContactInteraction){
                System.out.println("INTERRAAACCTINGG");
                fight(hasPokemon);


            }

        }

        /**
         * Handles interaction with a Sign, typically displaying its message.
         *
         * @param sign              The Sign the player is interacting with.
         * @param isCellInteraction True if the interaction is at the cell level.
         */
        @Override
        public void interactWith(Sign sign, boolean isCellInteraction) {
            openDialog(new Dialog(sign.pathSign));
            System.out.println("I heard that you were able to implement this step successfully. Congrats !");
        }

    }

    /**
     * Opens a dialog for the player, typically displaying a message or interaction prompt.
     *
     * @param dialog The dialog to open.
     */
    public void openDialog(Dialog dialog){
        dialogAttribut = dialog;
        isInDialog = true;
    }
}