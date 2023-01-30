package li.monoid.j8080.device;

/**
 * Debug output logs sent data to STDERR
 */
public class DebugOutput implements OutputDevice {
    private final String name;

    public DebugOutput(String name) {
        this.name = name;
    }

    @Override
    public void receiveData(byte data) {
        System.out.printf("DebugOutput %s received %02x%n", name, data);
    }
}
