package ch.epfl.rechor.journey;

import ch.epfl.rechor.FormatterFr;
import ch.epfl.rechor.IcalBuilder;

import java.time.LocalDateTime;
import java.util.StringJoiner;
import java.util.UUID;

/**
 * Classe utilitaire non instanciable permettant de convertir un objet {@link Journey}
 * en une chaîne au format iCalendar.
 * <p>
 * Cette classe offre un unique service : générer une représentation textuelle
 * conforme à la norme iCalendar pour un voyage donné, incluant les détails tels
 * que les horaires, les arrêts de départ et d'arrivée, ainsi que la description des étapes.
 * </p>
 *
 * @author Omar Ziyad Azgaoui (379136)
 * @author Mohamed Amine Goulahsen (400232)
 */
public class JourneyIcalConverter {

    /**
     * Constructeur privé pour empêcher l'instanciation de cette classe utilitaire.
     */
    private JourneyIcalConverter() {
    }

    /**
     * Convertit un objet {@link Journey} en une représentation textuelle au format iCalendar.
     * <p>
     * Cette méthode crée un événement iCalendar correspondant à un voyage,
     * en suivant les spécifications de la norme iCalendar. L'événement généré
     * inclut des attributs tels que l'UID unique, la date de création,
     * les horaires de départ et d'arrivée, ainsi que les arrêts de départ et d'arrivée.
     * </p>
     * <p>
     * Les étapes du voyage sont formatées dans la description en fonction de leur nature :
     * à pied ou en transport public.
     * </p>
     *
     * @param journey Le voyage à convertir en événement iCalendar.
     * @return Une chaîne de caractères représentant le voyage sous le format iCalendar.
     * @throws IllegalArgumentException Si le voyage fourni n'est pas valide selon les règles de validation de la classe {@link Journey}.
     */
    public static String toIcalendar(Journey journey) {
        IcalBuilder builder = new IcalBuilder();

        // Initialisation de l'objet iCalendar
        builder.begin(IcalBuilder.Component.VCALENDAR)
               .add(IcalBuilder.Name.VERSION, "2.0")
               .add(IcalBuilder.Name.PRODID, "ReCHor")
               .begin(IcalBuilder.Component.VEVENT)

        // Définition des attributs de l'événement
               .add(IcalBuilder.Name.UID, UUID.randomUUID().toString())
               .add(IcalBuilder.Name.DTSTAMP, LocalDateTime.now())
               .add(IcalBuilder.Name.DTSTART, journey.depTime())
               .add(IcalBuilder.Name.DTEND, journey.arrTime())
               .add(IcalBuilder.Name.SUMMARY,
                    journey.depStop().name() + " → " + journey.arrStop().name())
               .add(IcalBuilder.Name.DESCRIPTION, buildDescription(journey))

        // Fermeture des composants VEVENT et VCALENDAR
               .end()  // Termine VEVENT
               .end(); // Termine VCALENDAR

        // Retourne la chaîne iCalendar complète
        return builder.build();
    }

    /**
     * Construit la chaîne de description d’un voyage au format iCalendar.
     * <p>
     * Cette description liste chaque étape du voyage sous forme textuelle,
     * séparée par des sauts de ligne conformes au format iCalendar (\n échappé).
     *
     * @param journey Le voyage dont on souhaite obtenir la description.
     * @return Une chaîne iCalendar représentant toutes les étapes du voyage.
     */
    private static String buildDescription(Journey journey) {
        StringJoiner descriptionJoiner = new StringJoiner("\\n");
        for (Journey.Leg leg : journey.legs()) {
            switch (leg) {
                case Journey.Leg.Foot f -> descriptionJoiner.add(FormatterFr.formatLeg(f));
                case Journey.Leg.Transport t -> descriptionJoiner.add(FormatterFr.formatLeg(t));
            }
        }
        return descriptionJoiner.toString();
    }
}