package li.monoid.j8080.registers;

import li.monoid.j8080.cpu.registers.RegisterPair;
import li.monoid.j8080.cpu.registers.Registers;
import li.monoid.j8080.memory.Cast;
import org.junit.Assert;
import org.junit.Test;

public class RegistersTest {
    void assertEquals(String message, byte expected, byte actual) {
        Assert.assertEquals(message, String.format("%02x", expected), String.format("%02x", actual));
    }

    void assertEquals(String message, short expected, short actual) {
        Assert.assertEquals(message, String.format("%04x", expected), String.format("%04x", actual));
    }

    @Test
    public void readAfterWrite() {
        Assert.assertEquals("1234", String.format("%04x", (short) 0x1234));

        var registers = new Registers();

        registers.setSP(0x1122);
        assertEquals("SP", (short) 0x1122, registers.getSP());
        registers.setPC((short) 0x1234);
        assertEquals("PC", (short) 0x1234, registers.getPC());
        registers.setHL((short) 0x4321);
        assertEquals("HL", (short) 0x4321, registers.getHL());
        registers.setBC((short) 0x2143);
        assertEquals("BC", (short) 0x2143, registers.getBC());
        registers.setHL((short) 0x2080);
        assertEquals("HL", (short) 0x2080, registers.getHL());


        registers.setRegPair(RegisterPair.SP, (short) 0x1122);
        assertEquals("SP", (short) 0x1122, registers.getSP());
        assertEquals("SP", (short) 0x1122, registers.getRegPair(RegisterPair.SP));
        registers.setRegPair(RegisterPair.BC, (short) 0x2143);
        assertEquals("BC", (short) 0x2143, registers.getBC());
        assertEquals("BC", (short) 0x2143, registers.getRegPair(RegisterPair.BC));
        registers.setRegPair(RegisterPair.HL, (short) 0x4321);
        assertEquals("HL", (short) 0x4321, registers.getHL());
        assertEquals("HL", (short) 0x4321, registers.getRegPair(RegisterPair.HL));
        registers.setRegPair(RegisterPair.HL, (short) 0xc321);
        assertEquals("HL", (short) 0xc321, registers.getHL());
        assertEquals("HL", (short) 0xc321, registers.getRegPair(RegisterPair.HL));
        registers.setRegPair(RegisterPair.HL, (short) 0x2080);
        assertEquals("HL", (short) 0x2080, registers.getHL());
        assertEquals("HL", (short) 0x2080, registers.getRegPair(RegisterPair.HL));
    }

    @Test
    public void registerPairs() {
        var registers = new Registers();
        registers.setHL((short) 0x2080);
        assertEquals("H", (byte) 0x20, registers.getH());
        assertEquals("L", (byte) 0x80, registers.getL());

        registers.setH((short) 0x20);
        registers.setL((short) 0x80);
        assertEquals("HL", (short) 0x2080, registers.getHL());
    }

    @Test
    public void arithmetic() {
        var registers = new Registers();

        registers.setHL((short) 0x1234);
        registers.setRegPair(RegisterPair.HL, Cast.toShort(registers.getRegPair(RegisterPair.HL) + 1));
        assertEquals("HL", (short) 0x1235, registers.getHL());

        registers.setHL((short) 0x207f);
        registers.setRegPair(RegisterPair.HL, Cast.toShort(Short.toUnsignedInt(registers.getRegPair(RegisterPair.HL)) + 1));
        assertEquals("HL", (short) 0x2080, registers.getHL());
    }
}
