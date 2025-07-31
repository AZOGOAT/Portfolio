package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.timetable.Connections;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static ch.epfl.rechor.Bits32_24_8.unpack24;
import static ch.epfl.rechor.Bits32_24_8.unpack8;
import static ch.epfl.rechor.timetable.mapped.Structure.FieldType.S32;
import static ch.epfl.rechor.timetable.mapped.Structure.FieldType.U16;
import static ch.epfl.rechor.timetable.mapped.Structure.field;

/**
 * Classe immuable permettant d'accéder à une table de liaisons représentée de manière aplatie.
 * Cette classe implémente l'interface {@link Connections} et permet d'obtenir les informations
 * des liaisons, telles que les arrêts de départ et d'arrivée, leurs horaires,
 * ainsi que la course à laquelle elles appartiennent.
 * <p>
 * La structure des liaisons aplaties est définie comme suit :
 * <ul>
 *     <li>Champ 0 : DEP_STOP_ID (U16) - Index de l'arrêt de départ.</li>
 *     <li>Champ 1 : DEP_MINUTES (U16) - Heure de départ en minutes après minuit.</li>
 *     <li>Champ 2 : ARR_STOP_ID (U16) - Index de l'arrêt d'arrivée.</li>
 *     <li>Champ 3 : ARR_MINUTES (U16) - Heure d'arrivée en minutes après minuit.</li>
 *     <li>Champ 4 : TRIP_POS_ID (S32) - Identifiant empaqueté contenant l'index de la course et sa position.</li>
 * </ul>
 *
 * @author Omar Ziyad Azgaoui (379136)
 * @author Mohamed Amine Goulahsen (400232)
 */
public final class BufferedConnections implements Connections {
    private static final int DEP_STOP_ID = 0;
    private static final int DEP_MINUTES = 1;
    private static final int ARR_STOP_ID = 2;
    private static final int ARR_MINUTES = 3;
    private static final int TRIP_POS_ID = 4;
    private static final Structure STRUCTURE = new Structure(field(DEP_STOP_ID, U16),
                                                             field(DEP_MINUTES, U16),
                                                             field(ARR_STOP_ID, U16),
                                                             field(ARR_MINUTES, U16),
                                                             field(TRIP_POS_ID, S32));

    private final StructuredBuffer buffer;
    private final IntBuffer succBuffer;

    /**
     * Construit une instance donnant accès aux données aplaties des liaisons.
     *
     * @param buffer     Le tampon binaire contenant les données des liaisons.
     * @param succBuffer Le tampon binaire contenant l'index des liaisons suivantes dans chaque course.
     */
    public BufferedConnections(ByteBuffer buffer, ByteBuffer succBuffer) {
        this.buffer = new StructuredBuffer(STRUCTURE, buffer);
        this.succBuffer = succBuffer.asIntBuffer();
    }

    /**
     * Retourne le nombre total de liaisons disponibles dans la structure aplatie.
     *
     * @return Le nombre de liaisons présentes dans le buffer.
     */
    @Override
    public int size() {
        return buffer.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int depStopId(int id) {
        return buffer.getU16(DEP_STOP_ID, id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int depMins(int id) {
        return buffer.getU16(DEP_MINUTES, id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int arrStopId(int id) {
        return buffer.getU16(ARR_STOP_ID, id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int arrMins(int id) {
        return buffer.getU16(ARR_MINUTES, id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int tripId(int id) {
        return unpack24(buffer.getS32(TRIP_POS_ID, id)); // Extrait les 24 bits de poids fort
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int tripPos(int id) {
        return unpack8(buffer.getS32(TRIP_POS_ID, id)); // Extrait les 8 bits de poids faible
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int nextConnectionId(int id) {
        return succBuffer.get(id);
    }
}