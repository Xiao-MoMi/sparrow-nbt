package net.momirealms.sparrow.nbt;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;

public class CompoundTag implements Tag {
    public final Map<String, Tag> tags;

    public CompoundTag(Map<String, Tag> tags) {
        this.tags = tags;
    }

    public CompoundTag() {
        this.tags = new HashMap<>(8, 0.8f);
    }

    public int size() {
        return tags.size();
    }

    @NotNull
    public Set<String> keySet() {
        return this.tags.keySet();
    }

    /**
     * Adds or replaces a tag with the specified key.
     *
     * @param key     the key to associate the tag with
     * @param element the tag to store
     * @return the previous tag associated with the key, or null if none existed
     */
    @Nullable
    public Tag put(@NotNull String key, Tag element) {
        return this.tags.put(key, element);
    }

    /**
     * Adds a byte value to the compound tag with the specified key.
     *
     * @param key   the key to associate with the byte value
     * @param value the byte value to store
     */
    public void putByte(@NotNull String key, byte value) {
        this.tags.put(key, new ByteTag(value));
    }

    /**
     * Adds a boolean value to the compound tag with the specified key.
     * The boolean value is stored as a ByteTag (1 for true, 0 for false).
     *
     * @param key   the key to associate with the boolean value
     * @param value the boolean value to store
     */
    public void putBoolean(@NotNull String key, boolean value) {
        this.tags.put(key, new ByteTag(value));
    }

    /**
     * Adds a short value to the compound tag with the specified key.
     *
     * @param key   the key to associate with the short value
     * @param value the short value to store
     */
    public void putShort(@NotNull String key, short value) {
        this.tags.put(key, new ShortTag(value));
    }

    /**
     * Adds an integer value to the compound tag with the specified key.
     *
     * @param key   the key to associate with the integer value
     * @param value the integer value to store
     */
    public void putInt(@NotNull String key, int value) {
        this.tags.put(key, new IntTag(value));
    }

    /**
     * Adds a long value to the compound tag with the specified key.
     *
     * @param key   the key to associate with the long value
     * @param value the long value to store
     */
    public void putLong(@NotNull String key, long value) {
        this.tags.put(key, new LongTag(value));
    }

    /**
     * Adds a float value to the compound tag with the specified key.
     *
     * @param key   the key to associate with the float value
     * @param value the float value to store
     */
    public void putFloat(@NotNull String key, float value) {
        this.tags.put(key, new FloatTag(value));
    }

    /**
     * Adds a double value to the compound tag with the specified key.
     *
     * @param key   the key to associate with the double value
     * @param value the double value to store
     */
    public void putDouble(@NotNull String key, double value) {
        this.tags.put(key, new DoubleTag(value));
    }

    /**
     * Adds a string value to the compound tag with the specified key.
     *
     * @param key   the key to associate with the string value
     * @param value the string value to store
     */
    public void putString(@NotNull String key, @NotNull String value) {
        this.tags.put(key, new StringTag(value));
    }

    /**
     * Adds a byte array to the compound tag with the specified key.
     *
     * @param key   the key to associate with the byte array
     * @param value the byte array to store
     */
    public void putByteArray(@NotNull String key, byte @NotNull [] value) {
        this.tags.put(key, new ByteArrayTag(value));
    }

    /**
     * Adds a long array to the compound tag with the specified key.
     *
     * @param key   the key to associate with the long array
     * @param value the long array to store
     */
    public void putLongArray(@NotNull String key, long @NotNull [] value) {
        this.tags.put(key, new LongArrayTag(value));
    }

    /**
     * Adds an integer array to the compound tag with the specified key.
     *
     * @param key   the key to associate with the integer array
     * @param value the integer array to store
     */
    public void putIntArray(@NotNull String key, int @NotNull [] value) {
        this.tags.put(key, new IntArrayTag(value));
    }

    /**
     * Adds a UUID value to the compound tag with the specified key.
     * The UUID is converted to an IntArrayTag before storage.
     *
     * @param key   the key to associate with the UUID
     * @param value the UUID value to store
     */
    public void putUUID(@NotNull String key, @NotNull UUID value) {
        this.tags.put(key, NBT.createUUID(value));
    }

    /**
     * Retrieves the tag associated with the specified key.
     *
     * @param key the key to look up
     * @return the tag associated with the key, or null if not found
     */
    @Nullable
    public Tag get(@NotNull String key) {
        return this.tags.get(key);
    }

    /**
     * Retrieves a boolean value associated with the specified key.
     * Defaults to false if the key does not exist or is not a boolean-compatible tag.
     *
     * @param key the key to look up
     * @return the boolean value associated with the key
     */
    public boolean getBoolean(@NotNull String key) {
        return getBoolean(key, false);
    }

    /**
     * Retrieves a boolean value associated with the specified key, with a default fallback.
     *
     * @param key          the key to look up
     * @param defaultValue the default value to return if the key is not found
     * @return the boolean value associated with the key, or the default value if not found
     */
    public boolean getBoolean(@NotNull String key, boolean defaultValue) {
        return getByte(key, (byte) (defaultValue ? 1 : 0)) != 0;
    }

    /**
     * Retrieves a byte value associated with the specified key.
     * Defaults to 0 if the key does not exist or is not a byte-compatible tag.
     *
     * @param key the key to look up
     * @return the byte value associated with the key
     */
    public byte getByte(@NotNull String key) {
        return getByte(key, (byte) 0);
    }

    /**
     * Retrieves a byte value associated with the specified key, with a default fallback.
     *
     * @param key          the key to look up
     * @param defaultValue the default value to return if the key is not found
     * @return the byte value associated with the key, or the default value if not found
     */
    public byte getByte(@NotNull String key, byte defaultValue) {
        return getOrDefault(key, TAG_ANY_NUMERIC_ID, t -> ((NumericTag) t).getAsByte(), defaultValue);
    }

    /**
     * Retrieves a short value associated with the specified key.
     * Defaults to 0 if the key does not exist or is not a short-compatible tag.
     *
     * @param key the key to look up
     * @return the short value associated with the key
     */
    public short getShort(@NotNull String key) {
        return getShort(key, (short) 0);
    }

    /**
     * Retrieves a short value associated with the specified key, with a default fallback.
     *
     * @param key          the key to look up
     * @param defaultValue the default value to return if the key is not found
     * @return the short value associated with the key, or the default value if not found
     */
    public short getShort(@NotNull String key, short defaultValue) {
        return getOrDefault(key, TAG_ANY_NUMERIC_ID, t -> ((NumericTag) t).getAsShort(), defaultValue);
    }

    public int getInt(@NotNull String key) {
        return getInt(key, 0);
    }

    public int getInt(@NotNull String key, int defaultValue) {
        return getOrDefault(key, TAG_ANY_NUMERIC_ID, t -> ((NumericTag) t).getAsInt(), defaultValue);
    }

    public long getLong(@NotNull String key) {
        return getLong(key, 0L);
    }

    public long getLong(@NotNull String key, long defaultValue) {
        return getOrDefault(key, TAG_ANY_NUMERIC_ID, t -> ((NumericTag) t).getAsLong(), defaultValue);
    }

    public float getFloat(String key) {
        return getFloat(key, 0f);
    }

    public float getFloat(@NotNull String key, float defaultValue) {
        return getOrDefault(key, TAG_ANY_NUMERIC_ID, t -> ((NumericTag) t).getAsFloat(), defaultValue);
    }

    public double getDouble(String key) {
        return getDouble(key, 0d);
    }

    public double getDouble(@NotNull String key, double defaultValue) {
        return getOrDefault(key, TAG_ANY_NUMERIC_ID, t -> ((NumericTag) t).getAsDouble(), defaultValue);
    }

    /**
     * Retrieves a string value associated with the specified key.
     * Returns null if the key does not exist or is not a string-compatible tag.
     *
     * @param key the key to look up
     * @return the string value associated with the key, or null if not found
     */
    public String getString(@NotNull String key) {
        return getString(key, null);
    }

    /**
     * Retrieves a string value associated with the specified key, with a default fallback.
     *
     * @param key          the key to look up
     * @param defaultValue the default value to return if the key is not found
     * @return the string value associated with the key, or the default value if not found
     */
    public String getString(@NotNull String key, String defaultValue) {
        return getOrDefault(key, TAG_STRING_ID, Tag::getAsString, defaultValue);
    }

    /**
     * Retrieves a byte array associated with the specified key.
     * Returns null if the key does not exist or is not a byte array-compatible tag.
     *
     * @param key the key to look up
     * @return the byte array associated with the key, or null if not found
     */
    public byte[] getByteArray(String key) {
        return getByteArray(key, null);
    }

    /**
     * Retrieves a byte array associated with the specified key, with a default fallback.
     * The byte array is expected to be stored as a ByteArrayTag in the compound tag.
     *
     * @param key          the key to look up
     * @param defaultValue the default value to return if the key is not found or not a byte array-compatible tag
     * @return the byte array associated with the key, or the default value if not found
     */
    public byte[] getByteArray(@NotNull String key, byte[] defaultValue) {
        return getOrDefault(key, TAG_BYTE_ARRAY_ID, t -> ((ByteArrayTag) t).getAsByteArray(), defaultValue);
    }

    /**
     * Retrieves an integer array associated with the specified key.
     * Returns null if the key does not exist or is not an integer array-compatible tag.
     *
     * @param key the key to look up
     * @return the integer array associated with the key, or null if not found
     */
    public int[] getIntArray(String key) {
        return getIntArray(key, null);
    }

    /**
     * Retrieves an integer array associated with the specified key, with a default fallback.
     * The integer array is expected to be stored as an IntArrayTag in the compound tag.
     *
     * @param key          the key to look up
     * @param defaultValue the default value to return if the key is not found or not an integer array-compatible tag
     * @return the integer array associated with the key, or the default value if not found
     */
    public int[] getIntArray(@NotNull String key, int[] defaultValue) {
        return getOrDefault(key, TAG_INT_ARRAY_ID, t -> ((IntArrayTag) t).getAsIntArray(), defaultValue);
    }

    /**
     * Retrieves a UUID associated with the specified key.
     * Returns null if the key does not exist or is not a UUID-compatible tag.
     *
     * @param key the key to look up
     * @return the UUID associated with the key, or null if not found
     */
    public UUID getUUID(String key) {
        return getUUID(key, null);
    }

    /**
     * Retrieves a UUID associated with the specified key, with a default fallback.
     * The UUID is expected to be stored as an IntArrayTag in the compound tag.
     *
     * @param key          the key to look up
     * @param defaultValue the default value to return if the key is not found or not a UUID-compatible tag
     * @return the UUID associated with the key, or the default value if not found
     */
    public UUID getUUID(@NotNull String key, UUID defaultValue) {
        return getOrDefault(key, TAG_INT_ARRAY_ID, t -> ((IntArrayTag) t).getAsUUID(), defaultValue);
    }

    /**
     * Retrieves a long array associated with the specified key.
     * Returns null if the key does not exist or is not a long array-compatible tag.
     *
     * @param key the key to look up
     * @return the long array associated with the key, or null if not found
     */
    public long[] getLongArray(String key) {
        return getLongArray(key, null);
    }

    /**
     * Retrieves a long array associated with the specified key, with a default fallback.
     *
     * @param key          the key to look up
     * @param defaultValue the default value to return if the key is not found or not a long array-compatible tag
     * @return the long array associated with the key, or the default value if not found
     */
    public long[] getLongArray(@NotNull String key, long[] defaultValue) {
        return getOrDefault(key, TAG_LONG_ARRAY_ID, t -> ((LongArrayTag) t).getAsLongArray(), defaultValue);
    }

    /**
     * Retrieves a CompoundTag associated with the specified key.
     * Returns null if the key does not exist or is not a compound tag.
     *
     * @param key the key to look up
     * @return the CompoundTag associated with the key, or null if not found
     */
    public CompoundTag getCompound(String key) {
        return getCompound(key, null);
    }

    /**
     * Retrieves a CompoundTag associated with the specified key, with a default fallback.
     *
     * @param key          the key to look up
     * @param defaultValue the default value to return if the key is not found or not a compound tag
     * @return the CompoundTag associated with the key, or the default value if not found
     */
    public CompoundTag getCompound(@NotNull String key, CompoundTag defaultValue) {
        return getOrDefault(key, TAG_COMPOUND_ID, t -> (CompoundTag) t, defaultValue);
    }

    /**
     * Retrieves a ListTag associated with the specified key.
     * Returns null if the key does not exist or is not a list tag.
     *
     * @param key the key to look up
     * @return the ListTag associated with the key, or null if not found
     */
    public ListTag getList(String key) {
        return getList(key, null);
    }

    /**
     * Retrieves a ListTag associated with the specified key, with a default fallback.
     *
     * @param key          the key to look up
     * @param defaultValue the default value to return if the key is not found or not a list tag
     * @return the ListTag associated with the key, or the default value if not found
     */
    public ListTag getList(@NotNull String key, ListTag defaultValue) {
        return getOrDefault(key, TAG_LIST_ID, t -> (ListTag) t, defaultValue);
    }

    private <T> T getOrDefault(@NotNull String key, int expectedType, @NotNull Function<Tag, T> extractor, T defaultValue) {
        Tag tag = tags.get(key);
        return tag != null && tag.isTypeOf(expectedType) ? extractor.apply(tag) : defaultValue;
    }

    /**
     * Checks if the compound tag is empty.
     *
     * @return true if the compound tag contains no key-value pairs, otherwise false
     */
    public boolean isEmpty() {
        return this.tags.isEmpty();
    }

    /**
     * Removes the tag associated with the specified key from the compound tag.
     * If the key does not exist, this method does nothing.
     *
     * @param key the key of the tag to be removed
     */
    public void remove(String key) {
        this.tags.remove(key);
    }

    /**
     * Checks if the compound tag contains the specified key.
     *
     * @param key the key to check for
     * @return true if the compound tag contains the key, otherwise false
     */
    public boolean containsKey(String key) {
        return this.tags.containsKey(key);
    }

    /**
     * Checks if the compound tag contains the specified key.
     *
     * @param key the key to check for
     * @return true if the compound tag contains the key, otherwise false
     */
    public byte getTagType(String key) {
        Tag tag = this.tags.get(key);
        return tag == null ? 0 : tag.getId();
    }

    @Override
    public void write(DataOutput output) throws IOException {
        for (Map.Entry<String, Tag> entry : tags.entrySet()) {
            writeNamedTag(entry.getKey(), entry.getValue(), output);
        }
        output.writeByte(TAG_END_ID);
    }

    private static void writeNamedTag(String key, Tag element, DataOutput output) throws IOException {
        output.writeByte(element.getId());
        if (element.getId() != Tag.TAG_END_ID) {
            output.writeUTF(key);
            element.write(output);
        }
    }

    @Override
    public byte getId() {
        return TAG_COMPOUND_ID;
    }

    @Override
    public TagType<?> getType() {
        return TagTypes.COMPOUND;
    }

    @Override
    public CompoundTag copy() {
        Map<String, Tag> newTags = new HashMap<>(tags.size(), 0.8f);
        for (Map.Entry<String, Tag> entry : tags.entrySet()) {
            newTags.put(entry.getKey(), entry.getValue().copy());
        }
        return new CompoundTag(newTags);
    }

    @Override
    public CompoundTag deepClone() {
        Map<String, Tag> newTags = new HashMap<>(tags.size(), 0.8f);
        for (Map.Entry<String, Tag> entry : tags.entrySet()) {
            newTags.put(entry.getKey(), entry.getValue().deepClone());
        }
        return new CompoundTag(newTags);
    }

    @Override
    public void accept(TagVisitor visitor) {
        visitor.visitCompound(this);
    }

    /**
     * Reads the type ID of a named tag from the provided DataInput stream.
     *
     * @param input the DataInput stream to read from
     * @return the byte ID representing the tag type
     * @throws IOException if an I/O error occurs while reading
     */
    static byte readNamedTagType(DataInput input) throws IOException {
        return input.readByte();
    }

    /**
     * Reads the name of a named tag from the provided DataInput stream.
     *
     * @param input the DataInput stream to read from
     * @return the string representing the tag name
     * @throws IOException if an I/O error occurs while reading
     */
    static String readNamedTagName(DataInput input) throws IOException {
        return input.readUTF();
    }

    /**
     * Reads the data for a named tag using the specified TagType reader and depth.
     * Wraps any IOException into a RuntimeException for simplified error handling.
     *
     * @param reader the TagType responsible for reading the tag's data
     * @param input  the DataInput stream to read from
     * @param depth  the current depth of the tag structure (used for recursive reading)
     * @return the Tag instance read from the input
     * @throws RuntimeException if an IOException occurs while reading
     */
    static Tag readNamedTagData(TagType<?> reader, DataInput input, int depth) {
        try {
            return reader.read(input, depth);
        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        }
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CompoundTag that)) return false;
        return Objects.equals(tags, that.tags);
    }

    @Override
    public int hashCode() {
        return tags.hashCode();
    }

    @Override
    public String toString() {
        return this.getAsString();
    }
}
