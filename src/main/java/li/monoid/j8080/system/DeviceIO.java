package li.monoid.j8080.system;

public interface DeviceIO {
    void writeToDevice(byte deviceNo, byte data);

    byte readFromDevice(byte deviceNo);
}
