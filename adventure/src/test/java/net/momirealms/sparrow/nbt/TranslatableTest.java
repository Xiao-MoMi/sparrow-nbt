package net.momirealms.sparrow.nbt;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslationArgument;
import net.kyori.adventure.text.format.TextColor;
import net.momirealms.sparrow.nbt.adventure.NBTComponentSerializer;

public class TranslatableTest {

    public static void main(String[] args) {
        Component component = Component.translatable("xxx.xxx").arguments(TranslationArgument.numeric(100d), TranslationArgument.bool(true), Component.text("sb").color(TextColor.fromHexString("#ff0000")));
        System.out.println(NBTComponentSerializer.nbt().serialize(component));
    }
}
