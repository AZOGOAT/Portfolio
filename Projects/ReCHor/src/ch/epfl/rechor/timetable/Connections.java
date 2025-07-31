package ch.epfl.rechor.timetable;

/**
 * Représente des liaisons indexées dans l'horaire de transport public.
 * <p>
 * Les liaisons sont ordonnées par heure de départ décroissante pour les besoins de l'algorithme de recherche de voyages.
 * Toutes les méthodes lèvent une {@link IndexOutOfBoundsException} si l'index fourni est invalide,
 * c'est-à-dire inférieur à 0 ou supérieur ou égal à la taille retournée par {@code size()}.
 * <p>
 * Les index des arrêts de départ et d'arrivée retournés par {@code depStopId} et {@code arrStopId}
 * peuvent désigner des gares ou des voies/quais. Un index inférieur au nombre de gares représente une gare,
 * sinon il représente une voie/quai, dont l'index peut être obtenu en soustrayant le nombre de gares existant dans l'horaire.
 *
 * @author Omar Ziyad Azgaoui (379136)
 * @author Mohamed Amine Goulahsen (400232)
 */
public interface Connections extends Indexed {

    /**
     * Retourne l'index de l'arrêt de départ de la liaison d'index donné.
     *
     * @param id l'index de la liaison.
     * @return l'index de l'arrêt de départ de la liaison donnée.
     * @throws IndexOutOfBoundsException si l'index est invalide.
     */
    int depStopId(int id);

    /**
     * Retourne l'heure de départ de la liaison d'index donné, exprimée en minutes après minuit.
     *
     * @param id l'index de la liaison.
     * @return l'heure de départ en minutes après minuit.
     * @throws IndexOutOfBoundsException si l'index est invalide.
     */
    int depMins(int id);

    /**
     * Retourne l'index de l'arrêt d'arrivée de la liaison d'index donné.
     *
     * @param id l'index de la liaison.
     * @return l'index de l'arrêt d'arrivée de la liaison donnée.
     * @throws IndexOutOfBoundsException si l'index est invalide.
     */
    int arrStopId(int id);

    /**
     * Retourne l'heure d'arrivée de la liaison d'index donné, exprimée en minutes après minuit.
     *
     * @param id l'index de la liaison.
     * @return l'heure d'arrivée en minutes après minuit.
     * @throws IndexOutOfBoundsException si l'index est invalide.
     */
    int arrMins(int id);

    /**
     * Retourne l'index de la course à laquelle appartient la liaison d'index donné.
     *
     * @param id l'index de la liaison.
     * @return l'index de la course associée à la liaison donnée.
     * @throws IndexOutOfBoundsException si l'index est invalide.
     */
    int tripId(int id);

    /**
     * Retourne la position de la liaison d'index donné dans la course à laquelle elle appartient.
     * <p>
     * La première liaison d'une course a l'index 0.
     *
     * @param id l'index de la liaison.
     * @return la position de la liaison dans la course associée.
     * @throws IndexOutOfBoundsException si l'index est invalide.
     */
    int tripPos(int id);

    /**
     * Retourne l'index de la liaison suivant celle d'index donné dans la course à laquelle elle appartient.
     * <p>
     * Si la liaison donnée est la dernière de la course, retourne l'index de la première liaison de la course.
     *
     * @param id l'index de la liaison.
     * @return l'index de la liaison suivante ou de la première liaison de la course.
     * @throws IndexOutOfBoundsException si l'index est invalide.
     */
    int nextConnectionId(int id);
}