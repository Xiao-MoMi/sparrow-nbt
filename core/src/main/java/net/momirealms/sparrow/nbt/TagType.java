package net.momirealms.sparrow.nbt;

import java.io.DataInput;
import java.io.IOException;

/**
 * Interface representing a type of NBT (Named Binary Tag).
 * It defines methods for reading, skipping, and identifying tag types.
 *
 * @param <T> the specific Tag type associated with this TagType
 */
public interface TagType<T extends Tag> {

    /**
     * Returns the name of this tag type.
     *
     * @return the name of the tag type
     */
    String name();

    /**
     * Skips over the specified number of tags in the input stream.
     *
     * @param input the input stream to skip data from
     * @param count the number of tags to skip
     * @throws IOException if an I/O error occurs
     */
    void skip(DataInput input, int count) throws IOException;

    /**
     * Skips a single tag in the input stream.
     *
     * @param input the input stream to skip data from
     * @throws IOException if an I/O error occurs
     */
    void skip(DataInput input) throws IOException;

    /**
     * Reads a tag from the input stream with a specified depth.
     *
     * @param input the input stream to read data from
     * @param depth the depth of the current tag structure (used to avoid infinite recursion)
     * @return the tag read from the input stream
     * @throws IOException if an I/O error occurs
     */
    T read(DataInput input, int depth) throws IOException;

    /**
     * Checks whether this tag type represents a value.
     *
     * @return true if this tag type represents a value, otherwise false
     */
    default boolean isValue() {
        return false;
    }

    /**
     * Interface for tag types with a fixed size.
     *
     * @param <T> the specific Tag type associated with this TagType
     */
    interface FixedSize<T extends Tag> extends TagType<T> {

        /**
         * Returns the fixed size of this tag type in bytes.
         *
         * @return the size in bytes
         */
        int size();

        /**
         * Skips a single tag by skipping a fixed number of bytes in the input stream.
         *
         * @param input the input stream to skip data from
         * @throws IOException if an I/O error occurs
         */
        @Override
        default void skip(DataInput input) throws IOException {
            input.skipBytes(this.size());
        }

        /**
         * Skips the specified number of tags by skipping a fixed number of bytes in the input stream.
         *
         * @param input the input stream to skip data from
         * @param count the number of tags to skip
         * @throws IOException if an I/O error occurs
         */
        @Override
        default void skip(DataInput input, int count) throws IOException {
            input.skipBytes(this.size() * count);
        }
    }

    /**
     * Interface for tag types with a flexible (variable) size.
     *
     * @param <T> the specific Tag type associated with this TagType
     */
    interface FlexibleSize<T extends Tag> extends TagType<T> {

        /**
         * Skips the specified number of tags by repeatedly calling the skip method.
         *
         * @param input the input stream to skip data from
         * @param count the number of tags to skip
         * @throws IOException if an I/O error occurs
         */
        @Override
        default void skip(DataInput input, int count) throws IOException {
            for (int i = 0; i < count; ++i) {
                this.skip(input);
            }
        }
    }
}
