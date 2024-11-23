package net.momirealms.sparrow.nbt;

import net.momirealms.sparrow.nbt.util.ArrayUtil;
import net.momirealms.sparrow.nbt.util.UUIDUtil;

import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

public class IntArrayTag extends CollectionTag<IntTag> {

    private int[] value;

    public IntArrayTag(int[] value) {
        this.value = value;
    }

    @Override
    public IntTag get(int index) {
        return new IntTag(this.value[index]);
    }

    @Override
    public IntTag set(int index, IntTag tag) {
        int j = this.value[index];
        this.value[index] = tag.getAsInt();
        return new IntTag(j);
    }

    @Override
    public void add(int index, IntTag tag) {
        this.value = ArrayUtil.add(this.value, index, tag.getAsInt());
    }

    @Override
    public IntTag remove(int index) {
        int j = this.value[index];
        this.value = ArrayUtil.remove(this.value, index);
        return new IntTag(j);
    }

    @Override
    public boolean setTag(int index, Tag tag) {
        if (tag instanceof NumericTag) {
            this.value[index] = ((NumericTag) tag).getAsInt();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean addTag(int index, Tag tag) {
        if (tag instanceof NumericTag) {
            this.value = ArrayUtil.add(this.value, index, ((NumericTag) tag).getAsInt());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public byte elementType() {
        return TAG_INT_ID;
    }

    @Override
    public int size() {
        return this.value.length;
    }

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeInt(this.value.length);
        for (int k : this.value) {
            output.writeInt(k);
        }
    }

    @Override
    public byte getId() {
        return TAG_INT_ARRAY_ID;
    }

    @Override
    public TagType<?> getType() {
        return TagTypes.INT;
    }

    @Override
    public IntArrayTag deepClone() {
        return new IntArrayTag(this.value.clone());
    }

    @Override
    public IntArrayTag copy() {
        return deepClone();
    }

    @Override
    public void accept(TagVisitor visitor) {
        visitor.visitIntArray(this);
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IntArrayTag intTags)) return false;
        return Arrays.equals(value, intTags.value);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(value);
    }

    @Override
    public void clear() {
        this.value = new int[0];
    }

    @Override
    public boolean isEmpty() {
        return this.value.length == 0;
    }

    public int[] getAsIntArray() {
        return this.value;
    }

    public UUID getAsUUID() {
        if (this.value.length != 4) {
            throw new IllegalArgumentException("Failed to convert IntArray into UUID because the length of the array is " + value.length + " which is expected to be 4.");
        } else {
            return UUIDUtil.uuidFromIntArray(this.value);
        }
    }

    @Override
    public String toString() {
        return this.getAsString();
    }
}
