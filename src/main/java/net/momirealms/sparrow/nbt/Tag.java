package net.momirealms.sparrow.nbt;

import net.momirealms.sparrow.nbt.visitor.CompactStringTagVisitor;

import java.io.DataOutput;
import java.io.IOException;

public interface Tag {

    byte TAG_END_ID = 0;
    byte TAG_BYTE_ID = 1;
    byte TAG_SHORT_ID = 2;
    byte TAG_INT_ID = 3;
    byte TAG_LONG_ID = 4;
    byte TAG_FLOAT_ID = 5;
    byte TAG_DOUBLE_ID = 6;
    byte TAG_BYTE_ARRAY_ID = 7;
    byte TAG_STRING_ID = 8;
    byte TAG_LIST_ID = 9;
    byte TAG_COMPOUND_ID = 10;
    byte TAG_INT_ARRAY_ID = 11;
    byte TAG_LONG_ARRAY_ID = 12;
    byte TAG_ANY_NUMERIC_ID = 99;

    void write(DataOutput output) throws IOException;

    @Override
    String toString();

    byte getId();

    TagType<?> getType();

    Tag copy();

    Tag deepClone();

    default String getAsString() {
        return (new CompactStringTagVisitor()).visit(this);
    }

    void accept(TagVisitor visitor);

    default boolean isTypeOf(int type) {
        int i = getId();
        if (i == type) {
            return true;
        } else if (type != TAG_ANY_NUMERIC_ID) {
            return false;
        } else {
            return isNumericTag(i);
        }
    }

    static boolean isNumericTag(int tagType) {
        return switch (tagType) {
            case TAG_BYTE_ID, TAG_SHORT_ID, TAG_INT_ID, TAG_LONG_ID, TAG_FLOAT_ID, TAG_DOUBLE_ID -> true;
            default -> false;
        };
    }
}