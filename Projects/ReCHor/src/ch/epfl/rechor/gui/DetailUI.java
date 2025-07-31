package ch.epfl.rechor.gui;

import ch.epfl.rechor.FormatterFr;
import ch.epfl.rechor.Json;
import ch.epfl.rechor.journey.Journey;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.util.Pair;
import javafx.scene.paint.Color;

import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.io.File;
import java.io.UncheckedIOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import java.time.Duration;

import static ch.epfl.rechor.gui.VehicleIcons.iconFor;
import static ch.epfl.rechor.journey.JourneyGeoJsonConverter.toGeoJson;
import static ch.epfl.rechor.journey.JourneyIcalConverter.toIcalendar;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
/**
 * Représente la vue détaillée d'un voyage sélectionné, contenant l'affichage graphique
 * de ses différentes étapes (à pied ou en transport public).
 * <p>
 * Cette vue affiche, pour chaque étape :
 * <ul>
 *     <li>Les informations de départ et d’arrivée (heure, arrêt, voie/quai),</li>
 *     <li>Une icône représentant le type de transport, le nom de la ligne et la destination,</li>
 *     <li>Les arrêts intermédiaires, s’il y en a, dans un panneau repliable,</li>
 *     <li>Une ligne rouge reliant visuellement les arrêts de départ et d’arrivée,</li>
 *     <li>Deux boutons en bas permettant d’exporter le voyage au format iCalendar ou de le visualiser sur une carte (GeoJSON).</li>
 * </ul>
 *
 * Si aucun voyage n’est sélectionné, un message central indique « Aucun Voyage ».
 *
 * @param rootNode le nœud JavaFX racine de cette vue détaillée.
 *
 * @author Omar Ziyad Azgaoui (379136)
 * @author Mohamed Amine Goulahsen (400232)
 */
public record DetailUI(Node rootNode) {

    private static final double CIRCLE_RADIUS = 3.0;

    /**
     * Crée la vue détaillée correspondant à un voyage observable.
     * Cette vue est automatiquement mise à jour lorsque le voyage sélectionné change.
     *
     * @param journey une valeur observable contenant le voyage actuellement sélectionné.
     * @return une instance de {@code DetailUI} représentant la vue détaillée du voyage.
     */
    public static DetailUI create(ObservableValue<Journey> journey) {
        ScrollPane detailPane = new ScrollPane();
        detailPane.getStylesheets().add("detail.css");
        detailPane.setId("detail");
        StackPane firstStackPane = new StackPane();
        detailPane.setContent(firstStackPane);

        //Initialisation avec currentJourney avant d'ajouter le listener
        Journey currentJourney = journey.getValue();
        if(currentJourney == null) buildEmptyJourneyUI(firstStackPane);
        else buildDetailUI(firstStackPane, currentJourney);
        //Ajout du Listener
        journey.subscribe(( newj) -> {
            firstStackPane.getChildren().clear();
            if(newj == null) {
                buildEmptyJourneyUI(firstStackPane);
            }
            else{
                buildDetailUI(firstStackPane, newj);
            }
        });

        return new DetailUI(detailPane);
    }

    /**
     * Construit l'interface affichée lorsqu'aucun voyage n'est sélectionné.
     * <p>
     * Cette interface consiste en un simple texte centré « Aucun Voyage ».
     *
     * @param stackPane le conteneur dans lequel insérer le message vide.
     */
    private static void buildEmptyJourneyUI(StackPane stackPane) {
        stackPane.getChildren().add(new VBox(new Text("Aucun Voyage")) {{
            setId("no-journey");
        }});
    }

    /**
     * Construit et insère l'interface graphique détaillée représentant toutes les étapes
     * du voyage sélectionné. Cette vue inclut :
     * <ul>
     *     <li>Les informations de chaque étape (à pied ou en transport public),</li>
     *     <li>Un panneau repliable pour les arrêts intermédiaires,</li>
     *     <li>Une ligne rouge entre chaque arrêt de départ et d’arrivée,</li>
     *     <li>Un bouton pour afficher le voyage sur une carte GeoJSON,</li>
     *     <li>Un bouton pour exporter le voyage au format iCalendar.</li>
     * </ul>
     *
     * @param detailPane le conteneur dans lequel la vue détaillée est insérée.
     * @param journey le voyage à afficher.
     */
    private static void buildDetailUI(StackPane detailPane, Journey journey) {
        Json json = toGeoJson(journey);
        String jsonText = json.toString()
                .lines()
                .map(String::trim)
                .filter(s-> !s.isEmpty())
                .collect(Collectors.joining());

        VBox vbox = new VBox();
        StackPane stackPane = new StackPane();
        HBox hbox = new HBox();
        hbox.setId("buttons");
        hbox.getChildren().addAll(
                createMapButton(jsonText),
                createCalendarButton(journey)
        );
        vbox.getChildren().addAll(stackPane, hbox);
        Pane annotations = new Pane();
        annotations.setId("annotations");
        AnnotatedGridPane etapes = new AnnotatedGridPane(annotations);
        etapes.setId("legs");
        int currentRow = 0;
        for(Journey.Leg leg : journey.legs()){
            if(leg instanceof Journey.Leg.Foot foot){
                etapes.add(new Text(FormatterFr.formatLeg(foot)),
                           2, currentRow, 2,1);
                currentRow++;
            }
            else if(leg instanceof Journey.Leg.Transport tr){
                //1ere ligne
                Text deptime = new Text(FormatterFr.formatTime(tr.depTime()));
                deptime.getStyleClass().add("departure");
                GridPane.setHalignment(deptime, HPos.CENTER);
                Circle depCircle = new Circle(CIRCLE_RADIUS);
                Text depStopName = new Text(tr.depStop().name());
                Text depPlatform = new Text(FormatterFr.formatPlatformName(tr.depStop()));
                depPlatform.getStyleClass().add("departure");
                etapes.addRow(currentRow, deptime, depCircle, depStopName, depPlatform);
                currentRow++;

                //2eme ligne
                ImageView icon = new ImageView(iconFor(tr.vehicle()));
                icon.setFitWidth(31);
                icon.setFitHeight(31);
                Text routeDest = new Text(FormatterFr.formatRouteDestination(tr));
                if(tr.intermediateStops().isEmpty()) {
                    etapes.addRow(currentRow, icon, new Text(), routeDest, new Text());
                } else {
                    etapes.add(icon,0,currentRow,1,2);
                    etapes.add(routeDest,2,currentRow,2,1);
                    currentRow++;
                }
                if(!(tr.intermediateStops().isEmpty())){
                    Accordion accordion = new Accordion();
                    GridPane interStops = new GridPane();
                    interStops.getStyleClass().add("intermediates-stops");
                    interStops.setHgap(5);
                    interStops.setVgap(5);
                    int interStopRow = 0;
                    int size = tr.intermediateStops().size();
                    long legMinutes = Duration.between(tr.depTime(), tr.arrTime()).toMinutes();
                    Text accordionLabel = new Text(String.format("%d arrêt%s, %d min",
                            size, size > 1 ? "s" : "", legMinutes));
                    for(Journey.Leg.IntermediateStop stop : tr.intermediateStops()){
                        interStops.add(new Text(FormatterFr.formatTime(stop.arrTime())),
                                0,
                                interStopRow);
                        interStops.add(new Text(FormatterFr.formatTime(stop.depTime())),
                                1,
                                interStopRow);
                        interStops.add(new Text(stop.stop().name()),2,interStopRow);
                        interStopRow++;
                    }
                    TitledPane pane = new TitledPane(accordionLabel.getText(), interStops);

                    accordion.getPanes().add(pane);
                    etapes.add(accordion,2,currentRow,2,1);
                    currentRow++;

                }

                //4eme ligne
                Text arrTime = new Text(FormatterFr.formatTime(tr.arrTime()));
                GridPane.setHalignment(arrTime, HPos.RIGHT);
                Circle arrCircle = new Circle(CIRCLE_RADIUS);
                Text arrStopName = new Text(tr.arrStop().name());
                Text arrPlatform = new Text(FormatterFr.formatPlatformName(tr.arrStop()));
                currentRow++;
                etapes.addRow(currentRow, arrTime, arrCircle, arrStopName, arrPlatform);
                currentRow++;
                etapes.addCirclePair(depCircle, arrCircle);
            }
        }
        stackPane.getChildren().addAll(annotations, etapes);
        detailPane.getChildren().add(vbox);
        annotations.getParent().requestLayout();
    }


    /**
     * Représente une grille annotée pour l'affichage détaillé des étapes d’un voyage.
     * <p>
     * Cette grille permet de visualiser graphiquement les connexions entre les arrêts
     * de départ et d'arrivée de chaque étape en transport public, au moyen de cercles
     * reliés par une ligne rouge.
     * <p>
     * Les lignes de connexion sont recalculées automatiquement à chaque mise à jour
     * de la disposition des nœuds.
     */
    private static class AnnotatedGridPane extends GridPane {
        private final Pane annotations;
        private final List<Pair<Circle,Circle>> circlePairs = new ArrayList<>();

        /**
         * Construit une grille annotée avec le panneau graphique dans lequel seront
         * dessinées les lignes reliant les cercles.
         *
         * @param annotations le panneau dans lequel les lignes de connexion seront tracées.
         */
        AnnotatedGridPane(Pane annotations) {
            this.annotations = annotations;
        }

        /**
         * Redéfinit la disposition des enfants pour recalculer dynamiquement
         * la position des lignes reliant les cercles de départ et d’arrivée.
         * Les lignes sont tracées en rouge et superposées à la grille.
         */
        @Override
        public void layoutChildren(){
            super.layoutChildren();
            annotations.getChildren().clear();
            for(Pair<Circle,Circle> pair : circlePairs){
                Circle c1 = pair.getKey();
                Circle c2 = pair.getValue();
                Bounds b1 = c1.getBoundsInParent();
                Bounds b2 = c2.getBoundsInParent();
                double x1 = b1.getMinX() + b1.getWidth() / 2;
                double y1 = b1.getMinY() + b1.getHeight() / 2;
                double x2 = b2.getMinX() + b2.getWidth() / 2;
                double y2 = b2.getMinY() + b2.getHeight() / 2;
                Line line = new Line(x1, y1, x2, y2);
                line.setStroke(Color.RED);
                line.setStrokeWidth(2);
                annotations.getChildren().add(line);
            }
        }

        /**
         * Ajoute une paire de cercles représentant un départ et une arrivée d'étape
         * à relier par une ligne dans l'affichage graphique.
         *
         * @param c1 le cercle de départ.
         * @param c2 le cercle d’arrivée.
         */
        public void addCirclePair(Circle c1, Circle c2){
            circlePairs.add(new Pair<>(c1, c2));
        }
    }

    /**
     * Crée un bouton « Carte » permettant d’afficher le tracé du voyage dans un navigateur Web,
     * en utilisant une URL contenant un document GeoJSON vers le site uMap.
     * <p>
     * Le document GeoJSON est passé dans l’URL via un paramètre `data=`.
     *
     * @param jsonText le texte GeoJSON représentant le tracé du voyage.
     * @return le bouton configuré pour ouvrir la carte.
     */
    private static Button createMapButton(String jsonText) {
        return new Button("Carte") {{
            setOnAction(e -> {
                try {
                    URI uri = new URI("https",
                            "umap.osm.ch",
                            "/fr/map",
                            "data=" + jsonText,
                            null);
                    Desktop.getDesktop().browse(uri);
                } catch (IOException ex) {
                    throw new UncheckedIOException(ex);
                } catch (URISyntaxException ex) {
                    throw new RuntimeException(ex);
                }
            });
        }};
    }

    /**
     * Crée un bouton « Calendrier » permettant d’exporter le voyage affiché
     * au format iCalendar (.ics).
     * <p>
     * Lors d’un clic, une boîte de dialogue permet de choisir le fichier de destination.
     * Le fichier généré contient l’événement iCalendar correspondant au voyage.
     *
     * @param journey le voyage à exporter au format iCalendar.
     * @return le bouton configuré pour l’exportation iCalendar.
     */
    private static Button createCalendarButton(Journey journey) {
        return new Button("Calendrier") {{
            setOnAction(e -> {
                String Icalendar = toIcalendar(journey);
                FileChooser chooser = new FileChooser();
                String dateStr = journey.depTime().toLocalDate().format(ISO_LOCAL_DATE);
                chooser.setInitialFileName("voyage_" + dateStr + ".ics");
                File file = chooser.showSaveDialog(((Button) (e.getSource())).getScene().getWindow());
                if (file != null) {
                    try {
                        Files.writeString(file.toPath(), Icalendar);
                    } catch (IOException ex) {
                        throw new UncheckedIOException(ex);
                    }
                }
            });
        }};
    }
}