package net.momirealms.sparrow.nbt.adventure;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.nbt.api.BinaryTagHolder;
import net.kyori.adventure.text.*;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.DataComponentValue;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.*;
import net.kyori.adventure.text.object.ObjectContents;
import net.kyori.adventure.text.object.PlayerHeadObjectContents;
import net.kyori.adventure.text.object.SpriteObjectContents;
import net.kyori.adventure.util.Services;
import net.kyori.option.OptionSchema;
import net.kyori.option.OptionState;
import net.momirealms.sparrow.nbt.*;
import net.momirealms.sparrow.nbt.util.UUIDUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

final class NBTComponentSerializerImpl implements NBTComponentSerializer {
    private static final Optional<Provider> SERVICE = Services.service(Provider.class);
    static final Consumer<Builder> BUILDER = SERVICE
            .map(Provider::builder)
            .orElseGet(() -> builder -> {
            });

    static final class Instances {
        static final NBTComponentSerializer INSTANCE = SERVICE
                .map(Provider::nbt)
                .orElseGet(() -> new NBTComponentSerializerImpl(OptionSchema.emptySchema().emptyState()));
    }

    private static final String TEXT = "text";
    private static final String TRANSLATE = "translate";
    private static final String TRANSLATABLE = "translatable";
    private static final String TRANSLATE_FALLBACK = "fallback";
    private static final String TRANSLATE_WITH = "with";
    private static final String SCORE = "score";
    private static final String SCORE_NAME = "name";
    private static final String SCORE_OBJECTIVE = "objective";
    private static final String KEYBIND = "keybind";
    private static final String SELECTOR = "selector";
    private static final String SELECTOR_SEPARATOR = "separator";
    private static final String NBT = "nbt";
    private static final String NBT_SOURCE = "source";
    private static final String NBT_INTERPRET = "interpret";
    private static final String NBT_SEPARATOR = "separator";
    private static final String NBT_PLAIN = "plain";
    private static final String NBT_BLOCK = "block";
    private static final String NBT_ENTITY = "entity";
    private static final String NBT_STORAGE = "storage";
    private static final String OBJECT = "object";
    private static final String OBJECT_FALLBACK = "fallback";
    private static final String OBJECT_ATLAS = "atlas";
    private static final String OBJECT_SPRITE = "sprite";
    private static final String OBJECT_PLAYER = "player";
    private static final String OBJECT_PLAYER_NAME = "name";
    private static final String OBJECT_PLAYER_ID = "id";
    private static final String OBJECT_PLAYER_PROPERTIES = "properties";
    private static final String OBJECT_PLAYER_TEXTURE = "texture";
    private static final String OBJECT_PLAYER_CAPE = "cape";
    private static final String OBJECT_PLAYER_ELYTRA = "elytra";
    private static final String OBJECT_PLAYER_MODEL = "model";
    private static final String OBJECT_HAT = "hat";
    private static final String PROFILE_PROPERTY_NAME = "name";
    private static final String PROFILE_PROPERTY_VALUE = "value";
    private static final String PROFILE_PROPERTY_SIGNATURE = "signature";
    private static final String EXTRA = "extra";

    static final Map<String, ComponentReader> READER_BY_TYPE = new HashMap<>();
    static {
        READER_BY_TYPE.put(TEXT, ComponentReader.TEXT_COMPONENT);
        READER_BY_TYPE.put(TRANSLATABLE, ComponentReader.TRANSLATABLE_COMPONENT);
        READER_BY_TYPE.put(SCORE, ComponentReader.SCORE_COMPONENT);
        READER_BY_TYPE.put(KEYBIND, ComponentReader.KEYBIND_COMPONENT);
        READER_BY_TYPE.put(SELECTOR, ComponentReader.SELECTOR_COMPONENT);
        READER_BY_TYPE.put(NBT, ComponentReader.NBT_COMPONENT);
        READER_BY_TYPE.put(OBJECT, ComponentReader.OBJECT_COMPONENT);
    }
    static final Map<String, ComponentReader> READER_BY_TAG = new HashMap<>();
    static {
        READER_BY_TAG.put(TEXT, ComponentReader.TEXT_COMPONENT);
        READER_BY_TAG.put(TRANSLATE, ComponentReader.TRANSLATABLE_COMPONENT);
        READER_BY_TAG.put(TRANSLATE_WITH, ComponentReader.TRANSLATABLE_COMPONENT);
        READER_BY_TAG.put(SCORE, ComponentReader.SCORE_COMPONENT);
        READER_BY_TAG.put(KEYBIND, ComponentReader.KEYBIND_COMPONENT);
        READER_BY_TAG.put(SELECTOR, ComponentReader.SELECTOR_COMPONENT);
        READER_BY_TAG.put(NBT, ComponentReader.NBT_COMPONENT);
        READER_BY_TAG.put(NBT_SOURCE, ComponentReader.NBT_COMPONENT);
        READER_BY_TAG.put(NBT_BLOCK, ComponentReader.NBT_COMPONENT);
        READER_BY_TAG.put(NBT_STORAGE, ComponentReader.NBT_COMPONENT);
        READER_BY_TAG.put(NBT_ENTITY, ComponentReader.NBT_COMPONENT);
        READER_BY_TAG.put(OBJECT, ComponentReader.OBJECT_COMPONENT);
        READER_BY_TAG.put(OBJECT_ATLAS, ComponentReader.OBJECT_COMPONENT);
        READER_BY_TAG.put(OBJECT_SPRITE, ComponentReader.OBJECT_COMPONENT);
        READER_BY_TAG.put(OBJECT_PLAYER, ComponentReader.OBJECT_COMPONENT);
        READER_BY_TAG.put(OBJECT_HAT, ComponentReader.OBJECT_COMPONENT);
    }

    private static final String STYLE_COLOR = "color";
    private static final String STYLE_BOLD = "bold";
    private static final String STYLE_ITALIC = "italic";
    private static final String STYLE_UNDERLINED = "underlined";
    private static final String STYLE_STRIKETHROUGH = "strikethrough";
    private static final String STYLE_OBFUSCATED = "obfuscated";
    private static final String STYLE_FONT = "font";
    private static final String STYLE_INSERTION = "insertion";
    private static final String STYLE_SHADOW_COLOR = "shadow_color";
    private static final String STYLE_CLICK_EVENT = "click_event";
    private static final String STYLE_HOVER_EVENT = "hover_event";
    private static final String STYLE_LEGACY_CLICK_EVENT = "clickEvent";
    private static final String STYLE_LEGACY_HOVER_EVENT = "hoverEvent";

    static final Map<String, StyleApplier> STYLE_BY_TAG = new HashMap<>();
    static {
        STYLE_BY_TAG.put(STYLE_COLOR, StyleApplier.COLOR);
        STYLE_BY_TAG.put(STYLE_BOLD, StyleApplier.BOLD);
        STYLE_BY_TAG.put(STYLE_ITALIC, StyleApplier.ITALIC);
        STYLE_BY_TAG.put(STYLE_UNDERLINED, StyleApplier.UNDERLINED);
        STYLE_BY_TAG.put(STYLE_STRIKETHROUGH, StyleApplier.STRIKETHROUGH);
        STYLE_BY_TAG.put(STYLE_OBFUSCATED, StyleApplier.OBFUSCATED);
        STYLE_BY_TAG.put(STYLE_FONT, StyleApplier.FONT);
        STYLE_BY_TAG.put(STYLE_INSERTION, StyleApplier.INSERTION);
        STYLE_BY_TAG.put(STYLE_SHADOW_COLOR, StyleApplier.SHADOW_COLOR);
        STYLE_BY_TAG.put(STYLE_CLICK_EVENT, StyleApplier.CLICK_EVENT);
        STYLE_BY_TAG.put(STYLE_LEGACY_CLICK_EVENT, StyleApplier.CLICK_EVENT);
        STYLE_BY_TAG.put(STYLE_HOVER_EVENT, StyleApplier.HOVER_EVENT);
        STYLE_BY_TAG.put(STYLE_LEGACY_HOVER_EVENT, StyleApplier.HOVER_EVENT);
    }

    private static final String CLICK_EVENT_ACTION = "action";
    private static final String CLICK_EVENT_VALUE = "value";
    private static final String CLICK_EVENT_OPEN_URL = "open_url";
    private static final String CLICK_EVENT_OPEN_FILE = "open_file";
    private static final String CLICK_EVENT_RUN_COMMAND = "run_command";
    private static final String CLICK_EVENT_SUGGEST_COMMAND = "suggest_command";
    private static final String CLICK_EVENT_CHANGE_PAGE = "change_page";
    private static final String CLICK_EVENT_COPY_TO_CLIPBOARD = "copy_to_clipboard";
    private static final String CLICK_EVENT_SHOW_DIALOG = "show_dialog";
    private static final String CLICK_EVENT_CUSTOM = "custom";
    private static final String CLICK_EVENT_URL = "url";
    private static final String CLICK_EVENT_PATH = "path";
    private static final String CLICK_EVENT_COMMAND = "command";
    private static final String CLICK_EVENT_PAGE = "page";
    private static final String CLICK_EVENT_DIALOG = "dialog";
    private static final String CLICK_EVENT_CUSTOM_ID = "id";
    private static final String CLICK_EVENT_CUSTOM_PAYLOAD = "payload";

    private static final String HOVER_EVENT_ACTION = "action";
    private static final String HOVER_EVENT_SHOW_ITEM = "show_item";
    private static final String HOVER_EVENT_SHOW_TEXT = "show_text";
    private static final String HOVER_EVENT_SHOW_ENTITY = "show_entity";
    private static final String HOVER_EVENT_VALUE = "value";
    private static final String HOVER_EVENT_ID = "id";
    private static final String HOVER_EVENT_COUNT = "count";
    private static final String HOVER_EVENT_COMPONENTS = "components";
    private static final String HOVER_EVENT_NAME = "name";
    private static final String HOVER_EVENT_UUID = "uuid";
    private static final String HOVER_EVENT_TYPE = "type";
    private static final String HOVER_EVENT_TAG = "tag";
    private static final String HOVER_EVENT_CONTENTS = "contents";

    public final boolean modernEvent;
    public final boolean dataComponentRelease;
    public final boolean compactTextComponent;
    public final boolean serializeComponentType;
    public final boolean intArrayUUID;

    NBTComponentSerializerImpl(@NotNull final OptionState flags) {
        this.modernEvent = Boolean.TRUE.equals(flags.value(NBTSerializerOptions.MODERN_EVENT_TYPE));
        this.dataComponentRelease = Boolean.TRUE.equals(flags.value(NBTSerializerOptions.DATA_COMPONENT_RELEASE));
        this.compactTextComponent = Boolean.TRUE.equals(flags.value(NBTSerializerOptions.EMIT_COMPACT_TEXT_COMPONENT));
        this.serializeComponentType = Boolean.TRUE.equals(flags.value(NBTSerializerOptions.SERIALIZE_COMPONENT_TYPE));
        this.intArrayUUID = Boolean.TRUE.equals(flags.value(NBTSerializerOptions.INT_ARRAY_UUID));
    }

    @Override
    public boolean modernEvent() {
        return this.modernEvent;
    }

    @Override
    public boolean dataComponentRelease() {
        return this.dataComponentRelease;
    }

    @Override
    public boolean compactTextComponent() {
        return this.compactTextComponent;
    }

    @Override
    public boolean serializeComponentType() {
        return this.serializeComponentType;
    }

    @Override
    public boolean intArrayUUID() {
        return this.intArrayUUID;
    }

    @Override
    public @NotNull Component deserialize(@NotNull Tag inputTag) {
        if (!(inputTag instanceof CompoundTag input)) {
            return Component.text(inputTag.getAsString());
        }

        // 尝试直接获取 type
        String specifiedType = input.getString("type");
        ComponentBuilder<?, ?> builder = null;
        if (specifiedType != null) {
            ComponentReader componentReader = READER_BY_TYPE.get(specifiedType);
            if (componentReader != null) {
                builder = componentReader.deserialize(this, input);
            }
        }

        Style.Builder style = Style.style();
        boolean hasStyle = false;
        // 遍历全部元素的同时去猜测类型
        for (Map.Entry<String, Tag> entry : input.entrySet()) {
            String key = entry.getKey();
            if (builder == null) {
                ComponentReader componentReader = READER_BY_TAG.get(key);
                if (componentReader != null) {
                    builder = componentReader.deserialize(this, input);
                    continue;
                }
            }
            StyleApplier styleApplier = STYLE_BY_TAG.get(key);
            if (styleApplier != null) {
                styleApplier.apply(this, style, entry.getValue());
                hasStyle = true;
            }
        }

        // 没找到合适的组件
        if (builder == null) {
            throw new IllegalArgumentException("Could not infer the type of the component: " + input);
        }

        // 应用style
        if (hasStyle) {
            builder.style(style.build());
        }

        // 应用子组件
        ListTag binaryChildren = input.getList(EXTRA);
        if (binaryChildren != null) {
            List<Component> children = new ArrayList<>(binaryChildren.size());
            for (int i = 0; i < binaryChildren.size(); i++) {
                children.add(deserialize(binaryChildren.get(i)));
            }
            builder.append(children);
        }

        return builder.build();
    }

    @NotNull
    @Override
    public Tag serialize(@NotNull Component component) {
        if (this.compactTextComponent && component instanceof TextComponent textComponent && !component.hasStyling() && component.children().isEmpty()) {
            return new StringTag(textComponent.content());
        }
        CompoundTag tag = new CompoundTag();

        // 写入style
        serializeStyle(tag, component.style());

        // 写入 children
        List<Component> children = component.children();
        if (!children.isEmpty()) {
            List<Tag> serializedChildren = new ArrayList<>(children.size());
            for (int i = 0, size = children.size(); i < size; i++) {
                serializedChildren.add(serialize(children.get(i)));
            }
            tag.put(EXTRA, new ListTag(serializedChildren));
        }

        // 写入组件主体
        switch (component) {
            case TextComponent textComponent -> {
                this.writeComponentType(TEXT, tag);
                tag.putString(TEXT, textComponent.content());
            }
            case TranslatableComponent translatableComponent -> {
                this.writeComponentType(TRANSLATABLE, tag);
                tag.putString(TRANSLATE, translatableComponent.key());
                List<TranslationArgument> args = translatableComponent.arguments();
                if (!args.isEmpty()) {
                    List<Tag> argumentsTags = new ArrayList<>(args.size());
                    for (int i = 0, size = args.size(); i < size; i++) {
                        TranslationArgument translationArgument = args.get(i);
                        argumentsTags.add(serializeTranslationArgument(translationArgument));
                    }
                    tag.put(TRANSLATE_WITH, new ListTag(argumentsTags));
                }
                String fallback = translatableComponent.fallback();
                if (fallback != null) {
                    tag.putString(TRANSLATE_FALLBACK, fallback);
                }
            }
            case KeybindComponent keybindComponent -> {
                this.writeComponentType(KEYBIND, tag);
                tag.putString(KEYBIND, keybindComponent.keybind());
            }
            case ScoreComponent scoreComponent -> {
                this.writeComponentType(SCORE, tag);
                CompoundTag score = new CompoundTag(4, 0.75f);
                tag.put(SCORE, score);
                score.putString(SCORE_NAME, scoreComponent.name());
                score.putString(SCORE_OBJECTIVE, scoreComponent.objective());
            }
            case SelectorComponent selectorComponent -> {
                this.writeComponentType(SELECTOR, tag);
                tag.putString(SELECTOR, selectorComponent.pattern());
                Component separator = selectorComponent.separator();
                if (separator != null) {
                    tag.put(SELECTOR_SEPARATOR, this.serialize(separator));
                }
            }
            case NBTComponent<?> nbtComponent -> {
                this.writeComponentType(NBT, tag);
                tag.putString(NBT, nbtComponent.nbtPath());
                if (nbtComponent.interpret()) {
                    tag.putBoolean(NBT_INTERPRET, true);
                }
                if (nbtComponent.plain()) {
                    tag.putBoolean(NBT_PLAIN, true);
                }
                Component separator = nbtComponent.separator();
                if (separator != null) {
                    tag.put(NBT_SEPARATOR, this.serialize(separator));
                }
                switch (nbtComponent) {
                    case BlockNBTComponent blockNBTComponent ->
                            tag.putString(NBT_BLOCK, blockNBTComponent.pos().asString());
                    case EntityNBTComponent entityNBTComponent ->
                            tag.putString(NBT_ENTITY, entityNBTComponent.selector());
                    case StorageNBTComponent storageNBTComponent ->
                            tag.putString(NBT_STORAGE, storageNBTComponent.storage().asMinimalString());
                    default -> throw new IllegalStateException("Unexpected nbt content: " + component);
                }
            }
            case ObjectComponent objectComponent -> {
                this.writeComponentType(OBJECT, tag);
                ObjectContents contents = objectComponent.contents();
                Component fallback = objectComponent.fallback();
                if (fallback != null) {
                    tag.put(OBJECT_FALLBACK, this.serialize(fallback));
                }
                if (contents instanceof SpriteObjectContents spriteObjectContents) {
                    tag.putString(OBJECT, OBJECT_ATLAS);
                    Key atlas = spriteObjectContents.atlas();
                    if (!atlas.equals(SpriteObjectContents.DEFAULT_ATLAS)) {
                        tag.putString(OBJECT_ATLAS, atlas.asMinimalString());
                    }
                    tag.putString(OBJECT_SPRITE, spriteObjectContents.sprite().asMinimalString());
                } else if (contents instanceof PlayerHeadObjectContents playerHeadObjectContents) {
                    tag.putString(OBJECT, OBJECT_PLAYER);
                    if (!playerHeadObjectContents.hat()) {
                        tag.putBoolean(OBJECT_HAT, false);
                    }
                    CompoundTag playerHead = new CompoundTag();
                    tag.put(OBJECT_PLAYER, playerHead);
                    Key texture = playerHeadObjectContents.texture();
                    if (texture != null) {
                        playerHead.putString(OBJECT_PLAYER_TEXTURE, texture.asMinimalString());
                    }
                    String name = playerHeadObjectContents.name();
                    if (name != null) {
                        playerHead.putString(OBJECT_PLAYER_NAME, name);
                    }
                    UUID uuid = playerHeadObjectContents.id();
                    if (uuid != null) {
                        playerHead.putIntArray(OBJECT_PLAYER_ID, UUIDUtil.uuidToIntArray(uuid));
                    }
                    List<PlayerHeadObjectContents.ProfileProperty> profileProperties = playerHeadObjectContents.profileProperties();
                    if (!profileProperties.isEmpty()) {
                        List<Tag> propertiesTags = new ArrayList<>(profileProperties.size());
                        for (int i = 0, size = profileProperties.size(); i < size; i++) {
                            PlayerHeadObjectContents.ProfileProperty profileProperty = profileProperties.get(i);
                            CompoundTag propertyTag = new CompoundTag();
                            propertyTag.putString(PROFILE_PROPERTY_NAME, profileProperty.name());
                            propertyTag.putString(PROFILE_PROPERTY_VALUE, profileProperty.value());
                            String signature = profileProperty.signature();
                            if (signature != null) {
                                propertyTag.putString(PROFILE_PROPERTY_SIGNATURE, signature);
                            }
                            propertiesTags.add(propertyTag);
                        }
                        ListTag properties = new ListTag(propertiesTags);
                        playerHead.put(OBJECT_PLAYER_PROPERTIES, properties);
                    }
                }
            }
            default -> throw new IllegalStateException("Unexpected component: " + component);
        }
        return tag;
    }

    private void writeComponentType(final String componentType, final CompoundTag tag) {
        if (this.serializeComponentType) {
            tag.putString("type", componentType);
        }
    }

    private Tag serializeTranslationArgument(TranslationArgument argument) {
        Object value = argument.value();
        return switch (value) {
            case Boolean bool -> new ByteTag(bool);
            case Byte b -> new ByteTag(b);
            case Short s -> new ShortTag(s);
            case Integer i -> new IntTag(i);
            case Long l -> new LongTag(l);
            case Float f -> new FloatTag(f);
            case Number d -> new DoubleTag(d.doubleValue());
            case Component c -> serialize(c);
            default -> throw new IllegalStateException("Unexpected translation argument: " + argument);
        };
    }

    private void serializeStyle(CompoundTag tag, Style style) {
        if (style.isEmpty()) {
            return;
        }

        TextColor color = style.color();
        if (color != null) {
            tag.putString(STYLE_COLOR, color.toString());
        }

        // 避免创建 keyset 和 values
        TextDecoration.State bold = style.decoration(TextDecoration.BOLD);
        if (bold != TextDecoration.State.NOT_SET) {
            tag.putBoolean(STYLE_BOLD, bold == TextDecoration.State.TRUE);
        }
        TextDecoration.State italic = style.decoration(TextDecoration.ITALIC);
        if (italic != TextDecoration.State.NOT_SET) {
            tag.putBoolean(STYLE_ITALIC, italic == TextDecoration.State.TRUE);
        }
        TextDecoration.State underlined = style.decoration(TextDecoration.UNDERLINED);
        if (underlined != TextDecoration.State.NOT_SET) {
            tag.putBoolean(STYLE_UNDERLINED, underlined == TextDecoration.State.TRUE);
        }
        TextDecoration.State strikethrough = style.decoration(TextDecoration.STRIKETHROUGH);
        if (strikethrough != TextDecoration.State.NOT_SET) {
            tag.putBoolean(STYLE_STRIKETHROUGH, strikethrough == TextDecoration.State.TRUE);
        }
        TextDecoration.State obfuscated = style.decoration(TextDecoration.OBFUSCATED);
        if (obfuscated != TextDecoration.State.NOT_SET) {
            tag.putBoolean(STYLE_OBFUSCATED, obfuscated == TextDecoration.State.TRUE);
        }

        Key font = style.font();
        if (font != null) {
            tag.putString(STYLE_FONT, font.asMinimalString());
        }

        String insertion = style.insertion();
        if (insertion != null) {
            tag.putString(STYLE_INSERTION, insertion);
        }

        ShadowColor shadowColor = style.shadowColor();
        if (shadowColor != null) {
            tag.putInt(STYLE_SHADOW_COLOR, shadowColor.value());
        }

        ClickEvent<?> clickEvent = style.clickEvent();
        if (clickEvent != null) {
            serializeClickEvent(tag, clickEvent);
        }

        HoverEvent<?> hoverEvent = style.hoverEvent();
        if (hoverEvent != null) {
            serializeHoverEvent(tag, hoverEvent);
        }
    }

    private void serializeClickEvent(CompoundTag tag, ClickEvent<?> event) {
        CompoundTag clickTag = new CompoundTag();
        ClickEvent.Payload payload = event.payload();
        ClickEvent.Action<?> action = event.action();
        if (this.modernEvent) {
            clickTag.putString(CLICK_EVENT_ACTION, action.name());
            switch (action) {
                case ClickEvent.Action.OpenUrl openUrl -> {
                    clickTag.putString(CLICK_EVENT_URL, ((ClickEvent.Payload.Text) payload).value());
                }
                case ClickEvent.Action.SuggestCommand suggestCommand -> {
                    clickTag.putString(CLICK_EVENT_COMMAND, ((ClickEvent.Payload.Text) payload).value());
                }
                case ClickEvent.Action.RunCommand runCommand -> {
                    clickTag.putString(CLICK_EVENT_COMMAND, ((ClickEvent.Payload.Text) payload).value());
                }
                case ClickEvent.Action.CopyToClipboard copyToClipboard -> {
                    clickTag.putString(CLICK_EVENT_VALUE, ((ClickEvent.Payload.Text) payload).value());
                }
                case ClickEvent.Action.OpenFile openFile -> {
                    clickTag.putString(CLICK_EVENT_PATH, ((ClickEvent.Payload.Text) payload).value());
                }
                case ClickEvent.Action.ChangePage changePage -> {
                    clickTag.putInt(CLICK_EVENT_PAGE, ((ClickEvent.Payload.Int) payload).integer());
                }
                case ClickEvent.Action.ShowDialog showDialog -> {
                    ClickEvent.Payload.Dialog dialogPayload = (ClickEvent.Payload.Dialog) payload;
                    NBTDialog dialog = (NBTDialog) dialogPayload.dialog();
                    clickTag.put(CLICK_EVENT_DIALOG, dialog.tag());
                }
                case ClickEvent.Action.Custom custom -> {
                    ClickEvent.Payload.Custom customPayload = (ClickEvent.Payload.Custom) payload;
                    clickTag.putString(CLICK_EVENT_CUSTOM_ID, customPayload.key().asString());
                    BinaryTagHolder nbt = customPayload.nbt();
                    if (nbt != null) {
                        clickTag.putString(CLICK_EVENT_CUSTOM_PAYLOAD, nbt.string());
                    }
                }
                default -> throw new IllegalStateException("Unexpected action: " + action);
            }
            tag.put(STYLE_CLICK_EVENT, clickTag);
        } else {
            if (payload instanceof ClickEvent.Payload.Text textPayload) {
                clickTag.putString(CLICK_EVENT_ACTION, action.name());
                clickTag.putString(CLICK_EVENT_VALUE, textPayload.value());
            } else if (payload instanceof ClickEvent.Payload.Int intPayload) {
                clickTag.putString(CLICK_EVENT_ACTION, action.name());
                clickTag.putString(CLICK_EVENT_VALUE, String.valueOf(intPayload.integer()));
            } else {
                return;
            }
            tag.put(STYLE_LEGACY_CLICK_EVENT, clickTag);
        }
    }

    private void serializeHoverEvent(CompoundTag tag, HoverEvent<?> event) {
        HoverEvent.Action<?> action = event.action();
        CompoundTag hoverTag = new CompoundTag();
        if (this.modernEvent) {
            if (action == HoverEvent.Action.SHOW_TEXT) {
                hoverTag.putString(HOVER_EVENT_ACTION, HOVER_EVENT_SHOW_TEXT);
                hoverTag.put(HOVER_EVENT_VALUE, serialize((Component) event.value()));
            } else if (action == HoverEvent.Action.SHOW_ITEM) {
                HoverEvent.ShowItem item = (HoverEvent.ShowItem) event.value();
                hoverTag.putString(HOVER_EVENT_ACTION, HOVER_EVENT_SHOW_ITEM);
                if (item.count() != 1) {
                    hoverTag.putInt(HOVER_EVENT_COUNT, item.count());
                }
                hoverTag.putString(HOVER_EVENT_ID, item.item().asMinimalString());
                Map<Key, NBTDataComponentValue> components = item.dataComponentsAs(NBTDataComponentValue.class);
                if (!components.isEmpty()) {
                    CompoundTag dataComponents = new CompoundTag();
                    for (Map.Entry<Key, NBTDataComponentValue> entry : components.entrySet()) {
                        NBTDataComponentValue value = entry.getValue();
                        String component = entry.getKey().asString();
                        if (value.isRemoved()) {
                            dataComponents.put("!" + component, value.tag());
                        } else {
                            dataComponents.put(component, value.tag());
                        }
                    }
                    hoverTag.put(HOVER_EVENT_COMPONENTS, dataComponents);
                }
            } else if (action == HoverEvent.Action.SHOW_ENTITY) {
                HoverEvent.ShowEntity entity = (HoverEvent.ShowEntity) event.value();
                hoverTag.putString(HOVER_EVENT_ACTION, HOVER_EVENT_SHOW_ENTITY);
                hoverTag.putString(HOVER_EVENT_ID, entity.type().asMinimalString());
                hoverTag.put(HOVER_EVENT_UUID, serializeUUID(entity.id()));
                Component customName = entity.name();
                if (customName != null) {
                    hoverTag.put(HOVER_EVENT_NAME, serialize(customName));
                }
            } else {
                return;
            }
            tag.put(STYLE_HOVER_EVENT, hoverTag);
        } else {
            Tag contents;
            if (action == HoverEvent.Action.SHOW_TEXT) {
                contents = serialize((Component) event.value());
                hoverTag.putString(HOVER_EVENT_ACTION, HOVER_EVENT_SHOW_TEXT);
            } else if (action == HoverEvent.Action.SHOW_ITEM) {
                HoverEvent.ShowItem item = (HoverEvent.ShowItem) event.value();
                CompoundTag showItemTag = new CompoundTag();
                showItemTag.putString(HOVER_EVENT_ID, item.item().asMinimalString());
                if (item.count() != 1) {
                    showItemTag.putInt(HOVER_EVENT_COUNT, item.count());
                }
                if (this.dataComponentRelease) {
                    Map<Key, NBTDataComponentValue> components = item.dataComponentsAs(NBTDataComponentValue.class);
                    if (!components.isEmpty()) {
                        CompoundTag dataComponents = new CompoundTag();
                        for (Map.Entry<Key, NBTDataComponentValue> entry : components.entrySet()) {
                            NBTDataComponentValue value = entry.getValue();
                            String component = entry.getKey().asString();
                            if (value.isRemoved()) {
                                dataComponents.put("!" + component, value.tag());
                            } else {
                                dataComponents.put(component, value.tag());
                            }
                        }
                        showItemTag.put(HOVER_EVENT_COMPONENTS, dataComponents);
                    }
                } else {
                    BinaryTagHolder nbt = item.nbt();
                    if (nbt != null) {
                        showItemTag.putString(HOVER_EVENT_TAG, nbt.string());
                    }
                }
                contents = showItemTag;
            } else if (action == HoverEvent.Action.SHOW_ENTITY) {
                HoverEvent.ShowEntity entity = (HoverEvent.ShowEntity) event.value();
                CompoundTag showEntityTag = new CompoundTag();
                showEntityTag.putString(HOVER_EVENT_TYPE, entity.type().asMinimalString());
                showEntityTag.put(HOVER_EVENT_ID, serializeUUID(entity.id()));
                Component customName = entity.name();
                if (customName != null) {
                    showEntityTag.put(HOVER_EVENT_NAME, serialize(customName));
                }
                contents = showEntityTag;
            } else {
                return;
            }
            hoverTag.put(HOVER_EVENT_CONTENTS, contents);
            tag.put(STYLE_LEGACY_HOVER_EVENT, hoverTag);
        }
    }

    private Tag serializeUUID(UUID uuid) {
        if (this.intArrayUUID) {
            return new IntArrayTag(UUIDUtil.uuidToIntArray(uuid));
        } else {
            return new StringTag(uuid.toString());
        }
    }

    interface StyleApplier {
        StyleApplier COLOR = new Color0();
        StyleApplier BOLD = new Decoration0(TextDecoration.BOLD);
        StyleApplier ITALIC = new Decoration0(TextDecoration.ITALIC);
        StyleApplier UNDERLINED = new Decoration0(TextDecoration.UNDERLINED);
        StyleApplier STRIKETHROUGH = new Decoration0(TextDecoration.STRIKETHROUGH);
        StyleApplier OBFUSCATED = new Decoration0(TextDecoration.OBFUSCATED);
        StyleApplier FONT = new Font0();
        StyleApplier INSERTION = new Insertion0();
        StyleApplier SHADOW_COLOR = new ShadowColor0();
        StyleApplier CLICK_EVENT = new ClickEvent0();
        StyleApplier HOVER_EVENT = new HoverEvent0();

        void apply(@NotNull final NBTComponentSerializer serializer, @NotNull final Style.Builder builder, @NotNull final Tag tag);

        class Color0 implements StyleApplier {
            @Override
            public void apply(@NotNull NBTComponentSerializer serializer, Style.@NotNull Builder builder, @NotNull Tag tag) {
                String color = tag.getAsString();
                if (color.startsWith(TextColor.HEX_PREFIX)) {
                    builder.color(TextColor.fromHexString(color));
                } else {
                    builder.color(NamedTextColor.NAMES.value(color));
                }
            }
        }

        class Decoration0 implements StyleApplier {
            private final TextDecoration decoration;

            public Decoration0(TextDecoration decoration) {
                this.decoration = decoration;
            }

            @Override
            public void apply(@NotNull NBTComponentSerializer serializer, Style.@NotNull Builder builder, @NotNull Tag tag) {
                if (tag instanceof ByteTag byteTag) {
                    builder.decoration(this.decoration, byteTag.booleanValue());
                }
            }
        }

        class Font0 implements StyleApplier {
            @SuppressWarnings("all")
            @Override
            public void apply(@NotNull NBTComponentSerializer serializer, Style.@NotNull Builder builder, @NotNull Tag tag) {
                builder.font(Key.key(tag.getAsString()));
            }
        }

        class Insertion0 implements StyleApplier {
            @Override
            public void apply(@NotNull NBTComponentSerializer serializer, Style.@NotNull Builder builder, @NotNull Tag tag) {
                builder.insertion(tag.getAsString());
            }
        }

        class ShadowColor0 implements StyleApplier {
            @Override
            public void apply(@NotNull NBTComponentSerializer serializer, Style.@NotNull Builder builder, @NotNull Tag tag) {
                if (tag instanceof IntTag intTag) {
                    builder.shadowColor(ShadowColor.shadowColor(intTag.getAsInt()));
                } else if (tag instanceof ListTag listTag && listTag.size() == 4) {
                    int r = (int) listTag.getFloat(0) * 255;
                    int g = (int) listTag.getFloat(1) * 255;
                    int b = (int) listTag.getFloat(2) * 255;
                    int a = (int) listTag.getFloat(3) * 255;
                    builder.shadowColor(ShadowColor.shadowColor(r, g, b, a));
                }
            }
        }

        class ClickEvent0 implements StyleApplier {
            private static final Map<String, ClickEventApplier> CLICK_EVENT_BY_ACTION = new HashMap<>();
            static {
                CLICK_EVENT_BY_ACTION.put(CLICK_EVENT_OPEN_URL, ClickEventApplier.OPEN_URL);
                CLICK_EVENT_BY_ACTION.put(CLICK_EVENT_OPEN_FILE, ClickEventApplier.OPEN_FILE);
                CLICK_EVENT_BY_ACTION.put(CLICK_EVENT_RUN_COMMAND, ClickEventApplier.RUN_COMMAND);
                CLICK_EVENT_BY_ACTION.put(CLICK_EVENT_SUGGEST_COMMAND, ClickEventApplier.SUGGEST_COMMAND);
                CLICK_EVENT_BY_ACTION.put(CLICK_EVENT_CHANGE_PAGE, ClickEventApplier.CHANGE_PAGE);
                CLICK_EVENT_BY_ACTION.put(CLICK_EVENT_COPY_TO_CLIPBOARD, ClickEventApplier.COPY_TO_CLIPBOARD);
                CLICK_EVENT_BY_ACTION.put(CLICK_EVENT_SHOW_DIALOG, ClickEventApplier.SHOW_DIALOG);
                CLICK_EVENT_BY_ACTION.put(CLICK_EVENT_CUSTOM, ClickEventApplier.CUSTOM);
            }

            @Override
            public void apply(@NotNull NBTComponentSerializer serializer, Style.@NotNull Builder builder, @NotNull Tag tag) {
                if (!(tag instanceof CompoundTag input)) {
                    return;
                }
                String actionName = input.getString(CLICK_EVENT_ACTION);
                ClickEventApplier clickEventApplier = CLICK_EVENT_BY_ACTION.get(actionName);
                if (clickEventApplier != null) {
                    clickEventApplier.apply(serializer, builder, input);
                }
            }
        }

        interface ClickEventApplier {
            ClickEventApplier OPEN_URL = new StringClickEvent(CLICK_EVENT_URL, ClickEvent::openUrl);
            ClickEventApplier OPEN_FILE = new StringClickEvent(CLICK_EVENT_PATH, ClickEvent::openFile);
            ClickEventApplier CHANGE_PAGE = new IntegerClickEvent(CLICK_EVENT_PAGE, ClickEvent::changePage);
            ClickEventApplier COPY_TO_CLIPBOARD = new StringClickEvent(CLICK_EVENT_VALUE, ClickEvent::copyToClipboard);
            ClickEventApplier RUN_COMMAND = new StringClickEvent(CLICK_EVENT_COMMAND, ClickEvent::runCommand);
            ClickEventApplier SUGGEST_COMMAND = new StringClickEvent(CLICK_EVENT_COMMAND, ClickEvent::suggestCommand);
            ClickEventApplier SHOW_DIALOG = new ShowDialog();
            ClickEventApplier CUSTOM = new Custom();

            void apply(@NotNull NBTComponentSerializer serializer, Style.@NotNull Builder builder, @NotNull CompoundTag input);

            class StringClickEvent implements ClickEventApplier {
                private final String field;
                private final Function<String, ClickEvent<?>> factory;

                public StringClickEvent(String field, Function<String, ClickEvent<?>> factory) {
                    this.field = field;
                    this.factory = factory;
                }

                @Override
                public void apply(@NotNull NBTComponentSerializer serializer, Style.@NotNull Builder builder, @NotNull CompoundTag input) {
                    String value;
                    if (serializer.modernEvent()) {
                        value = input.getString(this.field);
                    } else {
                        value = input.getString(CLICK_EVENT_VALUE);
                    }
                    builder.clickEvent(this.factory.apply(value));
                }
            }

            class IntegerClickEvent implements ClickEventApplier {
                private final String field;
                private final Function<Integer, ClickEvent<?>> factory;

                public IntegerClickEvent(String field, Function<Integer, ClickEvent<?>> factory) {
                    this.field = field;
                    this.factory = factory;
                }

                @Override
                public void apply(@NotNull NBTComponentSerializer serializer, Style.@NotNull Builder builder, @NotNull CompoundTag input) {
                    int value;
                    if (serializer.modernEvent()) {
                        value = input.getInt(this.field);
                    } else {
                        value = Integer.parseInt(input.getString(CLICK_EVENT_VALUE));
                    }
                    builder.clickEvent(this.factory.apply(value));
                }
            }

            class ShowDialog implements ClickEventApplier {
                @Override
                public void apply(@NotNull NBTComponentSerializer serializer, Style.@NotNull Builder builder, @NotNull CompoundTag input) {
                    builder.clickEvent(ClickEvent.showDialog(NBTDialog.of(input.getCompound(CLICK_EVENT_DIALOG))));
                }
            }

            class Custom implements ClickEventApplier {
                @SuppressWarnings("all")
                @Override
                public void apply(@NotNull NBTComponentSerializer serializer, Style.@NotNull Builder builder, @NotNull CompoundTag input) {
                    String payload = input.getString(CLICK_EVENT_CUSTOM_PAYLOAD);
                    Key id = Key.key(input.getString(CLICK_EVENT_CUSTOM_ID));
                    if (payload != null) {
                        builder.clickEvent(ClickEvent.custom(id, BinaryTagHolder.binaryTagHolder(payload)));
                    } else {
                        builder.clickEvent(ClickEvent.custom(id));
                    }
                }
            }
        }

        class HoverEvent0 implements StyleApplier {
            private static final Map<String, HoverEventApplier> HOVER_EVENT_BY_ACTION = new HashMap<>();
            static {
                HOVER_EVENT_BY_ACTION.put(HOVER_EVENT_SHOW_ITEM, HoverEventApplier.SHOW_ITEM);
                HOVER_EVENT_BY_ACTION.put(HOVER_EVENT_SHOW_TEXT, HoverEventApplier.SHOW_TEXT);
                HOVER_EVENT_BY_ACTION.put(HOVER_EVENT_SHOW_ENTITY, HoverEventApplier.SHOW_ENTITY);
            }

            @Override
            public void apply(@NotNull NBTComponentSerializer serializer, Style.@NotNull Builder builder, @NotNull Tag tag) {
                if (!(tag instanceof CompoundTag input)) {
                    return;
                }
                String actionName = input.getString(HOVER_EVENT_ACTION);
                HoverEventApplier hoverEventApplier = HOVER_EVENT_BY_ACTION.get(actionName);
                if (hoverEventApplier != null) {
                    hoverEventApplier.apply(serializer, builder, input);
                }
            }
        }

        interface HoverEventApplier {
            HoverEventApplier SHOW_ITEM = new ShowItem();
            HoverEventApplier SHOW_TEXT = new ShowText();
            HoverEventApplier SHOW_ENTITY = new ShowEntity();

            void apply(@NotNull NBTComponentSerializer serializer, Style.@NotNull Builder builder, @NotNull CompoundTag input);

            class ShowItem implements HoverEventApplier {
                @SuppressWarnings("all")
                @Override
                public void apply(@NotNull NBTComponentSerializer serializer, Style.@NotNull Builder builder, @NotNull CompoundTag input) {
                    if (serializer.modernEvent()) {
                        Key item = Key.key(input.getString(HOVER_EVENT_ID));
                        int count = input.getInt(HOVER_EVENT_COUNT, 1);
                        CompoundTag components = input.getCompound(HOVER_EVENT_COMPONENTS);
                        if (components != null) {
                            Map<Key, DataComponentValue> componentValues = new HashMap<>();
                            for (Map.Entry<String, Tag> entry : components.entrySet()) {
                                String componentType = entry.getKey();
                                if (componentType.isEmpty()) continue;
                                if (componentType.charAt(0) == '!') {
                                    componentValues.put(Key.key(componentType.substring(1)), NBTDataComponentValue.removed());
                                } else {
                                    componentValues.put(Key.key(componentType), NBTDataComponentValue.of(entry.getValue()));
                                }
                            }
                            builder.hoverEvent(HoverEvent.showItem(item, count, componentValues));
                        } else {
                            builder.hoverEvent(HoverEvent.showItem(item, count));
                        }
                    } else {
                        Tag contentsTag = input.get(HOVER_EVENT_CONTENTS);
                        if (contentsTag instanceof CompoundTag contents) {
                            Key itemId = Key.key(contents.getString(HOVER_EVENT_ID));
                            int count = contents.getInt(HOVER_EVENT_COUNT, 1);
                            if (serializer.dataComponentRelease()) {
                                CompoundTag components = contents.getCompound(HOVER_EVENT_COMPONENTS);
                                if (components != null) {
                                    Map<Key, DataComponentValue> componentValues = new HashMap<>();
                                    for (Map.Entry<String, Tag> entry : components.entrySet()) {
                                        String componentType = entry.getKey();
                                        if (componentType.isEmpty()) continue;
                                        if (componentType.charAt(0) == '!') {
                                            componentValues.put(Key.key(componentType.substring(1)), NBTDataComponentValue.removed());
                                        } else {
                                            componentValues.put(Key.key(componentType), NBTDataComponentValue.of(entry.getValue()));
                                        }
                                    }
                                    builder.hoverEvent(HoverEvent.showItem(itemId, count, componentValues));
                                } else {
                                    builder.hoverEvent(HoverEvent.showItem(itemId, count));
                                }
                            } else {
                                String tag = contents.getString(HOVER_EVENT_TAG);
                                if (tag != null && !tag.isEmpty()) {
                                    builder.hoverEvent(HoverEvent.showItem(itemId, count, BinaryTagHolder.binaryTagHolder(tag)));
                                } else {
                                    builder.hoverEvent(HoverEvent.showItem(itemId, count));
                                }
                            }
                        } else if (contentsTag instanceof StringTag contents) {
                            builder.hoverEvent(HoverEvent.showItem(Key.key(contents.getAsString()), 1));
                        }
                    }
                }
            }

            class ShowText implements HoverEventApplier {
                @Override
                public void apply(@NotNull NBTComponentSerializer serializer, Style.@NotNull Builder builder, @NotNull CompoundTag input) {
                    if (serializer.modernEvent()) {
                        Tag tag = input.get(HOVER_EVENT_VALUE);
                        if (tag != null) {
                            builder.hoverEvent(HoverEvent.showText(serializer.deserialize(tag)));
                        }
                    } else {
                        Tag contents = input.get(HOVER_EVENT_CONTENTS);
                        if (contents != null) {
                            builder.hoverEvent(HoverEvent.showText(serializer.deserialize(contents)));
                        }
                    }
                }
            }

            class ShowEntity implements HoverEventApplier {
                @SuppressWarnings("all")
                @Override
                public void apply(@NotNull NBTComponentSerializer serializer, Style.@NotNull Builder builder, @NotNull CompoundTag input) {
                    if (serializer.modernEvent()) {
                        Key entityType = Key.key(input.getString(HOVER_EVENT_ID));
                        Tag entityName = input.get(HOVER_EVENT_NAME);
                        if (entityName != null) {
                            builder.hoverEvent(HoverEvent.showEntity(entityType, deserializeUUID(input.get(HOVER_EVENT_UUID)), serializer.deserialize(entityName)));
                        } else {
                            builder.hoverEvent(HoverEvent.showEntity(entityType, deserializeUUID(input.get(HOVER_EVENT_UUID))));
                        }
                    } else {
                        CompoundTag contents = input.getCompound(HOVER_EVENT_CONTENTS);
                        if (contents != null) {
                            Key entityType = Key.key(contents.getString(HOVER_EVENT_TYPE));
                            Tag entityName = contents.get(HOVER_EVENT_NAME);
                            if (entityName != null) {
                                builder.hoverEvent(HoverEvent.showEntity(entityType, deserializeUUID(contents.get(HOVER_EVENT_ID)), serializer.deserialize(entityName)));
                            } else {
                                builder.hoverEvent(HoverEvent.showEntity(entityType, deserializeUUID(contents.get(HOVER_EVENT_ID))));
                            }
                        }
                    }
                }

                private UUID deserializeUUID(Tag tag) {
                    UUID entityId;
                    if (tag instanceof StringTag stringTag) {
                        entityId = UUID.fromString(stringTag.getAsString());
                    } else if (tag instanceof IntArrayTag intArrayTag) {
                        entityId = UUIDUtil.uuidFromIntArray(intArrayTag.getAsIntArray());
                    } else if (tag instanceof ListTag listTag && listTag.size() == 4) {
                        entityId = UUIDUtil.uuidFromInts(listTag.getInt(0), listTag.getInt(1), listTag.getInt(2), listTag.getInt(3));
                    } else  {
                        entityId = new UUID(0, 0);
                    }
                    return entityId;
                }
            }
        }
    }

    interface ComponentReader {
        ComponentReader TEXT_COMPONENT = new Text();
        ComponentReader TRANSLATABLE_COMPONENT = new Translatable();
        ComponentReader SCORE_COMPONENT = new Score();
        ComponentReader KEYBIND_COMPONENT = new Keybind();
        ComponentReader SELECTOR_COMPONENT = new Selector();
        ComponentReader NBT_COMPONENT = new NBT();
        ComponentReader OBJECT_COMPONENT = new Object();

        ComponentBuilder<?, ?> deserialize(@NotNull NBTComponentSerializer serializer, @NotNull CompoundTag input);

        class Text implements ComponentReader {
            @Override
            public ComponentBuilder<?, ?> deserialize(@NotNull NBTComponentSerializer serializer, @NotNull CompoundTag input) {
                return Component.text()
                        .content(input.getString(TEXT));
            }
        }

        class Translatable implements ComponentReader {
            @Override
            public ComponentBuilder<?, ?> deserialize(@NotNull NBTComponentSerializer serializer, @NotNull CompoundTag input) {
                TranslatableComponent.Builder builder = Component.translatable()
                        .key(input.getString(TRANSLATE));
                String fallback = input.getString(TRANSLATE_FALLBACK);
                if (fallback != null) {
                    builder.fallback(fallback);
                }
                if (input.get(TRANSLATE_WITH) instanceof ListTag listTag) {
                    List<ComponentLike> arguments = new ArrayList<>(listTag.size());
                    for (int i = 0, size = listTag.size(); i < size; i++) {
                        arguments.add(deserializeTranslationArgument(serializer, listTag.get(i)));
                    }
                    builder.arguments(arguments);
                }
                return builder;
            }

            private ComponentLike deserializeTranslationArgument(NBTComponentSerializer serializer, Tag tag) {
                if (tag instanceof NumericTag numericTag) {
                    return TranslationArgument.numeric(numericTag.getAsNumber());
                } else {
                    return serializer.deserialize(tag);
                }
            }
        }

        class Score implements ComponentReader {
            @Override
            public ComponentBuilder<?, ?> deserialize(@NotNull NBTComponentSerializer serializer, @NotNull CompoundTag input) {
                CompoundTag scoreTag = input.getCompound(SCORE);
                String scoreName = scoreTag.getString(SCORE_NAME);
                String scoreObjective = scoreTag.getString(SCORE_OBJECTIVE);
                return Component.score()
                        .name(scoreName)
                        .objective(scoreObjective);
            }
        }

        class Keybind implements ComponentReader {
            @Override
            public ComponentBuilder<?, ?> deserialize(@NotNull NBTComponentSerializer serializer, @NotNull CompoundTag input) {
                return Component.keybind()
                        .keybind(input.getString(KEYBIND));
            }
        }

        class Selector implements ComponentReader {
            @Override
            public ComponentBuilder<?, ?> deserialize(@NotNull NBTComponentSerializer serializer, @NotNull CompoundTag input) {
                String selector = input.getString(SELECTOR);
                SelectorComponent.Builder builder = Component.selector().pattern(selector);
                Tag binarySelectorSeparator = input.get(SELECTOR_SEPARATOR);
                if (binarySelectorSeparator != null) {
                    builder.separator(serializer.deserialize(binarySelectorSeparator));
                }
                return builder;
            }
        }

        class NBT implements ComponentReader {
            @SuppressWarnings("all")
            @Override
            public ComponentBuilder<?, ?> deserialize(@NotNull NBTComponentSerializer serializer, @NotNull CompoundTag input) {
                String nbtPath = input.getString(NBT);

                NBTComponentBuilder<?, ?> builder = null;
                String block = input.getString(NBT_BLOCK);
                if (block != null) {
                    BlockNBTComponent.Pos pos = BlockNBTComponent.Pos.fromString(block);
                    builder = Component.blockNBT()
                            .pos(pos);
                } else {
                    String entity = input.getString(NBT_ENTITY);
                    if (entity != null) {
                        builder = Component.entityNBT()
                                .selector(entity);
                    } else {
                        String storage = input.getString(NBT_STORAGE);
                        if (storage != null) {
                            builder = Component.storageNBT()
                                    .storage(Key.key(storage));
                        } else {
                            throw new IllegalStateException("Could parse nbt component: " + input.toString());
                        }
                    }
                }

                builder.nbtPath(nbtPath);
                Tag binaryNbtSeparator = input.get(NBT_SEPARATOR);
                if (binaryNbtSeparator != null) {
                    builder.separator(serializer.deserialize(binaryNbtSeparator));
                }
                if (input.getBoolean(NBT_INTERPRET)) {
                    builder.interpret(true);
                }
                if (input.getBoolean(NBT_PLAIN)) {
                    builder.plain(true);
                }
                return builder;
            }
        }

        class Object implements ComponentReader {
            @SuppressWarnings("all")
            @Override
            public ComponentBuilder<?, ?> deserialize(@NotNull NBTComponentSerializer serializer, @NotNull CompoundTag input) {
                CompoundTag playerTag = input.getCompound(OBJECT_PLAYER);
                if (playerTag != null) {
                    PlayerHeadObjectContents.Builder builder = ObjectContents.playerHead();
                    if (!input.getBoolean(OBJECT_HAT)) {
                        builder.hat(false);
                    }
                    String playerName = playerTag.getString(OBJECT_PLAYER_NAME);
                    if (playerName != null) {
                        builder.name(playerName);
                    }
                    int[] playerUUID = playerTag.getIntArray(OBJECT_PLAYER_ID);
                    if (playerUUID != null) {
                        builder.id(UUIDUtil.uuidFromIntArray(playerUUID));
                    }
                    String playerTexture = playerTag.getString(OBJECT_PLAYER_TEXTURE);
                    if (playerTexture != null) {
                        builder.texture(Key.key(playerTexture));
                    }
                    ListTag properties = playerTag.getList(OBJECT_PLAYER_PROPERTIES);
                    if (properties != null) {
                        List<PlayerHeadObjectContents.ProfileProperty> profileProperties = new ArrayList<>(properties.size());
                        for (int i = 0, size = properties.size(); i < size; i++) {
                            if (properties.get(i) instanceof CompoundTag compoundTag) {
                                String name = compoundTag.getString(PROFILE_PROPERTY_NAME);
                                String value = compoundTag.getString(PROFILE_PROPERTY_VALUE);
                                String signature = compoundTag.getString(PROFILE_PROPERTY_SIGNATURE);
                                if (signature == null) {
                                    profileProperties.add(PlayerHeadObjectContents.property(name, value));
                                } else {
                                    profileProperties.add(PlayerHeadObjectContents.property(name, value, signature));
                                }
                            }
                        }
                        builder.profileProperties(profileProperties);
                    }
                    ObjectComponent.Builder componentBuilder = Component.object()
                            .contents(builder.build());
                    Tag fallback = input.getCompound(OBJECT_FALLBACK);
                    if (fallback != null) {
                        componentBuilder.fallback(serializer.deserialize(fallback));
                    }
                    return componentBuilder;
                }

                String sprite = input.getString(OBJECT_SPRITE);
                if (sprite != null) {
                    String atlas = input.getString(OBJECT_ATLAS);
                    Key atlasKey = atlas == null ? SpriteObjectContents.DEFAULT_ATLAS : Key.key(atlas);
                    ObjectComponent.Builder componentBuilder = Component.object()
                            .contents(ObjectContents.sprite(atlasKey, Key.key(sprite)));
                    Tag fallback = input.getCompound(OBJECT_FALLBACK);
                    if (fallback != null) {
                        componentBuilder.fallback(serializer.deserialize(fallback));
                    }
                    return componentBuilder;
                }

                throw new IllegalStateException("Could parse object component: " + input.toString());
            }
        }
    }

    static final class BuilderImpl implements Builder {
        private OptionState flags = NBTSerializerOptions.SCHEMA.emptyState();

        BuilderImpl() {
            BUILDER.accept(this);
        }

        @Override
        public @NotNull Builder options(@NotNull OptionState flags) {
            this.flags = requireNonNull(flags, "flags");
            return this;
        }

        @Override
        public @NotNull Builder editOptions(@NotNull Consumer<OptionState.Builder> optionEditor) {
            final OptionState.Builder builder = NBTSerializerOptions.SCHEMA.stateBuilder()
                    .values(this.flags);
            requireNonNull(optionEditor, "flagEditor").accept(builder);
            this.flags = builder.build();
            return this;
        }

        @Override
        public @NotNull NBTComponentSerializer build() {
            return new NBTComponentSerializerImpl(this.flags);
        }
    }
}