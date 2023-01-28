package li.monoid.j8080.cpu.opcodes.kinds;

import li.monoid.j8080.cpu.opcodes.OpType;

public class UnarKind extends BaseKind {
    public UnarKind(int opCode, OpType opType, int baseCycles, int size) {
        super(opCode, 0xff, opType, baseCycles, size);
    }

    public UnarKind(int opCode, OpType opType, int baseCycles) {
        this(opCode, opType, baseCycles, 1);
    }

    @Override
    public int getCycles(byte opCode) {
        return baseCycles;
    }
}
