package li.monoid.j8080.cpu;

import li.monoid.j8080.bus.Bus;
import li.monoid.j8080.cpu.instrset.Intel8080;
import li.monoid.j8080.cpu.registers.Registers;
import li.monoid.j8080.memory.Memory;
import org.junit.Assert;
import org.junit.Test;

import java.util.HexFormat;

public class CpuTest {
    void assertEquals(String message, byte expected, byte actual) {
        Assert.assertEquals(message, String.format("%02x", expected), String.format("%02x", actual));
    }

    @Test
    public void testOpCodes() {
        byte opCode = (byte) 0xc2;
        Assert.assertEquals("J NZ", new Intel8080().getOpCode(opCode).kind.fullMnemonic(opCode));
    }

    @Test
    public void testINX() {
        Memory mem = new Memory(0xff);
        Bus bus = new Bus(mem);
        Registers registers = new Registers();
        Alu alu = new Alu();
        Cpu cpu = new Cpu(new Intel8080(), registers, alu, bus);

        registers.setHL((short) 0x207f);
        registers.setPC((short) 0x0000);
        mem.loadRom(HexFormat.of().parseHex("23"));

        Assert.assertEquals("207f", String.format("%04x", registers.getHL()));
        cpu.processInstruction();
        Assert.assertEquals("2080", String.format("%04x", registers.getHL()));
    }

    @Test
    public void decr() {
        Memory mem = new Memory(0xff);
        Bus bus = new Bus(mem);
        Registers registers = new Registers();
        Alu alu = new Alu();
        Cpu cpu = new Cpu(new Intel8080(), registers, alu, bus);

        registers.setBC((short) 0x1400);
        mem.loadRom(HexFormat.of().parseHex("05"));  // DCR B

        assertEquals("B before", (byte) 0x14, registers.getB());
        cpu.processInstruction();
        assertEquals("B after", (byte) 0x13, registers.getB());
    }

    @Test
    public void cpi() {
        Memory mem = new Memory(0xff);
        Bus bus = new Bus(mem);
        Registers registers = new Registers();
        Alu alu = new Alu();
        Cpu cpu = new Cpu(new Intel8080(), registers, alu, bus);

        mem.loadRom(HexFormat.of().parseHex("FE01"));  // CPI 01

        alu.setAcc(0x02);
        cpu.processInstruction();
        Assert.assertFalse(alu.isZ());

        registers.setPC(0);
        alu.setAcc(0x01);
        cpu.processInstruction();
        Assert.assertTrue(alu.isZ());
    }

    @Test
    public void shifting() {
        Memory mem = new Memory(0xff);
        Bus bus = new Bus(mem);
        Registers registers = new Registers();
        Alu alu = new Alu();
        Cpu cpu = new Cpu(new Intel8080(), registers, alu, bus);

        mem.loadRom(HexFormat.of().parseHex("070f171f"));  // RLC RRC RAL RAR
        alu.setAcc(0x80);
        cpu.processInstruction();
        assertEquals("A rot left", (byte) 0x01, alu.getAcc());
        Assert.assertTrue(alu.isCY());

        alu.setAcc(0x31);
        alu.setCarry(0);
        cpu.processInstruction();
        assertEquals("A rot right", (byte) 0x98, alu.getAcc());
        Assert.assertTrue(alu.isCY());

        alu.setAcc(0x80);
        alu.setCarry(0);
        cpu.processInstruction();
        assertEquals("A rot left through carry", (byte) 0x00, alu.getAcc());
        Assert.assertTrue(alu.isCY());
        cpu.processInstruction();
        assertEquals("A rot right through carry", (byte) 0x80, alu.getAcc());
        Assert.assertFalse(alu.isCY());
    }

    @Test
    public void callRet() {
        Memory mem = new Memory(0xff);
        Bus bus = new Bus(mem);
        Registers registers = new Registers();
        Alu alu = new Alu();
        Cpu cpu = new Cpu(new Intel8080(), registers, alu, bus);

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
        cpu.processInstruction(); // Call, before pushing PC to the stack, PC is 0x18d5 (position after the arguments).
        Assert.assertEquals("SP", 0x23fe, 0xffff & registers.getSP());
        Assert.assertEquals("PC", 0x1a3a, 0xffff & registers.getPC());
        Assert.assertArrayEquals("Stack", new byte[]{0x00, 0x00, (byte) 0xd5, 0x18}, mem.readBytes(0x23fc, 4));
        cpu.processInstruction(); // Ret
        Assert.assertEquals("SP", 0x2400, 0xffff & registers.getSP());
        Assert.assertEquals("PC", 0x18d5, 0xffff & registers.getPC());
        Assert.assertArrayEquals("Stack", new byte[]{0x00, 0x00, (byte) 0xd5, 0x18}, mem.readBytes(0x23fc, 4));
    }
}
