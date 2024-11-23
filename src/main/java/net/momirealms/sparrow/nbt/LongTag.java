package net.momirealms.sparrow.nbt;

import java.io.DataOutput;
import java.io.IOException;

public class LongTag extends NumericTag {
    private final long value;

    public LongTag(final long value) {
        this.value = value;
    }

    @Override
    public long getAsLong() {
        return this.value;
    }

    @Override
    public int getAsInt() {
        return (int) this.value;
    }

    @Override
    public short getAsShort() {
        return (short) ((int) (this.value & 65535L));
    }

    @Override
    public byte getAsByte() {
        return (byte) ((int) (this.value & 255L));
    }

    @Override
    public double getAsDouble() {
        return this.value;
    }

    @Override
    public float getAsFloat() {
        return this.value;
    }

    @Override
    public Number getAsNumber() {
        return this.value;
    }

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeLong(this.value);
    }

    @Override
    public byte getId() {
        return TAG_LONG_ID;
    }

    @Override
    public TagType<?> getType() {
        return TagTypes.LONG;
    }

    @Override
    public LongTag deepClone() {
        return new LongTag(this.value);
    }

    @Override
    public LongTag copy() {
        return this;
    }

    @Override
    public void accept(TagVisitor visitor) {
        visitor.visitLong(this);
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LongTag longTag)) return false;
        return value == longTag.value;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(value);
    }
}
