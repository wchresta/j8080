package li.monoid.j8080.devices;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyboardInput implements KeyListener, InputDevice {
    private final short CREDIT = 0x01;
    private final short P2_START = 0x02;
    private final short P1_START = 0x04;
    private final short P1_FIRE = 0x10;
    private final short P1_LEFT = 0x20;
    private final short P1_RIGHT = 0x40;
    private final short P2_FIRE = 0x1000;
    private final short P2_LEFT = 0x2000;
    private final short P2_RIGHT = 0x4000;

    private short state = 0x0008;

    @Override
    public byte sendData(byte deviceNo) {
        return switch (deviceNo) {
            case 0x01 -> (byte) (state & 0xff);
            case 0x02 -> (byte) ((state >> 8) & 0xff);
            default -> throw new IllegalStateException("Unexpected value: " + deviceNo);
        };
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyChar()) {
            case '0' -> state |= CREDIT;
            case 'a' -> state |= P1_LEFT;
            case 'd' -> state |= P1_RIGHT;
            case '.' -> state |= P1_FIRE;
            case 'p' -> state |= P1_START;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyChar()) {
            case '0' -> state &= ~CREDIT;
            case 'a' -> state &= ~P1_LEFT;
            case 'd' -> state &= ~P1_RIGHT;
            case '.' -> state &= ~P1_FIRE;
            case 'p' -> state &= ~P1_START;
        }
    }
}
