package net.momirealms.sparrow.nbt.serializer;

import net.kyori.adventure.text.event.ClickEvent;
import net.momirealms.sparrow.nbt.CompoundTag;
import net.momirealms.sparrow.nbt.Tag;
import org.jetbrains.annotations.NotNull;

class NBTClickEventSerializer {
    private static final String ACTION = "action";
    private static final String VALUE = "value";

    private NBTClickEventSerializer() {
    }

    static @NotNull ClickEvent deserialize(@NotNull CompoundTag tag) {
        Tag actionTag = tag.get(ACTION);
        if (actionTag == null) {
            throw new IllegalArgumentException("The serialized click event doesn't contain an action");
        }
        String actionString = actionTag.getAsString();
        ClickEvent.Action action = ClickEvent.Action.NAMES.valueOrThrow(actionString);
        return ClickEvent.clickEvent(action, tag.getString(VALUE));
    }

    static @NotNull CompoundTag serialize(@NotNull ClickEvent event) {
        CompoundTag tag = new CompoundTag();
        tag.putString(ACTION, ClickEvent.Action.NAMES.keyOrThrow(event.action()));
        tag.putString(VALUE, event.value());
        return tag;
    }
}
