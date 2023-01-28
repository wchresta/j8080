package li.monoid.j8080.cpu.opcodes.kinds;

import li.monoid.j8080.cpu.opcodes.OpType;
import li.monoid.j8080.cpu.registers.Condition;

public class CondKind extends URegKind {
    public CondKind(int opCode, OpType opType, int baseCycles, int size) {
        super(opCode, opType, baseCycles, size);
    }

    public CondKind(int opCode, OpType opType, int baseCycles) {
        this(opCode, opType, baseCycles, 1);
    }

    @Override
    String registerName(byte opCode) {
        return Condition.fromOpCode(opCode).name();
    }
}
