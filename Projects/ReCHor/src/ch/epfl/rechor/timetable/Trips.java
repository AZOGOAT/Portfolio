package ch.epfl.rechor.timetable;

/**
 * Représente des courses de transport public indexées.
 * <p>
 * Cette interface permet d'obtenir l'index de la ligne à laquelle appartient une course,
 * ainsi que sa destination finale.
 * Toutes les méthodes lèvent une {@link IndexOutOfBoundsException} si l'index fourni est invalide,
 * c'est-à-dire inférieur à 0 ou supérieur ou égal à la taille retournée par {@code size()}.
 *
 * @author Omar Ziyad Azgaoui (379136)
 * @author Mohamed Amine Goulahsen (400232)
 */
public interface Trips extends Indexed {

    /**
     * Retourne l'index de la ligne à laquelle la course d'index donné appartient.
     *
     * @param id l'index de la course.
     * @return l'index de la ligne associée à la course d'index donné.
     * @throws IndexOutOfBoundsException si l'index est invalide.
     */
    int routeId(int id);

    /**
     * Retourne le nom de la destination finale de la course d'index donné.
     *
     * @param id l'index de la course.
     * @return le nom de la destination finale de la course d'index donné.
     * @throws IndexOutOfBoundsException si l'index est invalide.
     */
    String destination(int id);
}