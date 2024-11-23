package net.momirealms.sparrow.nbt.util;

public interface ObjectSize {

    int OBJECT_HEADER = 8;
    int ARRAY_HEADER = 12;
    int OBJECT_REFERENCE = 4;

    int BYTE = 1;
    int SHORT = 2;
    int INT = 4;
    int LONG = 8;
    int FLOAT = 4;
    int DOUBLE = 8;

    int HASH = 4;
    int CODER = 1;

    int MAX_DEPTH = 512;
}
