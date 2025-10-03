package net.momirealms.sparrow.nbt.adventure;

import net.kyori.adventure.text.event.DataComponentValue;
import net.momirealms.sparrow.nbt.Tag;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.NonExtendable
public interface NBTDataComponentValue extends DataComponentValue {

    @NotNull Tag tag();

    static @NotNull NBTDataComponentValue of(@NotNull Tag tag) {
        return new NBTDataComponentValueImpl(tag);
    }

    static @NotNull NBTDataComponentValue nbtDataComponentValue(@NotNull Tag tag) {
        return new NBTDataComponentValueImpl(tag);
    }

    class NBTDataComponentValueImpl implements NBTDataComponentValue {
        private final Tag tag;

        NBTDataComponentValueImpl(@NotNull Tag tag) {
            this.tag = tag;
        }

        @NotNull
        @Override
        public Tag tag() {
            return this.tag;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof NBTDataComponentValueImpl that)) return false;
            return this.tag.equals(that.tag());
        }

        @Override
        public int hashCode() {
            return this.tag.hashCode();
        }
    }
}
