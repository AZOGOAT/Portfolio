package ch.epfl.rechor.timetable;

import ch.epfl.rechor.journey.Vehicle;

/**
 * Représente des lignes de transport public indexées.
 * <p>
 * Cette interface permet d'obtenir le type de véhicule ainsi que le nom des lignes.
 * Toutes les méthodes lèvent une {@link IndexOutOfBoundsException} si l'index fourni est invalide,
 * c'est-à-dire inférieur à 0 ou supérieur ou égal à la taille retournée par {@code size()}.
 *
 * @author Omar Ziyad Azgaoui (379136)
 * @author Mohamed Amine Goulahsen (400232)
 */
public interface Routes extends Indexed {

    /**
     * Retourne le type de véhicule desservant la ligne d'index donné.
     *
     * @param id l'index de la ligne.
     * @return le type de véhicule desservant la ligne d'index donné.
     * @throws IndexOutOfBoundsException si l'index est invalide.
     */
    Vehicle vehicle(int id);

    /**
     * Retourne le nom de la ligne d'index donné.
     *
     * @param id l'index de la ligne.
     * @return le nom de la ligne d'index donné (p. ex. IR 15).
     * @throws IndexOutOfBoundsException si l'index est invalide.
     */
    String name(int id);
}