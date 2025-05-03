/*
 * This file is part of adventure, licensed under the MIT License.
 *
 * Copyright (c) 2017-2024 KyoriPowered
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.momirealms.sparrow.nbt.serializer;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.*;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.util.Services;
import net.kyori.option.OptionState;
import net.momirealms.sparrow.nbt.CompoundTag;
import net.momirealms.sparrow.nbt.ListTag;
import net.momirealms.sparrow.nbt.StringTag;
import net.momirealms.sparrow.nbt.Tag;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;

@SuppressWarnings("all")
class NBTComponentSerializerImpl implements NBTComponentSerializer {
    private static final Optional<Provider> SERVICE = Services.service(Provider.class);
    private static final Consumer<Builder> BUILDER = SERVICE
            .map(Provider::builder)
            .orElse(builder -> {
            });

    @Override
    public @NotNull Style deserializeStyle(@NotNull CompoundTag tag) {
        return NBTStyleSerializer.deserialize(tag, this);
    }

    @Override
    public @NotNull CompoundTag serializeStyle(@NotNull Style style) {
        CompoundTag builder = new CompoundTag();
        NBTStyleSerializer.serialize(style, builder, this);
        return builder;
    }

    // We cannot store these fields in NBTComponentSerializerImpl directly due to class initialisation issues.
    static final class Instances {
        static final NBTComponentSerializer INSTANCE = SERVICE
                .map(Provider::nbt)
                .orElseGet(() -> new NBTComponentSerializerImpl(OptionState.emptyOptionState()));
    }

    private static final String TYPE = "type";

    private static final String TYPE_TEXT = "text";
    private static final String TYPE_TRANSLATABLE = "translatable";
    private static final String TYPE_KEYBIND = "keybind";
    private static final String TYPE_SCORE = "score";
    private static final String TYPE_SELECTOR = "selector";
    private static final String TYPE_NBT = "nbt";

    private static final String EXTRA = "extra";

    private static final String TEXT = "text";

    private static final String TRANSLATE_KEY = "translate";
    private static final String TRANSLATE_WITH = "with";
    private static final String TRANSLATE_FALLBACK = "fallback";

    private static final String KEYBIND = "keybind";

    private static final String SCORE = "score";
    private static final String SCORE_NAME = "name";
    private static final String SCORE_OBJECTIVE = "objective";

    private static final String SELECTOR = "selector";
    private static final String SELECTOR_SEPARATOR = "separator";

    private static final String NBT_SOURCE = "source";
    private static final String NBT = "nbt";
    private static final String NBT_INTERPRET = "interpret";
    private static final String NBT_SEPARATOR = "separator";
    private static final String NBT_BLOCK = "block";
    private static final String NBT_ENTITY = "entity";
    private static final String NBT_STORAGE = "storage";

    private final OptionState flags;
    public final boolean modernHoverEvent;
    public final boolean modernClickEvent;

    NBTComponentSerializerImpl(@NotNull OptionState flags) {
        this.flags = flags;
        this.modernClickEvent = flags.value(NBTSerializerOptions.EMIT_CLICK_EVENT_TYPE);
        this.modernHoverEvent = flags.value(NBTSerializerOptions.EMIT_HOVER_EVENT_TYPE);
    }

    @Override
    public @NotNull Component deserialize(@NotNull Tag input) {
        if (!(input instanceof CompoundTag compound)) {
            return Component.text(input.getAsString());
        }
        // Optional. Specifies the content type. One of "text", "translatable", "score", "selector", "keybind", or "nbt".
        String type = compound.getString(TYPE);
        if (type == null) {
            if (compound.containsKey(TEXT)) {
                type = TYPE_TEXT;
            } else if (compound.containsKey(TRANSLATE_KEY)) {
                type = TYPE_TRANSLATABLE;
            } else if (compound.containsKey(KEYBIND)) {
                type = TYPE_KEYBIND;
            } else if (compound.containsKey(SCORE)) {
                type = TYPE_SCORE;
            } else if (compound.containsKey(SELECTOR)) {
                type = TYPE_SELECTOR;
            } else if (compound.containsKey(NBT) && (compound.containsKey(NBT_SOURCE) || compound.containsKey(NBT_BLOCK) || compound.containsKey(NBT_STORAGE) || compound.containsKey(NBT_ENTITY))) {
                type = TYPE_NBT;
            } else {
                throw new IllegalArgumentException("Could not infer the type of the component");
            }
        }

        Style style = NBTStyleSerializer.deserialize(compound, this);
        List<Component> children = new ArrayList<>();
        ListTag binaryChildren = compound.getList(EXTRA);
        if (binaryChildren != null)
            binaryChildren.forEach(child -> children.add(this.deserialize(child)));

        switch (type) {
            case TYPE_TEXT:
                return Component.text()
                        .content(compound.getString(TEXT))
                        .style(style)
                        .append(children)
                        .build();
            case TYPE_TRANSLATABLE:
                ListTag binaryArguments = compound.getList(TRANSLATE_WITH);
                String fallback = compound.getString(TRANSLATE_FALLBACK);
                List<Component> arguments = new ArrayList<>();
                if (binaryArguments != null) {
                    for (Tag argument : binaryArguments) {
                        arguments.add(this.deserialize(argument));
                    }
                }
                return Component.translatable()
                        .key(compound.getString(TRANSLATE_KEY))
                        .fallback(fallback)
                        .arguments(arguments)
                        .style(style)
                        .append(children)
                        .build();
            case TYPE_KEYBIND:
                return Component.keybind()
                        .keybind(compound.getString(KEYBIND))
                        .style(style)
                        .append(children)
                        .build();
            case TYPE_SCORE:
                CompoundTag binaryScore = compound.getCompound(SCORE);
                String scoreName = binaryScore.getString(SCORE_NAME);
                String scoreObjective = binaryScore.getString(SCORE_OBJECTIVE);
                return Component.score()
                        .name(scoreName)
                        .objective(scoreObjective)
                        .style(style)
                        .append(children)
                        .build();
            case TYPE_SELECTOR:
                String selector = compound.getString(SELECTOR);
                Component selectorSeparator = null;
                Tag binarySelectorSeparator = compound.get(SELECTOR_SEPARATOR);
                if (binarySelectorSeparator != null) {
                    selectorSeparator = this.deserialize(binarySelectorSeparator);
                }
                return Component.selector()
                        .pattern(selector)
                        .separator(selectorSeparator)
                        .style(style)
                        .append(children)
                        .build();
            case TYPE_NBT:
                String nbtPath = compound.getString(NBT);
                boolean nbtInterpret = compound.getBoolean(NBT_INTERPRET);
                Component nbtSeparator = null;
                Tag binaryNbtSeparator = compound.get(NBT_SEPARATOR);
                if (binaryNbtSeparator != null) {
                    nbtSeparator = this.deserialize(binaryNbtSeparator);
                }
                Tag binaryBlock = compound.get(NBT_BLOCK);
                if (binaryBlock != null) {
                    BlockNBTComponent.Pos pos = BlockNBTComponent.Pos.fromString(binaryBlock.getAsString());
                    return Component.blockNBT()
                            .nbtPath(nbtPath)
                            .interpret(nbtInterpret)
                            .separator(nbtSeparator)
                            .pos(pos)
                            .style(style)
                            .append(children)
                            .build();
                }
                Tag binaryEntity = compound.get(NBT_ENTITY);
                if (binaryEntity != null) {
                    return Component.entityNBT()
                            .nbtPath(nbtPath)
                            .interpret(nbtInterpret)
                            .separator(nbtSeparator)
                            .selector(binaryEntity.getAsString())
                            .style(style)
                            .append(children)
                            .build();
                }
                Tag binaryStorage = compound.get(NBT_STORAGE);
                if (binaryStorage != null) {
                    return Component.storageNBT()
                            .nbtPath(nbtPath)
                            .interpret(nbtInterpret)
                            .separator(nbtSeparator)
                            .storage(Key.key(binaryStorage.getAsString()))
                            .style(style)
                            .append(children)
                            .build();
                }
            default:
                throw new IllegalArgumentException("Unknown component type " + type);
        }
    }

    @Override
    public @NotNull Tag serialize(@NotNull Component component) {
        if (this.flags.value(NBTSerializerOptions.EMIT_COMPACT_TEXT_COMPONENT) && component instanceof TextComponent
                && !component.hasStyling() && component.children().isEmpty()) {
            return new StringTag(((TextComponent) component).content());
        }
        return writeCompoundComponent(component);
    }

    private @NotNull CompoundTag writeCompoundComponent(@NotNull Component component) {
        CompoundTag builder = new CompoundTag();
        NBTStyleSerializer.serialize(component.style(), builder, this);
        if (component instanceof TextComponent) {
            this.writeComponentType(TYPE_TEXT, builder);
            builder.putString(TEXT, ((TextComponent) component).content());
        } else if (component instanceof TranslatableComponent) {
            this.writeComponentType(TYPE_TRANSLATABLE, builder);
            TranslatableComponent translatable = (TranslatableComponent) component;
            builder.putString(TRANSLATE_KEY, translatable.key());
            List<TranslationArgument> arguments = translatable.arguments();
            if (!arguments.isEmpty()) {
                List<Tag> argumentsTags = new ArrayList<>();
                for (TranslationArgument argument : arguments) {
                    argumentsTags.add(this.writeCompoundComponent(argument.asComponent()));
                }
                builder.put(TRANSLATE_WITH, new ListTag(argumentsTags));
            }
            String fallback = translatable.fallback();
            if (fallback != null) {
                builder.putString(TRANSLATE_FALLBACK, fallback);
            }
        } else if (component instanceof KeybindComponent) {
            this.writeComponentType(TYPE_KEYBIND, builder);
            builder.putString(KEYBIND, ((KeybindComponent) component).keybind());
        } else if (component instanceof ScoreComponent) {
            this.writeComponentType(TYPE_SCORE, builder);
            ScoreComponent score = (ScoreComponent) component;
            CompoundTag scoreBuilder = new CompoundTag();
            scoreBuilder.putString(SCORE_NAME, score.name());
            scoreBuilder.putString(SCORE_OBJECTIVE, score.objective());
            builder.put(SCORE, scoreBuilder);
        } else if (component instanceof SelectorComponent) {
            this.writeComponentType(TYPE_SELECTOR, builder);
            SelectorComponent selector = (SelectorComponent) component;
            builder.putString(SELECTOR, selector.pattern());
            Component separator = selector.separator();
            if (separator != null) {
                builder.put(SELECTOR_SEPARATOR, this.serialize(separator));
            }
        } else if (component instanceof NBTComponent) {
            this.writeComponentType(TYPE_NBT, builder);
            NBTComponent<?, ?> nbt = (NBTComponent<?, ?>) component;
            builder.putString(NBT, nbt.nbtPath());
            builder.putBoolean(NBT_INTERPRET, nbt.interpret());
            Component separator = nbt.separator();
            if (separator != null) {
                builder.put(NBT_SEPARATOR, this.serialize(separator));
            }
            if (nbt instanceof BlockNBTComponent) {
                builder.putString(NBT_BLOCK, ((BlockNBTComponent) nbt).pos().asString());
            } else if (nbt instanceof EntityNBTComponent) {
                builder.putString(NBT_ENTITY, ((EntityNBTComponent) nbt).selector());
            } else if (nbt instanceof StorageNBTComponent) {
                builder.putString(NBT_STORAGE, ((StorageNBTComponent) nbt).storage().asString());
            } else {
                throw notSureHowToSerialize(component);
            }
        } else {
            throw notSureHowToSerialize(component);
        }
        List<Component> children = component.children();
        if (!children.isEmpty()) {
            List<Tag> serializedChildren = new ArrayList<>();
            for (Component child : children) {
                serializedChildren.add(this.writeCompoundComponent(child));
            }
            builder.put(EXTRA, new ListTag(serializedChildren));
        }
        return builder;
    }

    @NotNull OptionState flags() {
        return this.flags;
    }

    private void writeComponentType(final String componentType, final CompoundTag tag) {
        if (this.flags.value(NBTSerializerOptions.SERIALIZE_COMPONENT_TYPES)) {
            tag.putString(TYPE, componentType);
        }
    }

    private static IllegalArgumentException notSureHowToSerialize(final Component component) {
        return new IllegalArgumentException("Don't know how to serialize " + component + " as a Component");
    }

    static final class BuilderImpl implements NBTComponentSerializer.Builder {

        private OptionState flags = OptionState.emptyOptionState();

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
            final OptionState.Builder builder = OptionState.optionState()
                    .values(this.flags);
            requireNonNull(optionEditor, "optionEditor").accept(builder);
            this.flags = builder.build();
            return this;
        }

        @Override
        public @NotNull NBTComponentSerializer build() {
            return new NBTComponentSerializerImpl(this.flags);
        }
    }
}
