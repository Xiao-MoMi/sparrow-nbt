package net.momirealms.sparrow.nbt.adventure;

import net.kyori.adventure.key.Key;
import net.momirealms.sparrow.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

public class NBTItem {
    private final Key id;
    private final int count;
    @Nullable
    private CompoundTag components;

    public NBTItem(Key id, int count, @Nullable CompoundTag components) {
        this.components = components;
        this.count = count;
        this.id = id;
    }

    public CompoundTag components() {
        return components;
    }

    public void setComponents(@Nullable CompoundTag components) {
        this.components = components;
    }

    public int count() {
        return count;
    }

    public Key id() {
        return id;
    }
}
