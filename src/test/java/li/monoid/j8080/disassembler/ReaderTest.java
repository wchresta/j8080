package li.monoid.j8080.disassembler;

import org.junit.Assert;
import org.junit.Test;

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
}
