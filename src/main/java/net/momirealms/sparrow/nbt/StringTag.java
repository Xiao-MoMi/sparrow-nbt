package net.momirealms.sparrow.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class StringTag implements Tag {
    private final String value;

    public StringTag(String value) {
        this.value = value;
    }

    public static void skipString(DataInput input) throws IOException {
        input.skipBytes(input.readUnsignedShort());
    }

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeUTF(this.value);
    }

    @Override
    public byte getId() {
        return TAG_STRING_ID;
    }

    @Override
    public TagType<?> getType() {
        return TagTypes.STRING;
    }

    @Override
    public StringTag deepClone() {
        return new StringTag(this.value);
    }

    @Override
    public StringTag copy() {
        return this;
    }

    @Override
    public void accept(TagVisitor visitor) {
        visitor.visitString(this);
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StringTag stringTag)) return false;
        return this.value.equals(stringTag.value);
    }

    @Override
    public int hashCode() {
        return this.value.hashCode();
    }

    public static String quoteAndEscape(String value) {
        StringBuilder stringBuilder = new StringBuilder(" ");
        char c = 0;
        for (int i = 0; i < value.length(); ++i) {
            char d = value.charAt(i);
            if (d == '\\') {
                stringBuilder.append('\\');
            } else if (d == '"' || d == '\'') {
                if (c == 0) {
                    c = (char) (d == '"' ? 39 : 34);
                }
                if (c == d) {
                    stringBuilder.append('\\');
                }
            }
            stringBuilder.append(d);
        }
        if (c == 0) {
            c = '"';
        }
        stringBuilder.setCharAt(0, c);
        stringBuilder.append(c);
        return stringBuilder.toString();
    }

    @Override
    public String toString() {
        return Tag.super.getAsString();
    }

    @Override
    public String getAsString() {
        return this.value;
    }
}
