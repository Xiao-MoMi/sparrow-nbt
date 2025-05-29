package net.momirealms.sparrow.nbt;

import net.momirealms.sparrow.nbt.visitor.CompactStringTagVisitor;

import java.io.DataOutput;
import java.io.IOException;

/**
 * Interface representing a generic NBT (Named Binary Tag) element.
 * Each implementing class represents a specific NBT data type,
 * such as Byte, Short, String, etc.
 */
public interface Tag {

    byte TAG_END_ID = 0;
    byte TAG_BYTE_ID = 1;
    byte TAG_SHORT_ID = 2;
    byte TAG_INT_ID = 3;
    byte TAG_LONG_ID = 4;
    byte TAG_FLOAT_ID = 5;
    byte TAG_DOUBLE_ID = 6;
    byte TAG_BYTE_ARRAY_ID = 7;
    byte TAG_STRING_ID = 8;
    byte TAG_LIST_ID = 9;
    byte TAG_COMPOUND_ID = 10;
    byte TAG_INT_ARRAY_ID = 11;
    byte TAG_LONG_ARRAY_ID = 12;
    byte TAG_ANY_NUMERIC_ID = 99;

    /**
     * Writes the binary representation of this tag to the specified output stream.
     *
     * @param output the data output stream to write to
     * @throws IOException if an I/O error occurs during writing
     */
    void write(DataOutput output) throws IOException;

    /**
     * Returns a string representation of the tag.
     *
     * @return a string describing the tag
     */
    @Override
    String toString();

    /**
     * Returns the ID of this tag type.
     *
     * @return a byte representing the tag type ID
     */
    byte getId();

    /**
     * Retrieves the TagType of this tag.
     *
     * @return the specific TagType instance associated with this tag
     */
    TagType<?> getType();

    /**
     * Creates a shallow copy of this tag.
     *
     * @return a new Tag instance with the same content as this one
     */
    Tag copy();

    /**
     * Creates a deep clone of this tag, including all nested elements if applicable.
     *
     * @return a new Tag instance that is a deep copy of this one
     */
    Tag deepClone();

    /**
     * Returns a compact string representation of this tag.
     *
     * @return a string representing the tag in a compact format
     */
    default String getAsString() {
        return (new CompactStringTagVisitor()).visit(this);
    }

    /**
     * Accepts a visitor for processing this tag.
     *
     * @param visitor the visitor instance to process this tag
     */
    void accept(TagVisitor visitor);

    /**
     * Checks whether this tag is of the specified type or matches a generic numeric type.
     *
     * @param type the ID of the type to check
     * @return true if the tag matches the specified type, otherwise false
     */
    default boolean isTypeOf(int type) {
        int i = getId();
        if (i == type) {
            return true;
        } else if (type != TAG_ANY_NUMERIC_ID) {
            return false;
        } else {
            return isNumericTag(i);
        }
    }

    /**
     * Determines if the given tag type ID corresponds to a numeric type.
     *
     * @param tagType the ID of the tag type to check
     * @return true if the tag type is numeric, otherwise false
     */
    static boolean isNumericTag(int tagType) {
        return switch (tagType) {
            case TAG_BYTE_ID, TAG_SHORT_ID, TAG_INT_ID, TAG_LONG_ID, TAG_FLOAT_ID, TAG_DOUBLE_ID -> true;
            default -> false;
        };
    }
}
