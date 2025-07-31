package ch.epfl.cs107.icmon;


import ch.epfl.cs107.icmon.actor.Door;
import ch.epfl.cs107.icmon.actor.items.ICBall;
import ch.epfl.cs107.icmon.actor.npc.ICShopAssistant;
import ch.epfl.cs107.icmon.area.maps.*;
import ch.epfl.cs107.icmon.gamelogic.actions.*;
import ch.epfl.cs107.icmon.gamelogic.events.*;
import ch.epfl.cs107.icmon.gamelogic.fights.ICMonFight;
import ch.epfl.cs107.icmon.gamelogic.fights.ICMonFightableActor;
import ch.epfl.cs107.icmon.gamelogic.messaging.GamePlayMessage;
import ch.epfl.cs107.icmon.gamelogic.messaging.PassDoorMessage;
import ch.epfl.cs107.icmon.gamelogic.messaging.SuspendWithEvent;
import ch.epfl.cs107.play.areagame.AreaGame;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.engine.PauseMenu;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.icmon.actor.ICMonPlayer;
import ch.epfl.cs107.icmon.area.ICMonArea;
import ch.epfl.cs107.play.window.Keyboard;
import ch.epfl.cs107.play.window.Window;

import java.util.ArrayList;

/**
 * Represents the playable Game
 */
public class ICMon extends AreaGame {
    ///ATTRIBUTS :
    /** ??? */
    public final static float CAMERA_SCALE_FACTOR = 13.f;
    /** ??? */
    private final String[] areas = {"Town","lab","arena", "house", "shop"};
    /** ??? */
    private static ICMonPlayer player;
    /** ??? */
    private int areaIndex;

    private ICMonArea town;

    //private ICMonManager managerBigBoss;

    private ICBall ball;

    private ICShopAssistant madam;

    private ArrayList<ICMonEvent> events = new ArrayList<>();

    private ArrayList<ICMonEvent> startedEvents ;

    private ArrayList<ICMonEvent> completedEvents ;

    private GamePlayMessage mailBox ;

    private ICMonGameState statut = new ICMonGameState();

    // METHODES
    /**
     * ???
     */
    private void createAreas() {
        town = new Town();
        addArea(town);
        System.out.println("successfully added town");
        addArea(new Lab());
        System.out.println("successfully added lab");
        addArea(new Arena());
        System.out.println("successfully added arena");
        addArea(new House());
        System.out.println("successfully added house");
        addArea(new Shop());
        System.out.println("successfully added shop");
    }
    private void resetGame(Window window, FileSystem fileSystem){
        begin(window, fileSystem);

    }

    /**
     * ???
     * @param window (Window): display context. Not null
     * @param fileSystem (FileSystem): given file system. Not null
     * @return ???
     */
    @Override
    public boolean begin(Window window, FileSystem fileSystem) {
        if (super.begin(window, fileSystem)) {
            //Initialisation des areas de jeu
            createAreas();
            areaIndex = 3;
            initArea(areas[areaIndex]);
            ICMonManager manager1 = new ICMonManager();
            ICMonArea area = (ICMonArea) getCurrentArea();
            //CollectItem event IC Ball
            ball = new ICBall(town, new DiscreteCoordinates(6, 6));

            events(manager1, town);
            //area.registerActor(ball);

            //Creation of Ball Event

            /*CollectItemEvent CollectBallEvent = new CollectItemEvent(player, ball, manager1);
            LogAction collectStarted = new LogAction("CollectItemEvent PokeBall started !");
            CollectBallEvent.onStart(collectStarted);
            CollectBallEvent.onStart(new RegisterinAreaAction(area,ball));
            madam = new ICShopAssistant(area, Orientation.DOWN,new DiscreteCoordinates(8,8));
            CollectBallEvent.onStart(new RegisterinAreaAction(area,madam));

            LogAction collectComplete = new LogAction("CollectItemEvent PokeBall completed !");
            CollectBallEvent.onComplete(collectComplete); */


            //Action BallRegistered = new RegisterinAreaAction(getCurrentArea(),ball);

            //RegisterEventAction collectStartedRegister = new RegisterEventAction(CollectBallEvent,managerBigBoss);

            /* ICMonManager manager2 = new ICMonManager();

            EndOfTheGameEvent endOfTheGameEvent = new EndOfTheGameEvent(player,manager2);
            CollectBallEvent.onComplete(new StartEventAction(endOfTheGameEvent));
            CollectBallEvent.onComplete(new UnRegisterEventAction(CollectBallEvent,manager1));
            LogAction logAction = new LogAction("The second event has started !");

            endOfTheGameEvent.onStart(logAction);
            StartEventAction logAction1 = new StartEventAction(endOfTheGameEvent);
            endOfTheGameEvent.onComplete(logAction1);

            CollectBallEvent.start(); */


            //events.add(CollectBallEvent);

            /*
            event1.onStart(collectStarted);
            event1.onStart(BallRegistered);
            event1.onStart(collectStartedRegister);


            //Creation of Event
            EndOfTheGameEvent event2 = new EndOfTheGameEvent(player) ;

            LogAction MadamEventStarted = new LogAction("End Of the Game (Madam Event) started !");

            Action MadamRegistered = new RegisterEventAction(event2,managerBigBoss);


            event1.onComplete(collectComplete);
            //event1.onComplete(new UnRegisterEventAction(event1,managerBigBoss));

            event1.onComplete(new StartEventAction(event2));
            event1.onComplete(MadamRegistered);
            event1.onComplete(new UnRegisterEventAction(event1,managerBigBoss));

            event2.onStart(MadamEventStarted);
            //event2.onStart(MadamRegistered);

             */

            return true;
        }
        return false;
    }

    public void events(ICMonManager manager, ICMonArea area) { // je dois mettre une town en paramètre pour la balle quand je voudrais faire apparaitre le personnage dans la maison

        ICMonEvent introEvent = new IntroductionEvent(player, manager);
        ICMonEvent oakEvent = new FirstInteractionWithProfOakEvent(player, manager);
        ICMonEvent CollectBallEvent = new CollectItemEvent(player, ball, manager);
        ICMonEvent endOfTheGameEvent = new EndOfTheGameEvent(player,manager);

        oakEvent.onComplete(new RegisterinAreaAction(area, ball));
        LogAction collectStarted = new LogAction("CollectItemEvent PokeBall started !");
        CollectBallEvent.onStart(collectStarted);
        //CollectBallEvent.onStart(new RegisterinAreaAction(area, ball));
        //CollectBallEvent.onStart(new RegisterinAreaAction(area,ball));
        //madam = new ICShopAssistant(area, Orientation.DOWN,new DiscreteCoordinates(8,8));
        //CollectBallEvent.onStart(new RegisterinAreaAction(area,madam));

        LogAction collectComplete = new LogAction("CollectItemEvent PokeBall completed !");
        CollectBallEvent.onComplete(collectComplete);

        //CollectBallEvent.onComplete(new StartEventAction(endOfTheGameEvent));
        //CollectBallEvent.onComplete(new UnRegisterEventAction(CollectBallEvent,manager));
        LogAction logAction = new LogAction("The second event has started !");

        endOfTheGameEvent.onStart(logAction);
        StartEventAction logAction1 = new StartEventAction(endOfTheGameEvent);
        endOfTheGameEvent.onComplete(logAction1);

        ICMonChainedEvent chain = new ICMonChainedEvent(player, manager, introEvent, oakEvent, CollectBallEvent, endOfTheGameEvent);

        events.add(chain);
        chain.start();

        /*introEvent.onComplete(new StartEventAction(oakEvent));
        oakEvent.onComplete(new StartEventAction(collectBallEvent));
        //collectBallEvent.onStart(new RegisterInAreaAction(currentArea, ball)); je dois créer une action RegisterInAreaAction
        collectBallEvent.onStart(new LogAction("CollectItemEvent PokeBall started!"));
        collectBallEvent.onComplete(new LogAction("CollectItemEvent PokeBall completed!"));
        collectBallEvent.onComplete(new StartEventAction(endGameEvent));*/
    }

    /**
     * ???
     * @param deltaTime elapsed time since last update, in seconds, non-negative
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        for (ICMonEvent event:events){
            event.update(deltaTime);
        }
        for (ICMonEvent event : startedEvents){
            events.add(event);
        }
        for (ICMonEvent event : completedEvents){
            events.remove(event);
        }


        startedEvents.clear();
        completedEvents.clear();

        if(mailBox!=null) {
            if (mailBox instanceof SuspendWithEvent) {
                statut.addingPauseMenu(((SuspendWithEvent) mailBox).getEventCreatesSuspend().getPauseMenuICMonFight());
                //setPauseMenu( ((SuspendWithEvent) mailBox).getEventCreatesSuspend().getPauseMenuICMonFight()); //
                //requestPause();
                //((SuspendWithEvent) mailBox).getEventCreatesSuspend().onComplete(new RequestResumeAction(statut));

            }
            mailBox.process();
            mailBox = null;
            }



        Keyboard keyboard = getCurrentArea().getKeyboard();
        if(keyboard.get(Keyboard.R).isPressed()){
            resetGame(getWindow(),getFileSystem());
        }

    }

    /**
     * ???
     */
    @Override
    public void end() {

    }

    /**
     * ???
     * @return ???
     */
    @Override
    public String getTitle() {
        return "ICMon";
    }


    /**
     * ???
     * @param areaKey ???
     */
    private void initArea(String areaKey) {
        ICMonArea area = (ICMonArea) setCurrentArea(areaKey, true);
        DiscreteCoordinates coords = area.getPlayerSpawnPosition();
        player = new ICMonPlayer(area, Orientation.DOWN, coords,statut);
        player.enterArea(area, coords);
        player.centerCamera();
    }

    public class ICMonGameState{
        public void acceptInteraction(Interactable interactable , boolean isCellInteraction) {
            for (ICMonEvent event : events) {
                interactable.acceptInteraction(event, isCellInteraction);

            }

        }
        //old method
        public void addingPauseMenu(ICMonFight menu){
            setPauseMenu(menu);
            System.out.println("request pause");
            requestPause();

        }
        public void EndPauseMenu(){
            System.out.println("request resume");
            requestResume();
        }



        public void send(GamePlayMessage message) {
            System.out.println("MailBOX before : "+mailBox);
            mailBox= message; //ajoute le message dans boite aux lettres
            System.out.println("MailBOX after : "+mailBox);


        }



        public void switchingArea(String areaName, DiscreteCoordinates PlayerCoordinates){
            player.leaveArea();
            ICMonArea currentArea = (ICMonArea) setCurrentArea(areaName,false);
            player.enterArea(currentArea,PlayerCoordinates);
            player.centerCamera();

        }

        public ICMonManager createManager(){
            return new ICMon.ICMonManager();
        }

        public void suspendMessage(PokemonFightEvent eventBecaus, ICMonManager manager){
            //eventBecaus.start();
            //requestPause();
            eventBecaus.onStart(new SuspendEventAction(manager));
            eventBecaus.onComplete(new ResumeEventAction(manager));
            eventBecaus.onComplete(new RequestResumeAction(statut));
            eventBecaus.start();
        }




    }

    public class ICMonManager {
        public ICMonManager() {
            startedEvents = new ArrayList<>();
            completedEvents = new ArrayList<>();

        }

        public void registerEvent(ICMonEvent event) {
            startedEvents.add(event);
        }

        public void unregisterEvent(ICMonEvent event) {
            completedEvents.add(event);

        }
        public void suspendAllEvents(){
            for (ICMonEvent event : events){
                event.suspend();
            }
        }
        public void resumeAllEvents(){
            for (ICMonEvent event : events){
                event.resume();
            }
        }
    }

}