package net.momirealms.sparrow.nbt;

import net.momirealms.sparrow.nbt.util.UUIDUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Utility class for creating and handling NBT (Named Binary Tag) objects.
 * Provides methods for creating, reading, writing, and converting NBT tags.
 */
public class NBT {

    private NBT() {}

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

    public static ListTag createList(List<Tag> tags) {
        return new ListTag(tags);
    }

    /**
     * Reads an unnamed NBT tag from a DataInput stream.
     *
     * @param input the input stream to read from
     * @return the read NBT tag
     * @throws IOException if an I/O error occurs
     */
    public static Tag readUnnamedTag(DataInput input, boolean named) throws IOException {
        byte typeId = input.readByte();
        if (typeId == 0) {
            return EndTag.INSTANCE;
        } else {
            if (named) {
                StringTag.skipString(input);
            }
            try {
                return TagTypes.typeById(typeId).read(input, 0);
            } catch (IOException ioException) {
                throw new IOException(ioException);
            }
        }
    }

    /**
     * Writes a(n) (un)named NBT tag to a DataOutput stream.
     *
     * @param tag    the tag to write
     * @param output the output stream to write to
     * @throws IOException if an I/O error occurs
     */
    public static void writeUnnamedTag(Tag tag, DataOutput output, boolean named) throws IOException {
        output.writeByte(tag.getId());
        if (tag.getId() != Tag.TAG_END_ID) {
            if (named) {
                output.writeUTF("");
            }
            tag.write(output);
        }
    }

    /**
     * Reads a CompoundTag from a DataInput stream.
     *
     * @param input the input stream to read from
     * @return the read CompoundTag
     * @throws IOException if an I/O error occurs or the root tag is not a CompoundTag
     */
    public static CompoundTag readCompound(DataInput input, boolean named) throws IOException {
        Tag tag = readUnnamedTag(input, named);
        if (tag instanceof CompoundTag) {
            return (CompoundTag) tag;
        } else {
            throw new IOException("Root tag must be CompoundTag");
        }
    }

    /**
     * Writes a CompoundTag to a DataOutput stream.
     *
     * @param nbt    the CompoundTag to write
     * @param output the output stream to write to
     * @throws IOException if an I/O error occurs
     */
    public static void writeCompound(CompoundTag nbt, DataOutput output, boolean named) throws IOException {
        writeUnnamedTag(nbt, output, named);
    }

    /**
     * Reads a CompoundTag from a file.
     *
     * @param file the file to read from
     * @return the read CompoundTag, or null if the file does not exist or is empty
     * @throws IOException if an I/O error occurs
     */
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
            return readCompound(input, false);
        }
    }

    /**
     * Writes a CompoundTag to a file.
     *
     * @param file the file to write to
     * @param nbt  the CompoundTag to write
     * @throws IOException if an I/O error occurs
     */
    public static void writeFile(File file, CompoundTag nbt) throws IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(file);
             DataOutputStream dataOutputStream = new DataOutputStream(fileOutputStream)) {
            writeCompound(nbt, dataOutputStream, false);
        }
    }

    /**
     * Converts a byte array to a CompoundTag.
     *
     * @param bytes the byte array to convert
     * @return the CompoundTag, or null if the byte array is null or empty
     * @throws IOException if an I/O error occurs
     */
    @Nullable
    public static CompoundTag fromBytes(byte[] bytes) throws IOException {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
             DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream)) {
            return readCompound(dataInputStream, false);
        }
    }

    /**
     * Converts a CompoundTag to a byte array.
     *
     * @param nbt the CompoundTag to convert
     * @return the byte array representing the CompoundTag
     * @throws IOException if an I/O error occurs
     */
    public static byte @NotNull [] toBytes(@NotNull CompoundTag nbt) throws IOException {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream)) {
            writeCompound(nbt, dataOutputStream, false);
            return byteArrayOutputStream.toByteArray();
        }
    }

    /**
     * Converts a tag to a byte array.
     *
     * @param nbt the CompoundTag to convert
     * @return the byte array representing the tag
     * @throws IOException if an I/O error occurs
     */
    public static byte @NotNull [] toBytes(@NotNull Tag nbt, boolean named) throws IOException {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream)) {
            writeUnnamedTag(nbt, dataOutputStream, named);
            return byteArrayOutputStream.toByteArray();
        }
    }
}
