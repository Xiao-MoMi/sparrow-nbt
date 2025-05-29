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

public class LegacyNBTOps implements DynamicOps<Tag> {
    public static final LegacyNBTOps INSTANCE = new LegacyNBTOps();

    private LegacyNBTOps() {
    }

    public Tag empty() {
        return EndTag.INSTANCE;
    }

    @Override
    public <U> U convertTo(DynamicOps<U> dynamicOps, Tag tag) {
        U convertedValue;
        switch (tag.getId()) {
            case 0 -> convertedValue = dynamicOps.empty();
            case 1 -> convertedValue = dynamicOps.createByte(((NumericTag) tag).getAsByte());
            case 2 -> convertedValue = dynamicOps.createShort(((NumericTag) tag).getAsShort());
            case 3 -> convertedValue = dynamicOps.createInt(((NumericTag) tag).getAsInt());
            case 4 -> convertedValue = dynamicOps.createLong(((NumericTag) tag).getAsLong());
            case 5 -> convertedValue = dynamicOps.createFloat(((NumericTag) tag).getAsFloat());
            case 6 -> convertedValue = dynamicOps.createDouble(((NumericTag) tag).getAsDouble());
            case 7 ->
                    convertedValue = dynamicOps.createByteList(ByteBuffer.wrap(((ByteArrayTag) tag).getAsByteArray()));
            case 8 -> convertedValue = dynamicOps.createString(tag.getAsString());
            case 9 -> convertedValue = this.convertList(dynamicOps, tag);
            case 10 -> convertedValue = this.convertMap(dynamicOps, tag);
            case 11 -> convertedValue = dynamicOps.createIntList(Arrays.stream(((IntArrayTag) tag).getAsIntArray()));
            case 12 -> convertedValue = dynamicOps.createLongList(Arrays.stream(((LongArrayTag) tag).getAsLongArray()));
            default -> throw new IllegalStateException("Unknown tag type: " + tag);
        }
        return convertedValue;
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
    public Tag createString(String string) {
        return new StringTag(string);
    }

    @Override
    public Tag createIntList(IntStream data) {
        return new IntArrayTag(data.toArray());
    }

    @Override
    public Tag createByteList(ByteBuffer data) {
        ByteBuffer byteBuffer = data.duplicate().clear();
        byte[] bytes = new byte[data.capacity()];
        byteBuffer.get(0, bytes, 0, bytes.length);
        return new ByteArrayTag(bytes);
    }

    @Override
    public Tag createLongList(LongStream data) {
        return new LongArrayTag(data.toArray());
    }

    @Override
    public Tag createList(Stream<Tag> data) {
        return new ListTag(data.collect(toMutableList()));
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
        } else if (key instanceof StringTag stringKey) {
            String keyString = stringKey.getAsString();
            CompoundTag resultCompound;
            if (map instanceof CompoundTag existingCompound) {
                resultCompound = existingCompound.copy();
            } else {
                resultCompound = new CompoundTag();
            }
            resultCompound.put(keyString, value);
            return DataResult.success(resultCompound);
        } else {
            return DataResult.error(() -> "key is not a string: " + key, map);
        }
    }

    @Override
    public DataResult<Tag> mergeToMap(Tag map, MapLike<Tag> otherMap) {
        if (!(map instanceof CompoundTag) && !(map instanceof EndTag)) {
            return DataResult.error(() -> "mergeToMap called with not a map: " + map, map);
        } else {
            CompoundTag resultCompound = (map instanceof CompoundTag existingCompound)
                    ? existingCompound.copy()
                    : new CompoundTag();
            List<Tag> invalidKeys = new ArrayList<>();
            otherMap.entries().forEach(pair -> {
                Tag keyTag = pair.getFirst();
                if (keyTag instanceof StringTag stringKey) {
                    resultCompound.put(stringKey.getAsString(), pair.getSecond());
                } else {
                    invalidKeys.add(keyTag);
                }
            });
            return invalidKeys.isEmpty()
                    ? DataResult.success(resultCompound)
                    : DataResult.error(() -> "Invalid keys: " + invalidKeys, resultCompound);
        }
    }

    @Override
    public DataResult<Tag> mergeToMap(Tag inputTag, Map<Tag, Tag> entriesToMerge) {
        if (!(inputTag instanceof CompoundTag) && !(inputTag instanceof EndTag)) {
            return DataResult.error(() -> "mergeToMap called with not a map: " + inputTag, inputTag);
        }
        CompoundTag resultCompound = (inputTag instanceof CompoundTag existingCompound)
                ? existingCompound.copy()
                : new CompoundTag();
        List<Tag> invalidKeys = new ArrayList<>();
        for (Map.Entry<Tag, Tag> entry : entriesToMerge.entrySet()) {
            Tag keyTag = entry.getKey();
            if (keyTag instanceof StringTag stringKey) {
                resultCompound.put(stringKey.getAsString(), entry.getValue());
            } else {
                invalidKeys.add(keyTag);
            }
        }
        return invalidKeys.isEmpty()
                ? DataResult.success(resultCompound)
                : DataResult.error(() -> "Found non-string keys: " + invalidKeys, resultCompound);
    }

    @Override
    public DataResult<Stream<Pair<Tag, Tag>>> getMapValues(Tag map) {
        return map instanceof CompoundTag compoundTag
                ? DataResult.success(
                compoundTag.entrySet().stream()
                        .map(entry -> Pair.of(this.createString(entry.getKey()), entry.getValue()))
        ) : DataResult.error(() -> "Not a map: " + map);
    }

    @Override
    public DataResult<Consumer<BiConsumer<Tag, Tag>>> getMapEntries(Tag map) {
        return map instanceof CompoundTag compoundTag ? DataResult.success((biConsumer) -> {
            for (Map.Entry<String, Tag> entry : compoundTag.entrySet()) {
                biConsumer.accept(this.createString(entry.getKey()), entry.getValue());
            }
        }) : DataResult.error(() -> "Not a map: " + map);
    }

    @Override
    public DataResult<MapLike<Tag>> getMap(Tag map) {
        return map instanceof CompoundTag compoundTag ? DataResult.success(new MapLike<>() {
            @Nullable
            @Override
            public Tag get(Tag tag) {
                if (tag instanceof StringTag stringTag) {
                    return compoundTag.get(stringTag.getAsString());
                }
                throw new UnsupportedOperationException("Cannot get map entry with non-string key: " + tag);
            }

            @Nullable
            @Override
            public Tag get(String string) {
                return compoundTag.get(string);
            }

            @Override
            public Stream<Pair<Tag, Tag>> entries() {
                return compoundTag.entrySet().stream()
                        .map(entry -> Pair.of(LegacyNBTOps.this.createString((String) entry.getKey()), entry.getValue()));
            }

            @Override
            public String toString() {
                return "MapLike[" + compoundTag + "]";
            }
        }) : DataResult.error(() -> "Not a map: " + map);
    }

    @Override
    public Tag createMap(Stream<Pair<Tag, Tag>> data) {
        CompoundTag compoundTag = new CompoundTag();
        data.forEach(pair -> {
            if (pair.getFirst() instanceof StringTag stringTag) {
                compoundTag.put(stringTag.getAsString(), pair.getSecond());
            } else {
                throw new UnsupportedOperationException("Cannot create map with non-string key: " + pair.getFirst());
            }
        });
        return compoundTag;
    }

    @SuppressWarnings("unchecked")
    @Override
    public DataResult<Stream<Tag>> getStream(Tag tag) {
        return tag instanceof CollectionTag<?> collectionTag
                ? DataResult.success(((CollectionTag<Tag>) collectionTag).stream())
                : DataResult.error(() -> "Not a list");
    }

    @Override
    public DataResult<Consumer<Consumer<Tag>>> getList(Tag tag) {
        return tag instanceof CollectionTag<?> collectionTag
                ? DataResult.success(collectionTag::forEach)
                : DataResult.error(() -> "Not a list: " + tag);
    }

    @Override
    public DataResult<ByteBuffer> getByteBuffer(Tag tag) {
        return tag instanceof ByteArrayTag byteArrayTag
                ? DataResult.success(ByteBuffer.wrap(byteArrayTag.getAsByteArray()))
                : getStream(tag).flatMap(stream -> {
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

    @Override
    public DataResult<IntStream> getIntStream(Tag tag) {
        return tag instanceof IntArrayTag intArrayTag
                ? DataResult.success(Arrays.stream(intArrayTag.getAsIntArray()))
                : getStream(tag).flatMap(stream -> {
            final List<Tag> list = stream.toList();
            if (list.stream().allMatch(element -> getNumberValue(element).result().isPresent())) {
                return DataResult.success(list.stream().mapToInt(element -> getNumberValue(element).result().get().intValue()));
            }
            return DataResult.error(() -> "Some elements are not ints: " + tag);
        });
    }

    @Override
    public DataResult<LongStream> getLongStream(Tag tag) {
        return tag instanceof LongArrayTag longArrayTag
                ? DataResult.success(Arrays.stream(longArrayTag.getAsLongArray()))
                : getStream(tag).flatMap(stream -> {
            final List<Tag> list = stream.toList();
            if (list.stream().allMatch(element -> getNumberValue(element).result().isPresent())) {
                return DataResult.success(list.stream().mapToLong(element -> getNumberValue(element).result().get().longValue()));
            }
            return DataResult.error(() -> "Some elements are not longs: " + tag);
        });
    }

    public static <T> Collector<T, ?, List<T>> toMutableList() {
        return Collectors.toCollection(Lists::newArrayList);
    }

    @Override
    public Tag remove(Tag map, String removeKey) {
        if (map instanceof CompoundTag compoundTag) {
            CompoundTag copied = compoundTag.copy();
            copied.remove(removeKey);
            return copied;
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
            }
            return switch (collectionTag) {
                case ListTag listTag -> Optional.of(new GenericListCollector(listTag));
                case ByteArrayTag byteArrayTag -> Optional.of(new ByteListCollector(byteArrayTag.getAsByteArray()));
                case IntArrayTag intArrayTag -> Optional.of(new IntListCollector(intArrayTag.getAsIntArray()));
                case LongArrayTag longArrayTag -> Optional.of(new LongListCollector(longArrayTag.getAsLongArray()));
                default -> throw new IllegalStateException("Unexpected value: " + collectionTag);
            };
        } else {
            return Optional.empty();
        }
    }

    static class ByteListCollector implements ListCollector {
        private final ByteArrayList values = new ByteArrayList();

        ByteListCollector(byte[] values) {
            this.values.addElements(0, values);
        }

        public ListCollector accept(Tag tag) {
            if (tag instanceof ByteTag byteTag) {
                this.values.add(byteTag.getAsByte());
                return this;
            }
            return new GenericListCollector(this.values).accept(tag);
        }

        public Tag result() {
            return new ByteArrayTag(this.values.toByteArray());
        }
    }

    static class GenericListCollector implements ListCollector {
        private final ListTag result = new ListTag();

        GenericListCollector() {}

        GenericListCollector(ListTag list) {
            this.result.addAll(list);
        }

        GenericListCollector(IntArrayList list) {
            list.forEach(i -> this.result.add(new IntTag(i)));
        }

        GenericListCollector(ByteArrayList list) {
            list.forEach(b -> this.result.add(new ByteTag(b)));
        }

        GenericListCollector(LongArrayList list) {
            list.forEach(l -> this.result.add(new LongTag(l)));
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

        IntListCollector(int[] values) {
            this.values.addElements(0, values);
        }

        public ListCollector accept(Tag tag) {
            if (tag instanceof IntTag intTag) {
                this.values.add(intTag.getAsInt());
                return this;
            }
            return new GenericListCollector(this.values).accept(tag);
        }

        public Tag result() {
            return new IntArrayTag(this.values.toIntArray());
        }
    }

    interface ListCollector {
        ListCollector accept(Tag tag);

        default ListCollector acceptAll(Iterable<Tag> tags) {
            ListCollector listCollector = this;
            for (Tag tag : tags) {
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

        LongListCollector(long[] values) {
            this.values.addElements(0, values);
        }

        public ListCollector accept(Tag tag) {
            if (tag instanceof LongTag longTag) {
                this.values.add(longTag.getAsLong());
                return this;
            }
            return new GenericListCollector(this.values).accept(tag);
        }

        public Tag result() {
            return new LongArrayTag(this.values.toLongArray());
        }
    }

    class NbtRecordBuilder extends RecordBuilder.AbstractStringBuilder<Tag, CompoundTag> {
        protected NbtRecordBuilder() {
            super(LegacyNBTOps.this);
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
                }
                CompoundTag compoundTag2 = compoundTag1.copy();
                for (Map.Entry<String, Tag> entry : compoundTag.entrySet()) {
                    compoundTag2.put(entry.getKey(), entry.getValue());
                }
                return DataResult.success(compoundTag2);
            }
            return DataResult.success(compoundTag);
        }
    }
}
