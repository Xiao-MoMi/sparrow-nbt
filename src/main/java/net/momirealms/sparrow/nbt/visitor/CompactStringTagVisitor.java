package net.momirealms.sparrow.nbt.visitor;

import net.momirealms.sparrow.nbt.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class CompactStringTagVisitor implements TagVisitor {

    protected static final Pattern SIMPLE_VALUE = Pattern.compile("[A-Za-z0-9._+-]+");
    protected final StringBuilder builder = new StringBuilder();

    public String visit(Tag element) {
        element.accept(this);
        return this.builder.toString();
    }

    @Override
    public void visitString(StringTag element) {
        this.builder.append(StringTag.quoteAndEscape(element.getAsString()));
    }

    @Override
    public void visitByte(ByteTag element) {
        this.builder.append(element.getAsNumber()).append('b');
    }

    @Override
    public void visitShort(ShortTag element) {
        this.builder.append(element.getAsNumber()).append('s');
    }

    @Override
    public void visitInt(IntTag element) {
        this.builder.append(element.getAsNumber());
    }

    @Override
    public void visitLong(LongTag element) {
        this.builder.append(element.getAsNumber()).append('L');
    }

    @Override
    public void visitFloat(FloatTag element) {
        this.builder.append(element.getAsFloat()).append('f');
    }

    @Override
    public void visitDouble(DoubleTag element) {
        this.builder.append(element.getAsDouble()).append('d');
    }

    @Override
    public void visitByteArray(ByteArrayTag element) {
        this.builder.append("[B;");
        byte[] array = element.getAsByteArray();
        for(int i = 0; i < array.length; ++i) {
            if (i != 0) {
                this.builder.append(',');
            }
            this.builder.append(array[i]).append('B');
        }
        this.builder.append(']');
    }

    @Override
    public void visitIntArray(IntArrayTag element) {
        this.builder.append("[I;");
        int[] array = element.getAsIntArray();
        for(int i = 0; i < array.length; ++i) {
            if (i != 0) {
                this.builder.append(',');
            }
            this.builder.append(array[i]);
        }
        this.builder.append(']');
    }

    @Override
    public void visitLongArray(LongArrayTag element) {
        this.builder.append("[L;");
        long[] array = element.getAsLongArray();
        for(int i = 0; i < array.length; ++i) {
            if (i != 0) {
                this.builder.append(',');
            }
            this.builder.append(array[i]).append('L');
        }
        this.builder.append(']');
    }

    @Override
    public void visitList(ListTag element) {
        this.builder.append('[');
        for(int i = 0; i < element.size(); ++i) {
            if (i != 0) {
                this.builder.append(',');
            }
            this.builder.append((new CompactStringTagVisitor()).visit(element.get(i)));
        }
        this.builder.append(']');
    }

    @Override
    public void visitCompound(CompoundTag compound) {
        this.builder.append('{');
        List<String> list = new ArrayList<>(compound.keySet());
        Collections.sort(list);
        for (String string : list) {
            if (this.builder.length() != 1) {
                this.builder.append(',');
            }
            this.builder.append(handleEscape(string)).append(':').append((new CompactStringTagVisitor()).visit(Objects.requireNonNull(compound.get(string))));
        }
        this.builder.append('}');
    }

    protected static String handleEscape(String name) {
        return SIMPLE_VALUE.matcher(name).matches() ? name : StringTag.quoteAndEscape(name);
    }

    @Override
    public void visitEnd(EndTag element) {
        this.builder.append("END");
    }
}
