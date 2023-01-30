package li.monoid.j8080.device;

/**
 * A watchdog is a component that causes the sysetm to reset, unless it is "kicked" periodically.
 */
public class WatchDog implements OutputDevice {
    @Override
    public void receiveData(byte data) {
        // Kick the dog... we ignore this, so we never reset the system.
    }
}
