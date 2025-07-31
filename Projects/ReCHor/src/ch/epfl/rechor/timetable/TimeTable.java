package ch.epfl.rechor.timetable;

import java.time.LocalDate;

/**
 * Représente un horaire de transport public.
 * <p>
 * Cette interface fournit l'accès aux différentes données de l'horaire,
 * notamment les gares, les quais, les lignes, les courses et les connexions.
 *
 * @author Omar Ziyad Azgaoui (379136)
 * @author Mohamed Amine Goulahsen (400232)
 */
public interface TimeTable {

    /**
     * Retourne les gares indexées de l'horaire.
     *
     * @return les gares indexées de l'horaire.
     */
    Stations stations();

    /**
     * Retourne les noms alternatifs indexés des gares de l'horaire.
     *
     * @return les noms alternatifs indexés des gares de l'horaire.
     */
    StationAliases stationAliases();

    /**
     * Retourne les voies/quais indexés de l'horaire.
     *
     * @return les voies/quais indexés de l'horaire.
     */
    Platforms platforms();

    /**
     * Retourne les lignes indexées de l'horaire.
     *
     * @return les lignes indexées de l'horaire.
     */
    Routes routes();

    /**
     * Retourne les changements indexés de l'horaire.
     *
     * @return les changements indexés de l'horaire.
     */
    Transfers transfers();

    /**
     * Retourne les courses indexées de l'horaire actives le jour donné.
     *
     * @param date le jour pour lequel les courses sont demandées.
     * @return les courses indexées de l'horaire actives le jour donné.
     */
    Trips tripsFor(LocalDate date);

    /**
     * Retourne les liaisons indexées de l'horaire actives le jour donné.
     *
     * @param date le jour pour lequel les liaisons sont demandées.
     * @return les liaisons indexées de l'horaire actives le jour donné.
     */
    Connections connectionsFor(LocalDate date);

    /**
     * Vérifie si l'index d'arrêt donné est un index de gare.
     *
     * @param stopId l'index de l'arrêt à vérifier.
     * @return {@code true} si l'index est celui d'une gare, {@code false} sinon.
     */
    default boolean isStationId(int stopId) {
        return stopId < stations().size();
    }

    /**
     * Vérifie si l'index d'arrêt donné est un index de voie ou de quai.
     *
     * @param stopId l'index de l'arrêt à vérifier.
     * @return {@code true} si l'index est celui d'une voie ou d'un quai, {@code false} sinon.
     */
    default boolean isPlatformId(int stopId) {
        return stopId >= stations().size();
    }

    /**
     * Retourne l'index de la gare associée à l'arrêt d'index donné.
     *
     * @param stopId l'index de l'arrêt.
     * @return l'index de la gare associée à l'arrêt donné.
     */
    default int stationId(int stopId) {
        return isStationId(stopId) ? stopId : platforms().stationId(stopId - stations().size());
    }

    /**
     * Retourne le nom de voie ou de quai de l'arrêt d'index donné, ou {@code null} si cet arrêt est une gare.
     *
     * @param stopId l'index de l'arrêt.
     * @return le nom de la voie ou du quai si applicable, sinon {@code null}.
     */
    default String platformName(int stopId) {
        return isPlatformId(stopId) ? platforms().name(stopId - stations().size()) : null;
    }
}