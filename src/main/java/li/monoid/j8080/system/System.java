package li.monoid.j8080.system;

import li.monoid.j8080.bus.Bus;
import li.monoid.j8080.cpu.Cpu;
import li.monoid.j8080.cpu.instrset.InstrSet;
import li.monoid.j8080.device.ConstantInput;
import li.monoid.j8080.device.DebugOutput;
import li.monoid.j8080.device.WatchDog;
import li.monoid.j8080.memory.Memory;

public class System {
    long MICROS_PER_CYCLE = 0L;
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

        //cpu.addDebugPoint((short) 0x1a65);
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
        for (int i = 0; i < 80_000; ++i) {
            //java.lang.System.out.print(system);
            var cycles = step();
            try {
                Thread.sleep((cycles * MICROS_PER_CYCLE) / 1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public String toString() {
        return "CPU:\n" + cpu + '\n';
    }
}
