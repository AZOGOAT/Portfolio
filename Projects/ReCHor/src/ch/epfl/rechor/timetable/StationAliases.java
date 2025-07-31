package ch.epfl.rechor.timetable;

/**
 * Représente les noms alternatifs des gares dans l'horaire de transport public.
 * <p>
 * Cette interface permet d'obtenir les noms alternatifs ainsi que leur correspondance
 * avec les noms officiels des gares.
 * Toutes les méthodes lèvent une {@link IndexOutOfBoundsException} si l'index fourni est invalide,
 * c'est-à-dire inférieur à 0 ou supérieur ou égal à la taille retournée par {@code size()}.
 *
 * @author Omar Ziyad Azgaoui (379136)
 * @author Mohamed Amine Goulahsen (400232)
 */
public interface StationAliases extends Indexed {

    /**
     * Retourne le nom alternatif de la gare d'index donné.
     *
     * @param id l'index du nom alternatif.
     * @return le nom alternatif de la gare d'index donné.
     * @throws IndexOutOfBoundsException si l'index est invalide.
     */
    String alias(int id);

    /**
     * Retourne le nom de la gare correspondant au nom alternatif d'index donné.
     *
     * @param id l'index du nom alternatif.
     * @return le nom de la gare correspondant au nom alternatif donné.
     * @throws IndexOutOfBoundsException si l'index est invalide.
     */
    String stationName(int id);
}