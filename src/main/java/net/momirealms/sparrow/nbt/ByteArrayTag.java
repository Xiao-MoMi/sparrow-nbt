package net.momirealms.sparrow.nbt;

import net.momirealms.sparrow.nbt.util.ArrayUtil;

import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

public class ByteArrayTag extends CollectionTag<ByteTag> {
    private byte[] value;

    public ByteArrayTag(final byte[] value) {
        this.value = value;
    }

    @Override
    public ByteTag get(int index) {
        return new ByteTag(this.value[index]);
    }

    @Override
    public ByteTag set(int index, ByteTag tag) {
        byte b0 = this.value[index];
        this.value[index] = tag.getAsByte();
        return new ByteTag(b0);
    }

    @Override
    public void add(int index, ByteTag tag) {
        this.value = ArrayUtil.add(this.value, index, tag.getAsByte());
    }

    @Override
    public ByteTag remove(int index) {
        byte b0 = this.value[index];
        this.value = ArrayUtil.remove(this.value, index);
        return new ByteTag(b0);
    }

    @Override
    public boolean setTag(int index, Tag tag) {
        if (tag instanceof NumericTag) {
            this.value[index] = ((NumericTag) tag).getAsByte();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean addTag(int index, Tag tag) {
        if (tag instanceof NumericTag) {
            this.value = ArrayUtil.add(this.value, index, ((NumericTag) tag).getAsByte());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public byte elementType() {
        return TAG_BYTE_ID;
    }

    @Override
    public int size() {
        return this.value.length;
    }

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeInt(this.value.length);
        output.write(this.value);
    }

    @Override
    public byte getId() {
        return TAG_BYTE_ARRAY_ID;
    }

    @Override
    public TagType<?> getType() {
        return TagTypes.BYTE_ARRAY;
    }

    @Override
    public ByteArrayTag deepClone() {
        return new ByteArrayTag(this.value.clone());
    }

    @Override
    public ByteArrayTag copy() {
        return deepClone();
    }

    @Override
    public void accept(TagVisitor visitor) {
        visitor.visitByteArray(this);
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ByteArrayTag byteTags)) return false;
        return Arrays.equals(value, byteTags.value);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(value);
    }

    @Override
    public void clear() {
        this.value = new byte[0];
    }

    @Override
    public boolean isEmpty() {
        return this.value.length == 0;
    }

    public byte[] getAsByteArray() {
        return this.value;
    }

    @Override
    public String toString() {
        return this.getAsString();
    }
}
