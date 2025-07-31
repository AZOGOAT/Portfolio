package ch.epfl.rechor.journey;

import ch.epfl.rechor.Json;

import java.util.*;

import static ch.epfl.rechor.Json.*;

/**
 * Fournit une méthode utilitaire permettant de convertir un {@link Journey}
 * en document GeoJSON représentant le tracé du voyage sous forme d’une ligne brisée.
 * <p>
 * Le format de sortie respecte la spécification GeoJSON décrite dans l'étape 8 :
 * il s'agit d’un objet JSON contenant deux attributs :
 * <ul>
 *   <li>{@code "type"} avec pour valeur {@code "LineString"}</li>
 *   <li>{@code "coordinates"} contenant un tableau de points géographiques</li>
 * </ul>
 * Chaque point est une paire {@code [longitude, latitude]} arrondie à 5 décimales.
 * Les doublons consécutifs sont supprimés, conformément à la logique décrite à l’étape 8.
 *
 * @author Omar Ziyad Azgaoui (379136)
 * @author Mohamed Amine Goulahsen (400232)
 */
public class JourneyGeoJsonConverter {

    private static final int NB_COORDONNEES_GEO = 2;
    private static final double GEO_PRECISION = 100_000.0;

    private JourneyGeoJsonConverter() {
    }

    /**
     * Convertit un {@link Journey} en document GeoJSON compact,
     * représentant son tracé approximatif par une ligne brisée.
     * <p>
     * Le document retourné est un objet JSON de type {@code LineString}
     * dont les coordonnées sont :
     * <ul>
     *   <li>les points de départ et d’arrivée de chaque étape du voyage</li>
     *   <li>ainsi que les arrêts intermédiaires (s’il y en a)</li>
     * </ul>
     * Les coordonnées sont exprimées sous la forme {@code [longitude, latitude]},
     * arrondies à 5 décimales, et les doublons consécutifs sont éliminés.
     *
     * @param journey le voyage à convertir
     * @return un objet JSON de type GeoJSON représentant le tracé du voyage
     */
    public static Json toGeoJson(Journey journey) {
        List<Json> geoCoordinates = new ArrayList<>();
        Set<String> addedCoordinateStrings = new HashSet<>();

        for (Journey.Leg leg : journey.legs()) {
            // Point de départ
            addIfNewCoordinate(geoCoordinates, addedCoordinateStrings, leg.depStop());

            // Points intermédiaires
            for (Journey.Leg.IntermediateStop intermediate : leg.intermediateStops()) {
                addIfNewCoordinate(geoCoordinates, addedCoordinateStrings, intermediate.stop());
            }

            // Point d’arrivée
            addIfNewCoordinate(geoCoordinates, addedCoordinateStrings, leg.arrStop());
        }

        // Création de l’objet GeoJSON : {"type":"LineString", "coordinates": [...]}
        LinkedHashMap<String, Json> map = new LinkedHashMap<>();
        map.put("type", new JString("LineString"));
        map.put("coordinates", new JArray(geoCoordinates));

        return new JObject(map);
    }

    /**
     * Ajoute un point géographique au format {@code [longitude, latitude]} à la liste
     * des coordonnées GeoJSON, si ce point est différent du précédent, afin d’éviter
     * les doublons consécutifs.
     *
     * @param geoCoordinates la liste des coordonnées GeoJSON
     * @param addedCoordinateStrings ensemble des coordonnées déjà ajoutées sous forme de chaînes
     * @param stop l’arrêt dont les coordonnées doivent être ajoutées si elles ne sont pas déjà présentes
     */
    private static void addIfNewCoordinate(List<Json> geoCoordinates,
                                           Set<String> addedCoordinateStrings,
                                           Stop stop) {
        double roundedLon = round5(stop.longitude());
        double roundedLat = round5(stop.latitude());

        // Représentation textuelle pour comparaison rapide
        String coordinateString = roundedLon + "," + roundedLat;

        if (geoCoordinates.isEmpty() ||
            !coordinateString.equals(lastCoordinateString(geoCoordinates))) {
            geoCoordinates.add(new JArray(List.of(new JNumber(roundedLon),
                                                  new JNumber(roundedLat))));
            addedCoordinateStrings.add(coordinateString);
        }
    }

    /**
     * Retourne la représentation textuelle {@code "longitude,latitude"} du dernier
     * point géographique ajouté à la liste, afin de détecter les doublons consécutifs.
     *
     * @param geoCoordinates la liste des coordonnées GeoJSON
     * @return la chaîne représentant le dernier point ajouté sous la forme {@code "lon,lat"}
     */
    private static String lastCoordinateString(List<Json> geoCoordinates) {
        if (geoCoordinates.isEmpty())
            return "";

        Json lastPoint = geoCoordinates.getLast();
        if (lastPoint instanceof JArray(List<Json> coordinates) &&
            coordinates.size() == NB_COORDONNEES_GEO &&
            coordinates.get(0) instanceof JNumber(double lon) &&
            coordinates.get(1) instanceof JNumber(double lat)) {
            return lon + "," + lat;
        }

        return "";
    }

    /**
     * Arrondit une coordonnée géographique à 5 décimales, conformément aux
     * spécifications de l’exportation GeoJSON.
     *
     * @param value la valeur de la coordonnée à arrondir
     * @return la valeur arrondie à 5 décimales
     */
    private static double round5(double value) {
        return Math.round(value * GEO_PRECISION) / GEO_PRECISION;
    }
}