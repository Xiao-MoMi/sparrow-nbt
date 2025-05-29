package net.momirealms.sparrow.nbt.adventure;

import net.kyori.adventure.text.event.ClickEvent;
import net.momirealms.sparrow.nbt.CompoundTag;
import net.momirealms.sparrow.nbt.Tag;
import org.jetbrains.annotations.NotNull;

class NBTClickEventSerializer {
    public static final String ACTION = "action";
    public static final String CLICK_EVENT_VALUE = "value";
    public static final String CLICK_EVENT_URL = "url";
    public static final String CLICK_EVENT_PATH = "path";
    public static final String CLICK_EVENT_COMMAND = "command";
    public static final String CLICK_EVENT_PAGE = "page";

    private NBTClickEventSerializer() {
    }

    static @NotNull ClickEvent deserialize(@NotNull CompoundTag tag, @NotNull NBTComponentSerializerImpl serializer) {
        Tag actionTag = tag.get(ACTION);
        if (actionTag == null) {
            throw new IllegalArgumentException("The serialized click event doesn't contain an action");
        }
        String actionString = actionTag.getAsString();
        ClickEvent.Action action = ClickEvent.Action.NAMES.valueOrThrow(actionString);
        if (serializer.modernClickEvent) {
            if (action == ClickEvent.Action.OPEN_URL) {
                return ClickEvent.clickEvent(action, tag.getString(CLICK_EVENT_URL));
            } else if (action == ClickEvent.Action.SUGGEST_COMMAND || action == ClickEvent.Action.RUN_COMMAND) {
                return ClickEvent.clickEvent(action, tag.getString(CLICK_EVENT_COMMAND));
            } else if (action == ClickEvent.Action.COPY_TO_CLIPBOARD) {
                return ClickEvent.clickEvent(action, tag.getString(CLICK_EVENT_VALUE));
            } else if (action == ClickEvent.Action.CHANGE_PAGE) {
                return ClickEvent.clickEvent(action, tag.getString(CLICK_EVENT_PAGE));
            } else {
                // action == ClickEvent.Action.OPEN_FILE
                return ClickEvent.clickEvent(action, tag.getString(CLICK_EVENT_PATH));
            }
        } else {
            return ClickEvent.clickEvent(action, tag.getString(CLICK_EVENT_VALUE));
        }
    }

    static @NotNull CompoundTag serialize(@NotNull ClickEvent event, @NotNull NBTComponentSerializerImpl serializer) {
        CompoundTag tag = new CompoundTag();
        if (serializer.modernClickEvent) {
            ClickEvent.Action action = event.action();
            tag.putString(ACTION, ClickEvent.Action.NAMES.keyOrThrow(event.action()));
            if (action == ClickEvent.Action.OPEN_URL) {
                tag.putString(CLICK_EVENT_URL, event.value());
            } else if (action == ClickEvent.Action.SUGGEST_COMMAND || action == ClickEvent.Action.RUN_COMMAND) {
                tag.putString(CLICK_EVENT_COMMAND, event.value());
            } else if (action == ClickEvent.Action.COPY_TO_CLIPBOARD) {
                tag.putString(CLICK_EVENT_VALUE, event.value());
            } else if (action == ClickEvent.Action.CHANGE_PAGE) {
                tag.putString(CLICK_EVENT_PAGE, event.value());
            } else if (action == ClickEvent.Action.OPEN_FILE) {
                tag.putString(CLICK_EVENT_PATH, event.value());
            }
        } else {
            tag.putString(ACTION, ClickEvent.Action.NAMES.keyOrThrow(event.action()));
            tag.putString(CLICK_EVENT_VALUE, event.value());
        }
        return tag;
    }
}
