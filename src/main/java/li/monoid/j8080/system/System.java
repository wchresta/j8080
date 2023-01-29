package li.monoid.j8080.system;

import li.monoid.j8080.bus.Bus;
import li.monoid.j8080.cpu.Cpu;
import li.monoid.j8080.memory.Memory;

public class System {
    private final Memory memory = new Memory();
    private final Bus bus = new Bus(memory);
    private final Cpu cpu = new Cpu(bus);

    public System() {
    }

    public void loadRom(byte[] rom) {
        memory.loadRom(rom);
    }

    public void step() {
        try {
            cpu.step(); // TODO: Keep track of cycle
        } catch (IllegalStateException e) {
            java.lang.System.err.print(cpu);
            throw e;
        }
    }

    public String toString() {
        return "CPU:\n" + cpu + '\n';
    }
}
