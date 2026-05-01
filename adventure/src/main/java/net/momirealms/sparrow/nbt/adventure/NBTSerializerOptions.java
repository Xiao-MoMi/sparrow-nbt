package net.momirealms.sparrow.nbt.adventure;

import net.kyori.option.Option;
import net.kyori.option.OptionSchema;

public final class NBTSerializerOptions {
    public static final OptionSchema.Mutable SCHEMA = OptionSchema.emptySchema();
    public static final Option<Boolean> EMIT_COMPACT_TEXT_COMPONENT = SCHEMA.booleanOption(key("emit/compact_text_component"), true);
    public static final Option<Boolean> SERIALIZE_COMPONENT_TYPE = SCHEMA.booleanOption(key("serialize/component_types"), true);
    public static final Option<Boolean> MODERN_EVENT_TYPE = SCHEMA.booleanOption(key("emit/modern_event"), true);
    public static final Option<Boolean> DATA_COMPONENT_RELEASE = SCHEMA.booleanOption(key("serialize/data_component_release"), true);
    public static final Option<Boolean> INT_ARRAY_UUID = SCHEMA.booleanOption(key("serialize/int_array_uuid"), true);

    private NBTSerializerOptions() {
    }

    private static String key(final String value) {
        return "sparrow:nbt/" + value;
    }
}
