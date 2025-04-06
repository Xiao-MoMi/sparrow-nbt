package net.momirealms.sparrow.nbt.serializer;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.*;
import net.momirealms.sparrow.nbt.*;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("all")
class NBTStyleSerializer {
    private static final String COLOR = "color";
    private static final String BOLD = "bold";
    private static final String ITALIC = "italic";
    private static final String UNDERLINED = "underlined";
    private static final String STRIKETHROUGH = "strikethrough";
    private static final String OBFUSCATED = "obfuscated";
    private static final String FONT = "font";
    private static final String INSERTION = "insertion";
    private static final String SHADOW_COLOR = "shadow_color";
    private static final String CLICK_EVENT_LEGACY = "clickEvent";
    private static final String HOVER_EVENT_LEGACY = "hoverEvent";

    private NBTStyleSerializer() {
    }

    static @NotNull Style deserialize(@NotNull CompoundTag compound, @NotNull NBTComponentSerializerImpl serializer) {
        Style.Builder styleBuilder = Style.style();
        String colorString = compound.getString(COLOR);
        if (!colorString.isEmpty()) {
            if (colorString.startsWith(TextColor.HEX_PREFIX)) {
                styleBuilder.color(TextColor.fromHexString(colorString));
            } else {
                styleBuilder.color(NamedTextColor.NAMES.value(colorString));
            }
        }
        styleBuilder.decoration(TextDecoration.BOLD, readOptionalState(BOLD, compound))
                .decoration(TextDecoration.ITALIC, readOptionalState(ITALIC, compound))
                .decoration(TextDecoration.UNDERLINED, readOptionalState(UNDERLINED, compound))
                .decoration(TextDecoration.STRIKETHROUGH, readOptionalState(STRIKETHROUGH, compound))
                .decoration(TextDecoration.OBFUSCATED, readOptionalState(OBFUSCATED, compound));
        String fontString = compound.getString(FONT);
        if (!fontString.isEmpty()) {
            styleBuilder.font(Key.key(fontString));
        }
        Tag binaryClickEvent = compound.get(CLICK_EVENT_LEGACY);
        if (binaryClickEvent != null) {
            styleBuilder.clickEvent(NBTClickEventSerializer.deserialize((CompoundTag) binaryClickEvent));
        }
        Tag binaryInsertion = compound.get(INSERTION);
        if (binaryInsertion != null) {
            styleBuilder.insertion(((StringTag) binaryInsertion).getAsString());
        }
        Tag binaryShadewColor = compound.get(SHADOW_COLOR);
        if (binaryShadewColor != null) {
            if (binaryShadewColor instanceof IntTag intTag) {
                styleBuilder.shadowColor(ShadowColor.shadowColor(intTag.getAsInt()));
            } else if (binaryShadewColor instanceof ListTag listTag && listTag.size() == 4) {
                int r = (int) listTag.getFloat(0) * 255;
                int g = (int) listTag.getFloat(1) * 255;
                int b = (int) listTag.getFloat(2) * 255;
                int a = (int) listTag.getFloat(3) * 255;
                styleBuilder.shadowColor(ShadowColor.shadowColor(r, g, b, a));
            }
        }
        Tag binaryHoverEvent = compound.get(HOVER_EVENT_LEGACY);
        if (binaryHoverEvent != null) {
            styleBuilder.hoverEvent(NBTHoverEventSerializer.deserialize((CompoundTag) binaryHoverEvent, serializer));
        }
        return styleBuilder.build();
    }

    static void serialize(@NotNull Style style, CompoundTag tag,
                          @NotNull NBTComponentSerializerImpl serializer) {
        TextColor color = style.color();
        if (color != null) {
            tag.putString(COLOR, color.toString());
        }
        style.decorations().forEach((decoration, state) -> {
            if (state != TextDecoration.State.NOT_SET) {
                String decorationName;
                switch (decoration) {
                    case OBFUSCATED:
                        decorationName = OBFUSCATED;
                        break;
                    case BOLD:
                        decorationName = BOLD;
                        break;
                    case STRIKETHROUGH:
                        decorationName = STRIKETHROUGH;
                        break;
                    case UNDERLINED:
                        decorationName = UNDERLINED;
                        break;
                    case ITALIC:
                        decorationName = ITALIC;
                        break;
                    default:
                        throw new IllegalStateException("Unknown text decoration: " + decoration);
                }
                tag.putBoolean(decorationName, state == TextDecoration.State.TRUE);
            }
        });
        Key font = style.font();
        if (font != null) {
            tag.putString(FONT, font.asString());
        }
        String insertion = style.insertion();
        if (insertion != null) {
            tag.putString(INSERTION, insertion);
        }
        ClickEvent clickEvent = style.clickEvent();
        if (clickEvent != null) {
            tag.put(CLICK_EVENT_LEGACY, NBTClickEventSerializer.serialize(clickEvent));
        }
        HoverEvent<?> hoverEvent = style.hoverEvent();
        if (hoverEvent != null) {
            tag.put(HOVER_EVENT_LEGACY, NBTHoverEventSerializer.serialize(hoverEvent, serializer));
        }
        ShadowColor shadowColor = style.shadowColor();
        if (shadowColor != null) {
            tag.put(SHADOW_COLOR, new IntTag(shadowColor.value()));
        }
    }

    private static TextDecoration.@NotNull State readOptionalState(@NotNull String key, @NotNull CompoundTag compound) {
        Tag tag = compound.get(key);
        if (!(tag instanceof ByteTag byteTag)) {
            return TextDecoration.State.NOT_SET;
        }
        return TextDecoration.State.byBoolean(byteTag.booleanValue());
    }
}
