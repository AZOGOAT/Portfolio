package ch.epfl.rechor.timetable.mapped;

import java.nio.ByteBuffer;

import static ch.epfl.rechor.Preconditions.checkArgument;

/**
 * Représente un « tableau d'octets structuré ».
 * Cette classe offre un accès structuré à des données aplaties stockées dans un tableau d'octets,
 * et dont la structure est décrite par une instance de {@link Structure}.
 *
 * @author Omar Ziyad Azgaoui (379136)
 * @author Mohamed Amine Goulahsen (400232)
 */
public class StructuredBuffer {
    private final Structure structure;
    private final ByteBuffer buffer;

    /**
     * Construit un tableau structuré dont les éléments ont la structure donnée,
     * et dont les octets sont stockés dans le tableau buffer.
     *
     * @param structure la structure des éléments du tableau
     * @param buffer    le tableau d'octets contenant les données
     * @throws IllegalArgumentException si le nombre d'octets du buffer n'est pas un multiple de la taille totale de la structure
     */
    public StructuredBuffer(Structure structure, ByteBuffer buffer) {
        checkArgument(buffer.capacity() % structure.totalSize() == 0);
        this.structure = structure;
        this.buffer = buffer;
    }

    /**
     * Retourne le nombre d'éléments que contient le tableau.
     *
     * @return le nombre d'éléments stockés dans le buffer
     */
    public int size() {
        return buffer.capacity() / structure.totalSize();
    }

    /**
     * Retourne l'entier U8 correspondant au champ d'index donné de l'élément donné du tableau.
     *
     * @param fieldIndex   l'index du champ dans la structure
     * @param elementIndex l'index de l'élément dans le tableau
     * @return la valeur U8 interprétée de manière non signée
     * @throws IndexOutOfBoundsException si l'un des deux index est invalide
     */
    public int getU8(int fieldIndex, int elementIndex) {
        int offset = structure.offset(fieldIndex, elementIndex);
        return Byte.toUnsignedInt(buffer.get(offset));
    }

    /**
     * Retourne l'entier U16 correspondant au champ d'index donné de l'élément donné du tableau.
     *
     * @param fieldIndex   l'index du champ dans la structure
     * @param elementIndex l'index de l'élément dans le tableau
     * @return la valeur U16 interprétée de manière non signée
     * @throws IndexOutOfBoundsException si l'un des deux index est invalide
     */
    public int getU16(int fieldIndex, int elementIndex) {
        int offset = structure.offset(fieldIndex, elementIndex);
        return Short.toUnsignedInt(buffer.getShort(offset));
    }

    /**
     * Retourne l'entier S32 correspondant au champ d'index donné de l'élément donné du tableau.
     *
     * @param fieldIndex   l'index du champ dans la structure
     * @param elementIndex l'index de l'élément dans le tableau
     * @return la valeur S32 interprétée comme un entier signé
     * @throws IndexOutOfBoundsException si l'un des deux index est invalide
     */
    public int getS32(int fieldIndex, int elementIndex) {
        int offset = structure.offset(fieldIndex, elementIndex);
        return buffer.getInt(offset);
    }
}