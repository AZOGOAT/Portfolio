package ch.epfl.rechor;

import static ch.epfl.rechor.Bits32_24_8.unpack24;
import static ch.epfl.rechor.Bits32_24_8.unpack8;
import static ch.epfl.rechor.Preconditions.checkArgument;

/**
 * Classe utilitaire non instanciable offrant des méthodes pour manipuler
 * des intervalles d'entiers empaquetés dans un entier de 32 bits.
 *
 * <p>Un intervalle est représenté de manière empaquetée en utilisant :</p>
 * <ul>
 *   <li>Les 24 bits de poids fort pour la borne inférieure de l'intervalle.</li>
 *   <li>Les 8 bits de poids faible pour la longueur de l'intervalle.</li>
 * </ul>
 *
 * @author Omar Ziyad Azgaoui (379136)
 * @author Mohamed Amine Goulahsen (400232)
 */
public class PackedRange {

    private static final int NB_BITS_24 = 24;
    private static final int NB_BITS_8 = 8;

    /**
     * Constructeur privé empêchant l'instanciation de la classe.
     */
    private PackedRange() {
    }

    /**
     * Empaquette un intervalle d'entiers dans un entier de 32 bits.
     *
     * @param startInclusive la borne inférieure de l'intervalle (inclus).
     * @param endExclusive   la borne supérieure de l'intervalle (exclu).
     * @return un entier de 32 bits représentant l'intervalle empaqueté,
     * avec la borne inférieure dans les 24 bits de poids fort et
     * la longueur de l'intervalle dans les 8 bits de poids faible.
     * @throws IllegalArgumentException si la borne inférieure ({@code startInclusive}) utilise plus de 24 bits
     *                                  ou si la longueur ({@code length}) utilise plus de 8 bits.
     */
    public static int pack(int startInclusive, int endExclusive) {
        int length = endExclusive - startInclusive;
        checkArgument((startInclusive >> NB_BITS_24) == 0 && (length >> NB_BITS_8) == 0);
        return Bits32_24_8.pack(startInclusive, length);
    }

    /**
     * Retourne la longueur de l'intervalle d'entiers empaqueté.
     *
     * @param interval l'entier de 32 bits représentant l'intervalle empaqueté.
     * @return la longueur de l'intervalle sous forme d'un entier positif non signé.
     */
    public static int length(int interval) {
        return unpack8(interval);
    }

    /**
     * Retourne le début de l'intervalle d'entiers empaqueté donné.
     *
     * @param interval l'entier de 32 bits représentant l'intervalle empaqueté.
     * @return le plus petit entier inclus dans l'intervalle.
     */
    public static int startInclusive(int interval) {
        return unpack24(interval);
    }

    /**
     * @param interval l'entier de 32 bits représentant l'intervalle empaqueté.
     * @return le plus petit entier strictement supérieur à tous les entiers de l'intervalle.
     */
    public static int endExclusive(int interval) {
        return startInclusive(interval) + length(interval);
    }
}