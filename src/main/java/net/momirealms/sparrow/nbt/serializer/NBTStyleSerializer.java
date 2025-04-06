package net.momirealms.sparrow.nbt.serializer;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.momirealms.sparrow.nbt.ByteTag;
import net.momirealms.sparrow.nbt.CompoundTag;
import net.momirealms.sparrow.nbt.StringTag;
import net.momirealms.sparrow.nbt.Tag;
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
    private static final String CLICK_EVENT = "clickEvent";
    private static final String HOVER_EVENT = "hoverEvent";

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
        Tag binaryInsertion = compound.get(INSERTION);
        if (binaryInsertion != null) {
            styleBuilder.insertion(((StringTag) binaryInsertion).getAsString());
        }
        Tag binaryClickEvent = compound.get(CLICK_EVENT);
        if (binaryClickEvent != null) {
            styleBuilder.clickEvent(NBTClickEventSerializer.deserialize((CompoundTag) binaryClickEvent));
        }
        Tag binaryHoverEvent = compound.get(HOVER_EVENT);
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
                        // Never called, but needed for proper compilation
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
            tag.put(CLICK_EVENT, NBTClickEventSerializer.serialize(clickEvent));
        }

        HoverEvent<?> hoverEvent = style.hoverEvent();
        if (hoverEvent != null) {
            tag.put(HOVER_EVENT, NBTHoverEventSerializer.serialize(hoverEvent, serializer));
        }
    }

    private static TextDecoration.@NotNull State readOptionalState(@NotNull String key, @NotNull CompoundTag compound) {
        Tag tag = compound.get(key);
        if (tag == null) {
            return TextDecoration.State.NOT_SET;
        }
        return TextDecoration.State.byBoolean(((ByteTag) tag).booleanValue());
    }
}
