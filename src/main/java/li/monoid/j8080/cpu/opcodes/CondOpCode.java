package li.monoid.j8080.cpu.opcodes;

import li.monoid.j8080.cpu.registers.Condition;

public class CondOpCode extends URegOpCode {
    public CondOpCode(int opCode, String mnemonic, int baseCycles, int size) {
        super(opCode, mnemonic, baseCycles, size);
    }

    public CondOpCode(int opCode, String mnemonic, int baseCycles) {
        this(opCode, mnemonic, baseCycles, 1);
    }

    @Override
    String registerName(byte opCode) {
        return Condition.fromOpCode(opCode).name();
    }
}
