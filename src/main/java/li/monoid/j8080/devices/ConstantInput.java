package li.monoid.j8080.devices;

/**
 * A constant input device always outputs a constant value
 */
public class ConstantInput implements InputDevice {
    private final byte constValue;

    public ConstantInput(byte constValue) {
        this.constValue = constValue;
    }

    @Override
    public byte sendData(byte deviceNo) {
        return constValue;
    }
}
