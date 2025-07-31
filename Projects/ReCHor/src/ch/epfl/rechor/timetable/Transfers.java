package ch.epfl.rechor.timetable;

import java.util.NoSuchElementException;

/**
 * Représente des changements indexés dans l'horaire de transport public.
 * <p>
 * Les changements ne sont possibles qu'entre (ou au sein de) gares et ne concernent pas les voies ou quais.
 * Toutes les méthodes lèvent une {@link IndexOutOfBoundsException} si l'index fourni est invalide,
 * c'est-à-dire inférieur à 0 ou supérieur ou égal à la taille retournée par {@code size()}.
 *
 * @author Omar Ziyad Azgaoui (379136)
 * @author Mohamed Amine Goulahsen (400232)
 */
public interface Transfers extends Indexed {

    /**
     * Retourne l'index de la gare de départ du changement d'index donné.
     *
     * @param id l'index du changement.
     * @return l'index de la gare de départ du changement donné.
     * @throws IndexOutOfBoundsException si l'index est invalide.
     */
    int depStationId(int id);

    /**
     * Retourne la durée, en minutes, du changement d'index donné.
     *
     * @param id l'index du changement.
     * @return la durée du changement en minutes.
     * @throws IndexOutOfBoundsException si l'index est invalide.
     */
    int minutes(int id);

    /**
     * Retourne l'intervalle empaqueté des index des changements dont la gare d'arrivée est celle d'index donné.
     *
     * @param stationId l'index de la gare d'arrivée.
     * @return l'intervalle empaqueté des index des changements vers cette gare.
     * @throws IndexOutOfBoundsException si l'index est invalide.
     */
    int arrivingAt(int stationId);

    /**
     * Retourne la durée, en minutes, du changement entre les deux gares d'index donnés.
     *
     * @param depStationId l'index de la gare de départ.
     * @param arrStationId l'index de la gare d'arrivée.
     * @return la durée du changement en minutes.
     * @throws IndexOutOfBoundsException si l'un des index est invalide.
     * @throws NoSuchElementException    si aucun changement n'est possible entre ces deux gares.
     */
    int minutesBetween(int depStationId, int arrStationId);
}