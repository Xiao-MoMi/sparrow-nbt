package net.momirealms.sparrow.nbt.serializer;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.DataComponentValue;
import net.kyori.adventure.text.event.HoverEvent;
import net.momirealms.sparrow.nbt.CompoundTag;
import net.momirealms.sparrow.nbt.Tag;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@SuppressWarnings("all")
class NBTHoverEventSerializer {
    private static final String HOVER_EVENT_ACTION = "action";
    private static final String HOVER_EVENT_CONTENTS = "contents";

    private static final String HOVER_EVENT_SHOW_TEXT = "show_text";
    private static final String HOVER_EVENT_SHOW_ITEM = "show_item";
    private static final String HOVER_EVENT_SHOW_ENTITY = "show_entity";

    private static final String SHOW_ITEM_ID = "id";
    private static final String SHOW_ITEM_COUNT = "count";
    private static final String SHOW_ITEM_COMPONENTS = "components";

    private static final String SHOW_ENTITY_TYPE = "type";
    private static final String SHOW_ENTITY_ID = "id";
    private static final String SHOW_ENTITY_NAME = "name";

    private NBTHoverEventSerializer() {
    }

    static @NotNull HoverEvent<?> deserialize(@NotNull CompoundTag compound,
                                              @NotNull NBTComponentSerializerImpl serializer) {
        HoverEvent.Action<?> action = HoverEvent.Action.NAMES.valueOrThrow(compound.getString(HOVER_EVENT_ACTION));
        Class<?> actionType = action.type();
        Tag contents = compound.get(HOVER_EVENT_CONTENTS);
        if (contents == null) {
            throw new IllegalArgumentException("The hover event doesn't contain any contents");
        }
        if (Component.class.isAssignableFrom(actionType)) {
            return HoverEvent.showText(serializer.deserialize(contents));
        } else if (HoverEvent.ShowItem.class.isAssignableFrom(actionType)) {
            CompoundTag showItemContents = (CompoundTag) contents;
            Key itemId = Key.key(showItemContents.getString(SHOW_ITEM_ID));
            int itemCount = showItemContents.getInt(SHOW_ITEM_COUNT);
            Tag components = showItemContents.get(SHOW_ITEM_COMPONENTS);
            if (components != null) {
                CompoundTag componentsCompound = (CompoundTag) components;
                Map<Key, DataComponentValue> componentValues = new HashMap<>();
                for (String string : componentsCompound.keySet()) {
                    Tag value = componentsCompound.get(string);
                    if (value == null) continue;
                    componentValues.put(Key.key(string), NBTDataComponentValue.of(value));
                }
                return HoverEvent.showItem(itemId, itemCount, componentValues);
            } else {
                return HoverEvent.showItem(itemId, itemCount);
            }
        } else if (HoverEvent.ShowEntity.class.isAssignableFrom(actionType)) {
            CompoundTag showEntityContents = (CompoundTag) contents;
            Key entityType = Key.key(showEntityContents.getString(SHOW_ENTITY_TYPE));
            UUID entityId = Optional.ofNullable(showEntityContents.getString(SHOW_ENTITY_ID)).map(UUID::fromString).orElse(new UUID(0, 0));
            Tag entityName = showEntityContents.get(SHOW_ENTITY_NAME);
            if (entityName != null) {
                return HoverEvent.showEntity(entityType, entityId, serializer.deserialize(entityName));
            } else {
                return HoverEvent.showEntity(entityType, entityId);
            }
        } else {
            throw new IllegalArgumentException("Failed to deserialize hover event. Unkown action type: " + actionType);
        }
    }

    @NotNull
    static <V> CompoundTag serialize(@NotNull HoverEvent<V> event,
                                              @NotNull NBTComponentSerializerImpl serializer) {
        HoverEvent.Action<V> action = event.action();
        Tag contents;
        String actionType;
        if (action == HoverEvent.Action.SHOW_TEXT) {
            contents = serializer.serialize((Component) event.value());
            actionType = HOVER_EVENT_SHOW_TEXT;
        } else if (action == HoverEvent.Action.SHOW_ITEM) {
            HoverEvent.ShowItem item = (HoverEvent.ShowItem) event.value();
            CompoundTag showItemTag = new CompoundTag();
            showItemTag.putString(SHOW_ITEM_ID, item.item().asString());
            showItemTag.putInt(SHOW_ITEM_COUNT, item.count());
            Map<Key, NBTDataComponentValue> components = item.dataComponentsAs(NBTDataComponentValue.class);
            if (!components.isEmpty()) {
                CompoundTag dataComponents = new CompoundTag();
                for (Map.Entry<Key, NBTDataComponentValue> entry : components.entrySet()) {
                    dataComponents.put(entry.getKey().asString(), entry.getValue().tag());
                }
                showItemTag.put(SHOW_ITEM_COMPONENTS, dataComponents);
            }
            contents = showItemTag;
            actionType = HOVER_EVENT_SHOW_ITEM;
        } else if (action == HoverEvent.Action.SHOW_ENTITY) {
            HoverEvent.ShowEntity item = (HoverEvent.ShowEntity) event.value();
            CompoundTag showEntityTag = new CompoundTag();
            showEntityTag.putString(SHOW_ENTITY_TYPE, item.type().asString());
            showEntityTag.putString(SHOW_ENTITY_ID, item.id().toString());
            Component customName = item.name();
            if (customName != null) {
                showEntityTag.put(SHOW_ENTITY_NAME, serializer.serialize(customName));
            }
            contents = showEntityTag;
            actionType = HOVER_EVENT_SHOW_ENTITY;
        } else {
            throw new IllegalArgumentException("Failed to serialize " + event + " as a hoverTag. Unknown action type: " + action);
        }
        CompoundTag hoverTag = new CompoundTag();
        hoverTag.putString(HOVER_EVENT_ACTION, actionType);
        hoverTag.put(HOVER_EVENT_CONTENTS, contents);
        return hoverTag;
    }
}
