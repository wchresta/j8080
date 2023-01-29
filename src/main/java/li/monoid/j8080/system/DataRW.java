package li.monoid.j8080.system;

import li.monoid.j8080.memory.Cast;

public interface DataRW extends DataReader {
    void writeByte(int address, byte data);

    default void writeByte(short address, byte data) {
        writeByte(Short.toUnsignedInt(address), data);
    }

    default void writeShort(int address, short data) {
        writeByte(address + 1, Cast.toByte(data >> 8));
        writeByte(address, Cast.toByte(data));
    }

    default void writeShort(short address, short data) {
        writeShort(Short.toUnsignedInt(address), data);
    }
}
