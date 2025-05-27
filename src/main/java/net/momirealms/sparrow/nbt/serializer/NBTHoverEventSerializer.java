package net.momirealms.sparrow.nbt.serializer;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.DataComponentValue;
import net.kyori.adventure.text.event.HoverEvent;
import net.momirealms.sparrow.nbt.*;
import net.momirealms.sparrow.nbt.util.UUIDUtil;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("all")
class NBTHoverEventSerializer {
    private static final String HOVER_EVENT_ACTION = "action";
    private static final String HOVER_EVENT_CONTENTS = "contents";

    private static final String HOVER_EVENT_SHOW_TEXT = "show_text";
    private static final String HOVER_EVENT_SHOW_ITEM = "show_item";
    private static final String HOVER_EVENT_SHOW_ENTITY = "show_entity";

    private static final String SHOW_TEXT_VALUE = "value";

    private static final String SHOW_ITEM_ID = "id";
    private static final String SHOW_ITEM_COUNT = "count";
    private static final String SHOW_ITEM_COMPONENTS = "components";

    private static final String SHOW_ENTITY_NAME = "name";
    private static final String LEGACY_SHOW_ENTITY_TYPE = "type";
    private static final String LEGACY_SHOW_ENTITY_ID = "id";
    private static final String MODERN_SHOW_ENTITY_ID = "id";
    private static final String MODERN_SHOW_ENTITY_UUID = "uuid";

    private NBTHoverEventSerializer() {
    }

    static @NotNull HoverEvent<?> deserialize(@NotNull CompoundTag compound,
                                              @NotNull NBTComponentSerializerImpl serializer) {
        HoverEvent.Action<?> action = HoverEvent.Action.NAMES.valueOrThrow(compound.getString(HOVER_EVENT_ACTION));
        if (serializer.modernHoverEvent) {
            if (action == HoverEvent.Action.SHOW_TEXT) {
                return HoverEvent.showText(serializer.deserialize(compound.get(SHOW_TEXT_VALUE)));
            } else if (action == HoverEvent.Action.SHOW_ITEM) {
                Key itemId = Key.key(compound.getString(SHOW_ITEM_ID));
                int itemCount = compound.getInt(SHOW_ITEM_COUNT);
                Tag components = compound.get(SHOW_ITEM_COMPONENTS);
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
            } else {
                // action == HoverEvent.Action.SHOW_ENTITY
                Key entityType = Key.key(compound.getString(MODERN_SHOW_ENTITY_ID));
                Tag uuidTag = compound.get(MODERN_SHOW_ENTITY_UUID);
                UUID entityId;
                if (uuidTag instanceof StringTag stringTag) {
                    entityId = UUID.fromString(stringTag.getAsString());
                } else if (uuidTag instanceof IntArrayTag intArrayTag) {
                    entityId = UUIDUtil.uuidFromIntArray(intArrayTag.getAsIntArray());
                } else {
                    entityId = new UUID(0, 0);
                }
                Tag entityName = compound.get(SHOW_ENTITY_NAME);
                if (entityName != null) {
                    return HoverEvent.showEntity(entityType, entityId, serializer.deserialize(entityName));
                } else {
                    return HoverEvent.showEntity(entityType, entityId);
                }
            }
        } else {
            Tag contents = compound.get(HOVER_EVENT_CONTENTS);
            if (contents == null) {
                throw new IllegalArgumentException("The hover event doesn't contain any contents");
            }
            Class<?> actionType = action.type();
            if (Component.class.isAssignableFrom(actionType)) {
                return HoverEvent.showText(serializer.deserialize(contents));
            } else if (HoverEvent.ShowItem.class.isAssignableFrom(actionType)) {
                if (contents instanceof CompoundTag showItemContents) {
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
                } else if (contents instanceof StringTag stringTag) {
                    return HoverEvent.showItem(Key.key(stringTag.getAsString()), 1);
                } else {
                    throw new IllegalArgumentException("Failed to deserialize show item hover event. Unkown tag type: " + contents.getType().name() + ". " + contents.getAsString());
                }
            } else if (HoverEvent.ShowEntity.class.isAssignableFrom(actionType)) {
                CompoundTag showEntityContents = (CompoundTag) contents;
                Key entityType = Key.key(showEntityContents.getString(LEGACY_SHOW_ENTITY_TYPE));
                Tag uuidTag = showEntityContents.get(LEGACY_SHOW_ENTITY_ID);
                UUID entityId;
                if (uuidTag instanceof StringTag stringTag) {
                    entityId = UUID.fromString(stringTag.getAsString());
                } else if (uuidTag instanceof ListTag listTag) {
                    entityId = UUIDUtil.uuidFromIntArray(new int[]{listTag.getInt(0), listTag.getInt(1), listTag.getInt(2), listTag.getInt(3)});
                } else  {
                    entityId = new UUID(0, 0);
                }
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
    }

    @NotNull
    static <V> CompoundTag serialize(@NotNull HoverEvent<V> event,
                                              @NotNull NBTComponentSerializerImpl serializer) {
        HoverEvent.Action<V> action = event.action();
        CompoundTag hoverTag = new CompoundTag();
        if (serializer.modernHoverEvent) {
            if (action == HoverEvent.Action.SHOW_TEXT) {
                hoverTag.putString(HOVER_EVENT_ACTION, HOVER_EVENT_SHOW_TEXT);
                hoverTag.put(SHOW_TEXT_VALUE, serializer.serialize((Component) event.value()));
            } else if (action == HoverEvent.Action.SHOW_ITEM) {
                HoverEvent.ShowItem item = (HoverEvent.ShowItem) event.value();
                hoverTag.putString(HOVER_EVENT_SHOW_ITEM, HOVER_EVENT_SHOW_ITEM);
                hoverTag.putString(SHOW_ITEM_ID, item.item().asString());
                hoverTag.putInt(SHOW_ITEM_COUNT, item.count());
                Map<Key, NBTDataComponentValue> components = item.dataComponentsAs(NBTDataComponentValue.class);
                if (!components.isEmpty()) {
                    CompoundTag dataComponents = new CompoundTag();
                    for (Map.Entry<Key, NBTDataComponentValue> entry : components.entrySet()) {
                        dataComponents.put(entry.getKey().asString(), entry.getValue().tag());
                    }
                    hoverTag.put(SHOW_ITEM_COMPONENTS, dataComponents);
                }
            } else if (action == HoverEvent.Action.SHOW_ENTITY) {
                HoverEvent.ShowEntity entity = (HoverEvent.ShowEntity) event.value();
                hoverTag.putString(HOVER_EVENT_ACTION, HOVER_EVENT_SHOW_ENTITY);
                hoverTag.putString(MODERN_SHOW_ENTITY_ID, entity.type().asString());
                hoverTag.putString(MODERN_SHOW_ENTITY_UUID, entity.id().toString());
                Component customName = entity.name();
                if (customName != null) {
                    hoverTag.put(SHOW_ENTITY_NAME, serializer.serialize(customName));
                }
            } else {
                throw new IllegalArgumentException("Failed to serialize " + event + " as a hoverTag. Unknown action type: " + action);
            }
        } else {
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
                HoverEvent.ShowEntity entity = (HoverEvent.ShowEntity) event.value();
                CompoundTag showEntityTag = new CompoundTag();
                showEntityTag.putString(LEGACY_SHOW_ENTITY_TYPE, entity.type().asString());
                showEntityTag.putString(LEGACY_SHOW_ENTITY_ID, entity.id().toString());
                Component customName = entity.name();
                if (customName != null) {
                    showEntityTag.put(SHOW_ENTITY_NAME, serializer.serialize(customName));
                }
                contents = showEntityTag;
                actionType = HOVER_EVENT_SHOW_ENTITY;
            } else {
                throw new IllegalArgumentException("Failed to serialize " + event + " as a hoverTag. Unknown action type: " + action);
            }

            hoverTag.putString(HOVER_EVENT_ACTION, actionType);
            hoverTag.put(HOVER_EVENT_CONTENTS, contents);
        }

        return hoverTag;
    }
}
