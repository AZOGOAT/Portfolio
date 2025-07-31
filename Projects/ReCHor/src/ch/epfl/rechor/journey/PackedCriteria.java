package ch.epfl.rechor.journey;

import static ch.epfl.rechor.Preconditions.checkArgument;

/**
 * Classe utilitaire non instanciable permettant de manipuler des critères
 * d'optimisation augmentés, empaquetés dans des valeurs de type {@code long}.
 *
 * <p>Les critères augmentés contiennent :</p>
 * <ul>
 *   <li>Les 32 bits de poids fort représentant les critères d'optimisation :</li>
 *   <ul>
 *     <li>Le bit 63 : toujours égal à 0 pour garantir le signe positif (1 bit).</li>
 *     <li>Les bits 62 à 51 : heure de départ en minutes (12 bits).</li>
 *     <li>Les bits 50 à 39 : heure d'arrivée en minutes (12 bits).</li>
 *     <li>Les bits 38 à 32 : nombre de changements (7 bits).</li>
 *   </ul>
 *   <li>Les 32 bits de poids faible représentant la charge utile (payload), permettant de stocker des informations supplémentaires nécessaires pour la reconstruction des voyages.</li>
 * </ul>
 *
 * <p>Dans certains cas, les critères ne contiennent pas d'heure de départ.
 * Cette absence est représentée en fixant à 0 les bits correspondant à l'heure de départ.</p>
 *
 * <p>Les heures de départ et d'arrivée sont exprimées en minutes écoulées depuis minuit,
 * et sont valides si elles sont comprises entre -240 (inclus) et 2880 (exclu).
 * Ces valeurs sont translatées pour garantir des représentations positives, simplifiant ainsi les manipulations bit à bit.</p>
 *
 * @author Omar Ziyad Azgaoui (379136)
 * @author Mohamed Amine Goulahsen (400232)
 */
public final class PackedCriteria {

    // --- Intervalle valide pour les heures exprimées en minutes ---
    private static final int ORIGINE_MINS = -240; // Décalage pour garantir une représentat° tjrs >0
    private static final int MAX_HEURE_ARR = 2880; // 48h après minuit (=> 48*60=2880)

    // --- Nombre de bits par champ ---
    private static final int NB_BITS_CHARGE_UTILE = 32;
    private static final int NB_BITS_HEURE = 12;
    private static final int NB_BITS_CHGMTS = 7;

    // --- Valeur max encodable dans 12 bits (utilisée pour encoder l'heure de départ) ---
    private static final int MAX_DEP_TIME = (1 << NB_BITS_HEURE) - 1; // 4095

    // --- Décalages de chaque champ dans la valeur empaquetée ---
    private static final int OFFSET_HEURE_ARRIVE = NB_BITS_CHARGE_UTILE + NB_BITS_CHGMTS;
    private static final int OFFSET_HEURE_DEPART = OFFSET_HEURE_ARRIVE + NB_BITS_HEURE;

    // --- Masques de champ ---
    private static final long MASQUE_CHARGE_UTILE = (1L << NB_BITS_CHARGE_UTILE) - 1; // 0xFFFFFFFF
    private static final int MASQUE_12_BITS = (1 << NB_BITS_HEURE) - 1; // 0xFFF
    private static final int MASQUE_7_BITS = (1 << NB_BITS_CHGMTS) - 1; // 0x7F

    /**
     * Constructeur privé pour empêcher l’instanciation de la classe.
     */
    private PackedCriteria() {
    }

    /**
     * Empaquette les critères sous forme d'une valeur de type {@code long}.
     *
     * @param arrMins l'heure d'arrivée en minutes écoulées depuis minuit.
     * @param changes le nombre de changements (doit tenir sur 7 bits).
     * @param payload la charge utile associée.
     * @return les critères empaquetés sous forme d'un entier long.
     * @throws IllegalArgumentException si :
     *                                  <ul>
     *                                    <li>Les minutes d'arrivée ({@code arrMins}) sont en dehors de l'intervalle [-240, 2880].</li>
     *                                    <li>Le nombre de changements ({@code payload}) ne tient pas en 7 bits (~plus de 128 changements).</li>
     *                                  </ul>
     */
    public static long pack(int arrMins, int changes, int payload) {
        // Vérification des limites
        checkArgument(arrMins >= ORIGINE_MINS && arrMins < MAX_HEURE_ARR);
        checkArgument((changes >> NB_BITS_CHGMTS) == 0);

        // Masquage correct et encodage
        int arrMinsNormalise = arrMins - ORIGINE_MINS;
        long arrMinsEnc = (long) extractField(arrMinsNormalise, MASQUE_12_BITS) <<
                          OFFSET_HEURE_ARRIVE;
        long changesEnc = (long) extractField(changes, MASQUE_7_BITS) << NB_BITS_CHARGE_UTILE;
        long payloadEnc = Integer.toUnsignedLong(payload);

        return arrMinsEnc | changesEnc | payloadEnc;
    }

    /**
     * Indique si les critères empaquetés incluent une heure de départ.
     *
     * @param criteria les critères empaquetés.
     * @return {@code true} si une heure de départ est incluse, {@code false} sinon.
     */
    public static boolean hasDepMins(long criteria) {
        long criteriaDecaleDep = criteria >> OFFSET_HEURE_DEPART;
        return extractField(criteriaDecaleDep, MASQUE_12_BITS) != 0;
    }

    /**
     * Retourne l'heure de départ en minutes (après minuit).
     *
     * @param criteria les critères empaquetés.
     * @return l'heure de départ en minutes.
     * @throws IllegalArgumentException si {@code criteria} n'inclue aucune heure de départ.
     */
    public static int depMins(long criteria) {
        checkArgument(hasDepMins(criteria));
        long criteriaDecaleDep = criteria >> OFFSET_HEURE_DEPART;
        int depEnc = extractField(criteriaDecaleDep, MASQUE_12_BITS);
        return encodeDepMins(depEnc);
    }

    /**
     * Retourne l'heure d'arrivée en minutes (après minuit).
     *
     * @param criteria les critères empaquetés.
     * @return l'heure d'arrivée en minutes.
     */
    public static int arrMins(long criteria) {
        long criteriaDecaleArr = criteria >> OFFSET_HEURE_ARRIVE;
        return extractField(criteriaDecaleArr, MASQUE_12_BITS) + ORIGINE_MINS;
    }

    /**
     * Retourne le nombre de changements des critères empaquetés.
     *
     * @param criteria les critères empaquetés.
     * @return le nombre de changements.
     */
    public static int changes(long criteria) {
        long criteriaDecaleChgmts = criteria >> NB_BITS_CHARGE_UTILE;
        return extractField(criteriaDecaleChgmts, MASQUE_7_BITS);
    }

    /**
     * Retourne la "charge utile" associée aux critères empaquetés.
     *
     * @param criteria les critères empaquetés.
     * @return la charge utile sous forme d'un entier.
     */
    public static int payload(long criteria) {
        return extractField(criteria, MASQUE_CHARGE_UTILE);
    }

    /**
     * Vérifie si les premiers critères dominent ou sont égaux aux seconds.
     *
     * @param criteria1 les premiers critères empaquetés.
     * @param criteria2 les seconds critères empaquetés.
     * @return {@code true} si les premiers dominent ou sont égaux, {@code false} sinon.
     * @throws IllegalArgumentException si un ensemble ({@code criteria1}) possède une heure de départ et l'autre ({@code criteria2}) non.
     */
    public static boolean dominatesOrIsEqual(long criteria1, long criteria2) {
        checkArgument(hasDepMins(criteria1) == hasDepMins(criteria2));
        boolean baseOk = arrMins(criteria1) <= arrMins(criteria2) &&
                         changes(criteria1) <= changes(criteria2);
        return hasDepMins(criteria1) ? baseOk && depMins(criteria1) >= depMins(criteria2) : baseOk;
    }

    /**
     * @param criteria les critères empaquetés.
     * @return les critères sans l'heure de départ.
     */
    public static long withoutDepMins(long criteria) {
        return replacePackedField(criteria, OFFSET_HEURE_DEPART, MASQUE_12_BITS, 0);
    }

    /**
     * @param criteria les critères empaquetés.
     * @param depMins1 l'heure de départ à ajouter (en minutes).
     * @return les critères avec l'heure de départ ajoutée.
     */
    public static long withDepMins(long criteria, int depMins1) {
        int depEnc = encodeDepMins(depMins1);
        return replacePackedField(criteria, OFFSET_HEURE_DEPART, MASQUE_12_BITS, depEnc);
    }

    /**
     * Incrémente le nombre de changements des critères empaquetés de 1.
     *
     * @param criteria les critères empaquetés.
     * @return les critères avec un changement supplémentaire.
     */
    public static long withAdditionalChange(long criteria) {
        int newChanges = changes(criteria) + 1;
        return replacePackedField(criteria, NB_BITS_CHARGE_UTILE, MASQUE_7_BITS, newChanges);
    }

    /**
     * @param criteria les critères empaquetés.
     * @param payload1 la nouvelle charge utile.
     * @return les critères avec la charge utile.
     */
    public static long withPayload(long criteria, int payload1) {
        return replacePackedField(criteria,
                                  0,
                                  MASQUE_CHARGE_UTILE,
                                  Integer.toUnsignedLong(payload1));
    }

    /**
     * Encode une heure de départ en minutes dans sa forme empaquetée.
     *
     * @param depMins l'heure de départ (en minutes depuis minuit).
     * @return l'encodage entier correspondant.
     */
    private static int encodeDepMins(int depMins) {
        return MAX_DEP_TIME - depMins + ORIGINE_MINS;
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

    /**
     * Remplace un champ donné dans une valeur empaquetée représentant des critères de voyage.
     * <p>
     * Cette méthode efface les bits d’un champ (ex. heure d’arrivée, nombre de changements, payload, etc.)
     * à une position donnée, puis insère une nouvelle valeur dans ce champ.
     *
     * @param packed   la valeur de critères empaquetés à modifier.
     * @param offset   le décalage en bits à partir du bit de poids faible où commence le champ.
     * @param mask     le masque correspondant à la taille du champ (non décalé).
     * @param newValue la nouvelle valeur à insérer dans le champ.
     * @return une nouvelle valeur de critères empaquetés avec le champ remplacé.
     */
    private static long replacePackedField(long packed, int offset, long mask, long newValue) {
        long cleared = packed & ~(mask << offset);
        long inserted = (newValue & mask) << offset;
        return cleared | inserted;
    }
}