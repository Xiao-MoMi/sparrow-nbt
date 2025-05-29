package net.momirealms.sparrow.nbt;

import net.momirealms.sparrow.nbt.util.ArrayUtil;

import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

public class LongArrayTag extends CollectionTag<LongTag> {

    private long[] value;

    public LongArrayTag(long[] value) {
        this.value = value;
    }

    @Override
    public LongTag get(int index) {
        return new LongTag(value[index]);
    }

    @Override
    public LongTag set(int index, LongTag tag) {
        long l = this.value[index];
        this.value[index] = tag.getAsLong();
        return new LongTag(l);
    }

    @Override
    public void add(int index, LongTag tag) {
        this.value = ArrayUtil.add(this.value, index, tag.getAsLong());
    }

    @Override
    public LongTag remove(int index) {
        long l = this.value[index];
        this.value = ArrayUtil.remove(this.value, index);
        return new LongTag(l);
    }

    @Override
    public boolean setTag(int index, Tag tag) {
        if (tag instanceof NumericTag) {
            this.value[index] = ((NumericTag) tag).getAsLong();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean addTag(int index, Tag tag) {
        if (tag instanceof NumericTag) {
            this.value = ArrayUtil.add(this.value, index, ((NumericTag) tag).getAsLong());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int size() {
        return this.value.length;
    }

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeInt(this.value.length);
        for(long l : this.value) {
            output.writeLong(l);
        }
    }

    @Override
    public byte getId() {
        return TAG_LONG_ARRAY_ID;
    }

    @Override
    public TagType<?> getType() {
        return TagTypes.LONG_ARRAY;
    }

    @Override
    public LongArrayTag deepClone() {
        return new LongArrayTag(this.value.clone());
    }

    @Override
    public LongArrayTag copy() {
        return deepClone();
    }

    @Override
    public void accept(TagVisitor visitor) {
        visitor.visitLongArray(this);
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LongArrayTag longTags)) return false;
        return Arrays.equals(value, longTags.value);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(value);
    }

    @Override
    public void clear() {
        this.value = new long[0];
    }

    @Override
    public boolean isEmpty() {
        return this.value.length == 0;
    }

    public long[] getAsLongArray() {
        return this.value;
    }

    @Override
    public String toString() {
        return this.getAsString();
    }
}
