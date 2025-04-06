package net.momirealms.sparrow.nbt.serializer;

import net.kyori.option.Option;

class NBTSerializerOptions {
    public static final Option<Boolean> EMIT_COMPACT_TEXT_COMPONENT = Option.booleanOption(key("emit/compact_text_component"), true);
    public static final Option<Boolean> SERIALIZE_COMPONENT_TYPES = Option.booleanOption(key("serialize/component-types"), true);

    private NBTSerializerOptions() {
    }

    private static String key(final String value) {
        return "adventure:nbt/" + value;
    }
}
