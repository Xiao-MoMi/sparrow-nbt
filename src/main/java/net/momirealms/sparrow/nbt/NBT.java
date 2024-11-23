package net.momirealms.sparrow.nbt;

import net.momirealms.sparrow.nbt.util.UUIDUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class NBT {

    public static ByteTag createByte(byte b) {
        return new ByteTag(b);
    }

    public static ByteTag createBoolean(boolean b) {
        return new ByteTag(b);
    }

    public static ShortTag createShort(short s) {
        return new ShortTag(s);
    }

    public static IntTag createInt(int i) {
        return new IntTag(i);
    }

    public static LongTag createLong(long l) {
        return new LongTag(l);
    }

    public static FloatTag createFloat(float f) {
        return new FloatTag(f);
    }

    public static DoubleTag createDouble(double d) {
        return new DoubleTag(d);
    }

    public static StringTag createString(String s) {
        return new StringTag(s);
    }

    public static IntArrayTag createIntArray(int[] a) {
        return new IntArrayTag(a);
    }

    public static IntArrayTag createUUID(UUID uuid) {
        return new IntArrayTag(UUIDUtil.uuidToIntArray(uuid));
    }

    public static ByteArrayTag createByteArray(byte[] b) {
        return new ByteArrayTag(b);
    }

    public static LongArrayTag createLongArray(long[] a) {
        return new LongArrayTag(a);
    }

    public static CompoundTag createCompound(Map<String, Tag> tags) {
        return new CompoundTag(tags);
    }

    public static CompoundTag createCompound() {
        return new CompoundTag();
    }

    public static ListTag createList() {
        return new ListTag();
    }

    public static ListTag createList(List<Tag> tags, byte type) {
        return new ListTag(tags, type);
    }

    private static Tag readUnnamedTag(DataInput input) throws IOException {
        byte typeId = input.readByte();
        if (typeId == 0) {
            return EndTag.INSTANCE;
        } else {
            StringTag.skipString(input);
            try {
                return TagTypes.typeById(typeId).read(input, 0);
            } catch (IOException ioException) {
                throw new IOException(ioException);
            }
        }
    }

    private static void writeUnnamedTag(Tag tag, DataOutput output) throws IOException {
        output.writeByte(tag.getId());
        if (tag.getId() != Tag.TAG_END_ID) {
            output.writeUTF("");
            tag.write(output);
        }
    }

    public static CompoundTag readCompound(DataInput input) throws IOException {
        Tag tag = readUnnamedTag(input);
        if (tag instanceof CompoundTag) {
            return (CompoundTag) tag;
        } else {
            throw new IOException("Root tag must be CompoundTag");
        }
    }

    public static void writeCompound(CompoundTag nbt, DataOutput output) throws IOException {
        writeUnnamedTag(nbt, output);
    }

    @Nullable
    public static CompoundTag readFile(File file) throws IOException {
        if (!file.exists()) {
            return null;
        }
        if (file.length() == 0) {
            return null;
        }
        try (FileInputStream fis = new FileInputStream(file);
             DataInputStream input = new DataInputStream(fis)) {
            return readCompound(input);
        }
    }

    public static void writeFile(File file, CompoundTag nbt) throws IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(file);
             DataOutputStream dataOutputStream = new DataOutputStream(fileOutputStream)) {
            writeCompound(nbt, dataOutputStream);
        }
    }

    @Nullable
    public static CompoundTag fromBytes(byte[] bytes) throws IOException {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
             DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream)) {
            return readCompound(dataInputStream);
        }
    }

    public static byte @NotNull [] toBytes(@NotNull CompoundTag nbt) throws IOException {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream)) {
            writeCompound(nbt, dataOutputStream);
            return byteArrayOutputStream.toByteArray();
        }
    }
}
