package li.monoid.j8080.system;

import li.monoid.j8080.bus.Bus;
import li.monoid.j8080.cpu.Cpu;
import li.monoid.j8080.cpu.instrset.InstrSet;
import li.monoid.j8080.devices.ConstantInput;
import li.monoid.j8080.devices.DebugOutput;
import li.monoid.j8080.devices.ShiftDevice;
import li.monoid.j8080.devices.WatchDog;
import li.monoid.j8080.memory.Memory;
import li.monoid.j8080.screen.Screen;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class System implements Runnable {
    private final Memory memory = new Memory(0x4000);
    private final Bus bus = new Bus(memory);

    private final Screen screen = new Screen(memory, bus);

    private final Cpu cpu;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> cpuTask;

    public System(InstrSet instrSet) {
        cpu = new Cpu(instrSet, bus);
        bus.setInterruptHandler(cpu);

        bus.registerInputDevice((byte) 0x00, new ConstantInput((byte) 0b00001110)); // Human user interface (ignored)
        bus.registerInputDevice((byte) 0x01, new ConstantInput((byte) 0b00001000)); // Human user interface 2
        bus.registerInputDevice((byte) 0x02, new ConstantInput((byte) 0b01110000)); // Human user interface 3

        bus.registerOutputDevice((byte) 0x03, new DebugOutput("Sound port 3"));
        bus.registerOutputDevice((byte) 0x05, new DebugOutput("Sound port 5"));
        bus.registerOutputDevice((byte) 0x06, new WatchDog());

        var shiftDevice = new ShiftDevice();
        bus.registerInputDevice(ShiftDevice.SHIFT_DEVICE_OUTPUT, shiftDevice);
        bus.registerOutputDevice(ShiftDevice.SHIFT_DEVICE_OFFSET, shiftDevice);
        bus.registerOutputDevice(ShiftDevice.SHIFT_DEVICE_DATA, shiftDevice);
    }

    public void loadRom(byte[] rom) {
        memory.loadRom(rom);
    }

    public void run() {
        screen.turnOn();

        var wantHz = 1_996_800L;
        var nanosInSec = 1_000_000_000L;
        var nanoPeriod = Math.floorDiv(cpu.CYCLES_PER_TICK * nanosInSec,  wantHz);
        cpuTask = scheduler.scheduleAtFixedRate(cpu, 0, nanoPeriod, TimeUnit.NANOSECONDS);
    }

    public void turnOff() {
        screen.turnOff();
        cpuTask.cancel(false);
    }

    public String toString() {
        return "CPU:\n" + cpu + '\n';
    }
}
