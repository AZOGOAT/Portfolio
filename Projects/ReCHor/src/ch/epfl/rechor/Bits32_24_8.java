package ch.epfl.rechor;

import static ch.epfl.rechor.Preconditions.checkArgument;

/**
 * Classe utilitaire non instanciable fournissant des méthodes statiques
 * permettant de manipuler des valeurs empaquetées dans un entier 32 bits.
 * <p>
 * Elle permet de combiner deux valeurs entières, l'une sur 24 bits et l'autre sur 8 bits,
 * en un seul entier de 32 bits, ainsi que d'extraire chacune des valeurs empaquetées.
 *
 * @author Omar Ziyad Azgaoui (379136)
 * @author Mohamed Amine Goulahsen (400232)
 */
public class Bits32_24_8 {

    private static final int NB_BITS_8 = 8;
    private static final int NB_BITS_24 = 24;

    private static final int MASK_8_BITS = (1 << NB_BITS_8) - 1;   // 0xFF
    private static final int MASK_24_BITS = (1 << NB_BITS_24) - 1; // 0xFFFFFF

    /**
     * Constructeur privé pour empêcher l'instanciation de la classe.
     */
    private Bits32_24_8() {
    }

    /**
     * Empaquette deux valeurs entières dans un seul entier de 32 bits.
     *
     * @param bits24 une valeur entière sur 24 bits (les 8 bits supérieurs doivent être à 0).
     * @param bits8  une valeur entière sur 8 bits (les bits supérieurs doivent être à 0).
     * @return un entier de 32 bits contenant les {@code bits24} dans les 24 bits de poids fort
     * et les {@code bits8} dans les 8 bits de poids faible.
     * @throws IllegalArgumentException si {@code bits24} utilise plus de 24 bits ou {@code bits8} plus de 8 bits.
     */
    public static int pack(int bits24, int bits8) {
        checkArgument((bits24 >> NB_BITS_24) == 0 && (bits8 >> NB_BITS_8) == 0);
        return bits24 << NB_BITS_8 | bits8;
    }

    /**
     * Extrait les 24 bits de poids fort d'un entier de 32 bits.
     *
     * @param bits32 l'entier de 32 bits empaqueté.
     * @return les 24 bits de poids fort.
     */
    public static int unpack24(int bits32) {
        return extractField(bits32 >> NB_BITS_8, MASK_24_BITS);
    }

    /**
     * Extrait les 8 bits de poids faible d'un entier de 32 bits.
     *
     * @param bits32 l'entier de 32 bits empaqueté.
     * @return les 8 bits de poids faible.
     */
    public static int unpack8(int bits32) {
        return extractField(bits32, MASK_8_BITS);
    }

    /**
     * Extrait un champ de bits d'une valeur empaquetée à l’aide d’un masque donné.
     *
     * @param value la valeur empaquetée.
     * @param mask  le masque à appliquer pour extraire le champ.
     * @return la valeur du champ extrait.
     */
    private static int extractField(long value, long mask) {
        return (int) (value & mask);
    }
}