package li.monoid.j8080.disassembler;

import li.monoid.j8080.cpu.Condition;

public class CondOpCode extends URegOpCode {
    public CondOpCode(int opCode, String mnemonic, int size) {
        super(opCode, mnemonic, size);
    }

    public CondOpCode(int opCode, String mnemonic) {
        this(opCode, mnemonic, 1);
    }

    @Override
    String registerName(byte opCode) {
        return Condition.fromOpCode(opCode).name();
    }
}
