package li.monoid.j8080.cpu.opcodes;

abstract public class BaseOpCode {
    static final int LOWER_M = 0b00000110;
    static final int UPPER_M = 0b00110000;

    /** The masked opCode that this OpCode matches. */
    final int opCode;
    /** Instruction bytes are masked with opMask before being compared to opCode */
    final int opMask;
    /** Human-readable name of this opCode */
    final String mnemonic;

    /** Number of CPU cycles executing this opCode takes */
    final int baseCycles;

    /** Total byte length of this opCode (including arguments) */
    final int size;

    public BaseOpCode(int opCode, int opMask, String mnemonic, int baseCycles, int size) {
        this.opCode = opCode;
        this.opMask = opMask;
        this.mnemonic = mnemonic;
        this.baseCycles = baseCycles;
        this.size = size;
    }

    abstract public int getCycles(byte opCode);

    public int getOpCode() {
        return opCode;
    }

    public int getOpMask() {
        return opMask;
    }

    public String getMnemonic() {
        return mnemonic;
    }

    public int getSize() {
        return size;
    }

    public boolean match(byte opCode) {
        return (opCode & opMask) == this.opCode;
    }

    public String toString() {
        return mnemonic;
    }

    public String fullMnemonic(byte opCode) {
        return mnemonic;
    }
}