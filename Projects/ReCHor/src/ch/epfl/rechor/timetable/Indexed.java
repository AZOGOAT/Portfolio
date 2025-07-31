package ch.epfl.rechor.timetable;

/**
 * Représente des données indexées dans l'horaire de transport public.
 * <p>
 * Une donnée indexée est une entité stockée dans un tableau et identifiée
 * par un index allant de 0 (inclus) à la taille du tableau (exclue).
 *
 * @author Omar Ziyad Azgaoui (379136)
 * @author Mohamed Amine Goulahsen (400232)
 */
public interface Indexed {

    /**
     * Retourne la taille des données indexées.
     *
     * @return le nombre d'éléments des données indexées.
     */
    int size();
}