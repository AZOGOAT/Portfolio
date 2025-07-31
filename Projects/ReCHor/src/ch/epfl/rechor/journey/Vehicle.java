package ch.epfl.rechor.journey;

import java.util.List;

/**
 * Représente les différents types de véhicules ou de transports publics en Suisse.
 *
 * <p>Les types de véhicules disponibles sont :
 * <ul>
 *   <li>{@link #TRAM} - Tramway</li>
 *   <li>{@link #METRO} - Métro</li>
 *   <li>{@link #TRAIN} - Train</li>
 *   <li>{@link #BUS} - Bus ou Car</li>
 *   <li>{@link #FERRY} - Bac ou autre type de bateau</li>
 *   <li>{@link #AERIAL_LIFT} - Télécabine ou autre type de transport aérien à câble</li>
 *   <li>{@link #FUNICULAR} - Funiculaire</li>
 * </ul>
 * </p>
 *
 * @author Omar Ziyad Azgaoui (379136)
 * @author Mohamed Amine Goulahsen (400232)
 */
public enum Vehicle {
    TRAM,
    METRO,
    TRAIN,
    BUS,
    FERRY,
    AERIAL_LIFT,
    FUNICULAR;
    /**
     * Liste immuable contenant toutes les instances de {@link Vehicle}.
     * Elle permet d'accéder facilement à tous les types de véhicules disponibles.
     */
    public static final List<Vehicle> ALL = List.of(Vehicle.values());
}