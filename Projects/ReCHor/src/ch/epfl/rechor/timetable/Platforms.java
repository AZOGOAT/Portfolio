package ch.epfl.rechor.timetable;

/**
 * Représente des voies ou quais indexés dans l'horaire de transport public.
 * <p>
 * Cette interface permet d'obtenir le nom des voies ou quais ainsi que la gare
 * à laquelle ils appartiennent.
 * Toutes les méthodes lèvent une {@link IndexOutOfBoundsException} si l'index fourni est invalide,
 * c'est-à-dire inférieur à 0 ou supérieur ou égal à la taille retournée par {@code size()}.
 *
 * @author Omar Ziyad Azgaoui (379136)
 * @author Mohamed Amine Goulahsen (400232)
 */
public interface Platforms extends Indexed {

    /**
     * Retourne le nom de la voie ou du quai de l'index donné.
     *
     * @param id l'index de la voie ou du quai.
     * @return le nom de la voie ou du quai, qui peut être vide.
     * @throws IndexOutOfBoundsException si l'index est invalide.
     */
    String name(int id);

    /**
     * Retourne l'index de la gare à laquelle appartient la voie ou le quai donné.
     *
     * @param id l'index de la voie ou du quai.
     * @return l'index de la gare associée.
     * @throws IndexOutOfBoundsException si l'index est invalide.
     */
    int stationId(int id);
}