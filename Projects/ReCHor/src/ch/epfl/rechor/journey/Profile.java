package ch.epfl.rechor.journey;

import ch.epfl.rechor.timetable.Connections;
import ch.epfl.rechor.timetable.TimeTable;
import ch.epfl.rechor.timetable.Trips;

import java.time.LocalDate;
import java.util.List;

/**
 * Représente un profil contenant la frontière de Pareto pour toutes les gares du réseau
 * permettant d’atteindre une gare de destination donnée à une date donnée.
 * <p>
 * Un profil contient pour chaque gare la frontière de Pareto des voyages optimaux vers la gare
 * d'arrivée. Ces frontières permettent de déterminer les horaires optimaux (en termes
 * d'heure de départ, d'heure d'arrivée et de nombre de changements) à partir de chaque gare.
 *
 * @param timeTable    l’horaire utilisé pour calculer le profil
 * @param date         la date des voyages représentés dans le profil
 * @param arrStationId l’identifiant de la gare de destination
 * @param stationFront la liste des frontières de Pareto pour chaque gare
 * @author Omar Ziyad Azgaoui (379136)
 * @author Mohamed Amine Goulahsen (400232)
 */
public record Profile(TimeTable timeTable, LocalDate date, int arrStationId,
                      List<ParetoFront> stationFront) {

    public Profile {
        stationFront = List.copyOf(stationFront);
    }

    /**
     * Retourne l'ensemble des connexions de l'horaire à la date du profil.
     *
     * @return les connexions disponibles à la date spécifiée dans le profil
     */
    public Connections connections() {
        return timeTable.connectionsFor(date);
    }

    /**
     * Retourne l'ensemble des courses de l'horaire à la date du profil.
     *
     * @return les courses disponibles à la date spécifiée dans le profil
     */
    public Trips trips() {
        return timeTable.tripsFor(date);
    }

    /**
     * Retourne la frontière de Pareto associée à une gare donnée.
     *
     * @param stationId l’identifiant de la gare
     * @return la frontière de Pareto associée à la gare
     * @throws IndexOutOfBoundsException si l’identifiant de la gare est invalide
     */
    public ParetoFront forStation(int stationId) {
        return stationFront.get(stationId);
    }

    /**
     * Constructeur de profils, permettant de construire progressivement la frontière
     * de Pareto de chaque gare et de chaque course.
     *
     * @author Omar Ziyad Azgaoui (379136)
     * @author Mohamed Amine Goulahsen (400232)
     */
    public static final class Builder {
        private final TimeTable timeTable;
        private final LocalDate date;
        private final int arrStationId;
        private final int stationSize;
        private final ParetoFront.Builder[] stationFrontBuilder;
        private final ParetoFront.Builder[] tripsFront;

        /**
         * Crée un nouveau constructeur de profil pour une date et une gare de destination données.
         *
         * @param timeTable    l’horaire utilisé pour construire le profil
         * @param date         la date du profil
         * @param arrStationId l’identifiant de la gare de destination
         */
        public Builder(TimeTable timeTable, LocalDate date, int arrStationId) {
            this.timeTable = timeTable;
            this.date = date;
            this.arrStationId = arrStationId;
            this.stationSize = timeTable.stations()
                                        .size();
            int tripsSize = timeTable.tripsFor(date)
                                     .size();
            this.stationFrontBuilder = new ParetoFront.Builder[stationSize];
            this.tripsFront = new ParetoFront.Builder[tripsSize];
        }

        /**
         * Retourne le constructeur de frontière de Pareto pour une gare donnée.
         *
         * @param stationId l’identifiant de la gare
         * @return le constructeur de la frontière de Pareto de la gare
         * @throws IndexOutOfBoundsException si l’identifiant est invalide
         */
        public ParetoFront.Builder forStation(int stationId) {
            return stationFrontBuilder[stationId];
        }

        /**
         * Remplace la frontière de Pareto d'une gare par un constructeur donné.
         *
         * @param stationId l’identifiant de la gare
         * @param builder   le constructeur de la nouvelle frontière de Pareto
         * @throws IndexOutOfBoundsException si l’identifiant est invalide
         */
        public void setForStation(int stationId, ParetoFront.Builder builder) {
            stationFrontBuilder[stationId] = builder;
        }

        /**
         * Retourne le constructeur de frontière de Pareto pour une course donnée.
         *
         * @param tripId l’identifiant de la course
         * @return le constructeur de la frontière de Pareto de la course
         * @throws IndexOutOfBoundsException si l’identifiant est invalide
         */
        public ParetoFront.Builder forTrip(int tripId) {
            return tripsFront[tripId];
        }

        /**
         * Remplace la frontière de Pareto d'une course par un constructeur donné.
         *
         * @param tripId  l’identifiant de la course
         * @param builder le constructeur de la nouvelle frontière de Pareto
         * @throws IndexOutOfBoundsException si l’identifiant est invalide
         */
        public void setForTrip(int tripId, ParetoFront.Builder builder) {
            tripsFront[tripId] = builder;
        }

        /**
         * Construit et retourne un profil final contenant les frontières de Pareto de toutes les gares.
         * Les gares sans frontière spécifiée auront une frontière vide ({@link ParetoFront#EMPTY}).
         *
         * @return le profil construit à partir des données fournies
         */
        public Profile build() {
            return new Profile(this.timeTable, this.date, this.arrStationId, buildStationFronts());
        }

        /**
         * Construit la liste des frontières de Pareto pour chaque gare du réseau.
         * <p>
         * Si une gare ne possède pas de constructeur de frontière ({@code null}),
         * une frontière vide ({@link ParetoFront#EMPTY}) est utilisée à la place.
         *
         * @return une liste immuable contenant la frontière de Pareto de chaque gare,
         * dans l’ordre de leur identifiant.
         */
        private List<ParetoFront> buildStationFronts() {
            ParetoFront[] fronts = new ParetoFront[stationSize];
            for (int i = 0; i < stationSize; i++) {
                ParetoFront.Builder builder = stationFrontBuilder[i];
                fronts[i] = (builder != null) ? builder.build() : ParetoFront.EMPTY;
            }
            return List.of(fronts);
        }
    }
}