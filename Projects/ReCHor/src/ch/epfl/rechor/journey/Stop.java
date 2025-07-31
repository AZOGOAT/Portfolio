package ch.epfl.rechor.journey;

import java.util.Objects;

import static ch.epfl.rechor.Preconditions.checkArgument;

/**
 * Représente un arrêt dans un trajet avec un nom, un nom de quai, une longitude et une latitude.
 *
 * <p>Cette classe est immuable et garantit que les coordonnées géographiques
 * respectent les bornes valides pour la longitude et la latitude.</p>
 *
 * @param name         Le nom de l'arrêt ou de la gare à laquelle appartient l'arrêt si c'est une voie ou un quai. Ne peut pas être {@code null}.
 * @param platformName Le nom de la voie ou du quai associé à cet arrêt.
 * @param longitude    La longitude de la position de l'arrêt, comprise entre -180.0 et 180.0 degrés.
 * @param latitude     La latitude de la position de l'arrêt, comprise entre -90.0 et 90.0 degrés.
 * @author Omar Ziyad Azgaoui (379136)
 * @author Mohamed Amine Goulahsen (400232)
 */
public record Stop(String name, String platformName, double longitude, double latitude) {
    // Définition des constantes pour les bornes valides de la latitude et longitude
    private static final int LIMIT_LATITUDE = 90;
    private static final int LIMIT_LONGITUDE = 180;

    /**
     * Construit une instance de {@code Stop} en validant les arguments fournis.
     *
     * @throws NullPointerException     si {@code name} est {@code null}.
     * @throws IllegalArgumentException si {@code longitude} ou {@code latitude} sont hors des bornes valides.
     */
    public Stop {
        Objects.requireNonNull(name);
        checkArgument(longitude >= -LIMIT_LONGITUDE &&
                      longitude <= LIMIT_LONGITUDE &&
                      latitude >= -LIMIT_LATITUDE &&
                      latitude <= LIMIT_LATITUDE);
    }
}