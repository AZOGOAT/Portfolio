package ch.epfl.rechor;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Représente une valeur JSON pouvant être atomique (chaîne ou nombre)
 * ou composite (objet ou tableau), conformément à la structure décrite dans l'étape 8.
 * <p>
 * Cette interface scelle les quatre sous-types de valeurs JSON utilisés :
 * <ul>
 *   <li>{@link Json.JString} pour les chaînes de caractères</li>
 *   <li>{@link Json.JNumber} pour les nombres</li>
 *   <li>{@link Json.JObject} pour les objets JSON</li>
 *   <li>{@link Json.JArray} pour les tableaux JSON</li>
 * </ul>
 *
 * @author Omar Ziyad Azgaoui (379136)
 * @author Mohamed Amine Goulahsen (400232)
 */
public sealed interface Json {

    /**
     * Retourne la représentation textuelle de la valeur JSON selon la syntaxe standard.
     * <p>
     * Cette représentation dépend du type concret de la valeur JSON :
     * <ul>
     *   <li>{@link Json.JString#toString()} — une chaîne est entourée de guillemets ;</li>
     *   <li>{@link Json.JNumber#toString()} — un nombre est affiché selon la syntaxe des {@code double} Java ;</li>
     *   <li>{@link Json.JArray#toString()} — un tableau est entouré de crochets et ses éléments sont séparés par des virgules ;</li>
     *   <li>{@link Json.JObject#toString()} — un objet est entouré d’accolades et contient des paires clé/valeur séparées par des virgules ;</li>
     * </ul>
     *
     * @return la représentation JSON textuelle de la valeur
     */
    @Override
    String toString();

    /**
     * Représente un tableau JSON contenant une liste de valeurs JSON,
     * encadrées par des crochets et séparées par des virgules, selon le format JSON.
     *
     * @param elements les éléments du tableau
     * @author Omar Ziyad Azgaoui (379136)
     * @author Mohamed Amine Goulahsen (400232)
     */
    record JArray(List<Json> elements) implements Json {

        /**
         * Construit une instance de {@link JArray}.
         *
         * @param elements les éléments du tableau
         * @throws NullPointerException si la liste d’éléments est {@code null}
         */
        public JArray {
            Objects.requireNonNull(elements);
        }

        /**
         * Retourne la représentation textuelle du tableau JSON selon la syntaxe standard :
         * les éléments sont encadrés par des crochets et séparés par des virgules.
         * <p>
         * Par exemple, un tableau contenant deux chaînes vaut :
         * <pre>
         * ["Lausanne", "Genève"]
         * </pre>
         *
         * @return la représentation JSON textuelle du tableau
         */
        @Override
        public String toString() {
            return elements.stream()
                           .map(Json::toString)
                           .collect(Collectors.joining(",", "[", "]"));
        }
    }

    /**
     * Représente une chaîne de caractères JSON, encadrée par des guillemets.
     *
     * @param value la valeur de la chaîne
     * @author Omar Ziyad Azgaoui (379136)
     * @author Mohamed Amine Goulahsen (400232)
     */
    record JString(String value) implements Json {

        /**
         * Construit une instance de {@link JString}.
         *
         * @param value la valeur de la chaîne
         * @throws NullPointerException si la valeur est {@code null}
         */
        public JString {
            Objects.requireNonNull(value);
        }

        /**
         * Retourne la représentation textuelle de la chaîne JSON, entourée de guillemets.
         * <p>
         * Par exemple, la chaîne {@code Lausanne} devient :
         * <pre>
         * "Lausanne"
         * </pre>
         *
         * @return la représentation JSON textuelle de la chaîne
         */
        @Override
        public String toString() {
            return "\"" + value + "\"";
        }
    }

    /**
     * Représente un nombre JSON, encodé selon la syntaxe des {@code double} Java.
     *
     * @param value la valeur numérique
     *
     * @author Omar Ziyad Azgaoui (379136)
     * @author Mohamed Amine Goulahsen (400232)
     */
    record JNumber(double value) implements Json {
        /**
         * Retourne la représentation textuelle du nombre JSON,
         * selon la syntaxe des nombres à virgule flottante en Java.
         * <p>
         * Par exemple, la valeur {@code 6371.0} devient :
         * <pre>
         * 6371.0
         * </pre>
         *
         * @return la représentation JSON textuelle du nombre
         */
        @Override
        public String toString() {
            return Double.toString(value);
        }
    }

    /**
     * Représente un objet JSON composé de paires clé/valeur,
     * où chaque clé est une chaîne et chaque valeur est une valeur JSON.
     * L’objet est encadré par des accolades et ses paires sont séparées par des virgules.
     *
     * @param members les membres de l'objet
     * @author Omar Ziyad Azgaoui (379136)
     * @author Mohamed Amine Goulahsen (400232)
     */
    record JObject(Map<String, Json> members) implements Json {

        /**
         * Construit une instance de {@link JObject}.
         *
         * @param members les membres de l'objet
         * @throws NullPointerException si la map des membres est {@code null}
         */
        public JObject {
            Objects.requireNonNull(members);
        }

        /**
         * Retourne la représentation textuelle de l'objet JSON selon la syntaxe standard.
         * <p>
         * Par exemple, un objet contenant différentes sortes de valeurs JSON est représenté comme suit :
         * <pre>
         * {
         *   "name":"Earth",
         *   "radius":6371.0,
         *   "moons":["Moon"],
         *   "position":{"x":0.0,"y":0.0}
         * }
         * </pre>
         * où :
         * <ul>
         *   <li><b>"name"</b> a pour valeur une {@link JString}</li>
         *   <li><b>"radius"</b> a pour valeur une {@link JNumber}</li>
         *   <li><b>"moons"</b> a pour valeur un {@link JArray} contenant une chaîne</li>
         *   <li><b>"position"</b> a pour valeur un {@link JObject} imbriqué</li>
         * </ul>
         *
         * @return la représentation JSON textuelle de l'objet
         */
        @Override
        public String toString() {
            return members.entrySet().stream()
                          .map(e -> "\"" + e.getKey() + "\":" + e.getValue())
                          .collect(Collectors.joining(",", "{", "}"));
        }
    }
}