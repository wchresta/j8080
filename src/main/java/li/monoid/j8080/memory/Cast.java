package li.monoid.j8080.memory;

public class Cast {
    public static short toShort(int val) {
        return (short) (0xffff & val);
    }

    public static byte toByte(int val) {
        return (byte) (0xff & val);
    }
}
