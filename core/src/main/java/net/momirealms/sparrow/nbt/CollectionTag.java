package net.momirealms.sparrow.nbt;

import java.util.AbstractList;

/**
 * Abstract class representing a collection of NBT (Named Binary Tag) elements.
 * Provides a base implementation for list-like NBT structures (e.g., ListTag).
 *
 * @param <T> the type of tags that this collection holds
 */
public abstract class CollectionTag<T extends Tag> extends AbstractList<T> implements Tag {

    /**
     * Replaces the tag at the specified index with the given tag.
     *
     * @param index the index of the tag to replace
     * @param tag   the new tag to set at the specified index
     * @return the tag previously at the specified index
     */
    @Override
    public abstract T set(int index, T tag);

    /**
     * Inserts the specified tag at the specified index in the collection.
     * Shifts the tag currently at that position (if any) and subsequent tags to the right.
     *
     * @param index the index at which the tag is to be inserted
     * @param tag   the tag to insert
     */
    @Override
    public abstract void add(int index, T tag);

    /**
     * Removes the tag at the specified index from the collection.
     * Shifts any subsequent tags to the left.
     *
     * @param index the index of the tag to remove
     * @return the tag that was removed from the collection
     */
    @Override
    public abstract T remove(int index);

    /**
     * Returns the tag at the specified index in the collection.
     *
     * @param index the index of the tag to return
     * @return the tag at the specified index
     * @throws IndexOutOfBoundsException if the index is out of range
     *         (index < 0 || index >= size())
     */
    @Override
    public abstract T get(int index);

    /**
     * Replaces the tag at the specified index with the given tag.
     * Unlike {@link #set}, this method allows any {@link Tag} type.
     *
     * @param index the index of the tag to replace
     * @param tag   the new tag to set at the specified index
     * @return true if the tag was successfully replaced, otherwise false
     */
    public abstract boolean setTag(int index, Tag tag);

    /**
     * Inserts the specified tag at the specified index in the collection.
     * Unlike {@link #add}, this method allows any {@link Tag} type.
     *
     * @param index the index at which the tag is to be inserted
     * @param tag   the tag to insert
     * @return true if the tag was successfully added, otherwise false
     */
    public abstract boolean addTag(int index, Tag tag);
}
