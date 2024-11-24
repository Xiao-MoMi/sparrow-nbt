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

    /**
     * Escapes special characters in the given string and wraps the result in quotes.
     * The function dynamically determines whether to use single quotes ('') or double quotes ("")
     * based on the contents of the string, escaping quotes and backslashes as needed.
     *
     * @param value the input string to be quoted and escaped
     * @return the escaped and quoted string
     */
    public static String quoteAndEscape(String value) {
        StringBuilder stringBuilder = new StringBuilder(" ");
        char quoteChar = 0;
        for (int i = 0; i < value.length(); ++i) {
            char currentChar = value.charAt(i);
            if (currentChar == '\\') {
                stringBuilder.append('\\');
            } else if (currentChar == '"' || currentChar == '\'') {
                if (quoteChar == 0) {
                    quoteChar = currentChar == '"' ? '\'' : '"';
                }
                if (quoteChar == currentChar) {
                    stringBuilder.append('\\');
                }
            }
            stringBuilder.append(currentChar);
        }
        if (quoteChar == 0) {
            quoteChar = '"';
        }
        stringBuilder.setCharAt(0, quoteChar);
        stringBuilder.append(quoteChar);
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
