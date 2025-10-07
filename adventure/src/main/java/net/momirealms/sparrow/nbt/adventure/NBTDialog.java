package net.momirealms.sparrow.nbt.adventure;

import net.kyori.adventure.dialog.DialogLike;
import net.momirealms.sparrow.nbt.Tag;

public class NBTDialog implements DialogLike {
    private final Tag dialog;

    public NBTDialog(Tag dialog) {
        this.dialog = dialog;
    }

    public static NBTDialog of(Tag dialog) {
        return new NBTDialog(dialog);
    }

    public Tag tag() {
        return dialog;
    }
}
