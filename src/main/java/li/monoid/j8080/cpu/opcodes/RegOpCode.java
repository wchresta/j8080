package li.monoid.j8080.cpu.opcodes;

/** An OpCode that takes a register as an argumetn */
abstract class RegOpCode extends BaseOpCode {
    final int mFetchCycles;

    public RegOpCode(int opCode, int opMask, String mnemonic, int baseCycles, int mFetchCycles, int size) {
        super(opCode, opMask, mnemonic, baseCycles, size);
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
