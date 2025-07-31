package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.timetable.*;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import static java.nio.channels.FileChannel.MapMode.READ_ONLY;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

/**
 * Implémentation de {@link TimeTable} représentant un horaire de transport public dont
 * les données aplaties sont stockées dans des fichiers.
 * <p>
 * Le contenu de ces fichiers est mappé en mémoire de manière à permettre un accès rapide
 * et efficace aux données, tout en limitant la charge en mémoire.
 * <p>
 * Cette classe lit les fichiers binaires suivants du dossier donné :
 * <ul>
 *     <li>stations.bin</li>
 *     <li>station-aliases.bin</li>
 *     <li>platforms.bin</li>
 *     <li>routes.bin</li>
 *     <li>transfers.bin</li>
 *     <li>strings.txt</li>
 * </ul>
 * et, pour chaque jour, les fichiers :
 * <ul>
 *     <li>trips.bin</li>
 *     <li>connections.bin</li>
 *     <li>connections-succ.bin</li>
 * </ul>
 *
 * @param directory       le chemin vers le dossier contenant les fichiers horaires
 * @param stringTable     la table des chaînes de caractères encodées en ISO-8859-1
 * @param stations        les gares aplaties
 * @param stationAliases  les noms alternatifs des gares
 * @param platforms       les voies ou quais aplatis
 * @param routes          les lignes aplaties
 * @param transfers       les changements entre gares
 *
 * @author Omar Ziyad Azgaoui (379136)
 * @author Mohamed Amine Goulahsen (400232)
 */
public record FileTimeTable(Path directory,
                            List<String> stringTable,
                            Stations stations,
                            StationAliases stationAliases,
                            Platforms platforms,
                            Routes routes,
                            Transfers transfers)
        implements TimeTable {

    private static final String STRINGS_FILE = "strings.txt";
    private static final String STATIONS_FILE = "stations.bin";
    private static final String STATION_ALIASES_FILE = "station-aliases.bin";
    private static final String PLATFORMS_FILE = "platforms.bin";
    private static final String ROUTES_FILE = "routes.bin";
    private static final String TRANSFERS_FILE = "transfers.bin";
    private static final String TRIPS_FILE = "trips.bin";
    private static final String CONNECTIONS_FILE = "connections.bin";
    private static final String CONNECTIONS_SUCC_FILE = "connections-succ.bin";

    /**
     * Crée une instance de {@link FileTimeTable} à partir d’un dossier contenant les fichiers horaires.
     * <p>
     * Les fichiers doivent être organisés comme dans l’archive timetable fournie dans le projet.
     *
     * @param directory le chemin du dossier contenant les fichiers horaires
     * @return une nouvelle instance de {@link FileTimeTable}
     * @throws IOException en cas d’erreur de lecture des fichiers
     */
    public static TimeTable in(Path directory) throws IOException {
        Path path = directory.resolve(STRINGS_FILE);
        Charset cs = StandardCharsets.ISO_8859_1;
        List<String> stringTable = List.copyOf(Files.readAllLines(path, cs));

        ByteBuffer stationsBuffer = readMappedFile(directory, STATIONS_FILE);
        ByteBuffer stationAliasesBuffer = readMappedFile(directory, STATION_ALIASES_FILE);
        ByteBuffer platformsBuffer = readMappedFile(directory, PLATFORMS_FILE);
        ByteBuffer routesBuffer = readMappedFile(directory, ROUTES_FILE);
        ByteBuffer transferBuffer = readMappedFile(directory, TRANSFERS_FILE);

        return new FileTimeTable(
                directory,
                stringTable,
                new BufferedStations(stringTable, stationsBuffer),
                new BufferedStationAliases(stringTable, stationAliasesBuffer),
                new BufferedPlatforms(stringTable, platformsBuffer),
                new BufferedRoutes(stringTable, routesBuffer),
                new BufferedTransfers(transferBuffer)
        );
    }

    /**
     * Retourne les courses de l’horaire pour une date donnée.
     *
     * @param date la date à laquelle récupérer les courses
     * @return les courses de l’horaire pour la date donnée
     * @throws UncheckedIOException si une erreur d’entrée/sortie se produit
     */
    @Override
    public Trips tripsFor(LocalDate date) {
        try {
            Path dayDirectory = directory.resolve(date.toString());
            ByteBuffer tripsBuffer = readMappedFile(dayDirectory, TRIPS_FILE);
            return new BufferedTrips(stringTable, tripsBuffer);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Retourne les connexions de l’horaire pour une date donnée.
     *
     * @param date la date à laquelle récupérer les connexions
     * @return les connexions de l’horaire pour la date donnée
     * @throws UncheckedIOException si une erreur d’entrée/sortie se produit
     */
    @Override
    public Connections connectionsFor(LocalDate date) {
        try {
            Path dayDirectory = directory.resolve(date.toString());
            ByteBuffer connectionsBuffer = readMappedFile(dayDirectory, CONNECTIONS_FILE);
            ByteBuffer succBuffer = readMappedFile(dayDirectory, CONNECTIONS_SUCC_FILE);
            return new BufferedConnections(connectionsBuffer, succBuffer);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     Lit un fichier donné et retourne un ByteBuffer mappé en mémoire.
     * Le canal est fermé automatiquement grâce au try-with-resources.
     *
     * @param directory le dossier contenant le fichier
     * @param fileName  le nom du fichier à mapper
     * @return un ByteBuffer contenant le contenu du fichier mappé en lecture seule
     * @throws IOException en cas d’erreur lors de l'ouverture ou du mapping du fichier
     */
    private static ByteBuffer readMappedFile(Path directory, String fileName) throws IOException {
        try (FileChannel channel = FileChannel.open(directory.resolve(fileName))) {
            return channel.map(READ_ONLY, 0, channel.size());
        }
    }
}