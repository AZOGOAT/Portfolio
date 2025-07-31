package ch.epfl.rechor.gui;

import ch.epfl.rechor.journey.Vehicle;
import javafx.scene.image.Image;

import java.util.EnumMap;
import java.util.Map;

/**
 * Classe utilitaire permettant d'obtenir les icônes associées aux différents types de véhicules.
 * <p>
 * Cette classe fournit une méthode statique qui retourne l'icône (au format PNG) correspondant à un type
 * de véhicule donné. Les icônes sont chargées à la demande et mises en cache dans une table pour éviter
 * les rechargements inutiles.
 * </p>
 *
 * @author Omar Ziyad Azgaoui (379136)
 * @author Mohamed Amine Goulahsen (400232)
 */
public class VehicleIcons {
    private static Map<Vehicle, Image> icons = new EnumMap<>(Vehicle.class);

    private VehicleIcons() {}

    /**
     * Retourne l'icône associée au type de véhicule donné.
     * <p>
     * L'icône est chargée à partir d'un fichier nommé selon le nom du véhicule suivi de l'extension
     * « .png », et est ensuite mise en cache pour les appels suivants.
     * </p>
     *
     * @param vehicle le type de véhicule dont on veut obtenir l'icône.
     * @return l'image représentant l'icône du véhicule donné.
     */
    public static Image iconFor(Vehicle vehicle) {
        return icons.computeIfAbsent(vehicle, v -> new Image(String.format("%s.png", vehicle.name())));
    }
}
