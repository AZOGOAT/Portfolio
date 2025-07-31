package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.timetable.Platforms;

import java.nio.ByteBuffer;
import java.util.List;

import static ch.epfl.rechor.timetable.mapped.Structure.FieldType.U16;
import static ch.epfl.rechor.timetable.mapped.Structure.field;

/**
 * Représentation des voies ou quais sous forme de structure bufferisée.
 * Cette classe permet de récupérer les informations sur les voies ou quais
 * en utilisant une table de chaînes et une structure de données aplatie stockée dans un buffer.
 *
 * @author Omar Ziyad Azgaoui (379136)
 * @author Mohamed Amine Goulahsen (400232)
 */
public final class BufferedPlatforms implements Platforms {

    private static final int NAME_ID = 0;
    private static final int STATION_ID = 1;
    private static final Structure STRUCTURE = new Structure(field(NAME_ID, U16),
                                                             field(STATION_ID, U16));

    private final List<String> stringTable;
    private final StructuredBuffer buffer;

    /**
     * Construit une structure bufferisée représentant les voies ou quais.
     *
     * @param stringTable la table contenant les chaînes de caractères utilisées dans l'horaire aplati.
     * @param buffer      le buffer contenant les indices des noms de voies et des gares associées.
     */
    public BufferedPlatforms(List<String> stringTable, ByteBuffer buffer) {
        this.stringTable = List.copyOf(stringTable);
        this.buffer = new StructuredBuffer(STRUCTURE, buffer);
    }

    /**
     * Retourne le nombre total de voies/quais contenus dans l'instance.
     *
     * @return le nombre de voies/quais disponible.
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
    public int stationId(int id) {
        return buffer.getU16(STATION_ID, id);
    }

}