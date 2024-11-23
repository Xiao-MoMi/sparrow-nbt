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

    @NotNull
    public Set<String> keySet() {
        return this.tags.keySet();
    }

    @Nullable
    public Tag put(@NotNull String key, Tag element) {
        return this.tags.put(key, element);
    }

    public void putByte(@NotNull String key, byte value) {
        this.tags.put(key, new ByteTag(value));
    }

    public void putBoolean(@NotNull String key, boolean value) {
        this.tags.put(key, new ByteTag(value));
    }

    public void putShort(@NotNull String key, short value) {
        this.tags.put(key, new ShortTag(value));
    }

    public void putInt(@NotNull String key, int value) {
        this.tags.put(key, new IntTag(value));
    }

    public void putLong(@NotNull String key, long value) {
        this.tags.put(key, new LongTag(value));
    }

    public void putFloat(@NotNull String key, float value) {
        this.tags.put(key, new FloatTag(value));
    }

    public void putDouble(@NotNull String key, double value) {
        this.tags.put(key, new DoubleTag(value));
    }

    public void putString(@NotNull String key, @NotNull String value) {
        this.tags.put(key, new StringTag(value));
    }

    public void putByteArray(@NotNull String key, byte @NotNull [] value) {
        this.tags.put(key, new ByteArrayTag(value));
    }

    public void putLongArray(@NotNull String key, long @NotNull [] value) {
        this.tags.put(key, new LongArrayTag(value));
    }

    public void putIntArray(@NotNull String key, int @NotNull [] value) {
        this.tags.put(key, new IntArrayTag(value));
    }

    public void putUUID(@NotNull String key, @NotNull UUID value) {
        this.tags.put(key, NBT.createUUID(value));
    }

    @Nullable
    public Tag get(@NotNull String key) {
        return this.tags.get(key);
    }

    public boolean getBoolean(@NotNull String key) {
        return getBoolean(key, false);
    }

    public boolean getBoolean(@NotNull String key, boolean defaultValue) {
        return getByte(key, (byte) (defaultValue ? 1 : 0)) != 0;
    }

    public byte getByte(@NotNull String key) {
        return getByte(key, (byte) 0);
    }

    public byte getByte(@NotNull String key, byte defaultValue) {
        return getOrDefault(key, TAG_ANY_NUMERIC_ID, t -> ((NumericTag) t).getAsByte(), defaultValue);
    }

    public short getShort(@NotNull String key) {
        return getShort(key, (short) 0);
    }

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

    public String getString(@NotNull String key) {
        return getString(key, null);
    }

    public String getString(@NotNull String key, String defaultValue) {
        return getOrDefault(key, TAG_STRING_ID, Tag::getAsString, defaultValue);
    }

    public byte[] getByteArray(String key) {
        return getByteArray(key, null);
    }

    public byte[] getByteArray(@NotNull String key, byte[] defaultValue) {
        return getOrDefault(key, TAG_BYTE_ARRAY_ID, t -> ((ByteArrayTag) t).getAsByteArray(), defaultValue);
    }

    public int[] getIntArray(String key) {
        return getIntArray(key, null);
    }

    public int[] getIntArray(@NotNull String key, int[] defaultValue) {
        return getOrDefault(key, TAG_INT_ARRAY_ID, t -> ((IntArrayTag) t).getAsIntArray(), defaultValue);
    }

    public UUID getUUID(String key) {
        return getUUID(key, null);
    }

    public UUID getUUID(@NotNull String key, UUID defaultValue) {
        return getOrDefault(key, TAG_INT_ARRAY_ID, t -> ((IntArrayTag) t).getAsUUID(), defaultValue);
    }

    public long[] getLongArray(String key) {
        return getLongArray(key, null);
    }

    public long[] getLongArray(@NotNull String key, long[] defaultValue) {
        return getOrDefault(key, TAG_LONG_ARRAY_ID, t -> ((LongArrayTag) t).getAsLongArray(), defaultValue);
    }

    public CompoundTag getCompound(String key) {
        return getCompound(key, null);
    }

    public CompoundTag getCompound(@NotNull String key, CompoundTag defaultValue) {
        return getOrDefault(key, TAG_COMPOUND_ID, t -> (CompoundTag) t, defaultValue);
    }

    public ListTag getList(String key) {
        return getList(key, null);
    }

    public ListTag getList(@NotNull String key, ListTag defaultValue) {
        return getOrDefault(key, TAG_LIST_ID, t -> (ListTag) t, defaultValue);
    }

    private <T> T getOrDefault(@NotNull String key, int expectedType, @NotNull Function<Tag, T> extractor, T defaultValue) {
        Tag tag = tags.get(key);
        return tag != null && tag.isTypeOf(expectedType) ? extractor.apply(tag) : defaultValue;
    }

    public boolean isEmpty() {
        return this.tags.isEmpty();
    }

    public void remove(String key) {
        this.tags.remove(key);
    }

    public boolean containsKey(String key) {
        return this.tags.containsKey(key);
    }

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
    public Tag copy() {
        Map<String, Tag> newTags = new HashMap<>(tags.size(), 0.8f);
        for (Map.Entry<String, Tag> entry : tags.entrySet()) {
            newTags.put(entry.getKey(), entry.getValue().copy());
        }
        return new CompoundTag(newTags);
    }

    @Override
    public Tag deepClone() {
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

    static byte readNamedTagType(DataInput input) throws IOException {
        return input.readByte();
    }

    static String readNamedTagName(DataInput input) throws IOException {
        return input.readUTF();
    }

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
