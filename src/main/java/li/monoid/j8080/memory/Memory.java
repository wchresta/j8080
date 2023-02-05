package li.monoid.j8080.memory;

import li.monoid.j8080.system.MemoryRW;

import java.util.Arrays;

/**
 * Memory provides memory and some functions that reflect hardware circuitry like address space mirroring.
 */
public class Memory implements MemoryRW {
    public int memSize;  // The actual memory capacity, regardless of address space mapping.
    public int romPos;
    public int romSize;
    private final byte[] buffer;

    public Memory(int memSize) {
        this.memSize = memSize;
        this.buffer = new byte[memSize];
    }

    public void loadRom(byte[] rom) {
        loadRom(rom, 0);
    }
    public void loadRom(byte[] rom, int romPos) {
        romSize = rom.length;
        this.romPos = romPos;
        java.lang.System.arraycopy(rom, 0, buffer, romPos, romSize);
    }

    public void clear() {
        Arrays.fill(buffer, (byte) 0);
    }

    public void setRomSize(int romSize) {
        this.romSize = romSize;
    }

    public void writeByte(int address, byte data) {
        if (!(romSize+romPos <= address && address < memSize)) {
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
        if (address < memSize) {
            return buffer[address];
        }
        return 0;
    }

    /* Bulk read */
    public byte[] readBytes(int address, int length) {
        var out = new byte[length];
        System.arraycopy(buffer, address, out, 0, length);
        return out;
    }
}
