package ch.epfl.rechor.journey;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static ch.epfl.rechor.Preconditions.checkArgument;

/**
 * Représente un voyage constitué d'une ou plusieurs étapes (legs).
 * Chaque étape peut être réalisée à pied ou en transport public.
 * Un voyage est valide si ses étapes respectent les contraintes définies par le projet.
 *
 * @param legs La liste des étapes du voyage.
 * @author Omar Ziyad Azgaoui (379136)
 * @author Mohamed Amine Goulahsen (400232)
 */
public record Journey(List<Leg> legs) {
    /**
     * Construit une instance de {@code Journey} en validant les arguments fournis.
     *
     * @throws IllegalArgumentException IllegalArgumentException si :
     *                                   <ul>
     *                                       <li>La liste des étapes est vide {@code legs.isEmpty()}.</li>
     *                                       <li>L'heure de départ d'une étape {@code depTime} précède l'heure d'arrivée de l'étape précédente {@code arrTime}.</li>
     *                                       <li>L'arrêt de départ d'une étape {@code depStop} ne correspond pas à l'arrêt d'arrivée de l'étape précédente {@code arrStop}
     *                                       .</li>
     *                                       <li>Les étapes à pied et en transport public ne s'alternent pas correctement.</li>
     *                                  </ul>
     */
    public Journey {
        checkArgument(!legs.isEmpty());
        legs = List.copyOf(legs);
        for (int i = 1; i < legs.size(); i++) {
            Leg previous = legs.get(i - 1);
            Leg current = legs.get(i);

            // Vérifie que l'instant de dép d'une étape ne précède pas celui d'arr de la précédente
            checkArgument(!current.depTime().isBefore(previous.arrTime()));

            // Vérifie que l'arrêt de dép d'une étape correspond à l'arrêt d'arr de la précédente
            checkArgument(Objects.equals(current.depStop(), previous.arrStop()));

            // Vérifie l'alternance entre étapes à pied et en transport
            checkArgument(areAlternatingLegTypes(previous, current));
        }
    }

    /**
     * Retourne l'arrêt de départ du voyage (celui de sa première étape).
     *
     * @return L'arrêt de départ.
     */
    public Stop depStop() {
        return legs.getFirst()
                   .depStop();
    }

    /**
     * Retourne l'arrêt d'arrivée du voyage (celui de sa dernière étape).
     *
     * @return L'arrêt d'arrivée.
     */
    public Stop arrStop() {
        return legs.getLast()
                   .arrStop();
    }

    /**
     * Retourne l'instant de départ du voyage (celui de sa première étape).
     *
     * @return L'heure/date de départ.
     */
    public LocalDateTime depTime() {
        return legs.getFirst()
                   .depTime();
    }

    /**
     * Retourne l'instant d'arrivée du voyage (celui de sa dernière étape).
     *
     * @return L'heure/date d'arrivée.
     */
    public LocalDateTime arrTime() {
        return legs.getLast()
                   .arrTime();
    }

    /**
     * Retourne la durée totale du voyage.
     *
     * @return La durée entre le départ et l'arrivée.
     */
    public Duration duration() {
        return Duration.between(depTime(), arrTime());
    }

    /**
     * Interface représentant une étape d'un voyage.
     * Peut être implémentée par un trajet à pied ou un transport public.
     */
    public sealed interface Leg permits Leg.Transport, Leg.Foot {

        /**
         * Retourne l'arrêt de départ de l'étape.
         *
         * @return L'arrêt de départ.
         */
        Stop depStop();

        /**
         * Retourne l'instant de départ de l'étape.
         *
         * @return L'heure de départ.
         */
        LocalDateTime depTime();

        /**
         * Retourne l'arrêt d'arrivée de l'étape.
         *
         * @return L'arrêt d'arrivée.
         */
        Stop arrStop();

        /**
         * Retourne l'instant d'arrivée de l'étape.
         *
         * @return L'heure d'arrivée.
         */
        LocalDateTime arrTime();

        /**
         * Retourne la liste des arrêts intermédiaires de l'étape.
         *
         * @return La liste des arrêts intermédiaires.
         */
        List<IntermediateStop> intermediateStops();

        /**
         * Retourne la durée de l'étape.
         *
         * @return La durée de l'étape.
         */
        default Duration duration() {
            return Duration.between(depTime(), arrTime());
        }

        /**
         * Représente un arrêt intermédiaire d'une étape.
         * Il s'agit d'un arrêt auquel le moyen de transport utilisé s'arrête effectivement,
         * mais qui se trouve entre l'arrêt de départ et l'arrêt d'arrivée de l'étape.
         *
         * @param stop    L'arrêt intermédiaire.
         * @param arrTime L'heure d'arrivée à cet arrêt.
         * @param depTime L'heure de départ de cet arrêt.
         */

        record IntermediateStop(Stop stop, LocalDateTime arrTime, LocalDateTime depTime) {
            /**
             * @throws NullPointerException     si {@code stop} est {@code null}.
             * @throws IllegalArgumentException si {@code depTime} est antérieur à {@code arrTime}.
             */
            public IntermediateStop {
                Objects.requireNonNull(stop);
                checkArgument(!depTime.isBefore(arrTime));
            }
        }

        /**
         * Représente une étape effectuée en transport public.
         *
         * @param depStop           L'arrêt de départ.
         * @param depTime           L'heure/date de départ.
         * @param arrStop           L'arrêt d'arrivée.
         * @param arrTime           L'heure/date d'arrivée.
         * @param intermediateStops Liste des éventuels arrêts intermédiaires.
         * @param vehicle           Le type de véhicule utilisé.
         * @param route             Le nom de la ligne sur laquelle circule le véhicule utilisé pour cette étape.
         * @param destination       La destination finale du véhicule utilisé pour cette étape.
         */
        record Transport(Stop depStop, LocalDateTime depTime, Stop arrStop, LocalDateTime arrTime,
                         List<IntermediateStop> intermediateStops, Vehicle vehicle, String route,
                         String destination) implements Leg {
            /**
             * @throws NullPointerException     si l'un des paramètres {@code depStop}, {@code arrStop},
             *                                  {@code depTime}, {@code arrTime}, {@code vehicle},
             *                                  {@code route} ou {@code destination} est {@code null}.
             * @throws IllegalArgumentException si {@code arrTime} est antérieur à {@code depTime}.
             */
            public Transport {
                Objects.requireNonNull(depStop);
                Objects.requireNonNull(arrStop);
                Objects.requireNonNull(depTime);
                Objects.requireNonNull(arrTime);
                Objects.requireNonNull(vehicle);
                Objects.requireNonNull(route);
                Objects.requireNonNull(destination);

                checkArgument(!arrTime.isBefore(depTime));

                intermediateStops = List.copyOf(intermediateStops);
            }
        }

        /**
         * Représente une étape effectuée à pied.
         *
         * @param depStop L'arrêt de départ.
         * @param depTime L'heure/date de départ.
         * @param arrStop L'arrêt d'arrivée.
         * @param arrTime L'heure/date d'arrivée.
         */
        record Foot(Stop depStop, LocalDateTime depTime, Stop arrStop, LocalDateTime arrTime)
                implements Leg {
            /**
             * @throws NullPointerException     si l'un des paramètres {@code depStop},
             *                                  {@code arrStop}, {@code depTime} ou {@code arrTime} est {@code null}.
             * @throws IllegalArgumentException si {@code arrTime} est antérieur à {@code depTime}.
             */
            public Foot {
                Objects.requireNonNull(depStop);
                Objects.requireNonNull(arrStop);
                Objects.requireNonNull(depTime);
                Objects.requireNonNull(arrTime);

                checkArgument(!arrTime.isBefore(depTime));
            }

            /**
             * Retourne une liste vide, car une étape à pied ne comporte jamais d'arrêts intermédiaires.
             *
             * @return Une liste vide.
             */
            @Override
            public List<IntermediateStop> intermediateStops() {
                return List.of();
            }

            /**
             * Indique si l'étape représente un changement au sein du même arrêt ou pas.
             *
             * @return true si les noms des arrêts de départ et d'arrivée sont identiques, false sinon.
             */
            public boolean isTransfer() {
                return depStop.name()
                              .equals(arrStop.name());
            }
        }
    }

    /**
     * Indique si deux étapes sont correctement alternées, c'est-à-dire
     * qu'elles sont respectivement à pied et en transport public, dans un ordre quelconque.
     *
     * @param a La première étape.
     * @param b La seconde étape.
     * @return {@code true} si l'une est une étape à pied et l'autre une étape en transport public, {@code false} sinon.
     */
    private static boolean areAlternatingLegTypes(Leg a, Leg b) {
        return (a instanceof Leg.Foot && b instanceof Leg.Transport) ||
               (a instanceof Leg.Transport && b instanceof Leg.Foot);
    }
}