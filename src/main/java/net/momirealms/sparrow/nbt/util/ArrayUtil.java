package net.momirealms.sparrow.nbt.util;

import java.lang.reflect.Array;

public class ArrayUtil {

    public static int[] add(int[] array, int index, int element) {
        return (int[]) add(array, index, element, Integer.class);
    }

    public static byte[] add(byte[] array, int index, byte element) {
        return (byte[]) add(array, index, element, Byte.class);
    }

    public static long[] add(long[] array, int index, long element) {
        return (long[]) add(array, index, element, Long.TYPE);
    }

    private static Object add(Object array, int index, Object element, Class<?> clazz) {
        if (array == null) {
            if (index != 0) {
                throw new IndexOutOfBoundsException("Index: " + index + ", Length: 0");
            } else {
                Object joinedArray = Array.newInstance(clazz, 1);
                Array.set(joinedArray, 0, element);
                return joinedArray;
            }
        } else {
            int length = Array.getLength(array);
            if (index <= length && index >= 0) {
                Object result = Array.newInstance(clazz, length + 1);
                System.arraycopy(array, 0, result, 0, index);
                Array.set(result, index, element);
                if (index < length) {
                    System.arraycopy(array, index, result, index + 1, length - index);
                }
                return result;
            } else {
                throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + length);
            }
        }
    }

    public static int[] remove(int[] array, int index) {
        return (int[]) remove((Object) array, index);
    }

    public static byte[] remove(byte[] array, int index) {
        return (byte[]) remove((Object) array, index);
    }

    public static long[] remove(long[] array, int index) {
        return (long[]) remove((Object) array, index);
    }

    private static Object remove(Object array, int index) {
        int length = getLength(array);
        if (index >= 0 && index < length) {
            Object result = Array.newInstance(array.getClass().getComponentType(), length - 1);
            System.arraycopy(array, 0, result, 0, index);
            if (index < length - 1) {
                System.arraycopy(array, index + 1, result, index, length - index - 1);
            }
            return result;
        } else {
            throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + length);
        }
    }

    public static int getLength(Object array) {
        return array == null ? 0 : Array.getLength(array);
    }
}
