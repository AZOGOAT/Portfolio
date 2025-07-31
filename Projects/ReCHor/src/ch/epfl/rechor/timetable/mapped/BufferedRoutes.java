package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.journey.Vehicle;
import ch.epfl.rechor.timetable.Routes;

import java.nio.ByteBuffer;
import java.util.List;

import static ch.epfl.rechor.timetable.mapped.Structure.FieldType.U16;
import static ch.epfl.rechor.timetable.mapped.Structure.FieldType.U8;
import static ch.epfl.rechor.timetable.mapped.Structure.field;

/**
 * Classe immuable permettant d'accéder à une table de lignes représentée de manière aplatie.
 * Cette classe implémente l'interface {@link Routes} et fournit des méthodes permettant
 * d'obtenir le nom et le type de véhicule desservant une ligne spécifique.
 * <p>
 * La structure des lignes aplaties est définie comme suit :
 * <ul>
 *     <li>Champ 0 : NAME_ID (U16) - Index de chaîne du nom de la ligne.</li>
 *     <li>Champ 1 : KIND (U8) - Type de véhicule desservant la ligne.</li>
 * </ul>
 * Le type du véhicule est représenté comme un entier entre 0 et 6, correspondant directement
 * aux valeurs de l'énumération {@link Vehicle}.
 *
 * @author Omar Ziyad Azgaoui (379136)
 * @author Mohamed Amine Goulahsen (400232)
 */
public final class BufferedRoutes implements Routes {
    private static final int NAME_ID = 0;
    private static final int KIND = 1;
    private static final Structure STRUCTURE = new Structure(field(NAME_ID, U16),
                                                             field(KIND, U8));

    private final List<String> stringTable;
    private final StructuredBuffer buffer;

    /**
     * Construit une instance donnant accès aux données aplaties disponibles dans le buffer,
     * en utilisant la table de chaînes {@code stringTable} pour déterminer la valeur des chaînes référencées.
     *
     * @param stringTable La table de chaînes associée aux données aplaties.
     * @param buffer      Le tampon binaire contenant les données des lignes.
     */
    public BufferedRoutes(List<String> stringTable, ByteBuffer buffer) {
        this.stringTable = stringTable;
        this.buffer = new StructuredBuffer(STRUCTURE, buffer);
    }

    /**
     * Retourne le nombre total de lignes présentes dans la structure aplatie.
     *
     * @return Le nombre de lignes disponibles.
     */
    @Override
    public int size() {
        return buffer.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Vehicle vehicle(int id) {
        return Vehicle.ALL.get(buffer.getU8(KIND, id));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String name(int id) {
        return stringTable.get(buffer.getU16(NAME_ID, id));
    }
}