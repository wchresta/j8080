package li.monoid.j8080.cpu.opcodes.kinds;

import li.monoid.j8080.cpu.opcodes.OpType;
import li.monoid.j8080.memory.Cast;

abstract public class BaseKind {
    static final int LOWER_M = 0b00000110;
    static final int UPPER_M = 0b00110000;

    /**
     * The masked opCode that this OpCode matches.
     */
    final byte opCode;
    /**
     * Instruction bytes are masked with opMask before being compared to opCode
     */
    final byte opMask;
    /**
     * Human-readable name of this opCode
     */
    final OpType opType;

    /**
     * Number of CPU cycles executing this opCode takes
     */
    final int baseCycles;

    /**
     * Total byte length of this opCode (including arguments)
     */
    final int size;

    public BaseKind(byte opCode, byte opMask, OpType opType, int baseCycles, int size) {
        this.opCode = opCode;
        this.opMask = opMask;
        this.opType = opType;
        this.baseCycles = baseCycles;
        this.size = size;
    }

    public BaseKind(int opCode, int opMask, OpType opType, int baseCycles, int size) {
        this(Cast.toByte(opCode), Cast.toByte(opMask), opType, baseCycles, size);
    }

    abstract public int getCycles(byte opCode);

    public byte getOpCode() {
        return opCode;
    }

    public byte getOpMask() {
        return opMask;
    }

    public OpType getOpType() {
        return opType;
    }

    public int getSize() {
        return size;
    }

    public boolean match(byte opCode) {
        return (opCode & opMask) == this.opCode;
    }

    public String toString() {
        return opType.name();
    }

    public String fullMnemonic(byte opCode) {
        return String.format("%-4s", opType.name());
    }
}