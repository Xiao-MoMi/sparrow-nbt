package net.momirealms.sparrow.nbt;

import java.io.DataOutput;
import java.io.IOException;

public class ShortTag extends NumericTag {
    private final short value;

    public ShortTag(final short value) {
        this.value = value;
    }

    @Override
    public long getAsLong() {
        return this.value;
    }

    @Override
    public int getAsInt() {
        return this.value;
    }

    @Override
    public short getAsShort() {
        return this.value;
    }

    @Override
    public byte getAsByte() {
        return (byte) (this.value & 255);
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
        output.writeShort(this.value);
    }

    @Override
    public byte getId() {
        return TAG_SHORT_ID;
    }

    @Override
    public TagType<?> getType() {
        return TagTypes.SHORT;
    }

    @Override
    public ShortTag deepClone() {
        return new ShortTag(this.value);
    }

    @Override
    public ShortTag copy() {
        return this;
    }

    @Override
    public void accept(TagVisitor visitor) {
        visitor.visitShort(this);
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ShortTag shortTag)) return false;
        return value == shortTag.value;
    }

    @Override
    public int hashCode() {
        return value;
    }
}
