package ch.epfl.rechor.journey;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.function.LongConsumer;

import static ch.epfl.rechor.journey.PackedCriteria.*;

/**
 * Représente une frontière de Pareto de critères d'optimisation.
 * <p>
 * Cette classe immuable stocke les tuples de la frontière sous forme empaquetée
 * dans un tableau de type {@code long[]} privé.
 *
 * @author Omar Ziyad Azgaoui (379136)
 * @author Mohamed Amine Goulahsen (400232)
 */
public final class ParetoFront {

    /**
     * Frontière de Pareto vide.
     */
    public static final ParetoFront EMPTY = new ParetoFront(new long[0]);
    /**
     * Tableau contenant les critères empaquetés.
     */
    private final long[] packedCriteria;

    /**
     * Constructeur privé prenant un tableau de critères empaquetés.
     *
     * @param packedCriteria tableau contenant les critères empaquetés.
     *                       Il n'est pas copié, donc son immuabilité est garantie.
     */
    private ParetoFront(long[] packedCriteria) {
        this.packedCriteria = packedCriteria;
    }

    /**
     * Retourne la taille de la frontière de Pareto.
     *
     * @return le nombre de tuples contenus dans la frontière.
     */
    public int size() {
        return packedCriteria.length;
    }

    /**
     * Retourne les critères d'optimisation empaquetés correspondant à l'heure d'arrivée
     * et au nombre de changements donnés.
     *
     * @param arrMins l'heure d'arrivée en minutes après minuit.
     * @param changes le nombre de changements.
     * @return les critères d'optimisation empaquetés.
     * @throws NoSuchElementException si les critères ne font pas partie de la frontière.
     */
    public long get(int arrMins, int changes) {
        for (long criteria : packedCriteria) {
            if (PackedCriteria.arrMins(criteria) == arrMins &&
                PackedCriteria.changes(criteria) == changes) {
                return criteria;
            }
        }
        throw new NoSuchElementException();
    }

    /**
     * Applique l'action donnée à chacun des critères de la frontière.
     *
     * @param action action à appliquer à chaque critère.
     */
    public void forEach(LongConsumer action) {
        for (long criteria : packedCriteria) {
            action.accept(criteria);
        }
    }

    /**
     * Retourne une représentation textuelle de la frontière de Pareto.
     *
     * @return une chaîne décrivant les critères de la frontière de manière lisible.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("ParetoFront:");
        for (long criteria : packedCriteria) {
            int depMins = PackedCriteria.hasDepMins(criteria)
                          ? PackedCriteria.depMins(criteria)
                          : -1;
            int arrMins = PackedCriteria.arrMins(criteria);
            int changes = PackedCriteria.changes(criteria);
            sb.append(String.format("\n[Départ: %s, Arrivée: %s, Changements: %d]",
                                    (depMins == -1 ? "N/A" : depMins + " min"),
                                    arrMins + " min",
                                    changes));
        }
        return sb.toString();
    }

    /**
     * La classe publique et statique imbriquée Builder représente un bâtisseur de frontière de Pareto.
     * La frontière en cours de construction est stockée dans un tableau de type long[],
     * redimensionné si nécessaire en suivant les conseils de programmation fournis.
     *
     * @author Omar Ziyad Azgaoui (379136)
     * @author Mohamed Amine Goulahsen (400232)
     */
    public static class Builder {
        /**
         * Capacité initiale du tableau de stockage.
         */
        private static final int INITIAL_CAPACITY = 2;
        /**
         * Facteur de redimensionnement du tableau.
         */
        private static final double RESIZE_FACTOR = 1.5;
        /**
         * Bâtisseur permettant de construire une frontière de Pareto.
         */
        private long[] criterias;
        /**
         * Taille effective de la frontière.
         */
        private int size;

        /**
         * Construit un bâtisseur dont la frontière en cours de construction est vide.
         */
        public Builder() {
            criterias = new long[INITIAL_CAPACITY];
            size = 0;
        }

        /**
         * Construit un bâtisseur ayant les mêmes attributs que celui reçu en argument.
         *
         * @param that le bâtisseur à copier
         */
        public Builder(Builder that) {
            // on double la capacité pour éviter de devoir re-redimensionner immédiatement
            int capacity = Math.max((int)(that.size * RESIZE_FACTOR), INITIAL_CAPACITY);
            this.criterias = Arrays.copyOf(that.criterias, capacity);
            this.size = that.size;
        }

        /**
         * Vérifie si la frontière en cours de construction est vide.
         *
         * @return vrai si la frontière est vide, faux sinon
         */
        public boolean isEmpty() {
            return size == 0;
        }

        /**
         * Vide la frontière en supprimant tous ses éléments.
         *
         * @return ce bâtisseur après avoir supprimé tous les éléments de la frontière
         */
        public Builder clear() {
            size = 0;
            return this;
        }

        /**
         * Ajoute un tuple de critères empaquetés à la frontière.
         * L'ajout est effectué uniquement si le tuple n'est pas dominé ou égal à un autre présent.
         * Tous les tuples dominés par le nouveau sont supprimés.
         *
         * @param packedTuple le tuple de critères empaquetés à ajouter
         * @return ce bâtisseur après l'ajout
         */
        public Builder add(long packedTuple) {
            // Crée une version du tuple sans sa charge utile pour comparer uniquement les critères.
            long newTuple = withPayload(packedTuple, 0);

            // Étape 1 : Vérifie si le nouveau tuple est dominé ou égal à un tuple déjà présent.
            // Si c'est le cas, il n'est pas ajouté à la frontière.
            for (int i = 0; i < size; i++) {
                if (dominatesOrIsEqual(withPayload(criterias[i], 0), packedTuple)) {
                    return this;
                }
            }

            // Étape 2 : Supprime les tuples existants dominés par le nouveau.
            // On déplace les tuples non dominés vers le début du tableau.
            int dst = 0;
            for (int src = 0; src < size; src++) {
                if (dominatesOrIsEqual(packedTuple, withPayload(criterias[src], 0))) {
                    continue; // On saute les tuples dominés.
                }
                if (dst != src) {
                    criterias[dst] = criterias[src];

                }
                dst++;
            }

            // Étape 3 : Trouve la posit° d’insert° du nouveau tuple en respectant l’ordre lexico°.
            int pos = 0;
            while (pos < dst && withPayload(criterias[pos], 0) < newTuple) {
                pos++;
            }

            // Étape 4 : Si le tableau est trop petit pour accueillir le nouveau tuple, on l’agrandit.
            int newSize = dst + 1;
            if (newSize > criterias.length) {
                int newCapacity = (int) (criterias.length * RESIZE_FACTOR);
                long[] newArray = new long[newCapacity];
                System.arraycopy(criterias, 0, newArray, 0, dst);
                criterias = newArray;
            }

            // Étape 5 : Décale les éléments après la position d'insertion d’une place vers la droite.
            System.arraycopy(criterias, pos, criterias, pos + 1, dst - pos);

            // Étape 6 : Insère le nouveau tuple à la bonne position.
            criterias[pos] = packedTuple;
            size = newSize;

            return this;
        }

        /**
         * Ajoute à la frontière un tuple de critères augmentés avec l'heure d'arrivée,
         * le nombre de changements et la charge utile spécifiés, mais sans heure de départ.
         * L'ajout suit la même logique que la méthode add(long).
         *
         * @param arrMins l'heure d'arrivée en minutes
         * @param changes le nombre de changements
         * @param payload la charge utile
         * @return ce bâtisseur après l'ajout
         */
        public Builder add(int arrMins, int changes, int payload) {
            add(pack(arrMins, changes, payload));
            return this;
        }

        /**
         * Ajoute à la frontière tous les tuples présents dans la frontière en cours de construction par le bâtisseur donné.
         *
         * @param that le bâtisseur contenant les tuples à ajouter
         * @return ce bâtisseur après l'ajout
         */
        public Builder addAll(Builder that) {
            for (int i = 0; i < that.size; ++i) {
                this.add(that.criterias[i]);
            }
            return this;
        }

        /**
         * Vérifie si tous les tuples de la frontière donnée, avec l'heure de départ spécifiée,
         * sont dominés par au moins un tuple de ce bâtisseur.
         *
         * @param that    le bâtisseur contenant les tuples à comparer
         * @param depMins l'heure de départ en minutes
         * @return vrai si tous les tuples sont dominés, faux sinon
         */
        public boolean fullyDominates(Builder that, int depMins) {
            for (int i = 0; i < that.size; ++i) {
                long newTuple = withDepMins(that.criterias[i], depMins);
                boolean dominated = false;
                for (int j = 0; j < size; ++j) {
                    if (dominatesOrIsEqual(criterias[j], newTuple)) {
                        dominated = true;
                        break;
                    }
                }
                if (!dominated) {
                    return false;
                }
            }
            return true;
        }

        /**
         * Applique une action à chaque tuple de la frontière, de la même manière que la méthode forEach de ParetoFront.
         *
         * @param action l'action à appliquer à chaque tuple
         */
        public void forEach(LongConsumer action) {
            for (int i = 0; i < size; i++) {
                action.accept(criterias[i]);
            }
        }

        /**
         * Construit et retourne la frontière de Pareto en cours de construction.
         *
         * @return la frontière de Pareto construite
         */
        public ParetoFront build() {
            return new ParetoFront(Arrays.copyOf(criterias, size));
        }
    }
}