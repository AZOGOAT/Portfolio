package ch.epfl.rechor;

import ch.epfl.rechor.journey.Journey;
import ch.epfl.rechor.journey.Stop;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

/**
 * Classe utilitaire offrant des méthodes statiques pour formater des données liées
 * aux voyages de transport public au format textuel français.
 * Cette classe permet d'obtenir la représentation textuelle des durées, heures,
 * arrêts, étapes de voyage et lignes, conformément aux conventions spécifiées
 * dans l'énoncé du projet ReCHor.
 * Classe non-instantiable.
 *
 * @author Omar Ziyad Azgaoui (379136)
 * @author Mohamed Amine Goulahsen (400232)
 */
public final class FormatterFr {

    private static final DateTimeFormatter TIME_FORMATTER = new DateTimeFormatterBuilder()
            .appendValue(ChronoField.HOUR_OF_DAY)
            .appendLiteral('h')
            .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
            .toFormatter();

    /**
     * Constructeur privé pour empêcher l'instanciation de la classe.
     */
    private FormatterFr() {
    }

    /**
     * Retourne une représentation textuelle d'une durée sous la forme "X h Y min"
     * ou "Y min" si la durée est inférieure à une heure.
     *
     * @param duration La durée à formater.
     * @return La représentation textuelle de la durée.
     */
    public static String formatDuration(Duration duration) {
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();

        return hours > 0
               ? String.format("%d h %d min", hours, minutes)
               : String.format("%d min", minutes);
    }

    /**
     * Retourne une représentation textuelle d'un instant sous la forme "HhMM",
     * où H est l'heure et MM les minutes sur deux chiffres.
     *
     * @param dateTime L'instant à formater.
     * @return La représentation textuelle de l'heure.
     */
    public static String formatTime(LocalDateTime dateTime) {
        return dateTime.format(TIME_FORMATTER);
    }

    /**
     * Retourne une représentation textuelle du nom de la voie ou du quai de l'arrêt donné.
     * Si le nom commence par un chiffre, il est précédé de "voie", sinon de "quai".
     *
     * @param stop L'arrêt dont le nom de voie/quai doit être formaté.
     * @return La représentation textuelle de la voie/quai, ou une chaîne vide si aucune n'est précisée.
     */
    public static String formatPlatformName(Stop stop) {
        if (stop.platformName() == null || stop.platformName().isEmpty()) {
            return "";
        }
        return Character.isDigit(stop.platformName().charAt(0))
               ? "voie " + stop.platformName()
               : "quai " + stop.platformName();
    }

    /**
     * Retourne une représentation textuelle d'une étape à pied, en précisant s'il s'agit
     * d'un changement ou d'un trajet à pied, suivi de sa durée.
     *
     * @param footLeg L'étape à pied à formater.
     * @return La représentation textuelle de l'étape à pied.
     */
    public static String formatLeg(Journey.Leg.Foot footLeg) {
        String type = footLeg.isTransfer() ? "changement" : "trajet à pied";
        return String.format("%s (%s)", type, formatDuration(footLeg.duration()));
    }

    /**
     * Retourne une représentation textuelle d'une étape effectuée en transport public,
     * incluant l'heure de départ, les arrêts de départ et d'arrivée, et les voies/quais associés.
     *
     * @param leg L'étape de transport à formater.
     * @return La représentation textuelle de l'étape en transport public.
     */
    public static String formatLeg(Journey.Leg.Transport leg) {
        StringBuilder sb = new StringBuilder();
        sb.append(formatTime(leg.depTime()))
          .append(" ")
          .append(leg.depStop().name());

        String depPlatform = formatPlatformName(leg.depStop());
        if (!depPlatform.isEmpty()) {
            sb.append(" (")
              .append(depPlatform)
              .append(")");
        }

        sb.append(" → ")
          .append(leg.arrStop().name())
          .append(" (arr. ")
          .append(formatTime(leg.arrTime()));

        String arrPlatform = formatPlatformName(leg.arrStop());
        if (!arrPlatform.isEmpty()) {
            sb.append(" ")
              .append(arrPlatform);
        }

        sb.append(")");
        return sb.toString();
    }

    /**
     * Retourne une représentation textuelle de la ligne et du sens de parcours d'un véhicule.
     *
     * @param transportLeg L'étape de transport à formater.
     * @return La représentation textuelle de la ligne et de sa destination.
     */
    public static String formatRouteDestination(Journey.Leg.Transport transportLeg) {
        return transportLeg.route() + " Direction " + transportLeg.destination();
    }
}