package ch.epfl.rechor.gui;

import ch.epfl.rechor.StopIndex;
import ch.epfl.rechor.journey.*;
import ch.epfl.rechor.timetable.*;
import ch.epfl.rechor.timetable.mapped.FileTimeTable;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Classe principale du programme ReCHor.
 * <p>
 * Combine les différentes parties de l’interface graphique développées dans les étapes précédentes.
 * Permet à un utilisateur d’entrer les paramètres d’un voyage (arrêt de départ, arrêt d’arrivée,
 * date et heure) et d’afficher les résultats correspondants dans deux vues synchronisées : la vue d’ensemble
 * des voyages disponibles et la vue détaillée du voyage sélectionné.
 *
 * @author Omar Ziyad Azgaoui (379136)
 * @author Mohamed Amine Goulahsen (400232)
 */
public final class Main extends Application {

    private static final int LARGEUR_MINIMALE = 800;
    private static final int HAUTEUR_MINIMALE = 600;

    @SuppressWarnings("FieldCanBeLocal")
    private ObjectBinding<List<Journey>> journeysO;
    private ObservableValue<Profile> profileO;

    /**
     * Point d’entrée du programme.
     *
     * @param args les arguments passés au programme (ignorés).
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Méthode appelée au lancement de l'application JavaFX.
     * <p>
     * Installe l'horaire, initialise les composants de l'interface graphique (requête de recherche,
     * vue d'ensemble des voyages, vue détaillée du voyage) et relie dynamiquement les paramètres de requête
     * aux résultats affichés.
     *
     * @param primaryStage la scène principale.
     * @throws Exception en cas d’erreur lors de l’initialisation.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Charge l'horaire en le mappant via FileTimeTable et en le mettant en cache
        TimeTable timeTable = new CachedTimeTable(FileTimeTable.in(Path.of("timetable")));
        Stations stations = timeTable.stations();
        StationAliases aliases = timeTable.stationAliases();

        // Construit l'index des arrêts (incluant alias)
        StopIndex stopIndex = buildStopIndex(stations, aliases);

        // Prépare la structure graphique principale
        BorderPane root = new BorderPane();
        SplitPane splitPane = new SplitPane();

        // Crée la vue de requête (recherche départ, arrivée, date, heure)
        QueryUI queryUI = QueryUI.create(stopIndex);
        root.setTop(queryUI.rootNode());

        // Crée dynamiquement le profil (ensemble de voyages optimaux) en fonction de la date et de l'arrêt d’arrivée
        profileO = Bindings.createObjectBinding(() -> {
            String name = queryUI.arrStopO().getValue();
            if (name == null || name.isBlank()) return null;
            return new Router(timeTable).profile(queryUI.dateO().getValue(),
                                                 stationId(timeTable.stations(), name));
        }, queryUI.dateO(), queryUI.arrStopO());

        // Extrait dynamiquement les voyages à partir du profil et de l'arrêt de départ
        journeysO = Bindings.createObjectBinding(() -> {
            Profile p = profileO.getValue();
            String name = queryUI.depStopO().getValue();
            if (p == null || name == null || name.isBlank()) return List.of();
            return JourneyExtractor.journeys(p, stationId(timeTable.stations(), name));
        }, profileO, queryUI.depStopO());

        // Crée la vue d’ensemble (liste de voyages) et la vue détaillée (détail du voyage sélectionné)
        SummaryUI summaryUI = SummaryUI.create(journeysO, queryUI.timeO());
        DetailUI detailUI = DetailUI.create(summaryUI.selectedJourneyO());

        // Place les deux vues côte à côte dans un SplitPane
        splitPane.getItems().addAll(summaryUI.rootNode(), detailUI.rootNode());
        root.setCenter(splitPane);

        // Crée la scène et configure la fenêtre principale
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("ReCHor");
        primaryStage.setMinWidth(LARGEUR_MINIMALE);
        primaryStage.setMinHeight(HAUTEUR_MINIMALE);
        primaryStage.show();

        // Donne le focus initial au champ de l'arrêt de départ
        Platform.runLater(() -> scene.lookup("#depStop").requestFocus());
    }

    /**
     * Construit un index des arrêts à partir de la liste des gares et de leurs alias.
     *
     * @param stations les gares de l'horaire.
     * @param aliases les alias des gares.
     * @return l’index des arrêts.
     */
    private static StopIndex buildStopIndex(Stations stations, StationAliases aliases) {
        List<String> stopNames = new ArrayList<>();
        for (int i = 0; i < stations.size(); i++) {
            stopNames.add(stations.name(i));
        }

        Map<String, String> aliasMap = new LinkedHashMap<>();
        for (int i = 0; i < aliases.size(); i++) {
            aliasMap.put(aliases.alias(i), aliases.stationName(i));
        }

        return new StopIndex(stopNames, aliasMap);
    }

    /**
     * Retourne l’identifiant (index) de la station portant un nom donné.
     *
     * @param stations les stations.
     * @param name le nom de la station.
     * @return l’identifiant correspondant.
     * @throws IllegalArgumentException si aucune station ne correspond.
     */
    private static int stationId(Stations stations, String name) {
        for (int i = 0; i < stations.size(); i++) {
            if (stations.name(i).equals(name))
                return i;
        }
        throw new IllegalArgumentException("Station inconnue : " + name);
    }
}