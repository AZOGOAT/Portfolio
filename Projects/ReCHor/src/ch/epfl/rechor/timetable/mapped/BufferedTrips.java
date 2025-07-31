package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.timetable.Trips;

import java.nio.ByteBuffer;
import java.util.List;

import static ch.epfl.rechor.timetable.mapped.Structure.FieldType.U16;
import static ch.epfl.rechor.timetable.mapped.Structure.field;

/**
 * Classe immuable permettant d'accéder à une table de courses représentée de manière aplatie.
 * Cette classe implémente l'interface {@link Trips} et fournit un accès efficace aux données
 * aplaties des courses de transport public.
 * <p>
 * La structure des courses aplaties est définie comme suit :
 * <ul>
 *     <li>Champ 0 : ROUTE_ID (U16) - Index de la ligne associée à la course.</li>
 *     <li>Champ 1 : DESTINATION_ID (U16) - Index de chaîne du nom de la destination finale.</li>
 * </ul>
 *
 * @author Omar Ziyad Azgaoui (379136)
 * @author Mohamed Amine Goulahsen (400232)
 */
public final class BufferedTrips implements Trips {

    private static final int ROUTE_ID = 0;
    private static final int DESTINATION_ID = 1;
    private static final Structure STRUCTURE = new Structure(field(ROUTE_ID, U16),
                                                             field(DESTINATION_ID, U16));

    private final List<String> stringTable;
    private final StructuredBuffer buffer;

    /**
     * Construit une instance donnant accès aux données aplaties des courses,
     * en utilisant la table de chaînes {@code stringTable} pour obtenir les noms référencés.
     *
     * @param stringTable La table de chaînes associée aux données aplaties.
     * @param buffer      Le tampon binaire contenant les données des courses.
     */
    public BufferedTrips(List<String> stringTable, ByteBuffer buffer) {
        this.stringTable = List.copyOf(stringTable);
        this.buffer = new StructuredBuffer(STRUCTURE, buffer);
    }

    /**
     * Retourne le nombre total de courses disponibles dans la structure aplatie.
     *
     * @return Le nombre de courses présentes dans le buffer.
     */
    @Override
    public int size() {
        return buffer.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int routeId(int id) {
        return buffer.getU16(ROUTE_ID, id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String destination(int id) {
        return stringTable.get(buffer.getU16(DESTINATION_ID, id));
    }
}