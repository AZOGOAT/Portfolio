package ch.epfl.rechor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static ch.epfl.rechor.Preconditions.checkArgument;

/**
 * Représente un bâtisseur d'événements iCalendar permettant de générer un événement au format iCalendar.
 * Cette classe permet de construire des événements structurés avec les composants appropriés et garantit
 * le respect des spécifications du format iCalendar, notamment le pliage des lignes.
 *
 * @author Omar Ziyad Azgaoui (379136)
 * @author Mohamed Amine Goulahsen (400232)
 */
public final class IcalBuilder {

    private static final String CRLF = "\r\n";
    private static final int MAX_LINE_LENGTH = 75;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(
            "yyyyMMdd'T'HHmmss");

    private final List<Component> componentsStack;
    private final StringBuilder calendarBuilder;

    /**
     * Représente les composants définis par la norme iCalendar, encadrés par des lignes
     * {@code BEGIN} et {@code END} indiquant le début et la fin du bloc.
     *
     * <p>Deux types de composants sont définis et utilisés dans ce projet :</p>
     * <ul>
     *     <li>{@link #VCALENDAR} : Ce composant représente un objet iCalendar complet.
     *     Il sert de conteneur principal à l'ensemble des données de l'événement.</li>
     *     <li>{@link #VEVENT} : Représente un composant unique correspondant à un événement
     *     spécifique dans le calendrier. Il est contenu à l'intérieur du composant {@code VCALENDAR}.</li>
     * </ul>
     *
     * <p>Un fichier iCalendar valide commence toujours par une paire de lignes
     * {@code BEGIN:VCALENDAR} et {@code END:VCALENDAR}, et contient exactement un composant
     * {@code VEVENT}, également encadré par ses propres lignes {@code BEGIN:VEVENT} et
     * {@code END:VEVENT}.</p>
     *
     * <p>Les composants peuvent également contenir des attributs spécifiques entre les balises BEGIN/END,
     * définissant des détails de l'événement par exemple.</p>
     */
    public enum Component {
        VCALENDAR,
        VEVENT
    }

    /**
     * Représente les noms des attributs définis par la norme iCalendar.
     * Chaque nom correspond à une ligne d'information à l'intérieur d'un composant {@code VCALENDAR} ou {@code VEVENT}.
     *
     * <p>Les noms d'attributs sont utilisés pour définir soit des métadonnées globales, soit des détails spécifiques
     * à un événement. Selon leur contexte (dans VCALENDAR ou VEVENT), ils ont des rôles différents :</p>
     *
     * <ul>
     *     <li>{@link #BEGIN} : Marque le début d'un composant (VCALENDAR ou VEVENT).</li>
     *     <li>{@link #END} : Marque la fin du composant en cours.</li>
     * <p>
     *     <!-- Attributs pour VCALENDAR -->
     *     <li>{@link #PRODID} : Identifie le logiciel ayant généré le fichier iCalendar (exemple : ReCHor).</li>
     *     <li>{@link #VERSION} : Spécifie la version de la norme iCalendar utilisée, toujours {@code 2.0} pour ce projet.</li>
     * <p>
     *     <!-- Attributs pour VEVENT -->
     *     <li>{@link #UID} : Identifiant unique de l'événement, sous forme de chaîne UUID garantissant l'unicité.</li>
     *     <li>{@link #DTSTAMP} : Date et heure de création ou de dernière modification de l'événement, sans rapport avec sa date de début.</li>
     *     <li>{@link #DTSTART} : Date et heure du début de l'événement.</li>
     *     <li>{@link #DTEND} : Date et heure de fin de l'événement.</li>
     *     <li>{@link #SUMMARY} : Brève description de l'événement, affichée par défaut dans les calendriers électroniques.</li>
     *     <li>{@link #DESCRIPTION} : Description détaillée de l'événement, généralement affichée lors de la sélection de l'événement.</li>
     * </ul>
     *
     * <p>Chaque attribut doit suivre le format texte prescrit par la norme iCalendar, séparant
     * le nom et sa valeur par deux-points ({@code :}).</p>
     */
    public enum Name {
        // Balises de début et de fin de composants
        BEGIN,      // Marque le début d'un composant iCalendar
        END,        // Marque la fin d'un composant iCalendar

        // Attributs du composant VCALENDAR
        PRODID,     // Identifie le logiciel qui a créé le calendrier (ex: ReCHor dans ce projet)
        VERSION,    // Version de la norme iCalendar (toujours 2.0 dans ce projet)

        // Attributs du composant VEVENT
        UID,        // Identifiant unique de l'événement (de type UUID)
        DTSTAMP,    // Date/heure de création ou modification de l'événement
        DTSTART,    // Date/heure de début de l'événement
        DTEND,      // Date/heure de fin de l'événement
        SUMMARY,    // Brève description de l'événement
        DESCRIPTION // Description détaillée visible en sélectionnant l'événement
    }

    /**
     * Construit un bâtisseur iCalendar vide.
     */
    public IcalBuilder() {
        this.componentsStack = new ArrayList<>();
        this.calendarBuilder = new StringBuilder();
    }

    /**
     * Ajoute une ligne avec un nom donné et une valeur sous forme de chaîne de caractères.
     * Si la ligne dépasse 75 caractères, elle est pliée conformément à la norme iCalendar.
     *
     * @param name  Le nom de la ligne à ajouter.
     * @param value La valeur associée à ce nom.
     * @return L'instance actuelle du bâtisseur (pour le chaînage).
     */
    public IcalBuilder add(Name name, String value) {
        String line = name + ":" + value;

        int lineLength = MAX_LINE_LENGTH; // Longueur de la première ligne (75 caractères)
        boolean firstLine = true;
        while (line.length() > lineLength) {
            calendarBuilder.append(line, 0, lineLength)
                           .append(CRLF)
                           .append(" "); // Ajoute un espace pour indiquer la continuat° de la ligne
            line = line.substring(lineLength);

            if (firstLine) {
                lineLength--; // Le premier espace doit être pris en compte
                firstLine = false;
            }
        }
        calendarBuilder.append(line)
                       .append(CRLF);

        return this;
    }

    /**
     * Ajoute une ligne avec un nom donné et une valeur représentant une date/heure.
     * La date/heure est formatée selon la norme iCalendar (au format "yyyyMMdd'T'HHmmss").
     *
     * @param name     Le nom de la ligne à ajouter.
     * @param dateTime La date/heure à associer au nom donné.
     * @return L'instance actuelle du bâtisseur (pour le chaînage).
     */
    public IcalBuilder add(Name name, LocalDateTime dateTime) {
        String formattedDate = DATE_TIME_FORMATTER.format(dateTime);
        return add(name, formattedDate);
    }

    /**
     * Commence un nouveau composant en ajoutant une ligne BEGIN correspondante et en l'empilant.
     *
     * @param component Le composant à débuter.
     * @return L'instance actuelle du bâtisseur (pour le chaînage).
     */
    public IcalBuilder begin(Component component) {
        add(Name.BEGIN, component.name());
        componentsStack.add(component);
        return this;
    }

    /**
     * Termine le composant le plus récemment commencé en ajoutant une ligne END correspondante.
     *
     * @return L'instance actuelle du bâtisseur (pour le chaînage).
     * @throws IllegalArgumentException Si aucun composant n'a été commencé auparavant.
     */
    public IcalBuilder end() {
        checkArgument(!componentsStack.isEmpty());
        Component component = componentsStack.removeLast();
        add(Name.END, component.name());
        return this;
    }

    /**
     * Construit et retourne l'événement iCalendar sous forme de chaîne de caractères.
     *
     * @return La chaîne représentant l'événement iCalendar construit.
     * @throws IllegalArgumentException Si un composant a été commencé mais non terminé.
     */
    public String build() {
        checkArgument(componentsStack.isEmpty());
        return calendarBuilder.toString();
    }
}