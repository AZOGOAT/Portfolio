package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.timetable.Stations;

import java.nio.ByteBuffer;
import java.util.List;

import static ch.epfl.rechor.timetable.mapped.Structure.FieldType.S32;
import static ch.epfl.rechor.timetable.mapped.Structure.FieldType.U16;
import static ch.epfl.rechor.timetable.mapped.Structure.field;

/**
 * La classe {@code BufferedStations} permet d'accéder à une table de gares
 * représentée de manière aplatie.
 * Elle implémente l'interface {@code Stations}.
 *
 * @author Omar Ziyad Azgaoui (379136)
 * @author Mohamed Amine Goulahsen (400232)
 */
public final class BufferedStations implements Stations {

    /**
     * Identifiant du champ contenant le nom de la gare.
     */
    private static final int NAME_ID = 0;

    /**
     * Identifiant du champ contenant la longitude de la gare.
     */
    private static final int LONGITUDE = 1;

    /**
     * Identifiant du champ contenant la latitude de la gare.
     */
    private static final int LATITUDE = 2;
    private static final Structure STRUCTURE = new Structure(field(NAME_ID, U16),
                                                             field(LONGITUDE, S32),
                                                             field(LATITUDE, S32));

    /**
     * Facteur de conversion pour les longitudes et latitudes.
     */
    private static final double SCALING_FACTOR = StrictMath.scalb(360, -32);

    private final List<String> stringTable;
    private final StructuredBuffer buffer;

    /**
     * Construit une instance de {@code BufferedStations} permettant d'accéder aux données aplaties
     * disponibles dans {@code buffer}, en utilisant la table de chaînes {@code stringTable} pour
     * déterminer la valeur des chaînes référencées par ces données.
     *
     * @param stringTable La table de chaînes permettant de résoudre les références aux chaînes.
     * @param buffer      Le buffer contenant les données aplaties.
     */
    public BufferedStations(List<String> stringTable, ByteBuffer buffer) {
        this.stringTable = List.copyOf(stringTable);
        this.buffer = new StructuredBuffer(STRUCTURE, buffer);
    }

    /**
     * Retourne le nombre total de gares contenues dans cette instance.
     *
     * @return Le nombre total de gares.
     */
    @Override
    public int size() {
        return buffer.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String name(int id) {
        return stringTable.get(buffer.getU16(NAME_ID, id));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double longitude(int id) {
        return buffer.getS32(LONGITUDE, id) * SCALING_FACTOR;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double latitude(int id) {
        return buffer.getS32(LATITUDE, id) * SCALING_FACTOR;
    }
}