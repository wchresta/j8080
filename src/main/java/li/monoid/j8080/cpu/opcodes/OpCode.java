package li.monoid.j8080.cpu.opcodes;

import li.monoid.j8080.cpu.opcodes.kinds.BaseKind;

public class OpCode {
    public final byte opCode;
    public final OpType opType;
    public final BaseKind kind;
    public final String mnemonic;

    public OpCode(byte opCode, OpType opType, BaseKind kind, String mnemonic) {
        this.opCode = opCode;
        this.opType = opType;
        this.kind = kind;
        this.mnemonic = mnemonic;
    }
}
