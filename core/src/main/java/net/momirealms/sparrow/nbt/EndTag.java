package net.momirealms.sparrow.nbt;

import java.io.DataOutput;

public final class EndTag implements Tag {

    public static final EndTag INSTANCE = new EndTag();

    private EndTag() {
    }

    @Override
    public EndTag deepClone() {
        return new EndTag();
    }

    @Override
    public Tag copy() {
        return this;
    }

    @Override
    public void accept(TagVisitor visitor) {
        visitor.visitEnd(this);
    }

    @Override
    public void write(DataOutput output) {
    }

    @Override
    public byte getId() {
        return TAG_END_ID;
    }

    @Override
    public TagType<?> getType() {
        return TagTypes.END;
    }

    @Override
    public String toString() {
        return this.getAsString();
    }
}
