package net.momirealms.sparrow.nbt;

import net.kyori.adventure.text.BlockNBTComponent;
import net.kyori.adventure.text.Component;
import net.momirealms.sparrow.nbt.adventure.NBTComponentSerializer;

public final class BlockNBTTest {

    public static void main(String[] args) {
        BlockNBTComponent.Builder builder = Component.blockNBT().localPos(1, 2, 3);
        builder.nbtPath("sss");
        BlockNBTComponent build = builder.build();
        System.out.println(NBTComponentSerializer.nbt().serialize(build));
    }
}
