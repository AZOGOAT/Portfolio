package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.PackedRange;
import ch.epfl.rechor.timetable.Transfers;

import java.nio.ByteBuffer;
import java.util.NoSuchElementException;

import static ch.epfl.rechor.PackedRange.endExclusive;
import static ch.epfl.rechor.PackedRange.startInclusive;
import static ch.epfl.rechor.timetable.mapped.Structure.FieldType.U16;
import static ch.epfl.rechor.timetable.mapped.Structure.FieldType.U8;
import static ch.epfl.rechor.timetable.mapped.Structure.field;

/**
 * Classe immuable permettant d'accéder à une table de changements représentée de manière aplatie.
 * Cette classe implémente l'interface {@link Transfers} et permet d'obtenir les informations
 * relatives aux changements entre gares, telles que leur durée et leur indexation.
 * <p>
 * La structure des changements aplatis est définie comme suit :
 * <ul>
 *     <li>Champ 0 : DEP_STATION_ID (U16) - Index de la gare de départ.</li>
 *     <li>Champ 1 : ARR_STATION_ID (U16) - Index de la gare d'arrivée.</li>
 *     <li>Champ 2 : TRANSFER_MINUTES (U8) - Durée du changement en minutes.</li>
 * </ul>
 *
 * @author Omar Ziyad Azgaoui (379136)
 * @author Mohamed Amine Goulahsen (400232)
 */
public final class BufferedTransfers implements Transfers {
    private static final int DEP_STATION_ID = 0;
    private static final int ARR_STATION_ID = 1;
    private static final int TRANSFER_MINUTES = 2;
    private static final Structure STRUCTURE = new Structure(field(DEP_STATION_ID, U16),
                                                             field(ARR_STATION_ID, U16),
                                                             field(TRANSFER_MINUTES, U8));

    private final StructuredBuffer buffer;
    private final int[] arrivingAtTable;

    /**
     * Construit une instance donnant accès aux données aplaties des changements.
     *
     * @param buffer Le tampon binaire contenant les données des changements.
     */
    public BufferedTransfers(ByteBuffer buffer) {
        this.buffer = new StructuredBuffer(STRUCTURE, buffer);

        // Tableau associant à chaque gare d’arrivée l’intervalle (empaqueté) de changements la concernant.
        int currentStationId;
        int arrivingAtSize = 0;

        // 1ère passe : on détermine l’index de gare le plus élevé pour allouer le tableau.
        for (int i = 0; i < this.buffer.size(); i++) {
            arrivingAtSize = Math.max(arrivingAtSize, this.buffer.getU16(ARR_STATION_ID, i));
        }

        arrivingAtTable = new int[arrivingAtSize +
                                  1]; // +1 pour inclure la gare ayant l’ID maximal.

        // 2e passe : remplir arrivingAtTable.
        // Les changements sont triés par gare d’arrivée, donc les mêmes sont groupés.
        int i = 0;
        while (i < this.buffer.size()) {
            int startInclusive = i;
            currentStationId = this.buffer.getU16(ARR_STATION_ID, i++);

            // Tant qu’on reste sur la même gare d’arrivée, on avance.
            while (i < this.buffer.size() &&
                   currentStationId == this.buffer.getU16(ARR_STATION_ID, i)) {
                ++i;
            }
            // On empaquette l’intervalle [startInclusive, i) pour cette gare.
            arrivingAtTable[currentStationId] = PackedRange.pack(startInclusive, i);
        }
    }

    /**
     * Retourne le nombre total de changements disponibles dans la structure aplatie.
     *
     * @return Le nombre de changements présents dans le buffer.
     */
    @Override
    public int size() {
        return buffer.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int depStationId(int id) {
        return buffer.getU16(DEP_STATION_ID, id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int minutes(int id) {
        return buffer.getU8(TRANSFER_MINUTES, id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int arrivingAt(int stationId) {
        return arrivingAtTable[stationId];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int minutesBetween(int depStationId, int arrStationId) {
        int range = arrivingAt(arrStationId);
        for (int i = startInclusive(range); i < endExclusive(range); i++) {
            if (depStationId(i) == depStationId &&
                buffer.getU16(ARR_STATION_ID, i) == arrStationId) {
                return minutes(i);
            }
        }
        throw new NoSuchElementException();
    }
}