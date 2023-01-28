package li.monoid.j8080.disassembler;

import li.monoid.j8080.cpu.RegisterPair;

public class RPOpCode extends BaseOpCode {
    final int rpMask;

    public RPOpCode(int opCode, String mnemonic, int size) {
        super(opCode, 0xcf, mnemonic, size);
        this.rpMask = 0x30;
    }

    public RPOpCode(int opCode, String mnemonic) {
        this(opCode, mnemonic, 1);
    }

    @Override
    public String fullMnemonic(byte opCode) {
        var rpName = RegisterPair.fromOpCode(opCode);
        return mnemonic + " " + rpName;
    }
}
