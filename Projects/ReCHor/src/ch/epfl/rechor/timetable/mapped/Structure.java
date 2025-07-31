package ch.epfl.rechor.timetable.mapped;


import java.util.Objects;

import static ch.epfl.rechor.Preconditions.checkArgument;

/**
 * Représente la structure des données aplaties pour faciliter leur description.
 * Cette classe permet de définir la structure des champs avec leurs types et positions.
 *
 * @author Omar Ziyad Azgaoui (379136)
 * @author Mohamed Amine Goulahsen (400232)
 */
public class Structure {

    private final int[] offsets;
    private int currentOffset;

    /**
     * Enumération représentant les trois types de champs possibles dans une structure.
     */
    public enum FieldType {
        /**
         * Champ de type entier non signé sur 8 bits
         */
        U8,
        /**
         * Champ de type entier non signé sur 16 bits
         */
        U16,
        /**
         * Champ de type entier signé sur 32 bits
         */
        S32,
    }

    /**
     * Construit une structure à partir des champs fournis.
     *
     * @param fields les champs de la structure
     * @throws IllegalArgumentException si les champs ne sont pas donnés dans l'ordre,
     *                                  c'est-à-dire si l'index du premier champ n'est pas 0,
     *                                  le second 1, etc.
     */
    public Structure(Field... fields) {
        for (int i = 0; i < fields.length; i++) {
            checkArgument(fields[i].index() == i);
        }
        this.offsets = new int[fields.length];
        currentOffset = 0;
        for (int i = 0; i < fields.length; i++) {
            offsets[i] = currentOffset;
            switch (fields[i].type()) {
                case U8 -> currentOffset += 1;
                case U16 -> currentOffset += 2;
                case S32 -> currentOffset += 4;
            }
        }
    }

    /**
     * Crée une instance de Field sans nécessiter l'opérateur new.
     *
     * @param index l'index du champ
     * @param type  le type du champ
     * @return une instance de Field avec les attributs spécifiés
     */
    public static Field field(int index, FieldType type) {
        return new Field(index, type);
    }

    /**
     * Retourne la taille totale de la structure en octets.
     *
     * @return la taille totale de la structure en octets
     */
    public int totalSize() {
        return currentOffset;
    }

    /**
     * Calcule l'offset d'un champ donné pour un élément d'index donné.
     *
     * @param fieldIndex   l'index du champ dans la structure
     * @param elementIndex l'index de l'élément dans la table des données aplaties
     * @return l'index, dans le tableau d'octets, du premier octet du champ spécifié
     * @throws IndexOutOfBoundsException si l'index du champ est invalide
     */
    public int offset(int fieldIndex, int elementIndex) {
        return offsets[fieldIndex] + elementIndex * totalSize();
    }

    /**
     * Représente un champ d'une structure avec un index et un type.
     */
    public record Field(int index, FieldType type) {
        /**
         * Construit un champ avec l'index et le type donné.
         *
         * @param index l'index du champ dans la structure
         * @param type  le type du champ
         * @throws NullPointerException si le type est null
         */
        public Field {
            Objects.requireNonNull(type);
        }
    }
}