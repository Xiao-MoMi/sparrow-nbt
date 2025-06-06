package net.momirealms.sparrow.nbt;

import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class ListTag extends CollectionTag<Tag> {
    private final List<Tag> list;

    public ListTag(List<Tag> list) {
        this.list = list;
    }

    public ListTag() {
        this.list = new ArrayList<>();
    }

    public static Tag tryUnwrap(CompoundTag tag) {
        if (tag.size() == 1) {
            Tag tag1 = tag.get("");
            if (tag1 != null) {
                return tag1;
            }
        }
        return tag;
    }

    private static boolean isWrapper(CompoundTag tag) {
        return tag.size() == 1 && tag.containsKey("");
    }

    public static Tag wrapIfNeeded(byte elementType, Tag tag) {
        if (elementType != 10) { // compound
            return tag;
        } else {
            return tag instanceof CompoundTag compoundTag && !isWrapper(compoundTag) ? compoundTag : wrapElement(tag);
        }
    }

    private static CompoundTag wrapElement(Tag tag) {
        return new CompoundTag(Map.of("", tag));
    }

    @Override
    public Tag get(int index) {
        return this.list.get(index);
    }

    @Override
    public boolean isEmpty() {
        return this.list.isEmpty();
    }

    @Override
    public Tag set(int index, Tag tag) {
        return this.list.set(index, tag);
    }

    @Override
    public void add(int index, Tag tag) {
        this.list.add(index, tag);
    }

    @Override
    public Tag remove(int index) {
        return this.list.remove(index);
    }

    @Override
    public boolean setTag(int index, Tag tag) {
        this.list.set(index, tag);
        return true;
    }

    @Override
    public boolean addTag(int index, Tag tag) {
        this.list.add(index, tag);
        return true;
    }

    @Override
    public int size() {
        return this.list.size();
    }

    @Override
    public void write(DataOutput output) throws IOException {
        byte type = identifyRawElementType();
        output.writeByte(type);
        output.writeInt(this.list.size());
        for (Tag tag : this.list) {
            wrapIfNeeded(type, tag).write(output);
        }
    }

    public void addAndUnwrap(Tag tag) {
        if (tag instanceof CompoundTag compoundTag) {
            this.add(tryUnwrap(compoundTag));
        } else {
            this.add(tag);
        }
    }

    public byte identifyRawElementType() {
        byte type = 0;
        for (Tag tag : this.list) {
            byte id = tag.getId();
            if (type == 0) {
                type = id;
            } else if (type != id) {
                return 10;
            }
        }
        return type;
    }

    @Override
    public byte getId() {
        return TAG_LIST_ID;
    }

    @Override
    public TagType<?> getType() {
        return TagTypes.LIST;
    }

    @Override
    public ListTag copy() {
        return new ListTag(new ArrayList<>(this.list));
    }

    @Override
    public ListTag deepClone() {
        List<Tag> list = new ArrayList<>(this.list.size());
        for (Tag tag : this.list) {
            list.add(tag.deepClone());
        }
        return new ListTag(list);
    }

    @Override
    public void accept(TagVisitor visitor) {
        visitor.visitList(this);
    }

    /**
     * Retrieves a String from the list at the specified index.
     *
     * @param index the index of the element
     * @return the String value at the specified index, or {@code null} if not found or type mismatch
     */
    public String getString(int index) {
        return getString(index, null);
    }

    /**
     * Retrieves a String from the list at the specified index, with a default value.
     *
     * @param index the index of the element
     * @param defaultValue the default value to return if the element is not a String or does not exist
     * @return the String value at the specified index, or the default value
     */
    public String getString(int index, String defaultValue) {
        return this.getTypedValue(index, TAG_STRING_ID, Tag::getAsString, defaultValue);
    }

    /**
     * Retrieves a float from the list at the specified index.
     *
     * @param index the index of the element
     * @return the float value at the specified index, or 0 if not found or type mismatch
     */
    public float getFloat(int index) {
        return getFloat(index, 0f);
    }

    /**
     * Retrieves a float from the list at the specified index, with a default value.
     *
     * @param index the index of the element
     * @param defaultValue the default value to return if the element is not a float or does not exist
     * @return the float value at the specified index, or the default value
     */
    public float getFloat(int index, float defaultValue) {
        return this.getTypedValue(index, TAG_FLOAT_ID, t -> ((FloatTag) t).getAsFloat(), defaultValue);
    }

    /**
     * Retrieves a double from the list at the specified index.
     *
     * @param index the index of the element
     * @return the double value at the specified index, or 0 if not found or type mismatch
     */
    public double getDouble(int index) {
        return getDouble(index, 0d);
    }

    /**
     * Retrieves a double from the list at the specified index, with a default value.
     *
     * @param index the index of the element
     * @param defaultValue the default value to return if the element is not a double or does not exist
     * @return the double value at the specified index, or the default value
     */
    public double getDouble(int index, double defaultValue) {
        return this.getTypedValue(index, TAG_DOUBLE_ID, t -> ((DoubleTag) t).getAsDouble(), defaultValue);
    }

    /**
     * Retrieves a long array from the list at the specified index.
     *
     * @param index the index of the element
     * @return the long array at the specified index, or {@code null} if not found or type mismatch
     */
    public long[] getLongArray(int index) {
        return getLongArray(index, null);
    }

    /**
     * Retrieves a long array from the list at the specified index, with a default value.
     *
     * @param index the index of the element
     * @param defaultValue the default value to return if the element is not a long array or does not exist
     * @return the long array at the specified index, or the default value
     */
    public long[] getLongArray(int index, long[] defaultValue) {
        return this.getTypedValue(index, TAG_LONG_ARRAY_ID, t -> ((LongArrayTag) t).getAsLongArray(), defaultValue);
    }

    /**
     * Retrieves an int array from the list at the specified index.
     *
     * @param index the index of the element
     * @return the int array at the specified index, or {@code null} if not found or type mismatch
     */
    public int[] getIntArray(int index) {
        return getIntArray(index, null);
    }

    /**
     * Retrieves an int array from the list at the specified index, with a default value.
     *
     * @param index the index of the element
     * @param defaultValue the default value to return if the element is not an int array or does not exist
     * @return the int array at the specified index, or the default value
     */
    public int[] getIntArray(int index, int[] defaultValue) {
        return this.getTypedValue(index, TAG_INT_ARRAY_ID, t -> ((IntArrayTag) t).getAsIntArray(), defaultValue);
    }

    /**
     * Retrieves an int from the list at the specified index.
     *
     * @param index the index of the element
     * @return the int value at the specified index, or 0 if not found or type mismatch
     */
    public int getInt(int index) {
        return getInt(index, 0);
    }

    /**
     * Retrieves an int from the list at the specified index, with a default value.
     *
     * @param index the index of the element
     * @param defaultValue the default value to return if the element is not an int or does not exist
     * @return the int value at the specified index, or the default value
     */
    public int getInt(int index, int defaultValue) {
        return this.getTypedValue(index, TAG_INT_ID, t -> ((IntTag) t).getAsInt(), defaultValue);
    }

    /**
     * Retrieves a short from the list at the specified index.
     *
     * @param index the index of the element
     * @return the short value at the specified index, or 0 if not found or type mismatch
     */
    public short getShort(int index) {
        return getShort(index, (short) 0);
    }

    /**
     * Retrieves a short from the list at the specified index, with a default value.
     *
     * @param index the index of the element
     * @param defaultValue the default value to return if the element is not a short or does not exist
     * @return the short value at the specified index, or the default value
     */
    public short getShort(int index, short defaultValue) {
        return this.getTypedValue(index, TAG_SHORT_ID, t -> ((ShortTag) t).getAsShort(), defaultValue);
    }

    /**
     * Retrieves a byte from the list at the specified index.
     *
     * @param index the index of the element
     * @return the byte value at the specified index, or 0 if not found or type mismatch
     */
    public byte getByte(int index) {
        return this.getByte(index, (byte) 0);
    }

    /**
     * Retrieves a byte from the list at the specified index, with a default value.
     *
     * @param index the index of the element
     * @param defaultValue the default value to return if the element is not a byte or does not exist
     * @return the byte value at the specified index, or the default value
     */
    public byte getByte(int index, byte defaultValue) {
        return this.getTypedValue(index, TAG_BYTE_ID, t -> ((ByteTag) t).getAsByte(), defaultValue);
    }

    /**
     * Retrieves a long from the list at the specified index.
     *
     * @param index the index of the element
     * @return the long value at the specified index, or 0 if not found or type mismatch
     */
    public long getLong(int index) {
        return getLong(index, 0);
    }

    /**
     * Retrieves a long from the list at the specified index, with a default value.
     *
     * @param index the index of the element
     * @param defaultValue the default value to return if the element is not a long or does not exist
     * @return the long value at the specified index, or the default value
     */
    public long getLong(int index, long defaultValue) {
        return this.getTypedValue(index, TAG_LONG_ID, t -> ((LongTag) t).getAsLong(), defaultValue);
    }

    /**
     * Retrieves a ListTag from the list at the specified index.
     *
     * @param index the index of the element
     * @return the ListTag at the specified index, or {@code null} if not found or type mismatch
     */
    public ListTag getList(int index) {
        return getList(index, null);
    }

    /**
     * Retrieves a ListTag from the list at the specified index, with a default value.
     *
     * @param index the index of the element
     * @param defaultValue the default value to return if the element is not a ListTag or does not exist
     * @return the ListTag at the specified index, or the default value
     */
    public ListTag getList(int index, ListTag defaultValue) {
        return this.getTypedValue(index, TAG_LIST_ID, t -> (ListTag) t, defaultValue);
    }

    /**
     * Retrieves a CompoundTag from the list at the specified index.
     *
     * @param index the index of the element
     * @return the CompoundTag at the specified index, or {@code null} if not found or type mismatch
     */
    public CompoundTag getCompound(int index) {
        return getCompound(index, null);
    }

    /**
     * Retrieves a CompoundTag from the list at the specified index, with a default value.
     *
     * @param index the index of the element
     * @param defaultValue the default value to return if the element is not a CompoundTag or does not exist
     * @return the CompoundTag at the specified index, or the default value
     */
    public CompoundTag getCompound(int index, CompoundTag defaultValue) {
        return this.getTypedValue(index, TAG_COMPOUND_ID, t -> (CompoundTag) t, defaultValue);
    }

    private <T> T getTypedValue(int index, int expectedId, Function<Tag, T> extractor, T defaultValue) {
        if (index >= 0 && index < this.list.size()) {
            Tag tag = this.list.get(index);
            if (tag.getId() == expectedId) {
                return extractor.apply(tag);
            }
        }
        return defaultValue;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ListTag tags)) return false;
        return Objects.equals(list, tags.list);
    }

    @Override
    public int hashCode() {
        return this.list.hashCode();
    }
}
