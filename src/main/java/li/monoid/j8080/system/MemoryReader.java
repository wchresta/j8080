package li.monoid.j8080.system;

import li.monoid.j8080.memory.Cast;

public interface MemoryReader {
    byte readByte(int address);

    default byte readByte(short address) {
        return readByte(Short.toUnsignedInt(address));
    }

    default short readShort(int address) {
        int hi = Byte.toUnsignedInt(readByte(address + 1));
        int lo = Byte.toUnsignedInt(readByte(address));
        return Cast.toShort(hi << 8 | lo);
    }

    default short readShort(short address) {
        return readShort(Short.toUnsignedInt(address));
    }
}
