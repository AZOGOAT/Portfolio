package ch.epfl.rechor.gui;

import ch.epfl.rechor.StopIndex;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.converter.LocalTimeStringConverter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Partie de l'interface graphique permettant à l'utilisateur de paramétrer une requête de voyage.
 * <p>
 * Cette interface permet de spécifier :
 * <ul>
 *     <li>le nom de l'arrêt de départ ;</li>
 *     <li>le nom de l'arrêt d'arrivée ;</li>
 *     <li>la date de départ souhaitée ;</li>
 *     <li>l'heure de départ souhaitée.</li>
 * </ul>
 * Les champs textuels affichent une invite en grisé lorsqu’ils sont vides. Un bouton permet d’échanger
 * les arrêts de départ et d’arrivée. La date et l’heure sont initialisées à la date et l’heure actuelles.
 * <p>
 * Le champ de l'heure permet la saisie au format « HH:mm » ou « H:mm ».
 *
 * @param rootNode nœud JavaFX racine représentant l’interface de la requête.
 * @param depStopO nom observable de l’arrêt de départ.
 * @param arrStopO nom observable de l’arrêt d’arrivée.
 * @param dateO    date observable du voyage.
 * @param timeO    heure observable de départ du voyage.
 *
 * @see StopField
 * @see StopIndex
 * @author Omar Ziyad Azgaoui (379136)
 * @author Mohamed Amine Goulahsen (400232)
 */
public record QueryUI(Node rootNode,
                      ObservableValue<String> depStopO,
                      ObservableValue<String> arrStopO,
                      ObservableValue<LocalDate> dateO,
                      ObservableValue<LocalTime> timeO) {

    private static final DateTimeFormatter FORMAT_AFFICHAGE_HEURE = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter FORMAT_SAISIE_HEURE =  DateTimeFormatter.ofPattern("[H:mm]");
    private static final String NARROW_NBSP = "\u202f"; // NO_BREAK_SPACE

    /**
     * Crée une interface utilisateur permettant de saisir les paramètres d'une requête de voyage.
     * <p>
     * Cette interface comprend deux champs textuels pour les arrêts, un bouton d’inversion entre eux,
     * ainsi qu’un sélecteur de date et un champ de saisie d’heure. Les champs sont disposés en deux lignes
     * et intégrés dans un conteneur vertical stylisé.
     *
     * @param index index des arrêts permettant de vérifier la validité des noms saisis.
     * @return une instance {@code QueryUI} représentant cette interface.
     */
    public static QueryUI create(StopIndex index) {

        // Champs d'arrêts
        StopField depField = StopField.create(index);
        depField.textField().setPromptText("Nom de l'arrêt de départ");
        depField.textField().setId("depStop");

        StopField arrField = StopField.create(index);
        arrField.textField().setPromptText("Nom de l'arrêt d'arrivée");

        // Bouton d'inversion
        Button swapButton = new Button("↔");
        swapButton.setOnAction(e -> {
            String dep = depField.textField().getText();
            String arr = arrField.textField().getText();
            depField.setTo(arr);
            arrField.setTo(dep);
        });

        // Ligne 1 : départ ↔ arrivée
        HBox stopRow = new HBox(
                new Label("Départ" + NARROW_NBSP + ":"), depField.textField(),
                swapButton,
                new Label("Arrivée" + NARROW_NBSP + ":"), arrField.textField()
        );

        // Champs date/heure
        DatePicker datePicker = new DatePicker(LocalDate.now());
        datePicker.setId("date");
        ObjectProperty<LocalDate> dateProperty = datePicker.valueProperty();

        TextField timeField = new TextField();
        timeField.setId("time");
        LocalTimeStringConverter converter = new LocalTimeStringConverter(FORMAT_AFFICHAGE_HEURE,
                                                                          FORMAT_SAISIE_HEURE);
        TextFormatter<LocalTime> timeFormatter = new TextFormatter<>(converter, LocalTime.now());
        timeField.setTextFormatter(timeFormatter);
        ReadOnlyObjectProperty<LocalTime> timeProperty = timeFormatter.valueProperty();

        // Ligne 2 : date + heure
        HBox dateRow = new HBox(
                new Label("Date" + NARROW_NBSP + ":"), datePicker,
                new Label("Heure" + NARROW_NBSP + ":"), timeField
        );

        // Conteneur principal vertical
        VBox root = new VBox(stopRow, dateRow);
        root.getStylesheets().add("query.css");

        return new QueryUI(root, depField.stopO(), arrField.stopO(), dateProperty, timeProperty);
    }
}