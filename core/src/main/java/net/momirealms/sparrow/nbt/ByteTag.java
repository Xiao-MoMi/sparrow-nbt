package net.momirealms.sparrow.nbt;

import java.io.DataOutput;
import java.io.IOException;

public final class ByteTag extends NumericTag {
    private final byte value;

    public ByteTag(byte value) {
        this.value = value;
    }

    public ByteTag(boolean b) {
        this.value = (byte) (b ? 1 : 0);
    }

    public boolean booleanValue() {
        return this.value != 0;
    }

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeByte(this.value);
    }

    @Override
    public byte getId() {
        return TAG_BYTE_ID;
    }

    @Override
    public TagType<ByteTag> getType() {
        return TagTypes.BYTE;
    }

    @Override
    public ByteTag deepClone() {
        return new ByteTag(this.value);
    }

    @Override
    public ByteTag copy() {
        return this;
    }

    @Override
    public void accept(TagVisitor visitor) {
        visitor.visitByte(this);
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
        return this.value;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ByteTag byteTag)) return false;
        return value == byteTag.value;
    }

    @Override
    public int hashCode() {
        return value;
    }
}
