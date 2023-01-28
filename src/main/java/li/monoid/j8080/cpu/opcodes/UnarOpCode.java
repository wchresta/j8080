package li.monoid.j8080.cpu.opcodes;

public class UnarOpCode extends BaseOpCode {
    public UnarOpCode(int opCode, String mnemonic, int size) {
        super(opCode, 0xff, mnemonic, size);
    }

    public UnarOpCode(int opCode, String mnemonic) {
        this(opCode, mnemonic, 1);
    }
}
