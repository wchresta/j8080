package li.monoid.j8080.system;

import li.monoid.j8080.bus.Bus;
import li.monoid.j8080.cpu.Cpu;
import li.monoid.j8080.cpu.instrset.InstrSet;
import li.monoid.j8080.device.ConstantInput;
import li.monoid.j8080.device.DebugOutput;
import li.monoid.j8080.device.WatchDog;
import li.monoid.j8080.memory.Memory;

public class System {
    int NANOS_PER_CYCLE = 2;
    private final Memory memory = new Memory();
    private final Bus bus = new Bus(memory);
    private final Cpu cpu;

    public System(InstrSet instrSet) {
        cpu = new Cpu(instrSet, bus);

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

    public int step() {
        try {
            return cpu.step(); // TODO: Keep track of cycle
        } catch (IllegalStateException e) {
            java.lang.System.err.print(cpu);
            throw e;
        }
    }

    public void run() {
        long timeStart = java.lang.System.currentTimeMillis();
        long lastNano = java.lang.System.nanoTime();
        for (int i = 0; i < 10_000_000; ++i) {
            if (cpu.isHalted()) {
                java.lang.System.out.println("CPU is halted... waiting for interrupt.");
                break;
            }
            //java.lang.System.out.print(system);
            int nanosToSleep = step() * NANOS_PER_CYCLE;
            var currNano = java.lang.System.nanoTime();
            nanosToSleep -= currNano - lastNano;
            lastNano = currNano;

            // For testing: Send interrupt every 0.2 seconds
            var currTime = java.lang.System.currentTimeMillis();
            if (currTime > timeStart + 200L) {
                cpu.interrupt(0x02);
                timeStart = currTime;
            }

            if (nanosToSleep > 0) {
                try {
                    Thread.sleep(0, nanosToSleep);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public String toString() {
        return "CPU:\n" + cpu + '\n';
    }
}
