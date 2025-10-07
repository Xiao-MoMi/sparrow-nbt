package net.momirealms.sparrow.nbt.adventure;

import net.kyori.adventure.key.Key;
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
    public static final String CLICK_EVENT_DIALOG = "dialog";
    public static final String CLICK_EVENT_CUSTOM_ID = "id";
    public static final String CLICK_EVENT_CUSTOM_PAYLOAD = "payload";

    private NBTClickEventSerializer() {
    }

    @SuppressWarnings("all")
    static @NotNull ClickEvent deserialize(@NotNull CompoundTag tag, @NotNull NBTComponentSerializerImpl serializer) {
        Tag actionTag = tag.get(ACTION);
        if (actionTag == null) {
            throw new IllegalArgumentException("The serialized click event doesn't contain an action");
        }
        String actionString = actionTag.getAsString();
        ClickEvent.Action action = ClickEvent.Action.NAMES.valueOrThrow(actionString);
        if (serializer.modernClickEvent) {
            switch (action) {
                case OPEN_URL -> {
                    return ClickEvent.clickEvent(action, ClickEvent.Payload.string(tag.getString(CLICK_EVENT_URL)));
                }
                case OPEN_FILE -> {
                    return ClickEvent.clickEvent(action, ClickEvent.Payload.string(tag.getString(CLICK_EVENT_PATH)));
                }
                case CHANGE_PAGE -> {
                    return ClickEvent.clickEvent(action, ClickEvent.Payload.integer(tag.getInt(CLICK_EVENT_PAGE)));
                }
                case COPY_TO_CLIPBOARD -> {
                    return ClickEvent.clickEvent(action, ClickEvent.Payload.string(tag.getString(CLICK_EVENT_VALUE)));
                }
                case RUN_COMMAND, SUGGEST_COMMAND -> {
                    return ClickEvent.clickEvent(action, ClickEvent.Payload.string(tag.getString(CLICK_EVENT_COMMAND)));
                }
                case SHOW_DIALOG -> {
                    return ClickEvent.clickEvent(action, ClickEvent.Payload.dialog(NBTDialog.of(tag.get(CLICK_EVENT_DIALOG))));
                }
                case CUSTOM -> {
                    return ClickEvent.clickEvent(action, ClickEvent.Payload.custom(Key.key(tag.getString(CLICK_EVENT_CUSTOM_ID)), tag.getString(CLICK_EVENT_CUSTOM_PAYLOAD)));
                }
                default -> throw new IllegalArgumentException("The serialized click event doesn't contain an action");
            }
        } else {
            return ClickEvent.clickEvent(action, ClickEvent.Payload.string(tag.getString(CLICK_EVENT_VALUE)));
        }
    }

    static @NotNull CompoundTag serialize(@NotNull ClickEvent event, @NotNull NBTComponentSerializerImpl serializer) {
        CompoundTag tag = new CompoundTag();
        ClickEvent.Payload payload = event.payload();
        if (serializer.modernClickEvent) {
            ClickEvent.Action action = event.action();
            tag.putString(ACTION, ClickEvent.Action.NAMES.keyOrThrow(event.action()));
            switch (action) {
                case OPEN_URL -> tag.putString(CLICK_EVENT_URL, ((ClickEvent.Payload.Text) payload).value());
                case SUGGEST_COMMAND, RUN_COMMAND -> tag.putString(CLICK_EVENT_COMMAND, ((ClickEvent.Payload.Text) payload).value());
                case COPY_TO_CLIPBOARD -> tag.putString(CLICK_EVENT_VALUE, ((ClickEvent.Payload.Text) payload).value());
                case CHANGE_PAGE -> tag.putInt(CLICK_EVENT_PAGE, ((ClickEvent.Payload.Int) payload).integer()); // todo 版本兼容？
                case OPEN_FILE -> tag.putString(CLICK_EVENT_PATH, ((ClickEvent.Payload.Text) payload).value());
                case SHOW_DIALOG -> {
                    ClickEvent.Payload.Dialog dialogPayload = (ClickEvent.Payload.Dialog) payload;
                    NBTDialog dialog = (NBTDialog) dialogPayload.dialog();
                    tag.put(CLICK_EVENT_DIALOG, dialog.tag());
                }
                case CUSTOM -> {
                    ClickEvent.Payload.Custom custom = (ClickEvent.Payload.Custom) payload;
                    tag.putString(CLICK_EVENT_CUSTOM_ID, custom.key().asString());
                    tag.putString(CLICK_EVENT_CUSTOM_PAYLOAD, custom.nbt().string());
                }
            }
        } else {
            tag.putString(ACTION, ClickEvent.Action.NAMES.keyOrThrow(event.action()));
            if (payload instanceof ClickEvent.Payload.Text text) {
                tag.putString(CLICK_EVENT_VALUE, text.value());
            } else if (payload instanceof ClickEvent.Payload.Int in) {
                tag.putString(CLICK_EVENT_VALUE, String.valueOf(in.integer()));
            }
        }
        return tag;
    }
}