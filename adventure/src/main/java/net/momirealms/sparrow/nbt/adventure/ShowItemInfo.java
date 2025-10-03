package net.momirealms.sparrow.nbt.adventure;

import net.kyori.adventure.key.Key;
import net.momirealms.sparrow.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

public class ShowItemInfo {
    private Key id;
    private int count;
    @Nullable
    private CompoundTag components;
    @Nullable
    private String tag;

    public ShowItemInfo(Key id, int count, @Nullable CompoundTag components) {
        this.components = components;
        this.count = count;
        this.id = id;
        this.tag = null;
    }

    public ShowItemInfo(Key id, int count, @Nullable String tag) {
        this.components = null;
        this.count = count;
        this.id = id;
        this.tag = tag;
    }

    public Key id() {
        return id;
    }

    public int count() {
        return count;
    }

    @Nullable
    public CompoundTag components() {
        return components;
    }

    @Nullable
    public String tag() {
        return tag;
    }

    public void setId(Key id) {
        this.id = id;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setComponents(@Nullable CompoundTag components) {
        this.components = components;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
