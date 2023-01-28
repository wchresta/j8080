package li.monoid.j8080.cpu.opcodes.kinds;

import li.monoid.j8080.cpu.opcodes.OpType;

/**
 * An OpCode that takes a register as an argumetn
 */
abstract class RegKind extends BaseKind {
    final int mFetchCycles;

    public RegKind(int opCode, int opMask, OpType opType, int baseCycles, int mFetchCycles, int size) {
        super(opCode, opMask, opType, baseCycles, size);
        this.mFetchCycles = mFetchCycles;
    }

    abstract boolean doesFetchM(byte opCode);

    public int getCycles(byte opCode) {
        // Fetching memory for reg operations costs 3 cycles
        if (doesFetchM(opCode)) {
            return this.baseCycles + this.mFetchCycles;
        }
        return this.baseCycles;
    }
}
