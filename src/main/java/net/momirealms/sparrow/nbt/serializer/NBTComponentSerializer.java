package net.momirealms.sparrow.nbt.serializer;

import net.kyori.adventure.builder.AbstractBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import net.kyori.adventure.util.PlatformAPI;
import net.kyori.option.OptionState;
import net.momirealms.sparrow.nbt.CompoundTag;
import net.momirealms.sparrow.nbt.Tag;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public interface NBTComponentSerializer extends ComponentSerializer<Component, Component, Tag> {

    @NotNull Style deserializeStyle(@NotNull CompoundTag tag);

    @NotNull CompoundTag serializeStyle(@NotNull Style style);

    static @NotNull NBTComponentSerializer nbt() {
        return NBTComponentSerializerImpl.Instances.INSTANCE;
    }

    static @NotNull Builder builder() {
        return new NBTComponentSerializerImpl.BuilderImpl();
    }

    interface Builder extends AbstractBuilder<NBTComponentSerializer> {

        @NotNull Builder options(final @NotNull OptionState flags);

        @NotNull Builder editOptions(final @NotNull Consumer<OptionState.Builder> optionEditor);

        @NotNull Builder editItem(@NotNull Consumer<NBTItem> itemEditor);

        @Override
        @NotNull NBTComponentSerializer build();
    }

    @ApiStatus.Internal
    @PlatformAPI
    interface Provider {

        @ApiStatus.Internal
        @PlatformAPI
        @NotNull NBTComponentSerializer nbt();

        @ApiStatus.Internal
        @PlatformAPI
        @NotNull Consumer<Builder> builder();
    }
}
