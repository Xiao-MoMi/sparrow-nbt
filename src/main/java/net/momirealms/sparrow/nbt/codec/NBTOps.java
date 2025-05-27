package net.momirealms.sparrow.nbt.codec;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import net.momirealms.sparrow.nbt.*;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.*;

public class NBTOps implements DynamicOps<Tag> {
    public static final NBTOps INSTANCE = new NBTOps();

    private NBTOps() {}

    public Tag empty() {
        return EndTag.INSTANCE;
    }

    @Override
    public <U> U convertTo(DynamicOps<U> dynamicOps, Tag tag) {
        U var10000;
        switch (tag.getId()) {
            case 0 -> var10000 = dynamicOps.empty();
            case 1 -> var10000 = dynamicOps.createByte(((NumericTag)tag).getAsByte());
            case 2 -> var10000 = dynamicOps.createShort(((NumericTag)tag).getAsShort());
            case 3 -> var10000 = dynamicOps.createInt(((NumericTag)tag).getAsInt());
            case 4 -> var10000 = dynamicOps.createLong(((NumericTag)tag).getAsLong());
            case 5 -> var10000 = dynamicOps.createFloat(((NumericTag)tag).getAsFloat());
            case 6 -> var10000 = dynamicOps.createDouble(((NumericTag)tag).getAsDouble());
            case 7 -> var10000 = dynamicOps.createByteList(ByteBuffer.wrap(((ByteArrayTag)tag).getAsByteArray()));
            case 8 -> var10000 = dynamicOps.createString(tag.getAsString());
            case 9 -> var10000 = this.convertList(dynamicOps, tag);
            case 10 -> var10000 = this.convertMap(dynamicOps, tag);
            case 11 -> var10000 = dynamicOps.createIntList(Arrays.stream(((IntArrayTag)tag).getAsIntArray()));
            case 12 -> var10000 = dynamicOps.createLongList(Arrays.stream(((LongArrayTag)tag).getAsLongArray()));
            default -> throw new IllegalStateException("Unknown tag type: " + tag);
        }
        return var10000;
    }

    @Override
    public DataResult<Number> getNumberValue(Tag tag) {
        if (tag instanceof NumericTag numericTag) {
            return DataResult.success(numericTag.getAsNumber());
        } else {
            return DataResult.error(() -> "Not a number");
        }
    }

    @Override
    public Tag createNumeric(Number number) {
        return new DoubleTag(number.doubleValue());
    }

    @Override
    public Tag createByte(byte b) {
        return new ByteTag(b);
    }

    @Override
    public Tag createShort(short s) {
        return new ShortTag(s);
    }

    @Override
    public Tag createInt(int i) {
        return new IntTag(i);
    }

    @Override
    public Tag createLong(long l) {
        return new LongTag(l);
    }

    @Override
    public Tag createFloat(float f) {
        return new FloatTag(f);
    }

    @Override
    public Tag createDouble(double d) {
        return new DoubleTag(d);
    }

    @Override
    public Tag createBoolean(boolean b) {
        return new ByteTag(b);
    }

    @Override
    public DataResult<String> getStringValue(Tag tag) {
        if (tag instanceof StringTag stringTag) {
            return DataResult.success(stringTag.getAsString());
        } else {
            return DataResult.error(() -> "Not a string");
        }
    }

    @Override
    public Tag createString(String string) {
        return new StringTag(string);
    }

    @Override
    public DataResult<Tag> mergeToList(Tag tag, Tag tag2) {
        return createCollector(tag)
                .map((merger) -> DataResult.success(merger.accept(tag2).result()))
                .orElseGet(() -> DataResult.error(() -> "mergeToList called with not a list: " + tag, tag));
    }

    @Override
    public DataResult<Tag> mergeToList(Tag tag, List<Tag> list) {
        return createCollector(tag)
                .map((merger) -> DataResult.success(merger.acceptAll(list).result()))
                .orElseGet(() -> DataResult.error(() -> "mergeToList called with not a list: " + tag, tag));
    }

    @Override
    public DataResult<Tag> mergeToMap(Tag map, Tag key, Tag value) {
        if (!(map instanceof CompoundTag) && !(map instanceof EndTag)) {
            return DataResult.error(() -> "mergeToMap called with not a map: " + map, map);
        } else if (key instanceof StringTag var4) {
            String var10 = var4.getAsString();
            CompoundTag var11;
            if (map instanceof CompoundTag compoundTagx) {
                var11 = compoundTagx.copy();
            } else {
                var11 = new CompoundTag();
            }

            CompoundTag compoundTag1 = var11;
            compoundTag1.put(var10, value);
            return DataResult.success(compoundTag1);
        } else {
            return DataResult.error(() -> "key is not a string: " + key, map);
        }
    }

    @Override
    public DataResult<Tag> mergeToMap(Tag map, MapLike<Tag> otherMap) {
        if (!(map instanceof CompoundTag) && !(map instanceof EndTag)) {
            return DataResult.error(() -> "mergeToMap called with not a map: " + map, map);
        } else {
            CompoundTag var10000;
            if (map instanceof CompoundTag compoundTag) {
                var10000 = compoundTag.copy();
            } else {
                var10000 = new CompoundTag();
            }

            CompoundTag compoundTag1 = var10000;
            List<Tag> list = new ArrayList<>();
            otherMap.entries().forEach((pair) -> {
                Tag tag = pair.getFirst();
                if (tag instanceof StringTag stringTag) {
                    compoundTag1.put(stringTag.getAsString(), pair.getSecond());
                } else {
                    list.add(tag);
                }
            });
            return !list.isEmpty() ? DataResult.error(() -> "some keys are not strings: " + String.valueOf(list), compoundTag1) : DataResult.success(compoundTag1);
        }
    }

    @Override
    public DataResult<Tag> mergeToMap(Tag tag, Map<Tag, Tag> map) {
        if (!(tag instanceof CompoundTag) && !(tag instanceof EndTag)) {
            return DataResult.error(() -> "mergeToMap called with not a map: " + tag, tag);
        } else {
            CompoundTag var10000;
            if (tag instanceof CompoundTag compoundTag) {
                var10000 = compoundTag.copy();
            } else {
                var10000 = new CompoundTag();
            }
            CompoundTag compoundTag1 = var10000;
            List<Tag> list = new ArrayList<>();
            for(Map.Entry<Tag, Tag> entry : map.entrySet()) {
                Tag tag1 = entry.getKey();
                if (tag1 instanceof StringTag var8) {
                    String var10 = var8.getAsString();
                    compoundTag1.put(var10, entry.getValue());
                } else {
                    list.add(tag1);
                }
            }
            return !list.isEmpty() ? DataResult.error(() -> "some keys are not strings: " + String.valueOf(list), compoundTag1) : DataResult.success(compoundTag1);
        }
    }

    @Override
    public DataResult<Stream<Pair<Tag, Tag>>> getMapValues(Tag map) {
        DataResult<Stream<Pair<Tag, Tag>>> var10000;
        if (map instanceof CompoundTag compoundTag) {
            var10000 = DataResult.success(compoundTag.entrySet().stream().map((entry) -> Pair.of(this.createString((String)entry.getKey()), (Tag)entry.getValue())));
        } else {
            var10000 = DataResult.error(() -> "Not a map: " + map);
        }

        return var10000;
    }

    @Override
    public DataResult<Consumer<BiConsumer<Tag, Tag>>> getMapEntries(Tag map) {
        DataResult<Consumer<BiConsumer<Tag, Tag>>> var10000;
        if (map instanceof CompoundTag compoundTag) {
            var10000 = DataResult.success((biConsumer) -> {
                for(Map.Entry<String, Tag> entry : compoundTag.entrySet()) {
                    biConsumer.accept(this.createString(entry.getKey()), (Tag) entry.getValue());
                }
            });
        } else {
            var10000 = DataResult.error(() -> "Not a map: " + map);
        }
        return var10000;
    }

    @Override
    public DataResult<MapLike<Tag>> getMap(Tag map) {
        DataResult<MapLike<Tag>> var10000;
        if (map instanceof final CompoundTag compoundTag) {
            var10000 = DataResult.success(new MapLike<Tag>() {
                @Nullable
                public Tag get(Tag tag) {
                    if (tag instanceof StringTag stringTag) {
                        return compoundTag.get(stringTag.getAsString());
                    } else {
                        throw new UnsupportedOperationException("Cannot get map entry with non-string key: " + tag);
                    }
                }

                @Nullable
                public Tag get(String string) {
                    return compoundTag.get(string);
                }

                public Stream<Pair<Tag, Tag>> entries() {
                    return compoundTag.entrySet().stream().map((entry) -> Pair.of(NBTOps.this.createString((String)entry.getKey()), (Tag)entry.getValue()));
                }

                public String toString() {
                    return "MapLike[" + compoundTag + "]";
                }
            });
        } else {
            var10000 = DataResult.error(() -> "Not a map: " + map);
        }

        return var10000;
    }

    @Override
    public Tag createMap(Stream<Pair<Tag, Tag>> data) {
        CompoundTag compoundTag = new CompoundTag();
        data.forEach((pair) -> {
            Tag tag = pair.getFirst();
            Tag tag1 = pair.getSecond();
            if (tag instanceof StringTag stringTag) {
                compoundTag.put(stringTag.getAsString(), tag1);
            } else {
                throw new UnsupportedOperationException("Cannot create map with non-string key: " + tag);
            }
        });
        return compoundTag;
    }

    @SuppressWarnings("unchecked")
    @Override
    public DataResult<Stream<Tag>> getStream(Tag tag) {
        DataResult<Stream<Tag>> var10000;
        if (tag instanceof CollectionTag<?> collectionTag) {
            var10000 = DataResult.success(((CollectionTag<Tag>) collectionTag).stream());
        } else {
            var10000 = DataResult.error(() -> "Not a list");
        }
        return var10000;
    }

    @Override
    public DataResult<Consumer<Consumer<Tag>>> getList(Tag tag) {
        DataResult<Consumer<Consumer<Tag>>> var10000;
        if (tag instanceof CollectionTag<?> collectionTag) {
            Objects.requireNonNull(collectionTag);
            var10000 = DataResult.success(collectionTag::forEach);
        } else {
            var10000 = DataResult.error(() -> "Not a list: " + tag);
        }
        return var10000;
    }

    @Override
    public DataResult<ByteBuffer> getByteBuffer(Tag tag) {
        DataResult<ByteBuffer> var10000;
        if (tag instanceof ByteArrayTag byteArrayTag) {
            var10000 = DataResult.success(ByteBuffer.wrap(byteArrayTag.getAsByteArray()));
        } else {
            var10000 = getStream(tag).flatMap(stream -> {
                final List<Tag> list = stream.toList();
                if (list.stream().allMatch(element -> getNumberValue(element).result().isPresent())) {
                    final ByteBuffer buffer = ByteBuffer.wrap(new byte[list.size()]);
                    for (int i = 0; i < list.size(); i++) {
                        buffer.put(i, getNumberValue(list.get(i)).result().get().byteValue());
                    }
                    return DataResult.success(buffer);
                }
                return DataResult.error(() -> "Some elements are not bytes: " + tag);
            });
        }

        return var10000;
    }

    @Override
    public Tag createByteList(ByteBuffer data) {
        ByteBuffer byteBuffer = data.duplicate().clear();
        byte[] bytes = new byte[data.capacity()];
        byteBuffer.get(0, bytes, 0, bytes.length);
        return new ByteArrayTag(bytes);
    }

    @Override
    public DataResult<IntStream> getIntStream(Tag tag) {
        DataResult<IntStream> var10000;
        if (tag instanceof IntArrayTag intArrayTag) {
            var10000 = DataResult.success(Arrays.stream(intArrayTag.getAsIntArray()));
        } else {
            var10000 = getStream(tag).flatMap(stream -> {
                final List<Tag> list = stream.toList();
                if (list.stream().allMatch(element -> getNumberValue(element).result().isPresent())) {
                    return DataResult.success(list.stream().mapToInt(element -> getNumberValue(element).result().get().intValue()));
                }
                return DataResult.error(() -> "Some elements are not ints: " + tag);
            });
        }

        return var10000;
    }

    @Override
    public Tag createIntList(IntStream data) {
        return new IntArrayTag(data.toArray());
    }

    @Override
    public DataResult<LongStream> getLongStream(Tag tag) {
        DataResult<LongStream> var10000;
        if (tag instanceof LongArrayTag longArrayTag) {
            var10000 = DataResult.success(Arrays.stream(longArrayTag.getAsLongArray()));
        } else {
            var10000 = getStream(tag).flatMap(stream -> {
                final List<Tag> list = stream.toList();
                if (list.stream().allMatch(element -> getNumberValue(element).result().isPresent())) {
                    return DataResult.success(list.stream().mapToLong(element -> getNumberValue(element).result().get().longValue()));
                }
                return DataResult.error(() -> "Some elements are not longs: " + tag);
            });
        }
        return var10000;
    }

    @Override
    public Tag createLongList(LongStream data) {
        return new LongArrayTag(data.toArray());
    }

    @Override
    public Tag createList(Stream<Tag> data) {
        return new ListTag(data.collect(toMutableList()));
    }

    public static <T> Collector<T, ?, List<T>> toMutableList() {
        return Collectors.toCollection(Lists::newArrayList);
    }

    @Override
    public Tag remove(Tag map, String removeKey) {
        if (map instanceof CompoundTag compoundTag) {
            CompoundTag compoundTag1 = compoundTag.copy();
            compoundTag1.remove(removeKey);
            return compoundTag1;
        } else {
            return map;
        }
    }

    @Override
    public String toString() {
        return "SPARROW_NBT";
    }

    @Override
    public RecordBuilder<Tag> mapBuilder() {
        return new NbtRecordBuilder();
    }

    private static Optional<ListCollector> createCollector(Tag tag) {
        if (tag instanceof EndTag) {
            return Optional.of(new GenericListCollector());
        } else if (tag instanceof CollectionTag<?> collectionTag) {
            if (collectionTag.isEmpty()) {
                return Optional.of(new GenericListCollector());
            } else {
                Optional<ListCollector> var10000;
                switch (collectionTag) {
                    case ListTag listTag -> var10000 = Optional.of(new GenericListCollector(listTag));
                    case ByteArrayTag byteArrayTag -> var10000 = Optional.of(new ByteListCollector(byteArrayTag.getAsByteArray()));
                    case IntArrayTag intArrayTag -> var10000 = Optional.of(new IntListCollector(intArrayTag.getAsIntArray()));
                    case LongArrayTag longArrayTag -> var10000 = Optional.of(new LongListCollector(longArrayTag.getAsLongArray()));
                    default -> throw new IllegalStateException("Unexpected value: " + collectionTag);
                }
                return var10000;
            }
        } else {
            return Optional.empty();
        }
    }

    static class ByteListCollector implements ListCollector {
        private final ByteArrayList values = new ByteArrayList();

        public ByteListCollector(byte[] values) {
            this.values.addElements(0, values);
        }

        public ListCollector accept(Tag tag) {
            if (tag instanceof ByteTag byteTag) {
                this.values.add(byteTag.getAsByte());
                return this;
            } else {
                return (new GenericListCollector(this.values)).accept(tag);
            }
        }

        public Tag result() {
            return new ByteArrayTag(this.values.toByteArray());
        }
    }

    static class GenericListCollector implements ListCollector {
        private final ListTag result = new ListTag();

        GenericListCollector() {
        }

        GenericListCollector(ListTag list) {
            this.result.addAll(list);
        }

        public GenericListCollector(IntArrayList list) {
            list.forEach((i) -> this.result.add(new IntTag(i)));
        }

        public GenericListCollector(ByteArrayList list) {
            list.forEach((b) -> this.result.add(new ByteTag(b)));
        }

        public GenericListCollector(LongArrayList list) {
            list.forEach((l) -> this.result.add(new LongTag(l)));
        }

        public ListCollector accept(Tag tag) {
            this.result.add(tag);
            return this;
        }

        public Tag result() {
            return this.result;
        }
    }

    static class IntListCollector implements ListCollector {
        private final IntArrayList values = new IntArrayList();

        public IntListCollector(int[] values) {
            this.values.addElements(0, values);
        }

        public ListCollector accept(Tag tag) {
            if (tag instanceof IntTag intTag) {
                this.values.add(intTag.getAsInt());
                return this;
            } else {
                return (new GenericListCollector(this.values)).accept(tag);
            }
        }

        public Tag result() {
            return new IntArrayTag(this.values.toIntArray());
        }
    }

    interface ListCollector {
        ListCollector accept(Tag var1);

        default ListCollector acceptAll(Iterable<Tag> tags) {
            ListCollector listCollector = this;

            for(Tag tag : tags) {
                listCollector = listCollector.accept(tag);
            }

            return listCollector;
        }

        default ListCollector acceptAll(Stream<Tag> tags) {
            Objects.requireNonNull(tags);
            return this.acceptAll(tags::iterator);
        }

        Tag result();
    }

    static class LongListCollector implements ListCollector {
        private final LongArrayList values = new LongArrayList();

        public LongListCollector(long[] values) {
            this.values.addElements(0, values);
        }

        public ListCollector accept(Tag tag) {
            if (tag instanceof LongTag longTag) {
                this.values.add(longTag.getAsLong());
                return this;
            } else {
                return (new GenericListCollector(this.values)).accept(tag);
            }
        }

        public Tag result() {
            return new LongArrayTag(this.values.toLongArray());
        }
    }

    class NbtRecordBuilder extends RecordBuilder.AbstractStringBuilder<Tag, CompoundTag> {
        protected NbtRecordBuilder() {
            super(NBTOps.this);
        }

        protected CompoundTag initBuilder() {
            return new CompoundTag();
        }

        protected CompoundTag append(String key, Tag value, CompoundTag tag) {
            tag.put(key, value);
            return tag;
        }

        protected DataResult<Tag> build(CompoundTag compoundTag, Tag tag) {
            if (tag != null && tag != EndTag.INSTANCE) {
                if (!(tag instanceof CompoundTag compoundTag1)) {
                    return DataResult.error(() -> "mergeToMap called with not a map: " + tag, tag);
                } else {
                    CompoundTag compoundTag2 = compoundTag1.copy();
                    for(Map.Entry<String, Tag> entry : compoundTag.entrySet()) {
                        compoundTag2.put(entry.getKey(), (Tag)entry.getValue());
                    }
                    return DataResult.success(compoundTag2);
                }
            } else {
                return DataResult.success(compoundTag);
            }
        }
    }
}
