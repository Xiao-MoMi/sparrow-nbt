package net.momirealms.sparrow.nbt;

import net.momirealms.sparrow.nbt.util.MathUtil;

import java.io.DataOutput;
import java.io.IOException;

public class DoubleTag extends NumericTag {

    private final double value;

    public DoubleTag(double value) {
        this.value = value;
    }

    public double value() {
        return value;
    }

    @Override
    public long getAsLong() {
        return (long) Math.floor(this.value);
    }

    @Override
    public int getAsInt() {
        return MathUtil.fastFloor(this.value);
    }

    @Override
    public short getAsShort() {
        return (short) (MathUtil.fastFloor(this.value) & 65535);
    }

    @Override
    public byte getAsByte() {
        return (byte) (MathUtil.fastFloor(this.value) & 255);
    }

    @Override
    public double getAsDouble() {
        return this.value;
    }

    @Override
    public float getAsFloat() {
        return (float) this.value;
    }

    @Override
    public Number getAsNumber() {
        return this.value;
    }

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeDouble(this.value);
    }

    @Override
    public byte getId() {
        return TAG_DOUBLE_ID;
    }

    @Override
    public TagType<?> getType() {
        return TagTypes.DOUBLE;
    }

    @Override
    public DoubleTag deepClone() {
        return new DoubleTag(this.value);
    }

    @Override
    public DoubleTag copy() {
        return this;
    }

    @Override
    public void accept(TagVisitor visitor) {
        visitor.visitDouble(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DoubleTag doubleTag)) return false;
        return value == doubleTag.value;
    }

    @Override
    public int hashCode() {
        return Double.hashCode(this.value);
    }
}
