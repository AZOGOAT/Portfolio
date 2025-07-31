package ch.epfl.rechor.gui;

import ch.epfl.rechor.FormatterFr;
import ch.epfl.rechor.journey.Journey;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static ch.epfl.rechor.gui.VehicleIcons.iconFor;
import static java.time.Duration.between;

/**
 * Représente la vue d'ensemble des voyages optimaux correspondant à une requête donnée.
 * Cette vue affiche chaque voyage sous forme résumée, comprenant l'icône du type de véhicule,
 * la ligne et sa destination, les heures de départ et d'arrivée, une ligne avec les changements
 * et la durée totale du voyage. Les voyages sont affichés dans une liste triée.
 *
 * @param rootNode le nœud JavaFX racine du graphe de scène représentant la vue.
 * @param selectedJourneyO la valeur observable contenant le voyage actuellement sélectionné.
 *
 * @author Omar Ziyad Azgaoui (379136)
 * @author Mohamed Amine Goulahsen (400232)
 */
public record SummaryUI(Node rootNode, ObservableValue<Journey> selectedJourneyO) {

    /**
     * Crée une vue d'ensemble des voyages à partir d'une liste observable de voyages à afficher
     * et d'une heure de voyage désirée.
     * Lorsque l'heure de voyage désirée change, le premier voyage partant à cette heure-là ou plus tard
     * est sélectionné. S'il n'y en a aucun, alors le dernier voyage de la liste est sélectionné.
     *
     * @param displayJourneys0 la valeur observable contenant la liste des voyages à afficher.
     * @param journeyTime la valeur observable contenant l'heure de voyage désirée.
     * @return une instance de SummaryUI contenant le graphe de scène et la sélection observable.
     */
    public static SummaryUI create(ObservableValue<List<Journey>> displayJourneys0,
                                   ObservableValue<LocalTime> journeyTime) {
        ListView<Journey> summaryList = new ListView<>(); // Liste des voyages
        summaryList.setCellFactory(lv -> new MyListCell());
        summaryList.getStylesheets().add("summary.css");

        summaryList.getItems().setAll(displayJourneys0.getValue());
        selectClosest(summaryList, journeyTime.getValue());

        displayJourneys0.subscribe((newList) -> {
            summaryList.getItems().setAll(newList);
            selectClosest(summaryList, journeyTime.getValue());
        });

        journeyTime.subscribe((newTime) ->
                                        selectClosest(summaryList, newTime));


        // Retourne une instance de SummaryUI avec le nœud racine et la sélection observable
        return new SummaryUI(summaryList, summaryList.getSelectionModel().selectedItemProperty());
    }

    /**
     * Sélectionne dans la liste le premier voyage dont l'heure de départ est égale ou postérieure
     * à l'heure spécifiée. Si aucun tel voyage n'existe, sélectionne le dernier voyage de la liste.
     *
     * @param listView la liste des voyages affichée dans l'interface.
     * @param time l'heure de voyage désirée.
     */
    private static void selectClosest(ListView<Journey> listView, LocalTime time) {
        List<Journey> journeys = listView.getItems();
        if (journeys.isEmpty()) {
            return;
        }
        Journey found = journeys.stream()
                .filter(j -> !j.depTime().toLocalTime().isBefore(time))
                .findFirst()
                .orElse(journeys.getLast());
        int index = journeys.indexOf(found);
        listView.getSelectionModel().select(index);
        Platform.runLater(() -> listView.scrollTo(index));
    }

    /**
     * Représente une cellule personnalisée de la vue d'ensemble des voyages,
     * affichant de manière graphique les informations principales de chaque voyage.
     * Chaque cellule montre :
     * <ul>
     *     <li>une icône du type de véhicule,</li>
     *     <li>le nom de la ligne et la destination finale,</li>
     *     <li>l'heure de départ,</li>
     *     <li>une ligne avec des cercles noirs aux extrémités (départ et arrivée),</li>
     *     <li>des cercles blancs pour les changements entre étapes en véhicule,</li>
     *     <li>l'heure d'arrivée,</li>
     *     <li>et la durée du voyage.</li>
     * </ul>
     * Ces éléments sont disposés dans un panneau JavaFX de type {@code BorderPane}.
     * La ligne graphique est automatiquement redessinée à chaque mise à jour.
     *
     * @author Omar Ziyad Azgaoui (379136)
     * @author Mohamed Amine Goulahsen (400232)
     */
    private static final class MyListCell extends ListCell<Journey> {
        private static final double CIRCLE_RADIUS = 3;
        private final BorderPane root = new BorderPane();      // Conteneur principal de la cellule
        private final ImageView icon = new ImageView();        // Icône du véhicule
        private final Text routeText = new Text();             // Nom de la ligne + direction

        private final Text depTime = new Text();               // Heure de départ
        private final Text arrTime = new Text();               // Heure d’arrivée
        private final Text durText = new Text();               // Durée du trajet

        private final Pane changeLine;                         // Zone personnalisée pour la ligne graphique
        private final Line changeLinePath = new Line();        // Trait horizontal central
        private final Group changeLineDots = new Group();      // Disques des changements

        MyListCell() {
            icon.setFitWidth(20);
            icon.setFitHeight(20);
            icon.setPreserveRatio(true);                         // Ajuste l’icône en gardant son ratio

            // Barre du haut avec icône + texte
            HBox route = new HBox(5);
            route.getStyleClass().add("route");                  // Applique le style de la barre du haut
            route.getChildren().setAll(icon, routeText);         // Ajoute l’icône + texte à la HBox

            depTime.getStyleClass().add("departure");            // Style de l'heure de départ

            HBox durationBox = new HBox(durText);                // Conteneur de la durée
            durationBox.getStyleClass().add("duration");         // Style de la durée


            changeLine = new Pane() {
                /**
                 * Dispose et positionne graphiquement la ligne horizontale représentant le voyage,
                 * ainsi que les cercles noirs de départ et d’arrivée, et les cercles blancs de transfert.
                 * Les éléments sont centrés verticalement dans le panneau, avec des marges latérales fixes.
                 * Chaque disque de transfert est placé proportionnellement à son heure de départ
                 * par rapport à la durée totale du voyage.
                 */
                @Override
                protected void layoutChildren() {
                    double width = getWidth();                 // Largeur totale du panneau
                    double height = getHeight();               // Hauteur totale du panneau
                    double startX = 5;                         // Marge à gauche
                    double endX = width - 5;                   // Marge à droite
                    double centerY = height / 2;               // Hauteur centrale pour tous les éléments

                    changeLinePath.setStartX(startX);
                    changeLinePath.setEndX(endX);
                    changeLinePath.setStartY(centerY);
                    changeLinePath.setEndY(centerY);           // Trace la ligne horizontale


                    double length = endX - startX;             // Longueur disponible pour positionner les changements
                    for (Node node : changeLineDots.getChildren()) {
                        if (!node.isVisible()) continue;
                        Circle c = (Circle) node;
                        double frac = (Double) c.getUserData();  // Position relative [0, 1]
                        c.setCenterX(startX + frac * length);    // Position proportionnelle sur la ligne
                        c.setCenterY(centerY);
                    }
                }
            };

            changeLine.setPrefSize(0, 0);  // Laisse JavaFX décider la taille automatiquement
            changeLine.getChildren().addAll(changeLinePath, changeLineDots);

            root.getStyleClass().add("journey");              // Applique le style global à la cellule
            root.setTop(route);
            root.setLeft(depTime);
            root.setCenter(changeLine);
            root.setRight(arrTime);
            root.setBottom(durationBox);                      // Assemble tous les composants dans le BorderPane
        }

        /**
         * Met à jour le contenu graphique de la cellule avec les données du voyage donné.
         * Cette méthode formate les heures de départ et d'arrivée, la durée du trajet,
         * sélectionne la première étape en véhicule et met à jour les icônes et textes associés.
         * Elle met également à jour les disques représentant les changements
         * et déclenche la mise en page graphique de la ligne.
         *
         * @param journey le voyage à afficher dans cette cellule.
         * @param empty true si la cellule est vide, false sinon.
         */
        @Override
        protected void updateItem(Journey journey, boolean empty) {
            super.updateItem(journey, empty);
            if (empty || journey == null) {
                setGraphic(null);                                // Cellule vide → pas de graphique
                return;
            }

            depTime.setText(FormatterFr.formatTime(journey.depTime()));     // Formate heure départ
            arrTime.setText(FormatterFr.formatTime(journey.arrTime()));     // Formate heure arrivée
            durText.setText(FormatterFr.formatDuration(journey.duration())); // Formate durée

            Journey.Leg firstLeg = journey.legs().getFirst();                // Première étape
            if (firstLeg instanceof Journey.Leg.Foot) {
                firstLeg = journey.legs().get(1);                            // Si à pied → prend le 2e
            }
            Journey.Leg.Transport transportLeg = (Journey.Leg.Transport) firstLeg;

            icon.setImage(iconFor(transportLeg.vehicle()));                 // Icone véhicule
            routeText.setText(transportLeg.route() + " Direction " + transportLeg.destination());

            updateTransferDots(journey);                                    // Met à jour les cercles blancs
            setGraphic(root);                                               // Affiche le contenu
            changeLine.requestLayout();                                     // Redessine la ligne
        }

        /**
         * Met à jour les disques représentant les changements dans le voyage.
         * Seules les étapes à pied de transfert sont représentées par des disques blancs,
         * placés proportionnellement sur la ligne selon l'heure de départ de l'étape.
         *
         * @param journey le voyage dont les changements doivent être représentés.
         */
        private void updateTransferDots(Journey journey) {
            long totalMin = journey.duration().toMinutes();

            // 1) On vide tout
            changeLineDots.getChildren().clear();

            // Cercles de départ et d’arrivée
            Circle[] endpoints = {
                    new Circle(CIRCLE_RADIUS),
                    new Circle(CIRCLE_RADIUS)
            };
            for (int i = 0; i < endpoints.length; i++) {
                Circle c = endpoints[i];
                c.getStyleClass().add("dep-arr");
                c.setUserData((double)i);
                c.setViewOrder(-1);
                changeLineDots.getChildren().add(c);
            }

            // 3) Cercles de transfert
            List<LocalDateTime> depTimes = journey.legs().stream()
                    .limit(journey.legs().size() - 1) // ignore le dernier leg
                    .filter(l -> l instanceof Journey.Leg.Foot) // garde seulement les Foot
                    .map(Journey.Leg::depTime) // récupère le depTime
                    .toList();

            for (LocalDateTime t : depTimes) {
                long mins = between(journey.depTime(), t).toMinutes();
                double frac = (double) mins / totalMin;
                Circle c = new Circle(CIRCLE_RADIUS);
                c.setUserData(frac);
                if (t.equals(journey.depTime()) || t.equals(journey.arrTime())) {
                    continue;
                } else {
                    c.getStyleClass().add("transfer");
                }
                changeLineDots.getChildren().add(c);
            }
        }
    }
}