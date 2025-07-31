package ch.epfl.rechor;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.UNICODE_CASE;

/**
 * Classe permettant de rechercher efficacement des arrêts à partir d'une requête de l'utilisateur.
 * <p>
 * Un arrêt peut être recherché à l’aide d’une requête composée de plusieurs sous-requêtes séparées par des espaces.
 * Un nom d’arrêt correspond à une requête s’il contient chacune des sous-requêtes, l’ordre n’étant pas important.
 * Le test est accent-insensible, et insensible à la casse si aucune majuscule n’est présente dans la sous-requête.
 * Les noms d’arrêts peuvent aussi être des alias d’autres noms principaux.
 * Les résultats sont triés par pertinence décroissante, puis par ordre lexicographique.
 * </p>
 *
 * @author Omar Ziyad Azgaoui (379136)
 * @author Mohamed Amine Goulahsen (400232)
 */
public final class StopIndex {

    // Facteurs de pondération utilisés dans le calcul de pertinence (début/fin de mot)
    private static final int FACTEUR_DEBUT_MOT = 4;
    private static final int FACTEUR_FIN_MOT = 2;

    // Pourcentage de longueur de match
    private static final double SCORE_POURCENTAGE = 100.0;

    // Table des équivalences de lettres pour une correspondance accent-insensible
    private static final Map<Character, String> EQUIVALENT_CHARS = Map.of('c', "cç",
                                                                          'a', "aáàâä",
                                                                          'e', "eéèêë",
                                                                          'i', "iíìîï",
                                                                          'o', "oóòôö",
                                                                          'u', "uúùûü");

    // Sépare lettres et non-lettres pour construire une regex accent-insensible
    private static final Pattern SPLITTER = Pattern.compile("\\p{L}+|\\P{L}+");

    // Sépare la requête utilisateur en sous-requêtes sur les espaces ("blancs" Unicode)
    private static final Pattern QUERY_SPLITTER = Pattern.compile("\\s+");


    // Noms principaux des arrêts (p. ex. Lausanne, Renens VD, etc.)
    private final List<String> mainStopNames;

    // Table des alias associant un nom alternatif à son nom principal (p. ex. Losanna → Lausanne)
    private final LinkedHashMap<String, String> aliasToMain;


    /**
     * Construit un index des arrêts à partir de leurs noms principaux et d’un dictionnaire d’alias.
     *
     * @param stopNames la liste des noms principaux des arrêts.
     * @param aliases la table des alias, associant chaque nom alternatif à un nom principal.
     */
    public StopIndex(List<String> stopNames, Map<String, String> aliases) {
        mainStopNames = List.copyOf(stopNames);
        aliasToMain = new LinkedHashMap<>(aliases);
    }

    /**
     * Retourne les noms des arrêts correspondant à une requête, triés par pertinence décroissante,
     * puis par ordre lexicographique, et limités au nombre maximal donné.
     * <p>
     * Une requête vide ou constituée uniquement d’espaces retourne une liste vide.
     * </p>
     *
     * @param query la requête de l’utilisateur.
     * @param maxResults le nombre maximal de résultats à retourner.
     * @return la liste des noms des arrêts correspondant à la requête.
     */
    public List<String> stopsMatching(String query, int maxResults) {
        // Si la requête est vide, retourner les noms principaux triés alphabétiquement
        if (query.isBlank()) {
            return mainStopNames.stream()
                                .sorted()
                                .limit(maxResults)
                                .toList();
        }

        // Construit la liste de motifs regex à partir de la requête utilisateur
        List<Pattern> patterns = buildQueryPatterns(query);

        // Ensemble des noms principaux pour un accès rapide via contains (O(1)) dans le tri secondaire
        Set<String> mainSet = Set.copyOf(mainStopNames);

        return Stream.concat(mainStopNames.stream(), aliasToMain.keySet().stream())
                     .flatMap(name -> scoredMatch(name,
                                                  aliasToMain.getOrDefault(name, name),
                                                  patterns))
                     .sorted(Comparator.comparingInt(ScoredMatch::score).reversed()
                                       .thenComparing(ScoredMatch::name,
                                                      Comparator.comparing(name -> !mainSet.contains(name))))
                     .map(ScoredMatch::name)
                     .distinct()
                     .limit(maxResults)
                     .toList();
    }

    /**
     * Construit les expressions régulières à partir de la requête de l’utilisateur.
     * <p>
     * Chaque sous-requête est transformée en une expression insensible aux accents et,
     * si elle ne contient pas de majuscule, insensible à la casse.
     * </p>
     * @param query la requête de l’utilisateur.
     * @return la liste des motifs à appliquer aux noms d’arrêts.
     */
    private static List<Pattern> buildQueryPatterns(String query) {
        return QUERY_SPLITTER
                // Découpe la requête selon les espaces en sous-requêtes (e.g. "mez vil" → ["mez", "vil"])
                .splitAsStream(query.strip())
                .map(subQuery -> {
                    boolean caseInsensitive = subQuery.chars().noneMatch(Character::isUpperCase);

                    String regex = accentInsensitiveRegex(subQuery, caseInsensitive);

                    int flags = caseInsensitive ? (CASE_INSENSITIVE | UNICODE_CASE) : 0;

                    return Pattern.compile(regex, flags);
                })
                // Retourne la liste des motifs regex résultants
                .toList();
    }

    /**
     * Transforme une sous-requête en une regex insensible aux accents (et parfois à la casse).
     * <p>
     * La sous-requête est d'abord découpée en segments de lettres et non-lettres. Ensuite, chaque
     * caractère est converti en une classe de caractères comprenant toutes ses variantes accentuées
     * (pour les lettres), ou en une séquence échappée (pour les symboles).
     * </p>
     *
     * @param subQuery la sous-requête textuelle de l'utilisateur.
     * @param caseInsensitive vrai si la recherche doit être insensible à la casse.
     * @return l'expression régulière correspondante.
     */
    private static String accentInsensitiveRegex(String subQuery, boolean caseInsensitive) {
        return Arrays.stream(SPLITTER.splitWithDelimiters(subQuery, 0))
                     // On convertit chaque segment en flot de caractères (avec les délimiteurs)
                     .flatMapToInt(String::chars)
                     .mapToObj(c -> {
                         char ch = (char) c;

                         if (Character.isLetter(ch)) {
                             char key = caseInsensitive ? Character.toLowerCase(ch) : ch;

                             String eq = EQUIVALENT_CHARS.getOrDefault(key, String.valueOf(ch));
                             return "[" + Pattern.quote(eq) + "]";
                         } else {
                             return Pattern.quote(String.valueOf(ch));
                         }
                     })
                     .collect(Collectors.joining());
    }

    /**
     * Tente d’associer un score de pertinence à un nom (alias ou principal),
     * en retournant un flux contenant une entrée nom/score si le score est présent.
     *
     * @param rawName le nom à tester (potentiellement un alias).
     * @param mainName le nom principal associé à {@code rawName}.
     * @param patterns les motifs construits depuis la requête.
     * @return un flux contenant une entrée si le score existe, vide sinon.
     */
    private static Stream<ScoredMatch> scoredMatch(String rawName,
                                                   String mainName,
                                                   List<Pattern> patterns) {
        OptionalInt score = relevanceScore(rawName, patterns);
        return score.stream().mapToObj(s -> new ScoredMatch(mainName, s));
    }


    /**
     * Calcule un score de pertinence pour un nom d’arrêt vis-à-vis d’une liste de motifs.
     * <p>
     * Un score est retourné seulement si tous les motifs sont trouvés dans le nom.
     * Cette méthode est utilisée par {@code scoredMatch}.
     * </p>
     *
     * @param stopName le nom d’arrêt à tester.
     * @param patterns la liste des motifs construits à partir de la requête.
     * @return un score de pertinence, ou {@code OptionalInt.empty()} si un motif manque.
     */
    private static OptionalInt relevanceScore(String stopName, List<Pattern> patterns) {
        int totalScore = 0;
        for (Pattern pattern : patterns) {
            Matcher matcher = pattern.matcher(stopName);
            if (!matcher.find())
                return OptionalInt.empty();
            totalScore += matchScore(stopName, matcher);
        }
        return OptionalInt.of(totalScore);
    }

    /**
     * Calcule le score de correspondance d’un motif trouvé dans un nom.
     * <p>
     * Tient compte de la longueur du match par rapport au nom complet et applique
     * un multiplicateur si le match est au début ou à la fin d’un mot.
     * Utilisée exclusivement dans {@code relevanceScore}.
     * </p>
     *
     * @param stopName le nom dans lequel se trouve le motif.
     * @param matcher le résultat du motif trouvé.
     * @return le score de correspondance pondéré.
     */
    private static int matchScore(String stopName, Matcher matcher) {
        int matchLength = matcher.end() - matcher.start();
        int baseScore = (int) Math.floor(SCORE_POURCENTAGE * matchLength / stopName.length());

        int multiplier = 1;
        if (isWordStart(stopName, matcher.start()))
            multiplier *= FACTEUR_DEBUT_MOT;
        if (isWordEnd(stopName, matcher.end()))
            multiplier *= FACTEUR_FIN_MOT;

        return baseScore * multiplier;
    }

    /**
     * Indique si une position est le début d’un mot dans un texte.
     * <p>
     * Utilisée par {@code matchScore} pour déterminer si un match commence
     * en début de mot, afin de pondérer le score.
     * </p>
     *
     * @param text le texte à examiner.
     * @param pos la position à tester.
     * @return {@code true} si c’est le début d’un mot ou du texte.
     */
    private static boolean isWordStart(String text, int pos) {
        return pos == 0 || !Character.isLetter(text.charAt(pos - 1));
    }

    /**
     * Indique si une position est la fin d’un mot dans un texte.
     * <p>
     * Utilisée par {@code matchScore} pour déterminer si un match se termine
     * en fin de mot, afin de pondérer le score.
     * </p>
     *
     * @param text le texte à examiner.
     * @param pos la position à tester.
     * @return {@code true} si c’est la fin d’un mot ou du texte.
     */
    private static boolean isWordEnd(String text, int pos) {
        return pos == text.length() ||
               (pos < text.length() && !Character.isLetter(text.charAt(pos)));
    }

    /**
     * Enregistrement représentant un arrêt avec son score de pertinence et son index.
     * <p>
     * Contient le nom principal de l’arrêt, le score calculé selon les critères de correspondance
     * et l’index original de l’arrêt pour départager les égalités de score.
     * </p>
     *
     * @param name le nom principal de l’arrêt.
     * @param score le score de pertinence attribué à l’arrêt.
     */
    private record ScoredMatch(String name, int score) {}
}