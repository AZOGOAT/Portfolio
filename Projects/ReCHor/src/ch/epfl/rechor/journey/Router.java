package ch.epfl.rechor.journey;


import ch.epfl.rechor.PackedRange;
import ch.epfl.rechor.Bits32_24_8;
import ch.epfl.rechor.timetable.Connections;
import ch.epfl.rechor.timetable.TimeTable;
import ch.epfl.rechor.timetable.Transfers;
import ch.epfl.rechor.timetable.Trips;

import java.time.LocalDate;
import java.util.NoSuchElementException;

import static ch.epfl.rechor.Bits32_24_8.pack;
import static ch.epfl.rechor.journey.PackedCriteria.changes;
import static ch.epfl.rechor.journey.PackedCriteria.payload;


/**
 * Calcule les voyages optimaux permettant de se rendre à une gare donnée, un jour donné.
 * <p>
 * Cette classe implémente l’algorithme CSA (connection scan algorithm), qui explore
 * toutes les liaisons de l’horaire par heure de départ décroissante afin de construire
 * un profil contenant les frontières de Pareto pour chaque gare et chaque course.
 * <p>
 * Le résultat contient, pour chaque gare, l’ensemble des voyages optimaux permettant
 * de rejoindre la destination finale le jour donné. Il est également utilisé pour extraire
 * les voyages optimaux en tenant compte des informations auxiliaires nécessaires,
 * comme la première liaison à prendre et le nombre d’arrêts intermédiaires.
 *
 * @author Omar Ziyad Azgaoui (379136)
 * @author Mohamed Amine Goulahsen (400232)
 */
public record Router(TimeTable timetable) {

    private static final int UNWALKABLE = -1;

    /**
     * Calcule le profil des voyages optimaux permettant de se rendre à la station donnée
     * à la date donnée.
     * <p>
     * L’algorithme CSA utilise trois manières de continuer un voyage à partir d’une liaison :
     * marcher jusqu’à la destination si possible, continuer dans la même course,
     * ou changer de véhicule à la fin de la liaison. À chaque liaison, une frontière de Pareto
     * est construite et propagée à la course à laquelle elle appartient et à toutes les gares
     * pouvant atteindre la gare de départ de la liaison à pied (y compris elle-même).
     * <p>
     * Les informations nécessaires à l’extraction des voyages — à savoir l’identifiant de la
     * première liaison à prendre et le nombre d’arrêts à laisser passer — sont empaquetées
     * dans la charge utile de chaque tuple de la frontière.
     *
     * @param date                 la date pour laquelle les voyages optimaux doivent être calculés.
     * @param dstStationId l’identifiant de la gare de destination.
     * @return le profil des voyages optimaux vers la station de destination pour la date donnée.
     */
    public Profile profile(LocalDate date, int dstStationId) {
        // Récupération les données horaires (liaisons, transferts, courses) pour le jour donné
        Connections connections = timetable.connectionsFor(date);
        Transfers transfers = timetable.transfers();
        Trips trips = timetable.tripsFor(date);

        int stationCount = timetable.stations().size();
        int tripCount = trips.size();
        int connectionCount = connections.size();

        // Instancie le constructeur du profil augmenté, qui contiendra les frontières pour chaque gare et course
        Profile.Builder builder = new Profile.Builder(timetable, date, dstStationId);

        // Initialise une frontière vide pour chaque gare
        for (int i = 0; i < stationCount; i++)
            builder.setForStation(i, new ParetoFront.Builder());

        // Initialise une frontière vide pour chaque course
        for (int i = 0; i < tripCount; i++)
            builder.setForTrip(i, new ParetoFront.Builder());

        // Pré-calcul des temps de marche vers la destination
        int[] walkToDest = new int[stationCount];
        for (int i = 0; i < stationCount; i++) {
            try {
                walkToDest[i] = transfers.minutesBetween(i, dstStationId);
            } catch (NoSuchElementException e) {
                walkToDest[i] = UNWALKABLE; // -1 indique que le trajet n'est pas faisable à pied
            }
        }

        // Traitement de chaque liaison (ordonnées par heure de départ décroissante)
        for (int connectionIndex = 0; connectionIndex < connectionCount; connectionIndex++) {
            int depStopId = connections.depStopId(connectionIndex);
            int arrStopId = connections.arrStopId(connectionIndex);
            int depStationId = timetable.stationId(depStopId);
            int arrStationId = timetable.stationId(arrStopId);
            int depTime = connections.depMins(connectionIndex);
            int arrTime = connections.arrMins(connectionIndex);
            int tripId = connections.tripId(connectionIndex);
            int posInTrip = connections.tripPos(connectionIndex);

            // Construit une nouvelle frontière pour cette liaison
            ParetoFront.Builder connectionFront = new ParetoFront.Builder();

            // Option 1 : marcher depuis arrStationId jusqu'à destination
            int walkTime = walkToDest[arrStationId];
            if (walkTime != UNWALKABLE) {
                long packed = PackedCriteria.pack(arrTime + walkTime,
                                                  0,
                                                      pack(connectionIndex, 0));
                connectionFront.add(packed);
            }

            // Option 2 : rester dans la même course (propage la frontière de la course)
            if (!builder.forTrip(tripId).isEmpty())
                connectionFront.addAll(builder.forTrip(tripId));

            // Option 3 : changer de véhicule à la fin de la liaison
            propagateWithChange(connectionFront, builder, connectionIndex, arrStationId, arrTime);

            // OPTIMISATION 1 : on saute si la frontière est vide
            if (connectionFront.isEmpty()) continue;

            // OPTIMISATION 2 : on saute si f est entièrement dominée à depTime
            if (builder.forStation(depStationId).fullyDominates(connectionFront, depTime)) continue;

            // Mise à jour de la frontière de la course
            builder.forTrip(tripId)
                   .addAll(connectionFront);

            // Propagation de la frontière aux gares pouvant atteindre depStationId à pied
            int packedRange = transfers.arrivingAt(depStationId);
            int from = PackedRange.startInclusive(packedRange);
            int to = PackedRange.endExclusive(packedRange);
            for (int j = from; j < to; j++) {
                int fromStation = transfers.depStationId(j);
                int duration = transfers.minutes(j);
                int adjustedDep = depTime - duration;

                propagateToStation(connectionFront,
                                   connections,
                                   posInTrip,
                                   connectionIndex,
                                   adjustedDep,
                                   fromStation,
                                   builder);
            }
        }
        return builder.build(); // Construit le profil final
    }

    /**
     * Propage les tuples d’une frontière de Pareto vers une station cible,
     * en y ajoutant l’information auxiliaire nécessaire à l’extraction du voyage.
     * <p>
     * Pour chaque tuple de la frontière, la méthode extrait l’heure d’arrivée, le nombre
     * de changements, ainsi que la charge utile (payload), puis calcule une nouvelle
     * charge utile encodant :
     * <ul>
     *     <li>l’identifiant de la liaison actuelle,</li>
     *     <li>le nombre d’arrêts à laisser passer (différence entre les positions).</li>
     * </ul>
     * Cette charge utile est ensuite empaquetée avec les autres critères d’optimisation
     * et l’heure de départ ajustée, avant d’être ajoutée à la frontière de Pareto de la station cible.
     *
     * @param front             la frontière de Pareto construite pour une liaison donnée.
     * @param posInTrip         la position de la liaison dans sa course.
     * @param connectionIndex   l’identifiant de la liaison actuelle.
     * @param depTime           l’heure de départ ajustée pour le changement.
     * @param targetStationId   l’identifiant de la station cible vers laquelle propager.
     * @param builder           le constructeur du profil pour stocker les résultats.
     * @param connections       les liaisons de l’horaire.
     */
    private void propagateToStation(ParetoFront.Builder front,
                                    Connections connections,
                                    int posInTrip,
                                    int connectionIndex,
                                    int depTime,
                                    int targetStationId,
                                    Profile.Builder builder) {
        front.forEach(journey -> {
            int arr = PackedCriteria.arrMins(journey);
            int chg = changes(journey);
            int packedPayload = payload(journey);
            int posToReach = Bits32_24_8.unpack24(packedPayload);
            int deltaStops = connections.tripPos(posToReach) - posInTrip;
            int newPayload = pack(connectionIndex, deltaStops);

            long packed = PackedCriteria.pack(arr, chg, newPayload);
            packed = PackedCriteria.withDepMins(packed, depTime);

            builder.forStation(targetStationId)
                   .add(packed);
        });
    }

    /**
     * Propage dans une frontière de liaison les tuples atteignables par un changement
     * à la station d’arrivée de la liaison actuelle.
     * <p>
     * Pour chaque tuple de la frontière de la station d’arrivée — dont l’heure de départ
     * est postérieure ou égale à l’heure d’arrivée de la liaison — la méthode ajoute un tuple
     * dans la frontière de la liaison, en y ajoutant un changement, tout en conservant
     * l’heure d’arrivée et la charge utile.
     * <p>
     * Cette méthode correspond à l’option 3 de l’algorithme CSA : changement de véhicule.
     *
     * @param connectionFront   la frontière de la liaison en cours de traitement.
     * @param profileBuilder    le constructeur du profil contenant les frontières des stations.
     * @param connectionIndex   l’identifiant de la liaison actuelle.
     * @param arrStationId      l’identifiant de la station d’arrivée de la liaison actuelle.
     * @param arrTime           l’heure d’arrivée de la liaison actuelle.
     */
    private static void propagateWithChange(ParetoFront.Builder connectionFront,
                                            Profile.Builder profileBuilder,
                                            int connectionIndex,
                                            int arrStationId,
                                            int arrTime) {
        profileBuilder.forStation(arrStationId).forEach(t -> {
            int dep = PackedCriteria.depMins(t);
            if (dep >= arrTime) {
                int arr = PackedCriteria.arrMins(t);
                int chg = changes(t) + 1;
                long packed = PackedCriteria.pack(arr, chg, pack(connectionIndex, 0));
                connectionFront.add(packed);
            }
        });
    }
}