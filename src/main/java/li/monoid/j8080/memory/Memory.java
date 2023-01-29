package li.monoid.j8080.memory;

import li.monoid.j8080.system.DataRW;

/**
 * Memory provides memory and some functions that reflect hardware circuitry like address space mirroring.
 */
public class Memory implements DataRW {
    public static final int CAPACITY = 0x4000;  // The actual memory capacity, regardless of address space mapping.
    public static final int ROM_CAPCITY = 0x2000;
    private final byte[] buffer = new byte[CAPACITY];

    public void loadRom(byte[] rom) {
        java.lang.System.arraycopy(rom, 0, buffer, 0, rom.length);
    }

    public void writeByte(int address, byte data) {
        if (!(ROM_CAPCITY <= address && address < CAPACITY)) {
            throw new IllegalStateException(String.format("Address %04x is not writable", address));
        }
        buffer[address] = data;
    }

    /**
     * Write a bytes into the memory buffer without checking if the address is writable or in bounds. For debugging
     */
    public void unsafeWriteByte(int address, byte data) {
        buffer[address] = data;
    }

    public byte readByte(int address) {
        if (address < CAPACITY) {
            return buffer[address];
        }
        return 0;
    }

    /* Bulk read, for debugging */
    public byte[] readBytes(int address, int length) {
        var out = new byte[length];
        System.arraycopy(buffer, address, out, 0, length);
        return out;
    }
}
