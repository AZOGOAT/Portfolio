package ch.epfl.rechor.journey;

import ch.epfl.rechor.timetable.Connections;
import ch.epfl.rechor.timetable.TimeTable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static ch.epfl.rechor.Bits32_24_8.unpack24;
import static ch.epfl.rechor.Bits32_24_8.unpack8;
import static ch.epfl.rechor.journey.PackedCriteria.*;

/**
 * Classe non instanciable permettant d'extraire tous les voyages optimaux pour un arrêt de départ donné,
 * à partir d’un profil. Chaque voyage extrait contient toutes les étapes nécessaires pour atteindre la
 * gare de destination, y compris les étapes à pied initiales et finales si elles sont requises.
 * Chaque voyage est représenté sous forme d'une liste d'étapes (legs), contenant des étapes en transport
 * public et à pied, selon les besoins. Les voyages extraits respectent les critères du profil et sont triés
 * par heure de départ puis par heure d'arrivée.
 *
 * @author Omar Ziyad Azgaoui (379136)
 * @author Mohamed Amine Goulahsen (400232)
 */
public class JourneyExtractor {

    private JourneyExtractor() {
    }

    /**
     * Extrait et retourne tous les voyages optimaux depuis une gare de départ donnée,
     * en utilisant les critères stockés dans le profil.
     * <p>
     * Chaque voyage extrait est une suite d'étapes ({@code Journey.Leg}), comprenant :
     * <ul>
     *     <li>Éventuellement une étape à pied initiale entre la gare de départ et le premier arrêt,</li>
     *     <li>Une ou plusieurs étapes en transport public (avec arrêts intermédiaires si nécessaires),</li>
     *     <li>Éventuellement des étapes à pied entre les transports (changements),</li>
     *     <li>Et une étape à pied finale jusqu'à la gare de destination si elle n'est pas atteinte directement.</li>
     * </ul>
     * Les voyages sont triés par heure de départ, puis par heure d'arrivée.
     *
     * @param profile      le profil contenant les critères optimaux vers la destination cible
     * @param depStationId l’identifiant de la station de départ
     * @return la liste des voyages optimaux, chacun sous forme d’objet {@link Journey}
     */
    public static List<Journey> journeys(Profile profile, int depStationId) {
        List<Journey> journeys = new ArrayList<>();
        Connections connections = profile.connections();
        TimeTable timeTable = profile.timeTable();

        profile.forStation(depStationId)
               .forEach(criteria -> {
                   List<Journey.Leg> legs = new ArrayList<>();

                   // Gérer l'extraction de voyage avec une approche itérative
                   long currentCriteria = criteria;
                   int remainingChanges = changes(currentCriteria);

                   // Extraire les informations du premier critère pour vérifier si une étape à pied
                   // initiale est nécessaire
                   int firstConnectionId = extractConnectionId(payload(currentCriteria));
                   int firstDepStopId = connections.depStopId(firstConnectionId);
                   int firstDepStationId = timeTable.stationId(firstDepStopId);

                   // Ajouter une étape à pied initiale si nécessaire
                   if (depStationId != firstDepStationId) {
                       int depMins = connections.depMins(firstConnectionId);
                       LocalDateTime depTime = toDateTime(profile, depMins);
                       int transferMinutes = timeTable.transfers()
                                                      .minutesBetween(depStationId,
                                                                      firstDepStationId);

                       legs.add(createLeg(LegType.FOOT,
                                          createStop(profile, depStationId),
                                          depTime.minusMinutes(transferMinutes),
                                          createStop(profile, firstDepStopId),
                                          depTime,
                                          null,
                                          null,
                                          null,
                                          null));
                   }

                   while (remainingChanges >= 0) {
                       // Extraire les informations du critère actuel
                       processLeg(profile, currentCriteria, legs);

                       // Si plus de changements à faire, sortir de la boucle
                       if (remainingChanges == 0) {
                           break;
                       }

                       // Trouver le prochain critère pour le prochain changement
                       int targetArrMins = arrMins(currentCriteria);
                       int payload = payload(currentCriteria);
                       int connectionId = extractConnectionId(payload);
                       int stopsToSkip = extractStopsToSkip(payload);

                       // Trouver le dernier arrêt du segment actuel
                       int currentConnId = connectionId;
                       for (int i = 0; i < stopsToSkip; i++) {
                           currentConnId = connections.nextConnectionId(currentConnId);
                       }
                       int arrStopId = connections.arrStopId(currentConnId);
                       int arrStationId = timeTable.stationId(arrStopId);

                       // Chercher le critère suivant pour continuer le voyage
                       long nextCriteria = profile.forStation(arrStationId)
                                                  .get(targetArrMins, remainingChanges - 1);

                       // Passer au critère suivant
                       currentCriteria = nextCriteria;
                       remainingChanges--;
                   }

                   journeys.add(new Journey(legs));
               });

        journeys.sort(Comparator.comparing(Journey::depTime)
                                .thenComparing(Journey::arrTime));

        return journeys;
    }

    /**
     * Ajoute à la liste donnée une étape de transport public (et éventuellement une ou deux étapes à pied)
     * correspondant au critère d'optimisation fourni.
     * <p>
     * Cette méthode gère :
     * <ul>
     *   <li>La récupération des arrêts intermédiaires entre deux connexions successives,</li>
     *   <li>L’ajout éventuel d’une étape à pied entre deux étapes de transport, si une correspondance est nécessaire,</li>
     *   <li>L’ajout d’une étape de transport entre les arrêts de départ et d’arrivée déterminés par le critère,</li>
     *   <li>Et, s’il s’agit de la dernière étape du voyage, l’ajout d’une étape à pied finale vers la gare de destination du profil.</li>
     * </ul>
     *
     * @param profile  le profil contenant les données horaires et de transfert
     * @param criteria le critère d’optimisation décrivant l’étape à ajouter
     * @param legs     la liste des étapes du voyage en cours de construction (modifiée par effet de bord)
     */
    private static void processLeg(Profile profile, long criteria, List<Journey.Leg> legs) {
        // 1. Extraire les infos du critère et connexions
        int connectionId = extractConnectionId(payload(criteria));
        int stopsToSkip = extractStopsToSkip(payload(criteria));

        Connections connections = profile.connections();
        TimeTable timeTable = profile.timeTable();

        // 2. Collecter les arrêts intermédiaires
        List<Journey.Leg.IntermediateStop> intermediateStops = new ArrayList<>();
        int currentConnId = connectionId;

        for (int i = 0; i < stopsToSkip; i++) {
            int nextConnId = connections.nextConnectionId(currentConnId);
            int stopId = connections.arrStopId(currentConnId);

            intermediateStops.add(createIntermediateStop(createStop(profile, stopId),
                                                         toDateTime(profile,
                                                                    connections.arrMins(
                                                                            currentConnId)),
                                                         toDateTime(profile,
                                                                    connections.depMins(nextConnId)))
                                 );

            currentConnId = nextConnId;
        }

        // 3. Informations de la connexion finale
        int finalConnId = currentConnId;
        int depStopId = connections.depStopId(connectionId);
        int arrStopId = connections.arrStopId(finalConnId);
        int depMins = connections.depMins(connectionId);
        int arrMins = connections.arrMins(finalConnId);
        int tripId = connections.tripId(finalConnId);

        // 4. Créer les stops et obtenir les stations
        Stop depStop = createStop(profile, depStopId);
        Stop arrStop = createStop(profile, arrStopId);
        int depStationId = timeTable.stationId(depStopId);
        int arrStationId = timeTable.stationId(arrStopId);

        // 5. Ajouter une étape à pied entre deux transports (transfert)
        if (!legs.isEmpty() && legs.getLast() instanceof Journey.Leg.Transport lastTransport) {
            LocalDateTime lastArrTime = lastTransport.arrTime();

            // Nous avons besoin de l'ID de la station d'arrivée du dernier transport
            int fromStationId = 0;
            for (int i = 0; i < timeTable.stations().size(); i++) {
                if (timeTable.stations().name(i)
                             .equals(lastTransport.arrStop().name())) {
                    fromStationId = i;
                    break;
                }
            }
            // Calculer le temps de transfert entre les deux stations
            int transferMinutes = timeTable.transfers()
                                           .minutesBetween(fromStationId, depStationId);

            legs.add(createLeg(LegType.FOOT,
                               lastTransport.arrStop(),
                               lastArrTime,
                               depStop,
                               lastArrTime.plusMinutes(transferMinutes),
                               null,
                               null,
                               null,
                               null));
        }

        // 6. Ajouter l'étape de transport
        legs.add(createLeg(LegType.TRANSPORT,
                           depStop,
                           toDateTime(profile, depMins),
                           arrStop,
                           toDateTime(profile, arrMins),
                           intermediateStops,
                           timeTable.routes().vehicle(profile.trips().routeId(tripId)),
                           timeTable.routes().name(profile.trips().routeId(tripId)),
                           profile.trips().destination(tripId)));

        // 7. Ajouter une étape à pied finale si nécessaire
        int remainingChanges = changes(criteria);
        if (remainingChanges == 0 && arrStationId != profile.arrStationId()) {
            int targetStationId = profile.arrStationId();
            LocalDateTime transportArrTime = toDateTime(profile, arrMins);
            int transferMinutes = timeTable.transfers()
                                           .minutesBetween(arrStationId, targetStationId);

            legs.add(createLeg(LegType.FOOT,
                               arrStop,
                               transportArrTime,
                               createStop(profile, targetStationId),
                               transportArrTime.plusMinutes(transferMinutes),
                               null,
                               null,
                               null,
                               null));
        }
    }

    /**
     * Construit un objet {@link Stop} à partir de l'identifiant d'un arrêt (gare ou voie/quai).
     * <p>
     * Si l'ID correspond directement à une gare, le {@code Stop} est créé sans nom de voie.
     * Sinon, l'arrêt est une voie ou un quai, et le {@code Stop} est construit avec le nom de
     * la gare associée et le nom de la voie.
     *
     * @param profile le profil contenant l'horaire et les arrêts
     * @param stopId  l'identifiant de l'arrêt
     * @return le {@code Stop} correspondant à l'identifiant donné
     */
    private static Stop createStop(Profile profile, int stopId) {
        TimeTable timeTable = profile.timeTable();

        if (timeTable.isStationId(stopId)) {
            return new Stop(timeTable.stations().name(stopId),
                            "",
                            timeTable.stations().longitude(stopId),
                            timeTable.stations().latitude(stopId));
        } else {
            int stationId = timeTable.stationId(stopId);
            return new Stop(timeTable.stations().name(stationId),
                            timeTable.platformName(stopId),
                            timeTable.stations().longitude(stationId),
                            timeTable.stations().latitude(stationId));
        }
    }

    /**
     * Énumération représentant le type d'étape d'un voyage.
     * <p>
     * Une étape peut être soit :
     * <ul>
     *     <li>{@code FOOT} : une étape effectuée à pied (entre deux arrêts voisins ou lors d'un changement),</li>
     *     <li>{@code TRANSPORT} : une étape effectuée en transport public (train, bus, métro, etc.).</li>
     * </ul>
     */
    private enum LegType {
        FOOT,
        TRANSPORT,
    }

    /**
     * Crée une étape de voyage ({@code Journey.Leg}) du type spécifié.
     * <p>
     * Selon la valeur de {@code type}, crée soit une étape à pied, soit une étape en transport public,
     * avec les informations fournies.
     *
     * @param type              le type d’étape à créer (à pied ou en transport)
     * @param depStop           l’arrêt de départ de l’étape
     * @param depTime           l’heure de départ de l’étape
     * @param arrStop           l’arrêt d’arrivée de l’étape
     * @param arrTime           l’heure d’arrivée de l’étape
     * @param intermediateStops les arrêts intermédiaires (uniquement pour une étape en transport)
     * @param vehicle           le type de véhicule (uniquement pour une étape en transport)
     * @param route             le nom de la ligne (uniquement pour une étape en transport)
     * @param destination       la destination finale (uniquement pour une étape en transport)
     * @return une instance de {@code Journey.Leg} correspondant au type demandé
     */
    private static Journey.Leg createLeg(LegType type,
                                         Stop depStop,
                                         LocalDateTime depTime,
                                         Stop arrStop,
                                         LocalDateTime arrTime,
                                         List<Journey.Leg.IntermediateStop> intermediateStops,
                                         Vehicle vehicle,
                                         String route,
                                         String destination) {

        return switch (type) {
            case FOOT -> new Journey.Leg.Foot(depStop, depTime, arrStop, arrTime);
            case TRANSPORT -> new Journey.Leg.Transport(depStop,
                                                        depTime,
                                                        arrStop,
                                                        arrTime,
                                                        intermediateStops,
                                                        vehicle,
                                                        route,
                                                        destination);
        };
    }

    /**
     * Crée une étape intermédiaire dans une étape de transport public.
     * <p>
     * Une étape intermédiaire représente un arrêt où le véhicule s'arrête brièvement entre
     * l'arrêt de départ et l'arrêt d’arrivée d’un trajet.
     *
     * @param stop    l’arrêt intermédiaire
     * @param arrTime l’heure d’arrivée à cet arrêt
     * @param depTime l’heure de départ de cet arrêt
     * @return une instance de {@code Journey.Leg.IntermediateStop} avec les informations fournies
     */
    private static Journey.Leg.IntermediateStop createIntermediateStop(Stop stop,
                                                                       LocalDateTime arrTime,
                                                                       LocalDateTime depTime) {

        return new Journey.Leg.IntermediateStop(stop, arrTime, depTime);
    }

    /**
     * Extrait l'identifiant de la liaison (connection) à partir des 24 bits de poids fort
     * de la valeur empaquetée donnée.
     *
     * @param payload la valeur empaquetée contenant l'information
     * @return l'identifiant de la liaison
     */
    private static int extractConnectionId(int payload) {
        return unpack24(payload);
    }

    /**
     * Extrait le nombre d'arrêts intermédiaires à sauter à partir des 8 bits de poids faible
     * de la valeur empaquetée donnée.
     *
     * @param payload la valeur empaquetée contenant l'information
     * @return le nombre d'arrêts à sauter
     */
    private static int extractStopsToSkip(int payload) {
        return unpack8(payload);
    }

    /**
     * Calcule la date et l'heure correspondant à un nombre de minutes après minuit
     * le jour du profil donné.
     *
     * @param profile le profil contenant la date de référence
     * @param minutes le nombre de minutes après minuit
     * @return la date et l'heure résultantes
     */
    private static LocalDateTime toDateTime(Profile profile, int minutes) {
        return profile.date().atStartOfDay().plusMinutes(minutes);
    }
}