package li.monoid.j8080.cpu.opcodes.kinds;

import li.monoid.j8080.cpu.opcodes.OpType;
import li.monoid.j8080.cpu.registers.RegisterPair;

public class RegPKind extends BaseKind {
    final int rpMask;

    public RegPKind(int opCode, OpType opType, int baseCycles, int size) {
        super(opCode, 0xcf, opType, baseCycles, size);
        this.rpMask = 0x30;
    }

    public RegPKind(int opCode, OpType opType, int baseCycles) {
        this(opCode, opType, baseCycles, 1);
    }

    @Override
    public String fullMnemonic(byte opCode) {
        var rpName = RegisterPair.fromOpCode(opCode);
        return opType + " " + rpName;
    }

    @Override
    public int getCycles(byte opCode) {
        return baseCycles;
    }
}
