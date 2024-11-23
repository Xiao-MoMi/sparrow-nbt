package net.momirealms.sparrow.nbt;

import java.io.DataInput;
import java.io.IOException;

public interface TagType<T extends Tag> {

    String name();

    void skip(DataInput input, int count) throws IOException;

    void skip(DataInput input) throws IOException;

    T read(DataInput input, int depth) throws IOException;

    default boolean isValue() {
        return false;
    }

    interface FixedSize<T extends Tag> extends TagType<T> {

        int size();

        @Override
        default void skip(DataInput input) throws IOException {
            input.skipBytes(this.size());
        }

        @Override
        default void skip(DataInput input, int count) throws IOException {
            input.skipBytes(this.size() * count);
        }
    }

    interface FlexibleSize<T extends Tag> extends TagType<T> {

        @Override
        default void skip(DataInput input, int count) throws IOException {
            for(int i = 0; i < count; ++i) {
                this.skip(input);
            }
        }
    }
}
