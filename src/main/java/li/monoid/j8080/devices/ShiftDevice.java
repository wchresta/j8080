package li.monoid.j8080.devices;

import li.monoid.j8080.memory.Cast;

public class ShiftDevice implements InputDevice, OutputDevice {
    private int offset = 0;
    private int register = 0;

    @Override
    public byte sendData(byte deviceNo) {
        return Cast.toByte((register & 0xffff) >> (8 - offset));
    }

    @Override
    public void receiveData(byte deviceNo, byte data) {
        switch (deviceNo) {
            case 0x02 -> register = (((register & 0xff00) >> 8) & 0xff) | (((data & 0xff) << 8) & 0xff00);
            case 0x04 -> offset = data;
            default -> System.err.println("ShftDevice unsupported deviceNo " + deviceNo);
        }
    }
}
