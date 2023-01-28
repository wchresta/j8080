package li.monoid.j8080.cpu.opcodes.kinds;

import li.monoid.j8080.cpu.opcodes.OpType;
import li.monoid.j8080.cpu.registers.Register;

public class URegKind extends RegKind {
    public URegKind(int opCode, OpType opType, int baseCycles, int mFetchCycles, int size) {
        super(opCode, ~Register.UPPER_REG_MASK, opType, baseCycles, mFetchCycles, size);
    }

    public URegKind(int opCode, OpType opType, int baseCycles, int mFetchCycles) {
        this(opCode, opType, baseCycles, mFetchCycles, 1);
    }


    String registerName(byte opCode) {
        return Register.fromUpperOpCode(opCode).name();
    }

    @Override
    public String fullMnemonic(byte opCode) {
        var regName = registerName(opCode);
        return opType + " " + regName;
    }

    @Override
    boolean doesFetchM(byte opCode) {
        return (opCode & Register.UPPER_REG_MASK) == UPPER_M;
    }
}