package li.monoid.j8080.system;

import li.monoid.j8080.bus.Bus;
import li.monoid.j8080.cpu.Cpu;
import li.monoid.j8080.cpu.instrset.InstrSet;
import li.monoid.j8080.device.ConstantInput;
import li.monoid.j8080.device.DebugOutput;
import li.monoid.j8080.device.WatchDog;
import li.monoid.j8080.memory.Memory;
import li.monoid.j8080.screen.Screen;

import java.util.concurrent.*;

public class System implements Runnable {
    private final Memory memory = new Memory();
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

        bus.registerOutputDevice((byte) 0x02, new DebugOutput("Shift amount"));
        bus.registerOutputDevice((byte) 0x03, new DebugOutput("Sound port 3"));
        bus.registerOutputDevice((byte) 0x04, new DebugOutput("Shift data"));
        bus.registerOutputDevice((byte) 0x05, new DebugOutput("Sound port 5"));
        bus.registerOutputDevice((byte) 0x06, new WatchDog());

        cpu.addDebugPoint((short) 0x08);  // Visualize interrupt 1
        cpu.addDebugPoint((short) 0x10);  // Visualize interrupt 2
    }

    public void loadRom(byte[] rom) {
        memory.loadRom(rom);
    }

    public void run() {
        if (cpu.isHalted()) {
            java.lang.System.out.println("CPU is halted... waiting for interrupt.");
            return;
        }

        try {
            cpu.step(); // TODO: Keep track of cycle
        } catch (IllegalStateException e) {
            java.lang.System.err.print(cpu);
            java.lang.System.exit(1);
        }
    }

    public void turnOn() {
        screen.turnOn();

        cpuTask = scheduler.scheduleAtFixedRate(this, 0, 1000000000 / 1996800L, TimeUnit.NANOSECONDS);
    }

    public void turnOff() {
        screen.turnOff();
        cpuTask.cancel(false);
    }

    public String toString() {
        return "CPU:\n" + cpu + '\n';
    }
}
