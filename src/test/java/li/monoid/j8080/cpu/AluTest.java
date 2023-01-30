package li.monoid.j8080.cpu;

import org.junit.Assert;
import org.junit.Test;

public class AluTest {
    @Test
    public void testZSP() {
        var alu = new Alu();

        alu.setZSPFrom(0x00);
        Assert.assertTrue("Z=1", alu.isZ());
        Assert.assertFalse("S=0", alu.isS());
        Assert.assertTrue("P=1", alu.isP());

        alu.setZSPFrom(0x01);
        Assert.assertFalse("Z=0", alu.isZ());
        Assert.assertFalse("S=0", alu.isS());
        Assert.assertFalse("P=0", alu.isP());

        alu.setZSPFrom(0x81);
        Assert.assertFalse("Z=0", alu.isZ());
        Assert.assertTrue("S=1", alu.isS());
        Assert.assertTrue("P=1", alu.isP());
    }

    @Test
    public void testCmp() {
        var alu = new Alu();
        alu.setAcc(0x40);

        alu.cmp(0x40);  // 0x40 - 0x40 = 0x00
        Assert.assertTrue("Z=1", alu.isZ());
        Assert.assertFalse("S=0", alu.isS());
        Assert.assertTrue("P=1", alu.isP());
        Assert.assertFalse("C=0", alu.isCY());
        alu.cmp(0x3f);  // 0x40 - 0x3f = 0x01
        Assert.assertFalse("Z=0", alu.isZ());
        Assert.assertFalse("S=0", alu.isS());
        Assert.assertFalse("P=0", alu.isP());
        Assert.assertFalse("C=0", alu.isCY());
        alu.cmp(0x41);  // 0x40 - 0x41 = 0xff
        Assert.assertFalse("Z=0", alu.isZ());
        Assert.assertTrue("S=1", alu.isS());
        Assert.assertTrue("P=1", alu.isP());
        Assert.assertTrue("C=1", alu.isCY());
    }
}
