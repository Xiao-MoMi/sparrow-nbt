package net.momirealms.sparrow.nbt.adventure;

import net.kyori.option.Option;
import net.kyori.option.OptionSchema;

public class NBTSerializerOptions {
    public static final Option<Boolean> EMIT_COMPACT_TEXT_COMPONENT = OptionSchema.globalSchema().booleanOption(key("emit/compact_text_component"), true);
    public static final Option<Boolean> SERIALIZE_COMPONENT_TYPES = OptionSchema.globalSchema().booleanOption(key("serialize/component-types"), true);
    public static final Option<Boolean> EMIT_HOVER_EVENT_TYPE = OptionSchema.globalSchema().booleanOption(key("emit/modern_hover_event"), true);
    public static final Option<Boolean> EMIT_CLICK_EVENT_TYPE = OptionSchema.globalSchema().booleanOption(key("emit/modern_click_event"), true);

    private NBTSerializerOptions() {
    }

    private static String key(final String value) {
        return "sparrow:nbt/" + value;
    }
}
