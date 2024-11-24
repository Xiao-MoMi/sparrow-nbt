package net.momirealms.sparrow.nbt;

/**
 * Abstract class representing a numeric NBT (Named Binary Tag) element.
 * This serves as a base class for all tags that hold numeric values,
 * providing a common interface for accessing the value in various numeric forms.
 */
public abstract class NumericTag implements Tag {

    /**
     * Protected constructor to prevent instantiation of this abstract class.
     */
    protected NumericTag() {}

    /**
     * Gets the value of this tag as a long.
     *
     * @return the numeric value as a long
     */
    public abstract long getAsLong();

    /**
     * Gets the value of this tag as an int.
     *
     * @return the numeric value as an int
     */
    public abstract int getAsInt();

    /**
     * Gets the value of this tag as a short.
     *
     * @return the numeric value as a short
     */
    public abstract short getAsShort();

    /**
     * Gets the value of this tag as a byte.
     *
     * @return the numeric value as a byte
     */
    public abstract byte getAsByte();

    /**
     * Gets the value of this tag as a double.
     *
     * @return the numeric value as a double
     */
    public abstract double getAsDouble();

    /**
     * Gets the value of this tag as a float.
     *
     * @return the numeric value as a float
     */
    public abstract float getAsFloat();

    /**
     * Gets the value of this tag as a generic Number object.
     * This can represent the value in its most natural numeric type.
     *
     * @return the numeric value as a Number
     */
    public abstract Number getAsNumber();

    @Override
    public String toString() {
        return this.getAsString();
    }
}
