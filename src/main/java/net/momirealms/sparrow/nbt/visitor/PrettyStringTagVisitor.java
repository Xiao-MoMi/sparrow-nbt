package net.momirealms.sparrow.nbt.visitor;

import net.momirealms.sparrow.nbt.*;

import java.util.*;

public class PrettyStringTagVisitor extends CompactStringTagVisitor {

    @Override
    public void visitByteArray(ByteArrayTag element) {
        this.builder.append("[");
        byte[] array = element.getAsByteArray();
        StringJoiner joiner = new StringJoiner(", ");
        for (long l : array) {
            joiner.add(String.valueOf(l));
        }
        this.builder.append(joiner);
        this.builder.append(']');
    }

    @Override
    public void visitIntArray(IntArrayTag element) {
        this.builder.append("[");
        int[] array = element.getAsIntArray();
        StringJoiner joiner = new StringJoiner(", ");
        for (long l : array) {
            joiner.add(String.valueOf(l));
        }
        this.builder.append(joiner);
        this.builder.append(']');
    }

    @Override
    public void visitLongArray(LongArrayTag element) {
        this.builder.append("[");
        long[] array = element.getAsLongArray();
        StringJoiner joiner = new StringJoiner(", ");
        for (long l : array) {
            joiner.add(String.valueOf(l));
        }
        this.builder.append(joiner);
        this.builder.append(']');
    }

    @Override
    public void visitList(ListTag element) {
        this.builder.append("[\r\n");
        StringJoiner joiner = new StringJoiner(",\r\n");
        for (Tag tag : element) {
            joiner.add("   " + (new PrettyStringTagVisitor().visit(tag)).replaceAll("\r\n", "\r\n   "));
        }
        this.builder.append(joiner);
        this.builder.append("\r\n]");
    }

    @Override
    public void visitCompound(CompoundTag compound) {
        this.builder.append("{\r\n");
        List<String> list = new ArrayList<>(compound.keySet());
        Collections.sort(list);
        StringJoiner joiner = new StringJoiner(",\r\n");
        for (String string : list) {
            Tag tag = Objects.requireNonNull(compound.get(string));
            joiner.add("   " + tag.getType().name() + "(\"" + handleEscape(string) + "\"): " + (new PrettyStringTagVisitor()).visit(tag).replaceAll("\r\n", "\r\n   "));
        }
        this.builder.append(joiner);
        this.builder.append("\r\n}");
    }
}
