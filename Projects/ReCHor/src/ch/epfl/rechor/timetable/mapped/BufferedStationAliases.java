package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.timetable.StationAliases;

import java.nio.ByteBuffer;
import java.util.List;

import static ch.epfl.rechor.timetable.mapped.Structure.FieldType.U16;
import static ch.epfl.rechor.timetable.mapped.Structure.field;

/**
 * Représentation des alias des noms de gares sous forme de structure bufferisée.
 * Cette classe permet de récupérer les noms alternatifs des gares en utilisant une table de chaînes
 * et une structure de données aplatie stockée dans un buffer.
 *
 * @author Omar Ziyad Azgaoui (379136)
 * @author Mohamed Amine Goulahsen (400232)
 */
public final class BufferedStationAliases implements StationAliases {

    private static final int ALIAS_ID = 0;
    private static final int STATION_NAME_ID = 1;
    private static final Structure STRUCTURE = new Structure(field(ALIAS_ID, U16),
                                                             field(STATION_NAME_ID, U16));

    private final List<String> stringTable;
    private final StructuredBuffer buffer;

    /**
     * Construit un alias de station bufferisé à partir d'une table de chaînes et d'un buffer contenant les données aplaties.
     *
     * @param stringTable la table contenant les chaînes de caractères utilisées dans l'horaire aplati.
     * @param buffer      le buffer contenant les indices des noms alternatifs et des noms de gares.
     */
    public BufferedStationAliases(List<String> stringTable, ByteBuffer buffer) {
        this.stringTable = List.copyOf(stringTable);
        this.buffer = new StructuredBuffer(STRUCTURE, buffer);
    }

    /**
     * Retourne le nombre total d'alias contenus dans l'instance.
     *
     * @return le nombre d'alias disponible.
     */
    @Override
    public int size() {
        return buffer.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String alias(int id) {
        return stringTable.get(buffer.getU16(ALIAS_ID, id));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String stationName(int id) {
        return stringTable.get(buffer.getU16(STATION_NAME_ID, id));
    }

}
