package ch.epfl.rechor;

/**
 * Classe utilitaire contenant des méthodes pour vérifier les préconditions des méthodes.
 * <p>
 * Cette classe ne peut pas être instanciée et est uniquement utilisée pour vérifier
 * si certaines conditions sont respectées avant l'exécution d'une méthode.
 * Si la condition spécifiée est fausse, une exception de type {@link IllegalArgumentException} est levée.
 * </p>
 *
 * @author Omar Ziyad Azgaoui (379136)
 * @author Mohamed Amine Goulahsen (400232)
 */
public final class Preconditions {

    /**
     * Constructeur privé pour empêcher l'instanciation de la classe.
     */
    private Preconditions() {
    }

    /**
     * Vérifie si la condition spécifiée est vraie.
     * <p>
     * Si la condition est fausse, lève une exception de type {@link IllegalArgumentException}.
     * </p>
     *
     * @param shouldBeTrue la condition devant être vraie
     * @throws IllegalArgumentException si {@code shouldBeTrue} est faux
     */
    public static void checkArgument(boolean shouldBeTrue) {
        if (!shouldBeTrue) {
            throw new IllegalArgumentException();
        }
    }
}