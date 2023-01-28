package li.monoid.j8080.cpu.opcodes;

public class RegPOpCode extends BaseOpCode {
    final int rpMask;

    public RegPOpCode(int opCode, String mnemonic, int baseCycles, int size) {
        super(opCode, 0xcf, mnemonic, baseCycles, size);
        this.rpMask = 0x30;
    }

    public RegPOpCode(int opCode, String mnemonic, int baseCycles) {
        this(opCode, mnemonic, baseCycles, 1);
    }

    @Override
    public String fullMnemonic(byte opCode) {
        var rpName = RegisterPair.fromOpCode(opCode);
        return mnemonic + " " + rpName;
    }

    @Override
    public int getCycles(byte opCode) {
        return baseCycles;
    }
}
