package ch.epfl.rechor.timetable;

/**
 * Représente des gares indexées dans l'horaire de transport public.
 * <p>
 * Cette interface fournit l'accès aux noms et aux coordonnées géographiques des gares.
 * Toutes les méthodes lèvent une {@link IndexOutOfBoundsException} si l'index fourni est invalide,
 * c'est-à-dire inférieur à 0 ou supérieur ou égal à la taille retournée par {@code size()}.
 *
 * @author Omar Ziyad Azgaoui (379136)
 * @author Mohamed Amine Goulahsen (400232)
 */
public interface Stations extends Indexed {

    /**
     * Retourne le nom de la gare d'index donné.
     *
     * @param id l'index de la gare.
     * @return le nom de la gare d'index donné.
     * @throws IndexOutOfBoundsException si l'index est invalide.
     */
    String name(int id);

    /**
     * Retourne la longitude, en degrés, de la gare d'index donné.
     *
     * @param id l'index de la gare.
     * @return la longitude de la gare d'index donné.
     * @throws IndexOutOfBoundsException si l'index est invalide.
     */
    double longitude(int id);

    /**
     * Retourne la latitude, en degrés, de la gare d'index donné.
     *
     * @param id l'index de la gare.
     * @return la latitude de la gare d'index donné.
     * @throws IndexOutOfBoundsException si l'index est invalide.
     */
    double latitude(int id);
}