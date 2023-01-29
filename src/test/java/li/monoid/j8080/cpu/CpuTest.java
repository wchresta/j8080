package li.monoid.j8080.cpu;

import li.monoid.j8080.bus.Bus;
import li.monoid.j8080.cpu.registers.Registers;
import li.monoid.j8080.memory.Memory;
import org.junit.Assert;
import org.junit.Test;

import java.util.HexFormat;

public class CpuTest {
    @Test
    public void testOpCodes() {
        byte opCode = (byte) 0xc2;
        Assert.assertEquals("J NZ", Cpu.getOpCode(opCode).kind.fullMnemonic(opCode));
    }

    @Test
    public void testINX() {
        Memory mem = new Memory();
        Bus bus = new Bus(mem);
        Registers registers = new Registers();
        Alu alu = new Alu();
        Cpu cpu = new Cpu(registers, alu, bus);

        registers.setHL((short) 0x207f);
        registers.setPC((short) 0x0000);
        mem.loadRom(HexFormat.of().parseHex("23"));

        Assert.assertEquals("207f", String.format("%04x", registers.getHL()));
        cpu.step();
        Assert.assertEquals("2080", String.format("%04x", registers.getHL()));
    }

    @Test
    public void callRet() {
        Memory mem = new Memory();
        Bus bus = new Bus(mem);
        Registers registers = new Registers();
        Alu alu = new Alu();
        Cpu cpu = new Cpu(registers, alu, bus);

        mem.unsafeWriteByte(0x18d2, (byte) 0xcd); // CALL
        mem.unsafeWriteByte(0x18d3, (byte) 0x3a); // CALL argL
        mem.unsafeWriteByte(0x18d4, (byte) 0x1a); // CALL argH
        mem.unsafeWriteByte(0x1a3a, (byte) 0xc9); // RET
        registers.setSP((short) 0x2400);
        registers.setPC((short) 0x18d2);
        // Stack addresses  1 2 3 4 5 6 7 8
        // Stack before:    0 0 0 0 0>1 2 3
        // Stack after:     0 0 0>3 0 1 2 3
        Assert.assertEquals("SP", 0x2400, 0xffff & registers.getSP());
        Assert.assertEquals("PC", 0x18d2, 0xffff & registers.getPC());
        Assert.assertArrayEquals("Stack", new byte[]{0x00, 0x00, 0x00, 0x00}, mem.readBytes(0x23fc, 4));
        cpu.step(); // Call, before pushing PC to the stack, PC is 0x18d5 (position after the arguments).
        Assert.assertEquals("SP", 0x23fe, 0xffff & registers.getSP());
        Assert.assertEquals("PC", 0x1a3a, 0xffff & registers.getPC());
        Assert.assertArrayEquals("Stack", new byte[]{0x00, 0x00, (byte) 0xd5, 0x18}, mem.readBytes(0x23fc, 4));
        cpu.step(); // Ret
        Assert.assertEquals("SP", 0x2400, 0xffff & registers.getSP());
        Assert.assertEquals("PC", 0x18d5, 0xffff & registers.getPC());
        Assert.assertArrayEquals("Stack", new byte[]{0x00, 0x00, (byte) 0xd5, 0x18}, mem.readBytes(0x23fc, 4));
    }
}
