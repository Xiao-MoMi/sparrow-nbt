package net.momirealms.sparrow.nbt.util;

public class MathUtil {

    private MathUtil() {}

    public static int fastFloor(double value) {
        int i = (int) value;
        return value < (double) i ? i - 1 : i;
    }
}
