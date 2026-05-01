package net.momirealms.sparrow.nbt.adventure;

import net.kyori.option.Option;
import net.kyori.option.OptionSchema;

public final class NBTSerializerOptions {
    public static final Option<Boolean> EMIT_COMPACT_TEXT_COMPONENT = OptionSchema.globalSchema().booleanOption(key("emit/compact_text_component"), true);
    public static final Option<Boolean> SERIALIZE_COMPONENT_TYPE = OptionSchema.globalSchema().booleanOption(key("serialize/component_types"), true);
    public static final Option<Boolean> MODERN_EVENT_TYPE = OptionSchema.globalSchema().booleanOption(key("emit/modern_event"), true);
    public static final Option<Boolean> DATA_COMPONENT_RELEASE = OptionSchema.globalSchema().booleanOption(key("serialize/data_component_release"), true);
    public static final Option<Boolean> INT_ARRAY_UUID = OptionSchema.globalSchema().booleanOption(key("serialize/int_array_uuid"), true);

    private NBTSerializerOptions() {
    }

    private static String key(final String value) {
        return "sparrow:nbt/" + value;
    }
}
