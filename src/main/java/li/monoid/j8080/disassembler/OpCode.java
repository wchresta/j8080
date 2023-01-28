package li.monoid.j8080.disassembler;

public class OpCode extends BaseOpCode {
    public OpCode(int opCode, String mnemonic, int size) {
        super(opCode, 0xff, mnemonic, size);
    }

    public OpCode(int opCode, String mnemonic) {
        this(opCode, mnemonic, 1);
    }
}
