package li.monoid.j8080.devices;

import li.monoid.j8080.memory.Cast;

public class ShiftDevice implements InputDevice, OutputDevice {
    public static final byte SHIFT_DEVICE_OFFSET = 0x02;
    public static final byte SHIFT_DEVICE_OUTPUT = 0x03;
    public static final byte SHIFT_DEVICE_DATA = 0x04;
    private int offset = 0;
    private short register = 0;

    @Override
    public byte sendData(byte deviceNo) {
        return Cast.toByte(register >> (8 - offset));
    }

    @Override
    public void receiveData(byte deviceNo, byte data) {
        switch (deviceNo) {
            case 0x02 -> offset = data & 0x7;
            case 0x04 -> {
                register >>= 8;
                register &= 0xff;
                register |= (data << 8) & 0xff00;
            }
            default -> System.err.println("ShftDevice unsupported deviceNo " + deviceNo);
        }
    }
}
