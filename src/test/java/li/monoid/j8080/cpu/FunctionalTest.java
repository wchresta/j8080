package li.monoid.j8080.cpu;

import li.monoid.j8080.bus.Bus;
import li.monoid.j8080.cpu.instrset.Intel8080;
import li.monoid.j8080.cpu.registers.Registers;
import li.monoid.j8080.memory.Memory;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FunctionalTest {
    @Test
    public void runFunctionalTest() {
        var exerRom = Paths.get("./resources/8080EXER.COM");
        byte[] rom = {};
        try {
            rom = Files.readAllBytes(exerRom);
        } catch (IOException e) {
            java.lang.System.err.println("Could not read rom " + exerRom);
            java.lang.System.exit(1);
        }

        var mem = new Memory(0x10000);
        var registers = new Registers();
        var alu = new Alu();
        var bus = new Bus(mem);
        var cpu = new Cpu(new Intel8080(), registers, alu, bus);

        mem.loadRom(rom, 0x100); // org 100h
        mem.setRomSize(0);  // Some code is self-modifying, do not protect the rom.
        mem.unsafeWriteByte(0x00, (byte) 0xc3); // JMP
        mem.unsafeWriteByte(0x01, (byte) 0x00); // Beginning of code
        mem.unsafeWriteByte(0x02, (byte) 0x01); // Beginning of code
        mem.unsafeWriteByte(368, (byte) 0x07); // Bugfix?
        //cpu.setDebugInstructions(true);
        /*
        cpu.addDebugPoint((short) 0x0c63);
        cpu.addDebugPoint((short) 0x0c64);
        cpu.addDebugPoint((short) 0x0c65);
        cpu.addDebugPoint((short) 0x0c68);
        cpu.addDebugPoint((short) 0x0c6d);
        */
        while (true) {
            if ((mem.readByte(registers.getPC()) == (byte) 0xcd /*CALL*/)
            && (mem.readShort(registers.getPC()+1) == (short) 0x005)) {
                // This is supposed to be a call to PRINT
                // We intercept this call instead.
                var addr = registers.getDE();
                while (true) {
                    byte chr = mem.readByte(addr);
                    if (chr == '$') {
                        break;
                    }
                    System.out.printf("%c", chr & 0xff);
                    ++addr;
                }
                System.out.println();
                registers.incPC(3);
            }
            try {
                cpu.run();
            } catch (RuntimeException e) {
                System.err.println(e);
                System.err.println(cpu);
                Assert.fail();
            }
        }
    }
}
