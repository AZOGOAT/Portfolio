package ch.epfl.cs107.icmon.gamelogic.fights;

import ch.epfl.cs107.icmon.ICMon;
import ch.epfl.cs107.icmon.actor.pokemon.Pokemon;
import ch.epfl.cs107.icmon.actor.pokemon.actions.Attack;
import ch.epfl.cs107.icmon.graphics.ICMonFightActionSelectionGraphics;
import ch.epfl.cs107.icmon.graphics.ICMonFightArenaGraphics;
import ch.epfl.cs107.icmon.graphics.ICMonFightTextGraphics;
import ch.epfl.cs107.play.engine.PauseMenu;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.window.Keyboard;

import java.util.List;

public class ICMonFight extends PauseMenu {
    float pokemonCompteur ;
    private Pokemon pokemonPlayer;
    private Pokemon pokemonOppenent;
    private ICMonFightArenaGraphics arena;
    private boolean isReallyFinish;
    private enum combatState { INTRODUCTION,SELECTION_ACTION,EXECUTION_ACTION,OPPENENT_ACTION,CONCLUSION};
    private combatState currentstate=combatState.INTRODUCTION;
    private ICMonFightAction actionChosen;
    private List<ICMonFightAction> playerActions;
    private boolean playerIsWinning;

    private boolean oppenentIsWinning;
    private boolean playerWithdraw;
    private boolean opponentWithdraw;
    ICMonFightActionSelectionGraphics choosingWindow;


    private ICMon.ICMonGameState stating;

    public ICMonFight(Pokemon PokemonPlayer, Pokemon PokemonOppenent, ICMon.ICMonGameState state){
        pokemonCompteur = 5f;
        pokemonPlayer = PokemonPlayer;
        pokemonOppenent=PokemonOppenent;
        stating = state;

        arena = new ICMonFightArenaGraphics(CAMERA_SCALE_FACTOR , pokemonPlayer.properties(),
                pokemonOppenent.properties());

    }

    public boolean getIsReallyFinish(){
        return isReallyFinish;
    }


    /**
     * @param c (Canvas): the context canvas : here the Window
     */
    @Override
    protected void drawMenu(Canvas c) {
        arena.draw(c);

    }
    public void update(float deltaTime){
        super.update(deltaTime);
        Keyboard keyboard = getKeyboard();
        System.out.println(currentstate);

        switch (currentstate) {

            case INTRODUCTION -> {
                arena.setInteractionGraphics(new ICMonFightTextGraphics(CAMERA_SCALE_FACTOR,
                        "Welcome to the fight"));
                if (keyboard.get(Keyboard.SPACE).isPressed()) {
                    //arena.set
                    playerActions = pokemonPlayer.getFightActions();
                    choosingWindow = new ICMonFightActionSelectionGraphics(CAMERA_SCALE_FACTOR,keyboard,playerActions);
                    arena.setInteractionGraphics(choosingWindow);
                    currentstate = combatState.SELECTION_ACTION;
                }
            }
            case SELECTION_ACTION -> {

                choosingWindow.update(deltaTime);

                if(choosingWindow.choice()==null){
                    choosingWindow.update(deltaTime);

                }
                else {
                    //arena.setInteractionGraphics(choosingWindow);
                    actionChosen = choosingWindow.choice();
                    choosingWindow = new ICMonFightActionSelectionGraphics(CAMERA_SCALE_FACTOR,keyboard,playerActions);

                    currentstate = combatState.EXECUTION_ACTION;


                }
            }
            case EXECUTION_ACTION -> {
                //arena.set
                boolean ConditionPassPlayer = actionChosen.doAction(pokemonOppenent);
                System.out.println( ConditionPassPlayer);
                if (!ConditionPassPlayer) {
                    playerWithdraw = true;
                    currentstate = combatState.CONCLUSION;
                } else if (!pokemonOppenent.isAlive()) {
                    playerIsWinning = true;
                    currentstate = combatState.CONCLUSION;
                } else {
                    currentstate = combatState.OPPENENT_ACTION;
                }



            }
            case OPPENENT_ACTION -> {
                for (ICMonFightAction action : pokemonOppenent.getFightActions()){
                    if ( action instanceof Attack){
                        boolean doActionBool = action.doAction(pokemonPlayer);
                        if (!pokemonPlayer.isAlive()){
                            oppenentIsWinning=true;
                            currentstate= combatState.CONCLUSION;

                        }
                        else if (!doActionBool){
                            opponentWithdraw=true;
                            currentstate= combatState.CONCLUSION;

                        }
                        else{
                            currentstate = combatState.SELECTION_ACTION;


                        }

                    }


                }
            }
            case CONCLUSION -> {
                if (playerIsWinning){
                    arena.setInteractionGraphics(new ICMonFightTextGraphics(CAMERA_SCALE_FACTOR,
                            "Your " + pokemonPlayer.properties().name() + " has  W O N  the fight !! "));
                }
                else if (oppenentIsWinning){
                    arena.setInteractionGraphics(new ICMonFightTextGraphics(CAMERA_SCALE_FACTOR,
                            pokemonOppenent.properties().name() + "' s oppenent has WON the fight ... "));

                }
                else if (playerWithdraw){
                    arena.setInteractionGraphics(new ICMonFightTextGraphics(CAMERA_SCALE_FACTOR,
                            "You decided to withdraw ... ^^ "));

                }
                else if (opponentWithdraw){
                    arena.setInteractionGraphics(new ICMonFightTextGraphics(CAMERA_SCALE_FACTOR,
                            "Opponent decided to not continue the fight !"));

                }

                System.out.println("whenever entered there");

                if (keyboard.get(Keyboard.SPACE).isPressed()) {
                    isReallyFinish=true;
                    System.out.println("we entered there");
                }

            }
            /*
            case COMPTOR_GESTION -> {

                    if (pokemonCompteur > 0) {
                        pokemonCompteur -= deltaTime;
                    }
                    if (pokemonCompteur <= 0) {
                        pokemonCompteur = 0;
                        arena.setInteractionGraphics(new ICMonFightTextGraphics(CAMERA_SCALE_FACTOR,
                                "Good Fight"));
                        if (keyboard.get(Keyboard.SPACE).isPressed()) {

                            currentstate = combatState.CONCLUSION;
                            System.out.println("we entered there");
                        }
                    }
                    System.out.println(pokemonCompteur);

                }

             */




        }



    }

    @Override
    public void end() {

        //stating.EndPauseMenu();


    }
    public boolean isRunning(){
        if (pokemonCompteur > 0){
            return true;
        }
        return false ;  //|| (getKeyboard().get(Keyboard.SPACE).isPressed());
    }

}
