package li.monoid.j8080.devices;

import org.junit.Assert;
import org.junit.Test;

public class ShiftDeviceTest {

    void assertEquals(String message, byte expected, byte actual) {
        Assert.assertEquals(message, String.format("%02x", expected), String.format("%02x", actual));
    }

    @Test
    public void trivial() {
        var shiftDevice = new ShiftDevice();

        shiftDevice.receiveData(ShiftDevice.SHIFT_DEVICE_DATA, (byte) 0xaa);
        assertEquals("First push", (byte) 0xaa, shiftDevice.sendData((byte) 0x03));
        shiftDevice.receiveData(ShiftDevice.SHIFT_DEVICE_DATA, (byte) 0xff);
        assertEquals("Second push", (byte) 0xff, shiftDevice.sendData((byte) 0x03));
        shiftDevice.receiveData(ShiftDevice.SHIFT_DEVICE_DATA, (byte) 0x12);
        assertEquals("Third push", (byte) 0x12, shiftDevice.sendData((byte) 0x03));
    }


    @Test
    public void shift1() {
        var shiftDevice = new ShiftDevice();

        shiftDevice.receiveData(ShiftDevice.SHIFT_DEVICE_OFFSET, (byte) 0x04);
        shiftDevice.receiveData(ShiftDevice.SHIFT_DEVICE_DATA, (byte) 0xaa);
        assertEquals("First push", (byte) 0xa0, shiftDevice.sendData((byte) 0x03));
        shiftDevice.receiveData(ShiftDevice.SHIFT_DEVICE_DATA, (byte) 0xff);
        assertEquals("Second push", (byte) 0xfa, shiftDevice.sendData((byte) 0x03));
        shiftDevice.receiveData(ShiftDevice.SHIFT_DEVICE_DATA, (byte) 0x12);
        assertEquals("Third push", (byte) 0x2f, shiftDevice.sendData((byte) 0x03));
    }
}
