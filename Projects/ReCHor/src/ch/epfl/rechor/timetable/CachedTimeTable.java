package ch.epfl.rechor.timetable;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Horaire de transport public avec cache interne pour les données dépendant de la date.
 * <p>
 * Cette classe implémente {@link TimeTable} en mémorisant les résultats des méthodes
 * {@code tripsFor(date)} et {@code connectionsFor(date)} pour la dernière date utilisée.
 * Ainsi, si plusieurs appels sont effectués consécutivement avec la même date, les données
 * sont récupérées directement depuis le cache, évitant ainsi un rechargement coûteux.
 *
 * @author Omar Ziyad Azgaoui (379136), Mohamed Amine Goulahsen (400232)
 */
public class CachedTimeTable implements TimeTable {

    private final TimeTable underlyingTimetable;

    private LocalDate cachedDate = null;
    private Trips cachedTrips = null;
    private Connections cachedConnections = null;

    /**
     * Construit un horaire avec cache basé sur l’horaire sous-jacent donné.
     *
     * @param underlyingTimetable l’horaire à partir duquel les données seront chargées
     */
    public CachedTimeTable(TimeTable underlyingTimetable) {
        this.underlyingTimetable = Objects.requireNonNull(underlyingTimetable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Stations stations() {
        return underlyingTimetable.stations();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StationAliases stationAliases() {
        return underlyingTimetable.stationAliases();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Platforms platforms() {
        return underlyingTimetable.platforms();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Routes routes() {
        return underlyingTimetable.routes();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Transfers transfers() {
        return underlyingTimetable.transfers();
    }

    /**
     * Retourne les courses de l’horaire pour un jour donné.
     * Utilise un cache pour éviter les rechargements inutiles.
     *
     * @param date la date pour laquelle on veut les courses
     * @return les courses pour le jour donné
     */
    @Override
    public Trips tripsFor(LocalDate date) {
        cacheUpdate(date);
        return cachedTrips;
    }

    /**
     * Retourne les liaisons de l’horaire pour un jour donné.
     * Utilise un cache pour éviter les rechargements inutiles.
     *
     * @param date la date pour laquelle on veut les liaisons
     * @return les liaisons pour le jour donné
     */
    @Override
    public Connections connectionsFor(LocalDate date) {
        cacheUpdate(date);
        return cachedConnections;
    }

    /**
     * Met à jour le cache si la date donnée est différente de la date actuellement en cache.
     * <p>
     * Cette méthode privée est utilisée pour éviter de recharger les données de l'horaire
     * si elles ont déjà été calculées pour la même date.
     *
     * @param date la date pour laquelle les données doivent être mises à jour dans le cache
     */
    private void cacheUpdate(LocalDate date) {
        if (!date.equals(cachedDate)) {
            cachedTrips = underlyingTimetable.tripsFor(date);
            cachedConnections = underlyingTimetable.connectionsFor(date);
            cachedDate = date;
        }
    }
}