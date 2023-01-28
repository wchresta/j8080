package li.monoid.j8080.disassembler;

import org.junit.Assert;
import org.junit.Test;

import java.util.HexFormat;

public class ReaderTest {
    @Test
    public void allBytesAreValidOpCodes() {
        byte[] buf = {0,0,0};
        var reader = new Reader(buf);
        for (int i = 0; i <= 0xff; ++i) {
            switch (i) {
                // Ignore invalid opCodes
                case 0x08, 0x10, 0x18, 0x20, 0x28, 0x30, 0x38, 0xcb, 0xd9, 0xdd, 0xed, 0xfd:
                    continue;
            }
            buf[0] = (byte) i;
            reader.seek(0);
            var mnemonic = "";
            try {
                mnemonic = reader.readNext();
            } catch (Exception e) {
                Assert.fail("readNext() got unexpected error: " + e);
            }
            Assert.assertTrue(mnemonic.startsWith("0000 "));
        }
    }

    @Test
    public void goldenDisassemble() {
        byte[] buf = HexFormat.of().parseHex("00c3d4180000f5c5d5e5c38c00");
        var want = ""
                .concat("0000 NOP \n")
                .concat("0001 JMP  18d4\n")
                .concat("0004 NOP \n")
                .concat("0005 NOP \n")
                .concat("0006 PUSH SP\n")
                .concat("0007 PUSH BC\n")
                .concat("0008 PUSH DE\n")
                .concat("0009 PUSH HL\n")
                .concat("000a JMP  008c\n");

        var reader = new Reader(buf);
        var got = "";
        try {
            got = reader.readAll();
        } catch (Reader.Error e) {
            Assert.fail("readAll() got unexpected error: " + e);
        }

        Assert.assertEquals(want, got);
    }
}
