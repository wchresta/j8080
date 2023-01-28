package li.monoid.j8080.cpu.opcodes.kinds;

import li.monoid.j8080.cpu.opcodes.OpType;
import li.monoid.j8080.cpu.registers.Register;

public class LRegKind extends RegKind {
    public LRegKind(int opCode, OpType opType, int baseCycles, int size) {
        super(opCode, ~Register.LOWER_REG_MASK, opType, baseCycles, 3, size);
    }

    public LRegKind(int opCode, OpType opType, int baseCycles) {
        this(opCode, opType, baseCycles, 1);
    }

    @Override
    public String fullMnemonic(byte opCode) {
        var regName = Register.fromLowerOpCode(opCode).name();
        return opType + " " + regName;
    }

    @Override
    boolean doesFetchM(byte opCode) {
        return (opCode & Register.LOWER_REG_MASK) == LOWER_M;
    }
}
