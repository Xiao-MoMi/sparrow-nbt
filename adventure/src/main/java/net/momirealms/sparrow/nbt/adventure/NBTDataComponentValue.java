package net.momirealms.sparrow.nbt.adventure;

import net.kyori.adventure.text.event.DataComponentValue;
import net.momirealms.sparrow.nbt.CompoundTag;
import net.momirealms.sparrow.nbt.Tag;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@ApiStatus.NonExtendable
public interface NBTDataComponentValue extends DataComponentValue {

    @NotNull Tag tag();

    default boolean isRemoved() {
        return false;
    }

    static @NotNull NBTDataComponentValue of(@NotNull Tag tag) {
        return new NBTDataComponentValueImpl(tag);
    }

    static @NotNull NBTDataComponentValue nbtDataComponentValue(@NotNull Tag tag) {
        return new NBTDataComponentValueImpl(tag);
    }

    static NBTDataComponentValue removed() {
        return RemovedNBTDataComponentValue.INSTANCE;
    }

    class RemovedNBTDataComponentValue implements NBTDataComponentValue {
        public static final @NotNull RemovedNBTDataComponentValue INSTANCE = new RemovedNBTDataComponentValue();
        private final CompoundTag tag;

        private RemovedNBTDataComponentValue() {
            this.tag = new CompoundTag(Map.of());
        }

        @Override
        public @NotNull Tag tag() {
            return this.tag;
        }

        @Override
        public boolean isRemoved() {
            return true;
        }
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
