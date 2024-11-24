package net.momirealms.sparrow.nbt;

/**
 * Interface for visiting different types of NBT (Named Binary Tag) elements.
 */
public interface TagVisitor {

    /**
     * Visit a StringTag element.
     *
     * @param element the StringTag to visit
     */
    void visitString(StringTag element);

    /**
     * Visit a ByteTag element.
     *
     * @param element the ByteTag to visit
     */
    void visitByte(ByteTag element);

    /**
     * Visit a ShortTag element.
     *
     * @param element the ShortTag to visit
     */
    void visitShort(ShortTag element);

    /**
     * Visit an IntTag element.
     *
     * @param element the IntTag to visit
     */
    void visitInt(IntTag element);

    /**
     * Visit a LongTag element.
     *
     * @param element the LongTag to visit
     */
    void visitLong(LongTag element);

    /**
     * Visit a FloatTag element.
     *
     * @param element the FloatTag to visit
     */
    void visitFloat(FloatTag element);

    /**
     * Visit a DoubleTag element.
     *
     * @param element the DoubleTag to visit
     */
    void visitDouble(DoubleTag element);

    /**
     * Visit a ByteArrayTag element.
     *
     * @param element the ByteArrayTag to visit
     */
    void visitByteArray(ByteArrayTag element);

    /**
     * Visit an IntArrayTag element.
     *
     * @param element the IntArrayTag to visit
     */
    void visitIntArray(IntArrayTag element);

    /**
     * Visit a LongArrayTag element.
     *
     * @param element the LongArrayTag to visit
     */
    void visitLongArray(LongArrayTag element);

    /**
     * Visit a ListTag element.
     *
     * @param element the ListTag to visit
     */
    void visitList(ListTag element);

    /**
     * Visit a CompoundTag element, representing a collection of key-value pairs.
     *
     * @param compound the CompoundTag to visit
     */
    void visitCompound(CompoundTag compound);

    /**
     * Visit an EndTag element, which is used to signify the end of a compound or list tag.
     *
     * @param element the EndTag to visit
     */
    void visitEnd(EndTag element);
}
