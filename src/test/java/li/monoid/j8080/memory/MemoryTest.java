package li.monoid.j8080.memory;

import org.junit.Assert;
import org.junit.Test;

import java.util.HexFormat;

public class MemoryTest {
    @Test
    public void testMemoryRead() {
        var memory = new Memory(0xff);
        memory.loadRom(HexFormat.of().parseHex("00c3d4180000f5c5d5e5c38c00"));

        Assert.assertEquals("c3", String.format("%02x", memory.readByte(0x01)));
        Assert.assertEquals("d4", String.format("%02x", memory.readByte(0x02)));
        Assert.assertEquals("d4", String.format("%02x", memory.readByte((short) 0x02)));
        Assert.assertEquals("18d4", String.format("%04x", memory.readShort(0x02)));
    }
}
