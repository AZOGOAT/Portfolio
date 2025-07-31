package ch.epfl.rechor.gui;

import ch.epfl.rechor.StopIndex;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Popup;
import javafx.util.Subscription;

import java.util.List;

/**
 * Représente la combinaison d’un champ textuel et d’une fenêtre, qui permet de choisir un arrêt de transport public.
 * Le champ textuel permet de saisir une requête et la fenêtre affiche dynamiquement les 30 meilleurs résultats de recherche d’arrêt,
 * triés par pertinence. Le premier résultat est sélectionné initialement. La sélection se fait par la touche tabulation
 * tandis que les touches ↑ et ↓ permettent le "déplacement" dans la barre de recherche.
 * Lorsque le champ textuel devient inactif (perte de focus), le texte est remplacé par le résultat sélectionné s’il y en a un, ou vidé sinon.
 *
 * @param textField le champ textuel, de type {@code TextField}
 * @param stopO une valeur observable contenant le nom de l'arrêt sélectionné, ou une chaîne vide si aucun arrêt ne correspond à la requête
 *
 * @author Omar Ziyad Azgaoui (379136)
 * @author Mohamed Amine Goulahsen (400232)
 */
public record StopField(TextField textField, ObservableValue<String> stopO) {

    private static final double HAUTEUR_LISTE_ARRETS = 240;
    private static final int MAX_RESULTATS_ARRETS = 30;

    /**
     * Crée un champ textuel et une fenêtre permettant de choisir un arrêt de transport public à partir d'un index.
     * La valeur observable retournée contient le nom de l'arrêt sélectionné ou une chaîne vide si aucun n'est sélectionné.
     *
     * @param index l’index des arrêts à utiliser pour rechercher les arrêts
     * @return une instance de {@code StopField} initialisée
     */
    public static StopField create(StopIndex index) {
        TextField textField = new TextField();
        StringProperty selectedStop = new SimpleStringProperty("");

        ListView<String> listView = new ListView<>();
        listView.setFocusTraversable(false);
        listView.setMaxHeight(HAUTEUR_LISTE_ARRETS);

        Popup popup = new Popup();
        popup.setHideOnEscape(false);
        popup.getContent().add(listView);

        //Force la perte de focus + la validation du champ quand on clique en dehors du champ ou du popup
        textField.sceneProperty().subscribe(scene -> {
            if (scene == null) return;

            scene.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_PRESSED, event -> {
                if (!textField.isFocused()) return;

                Node target = event.getPickResult().getIntersectedNode();
                while (target != null) {
                    if (target == textField || target == listView || target.getParent() == listView)
                        return;
                    target = target.getParent();
                }

                Platform.runLater(() -> textField.getParent().requestFocus());
            });
        });

        // Observers dynamiques (créés au gain de focus, supprimés à la perte)
        SimpleObjectProperty<Subscription> textSubscription = new SimpleObjectProperty<>();
        SimpleObjectProperty<Subscription> boundsSubscription = new SimpleObjectProperty<>();

        // ↑ / ↓ navigation dans la liste
        textField.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            switch (event.getCode()) {
                case UP -> moveSelection(listView, false, event);
                case DOWN -> moveSelection(listView, true, event);
            }
        });

        // Gestion de Focus
        textField.focusedProperty().subscribe(focused -> {
            if (focused) {
                // Observer text → update list
                textSubscription.set(textField.textProperty().subscribe(s -> {
                    String query = textField.getText().strip();
                    List<String> results = index.stopsMatching(query, MAX_RESULTATS_ARRETS);
                    listView.setItems(FXCollections.observableArrayList(results));
                    if (!results.isEmpty())
                        listView.getSelectionModel().selectFirst();
                }));

                // Observer bounds → reposition popup dynamiquement
                boundsSubscription.set(textField.boundsInLocalProperty().subscribe(b -> {
                    Bounds bounds = textField.localToScreen(textField.getBoundsInLocal());
                    popup.setAnchorX(bounds.getMinX());
                    popup.setAnchorY(bounds.getMaxY());
                }));

                // Initialiser la popup
                Platform.runLater(() -> {
                    Bounds b = textField.localToScreen(textField.getBoundsInLocal());
                    popup.show(textField, b.getMinX(), b.getMaxY());
                });

            } else {
                popup.hide();
                if (textSubscription.get() != null) textSubscription.get().unsubscribe();
                if (boundsSubscription.get() != null) boundsSubscription.get().unsubscribe();

                String selected = listView.getSelectionModel().getSelectedItem();
                String previous = selectedStop.get();

                if (selected != null && !selected.equals(previous)) {
                    textField.setText(selected);
                    selectedStop.set(selected);
                } else if (selected == null && !previous.isEmpty()) {
                    textField.clear();
                    selectedStop.set("");
                }
            }
        });

        return new StopField(textField, selectedStop);
    }

    /**
     * Met à jour le champ textuel associé avec le nom de l'arrêt donné.
     *
     * @param stopName le nom de l'arrêt à afficher dans le champ textuel
     */
    public void setTo(String stopName) {
        textField.setText(stopName);
        // Mettre aussi à jour directement la valeur observable
        if (stopO instanceof StringProperty sp) {
            sp.set(stopName);
        }
    }

    /**
     * Modifie la sélection courante dans une liste d'arrêts en fonction de la direction donnée.
     * Si la direction est vers le bas, sélectionne l'élément suivant (si possible).
     * Si la direction est vers le haut, sélectionne l'élément précédent (si possible).
     * L'événement est consommé pour empêcher d'autres traitements.
     *
     * @param listView la liste contenant les résultats de recherche des arrêts
     * @param isDirectionDown vrai pour se déplacer vers le bas, faux pour se déplacer vers le haut
     * @param event l'événement de touche déclencheur du déplacement
     */
    private static void moveSelection(ListView<String> listView,
                                      boolean isDirectionDown,
                                      KeyEvent event) {
        int currentIndex = listView.getSelectionModel().getSelectedIndex();
        int maxIndex = listView.getItems().size() - 1;

        if (isDirectionDown && currentIndex < maxIndex) {
            listView.getSelectionModel().select(currentIndex + 1);
            listView.scrollTo(currentIndex + 1);
            event.consume();
        } else if (!isDirectionDown && currentIndex > 0) {
            listView.getSelectionModel().select(currentIndex - 1);
            listView.scrollTo(currentIndex - 1);
            event.consume();
        }
    }
}