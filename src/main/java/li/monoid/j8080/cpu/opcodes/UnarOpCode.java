package li.monoid.j8080.cpu.opcodes;

public class UnarOpCode extends BaseOpCode {
    public UnarOpCode(int opCode, String mnemonic, int baseCycles, int size) {
        super(opCode, 0xff, mnemonic, baseCycles, size);
    }

    public UnarOpCode(int opCode, String mnemonic, int baseCycles) {
        this(opCode, mnemonic, baseCycles, 1);
    }

    @Override
    public int getCycles(byte opCode) {
        return baseCycles;
    }
}
