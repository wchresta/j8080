package li.monoid.j8080.cpu.opcodes;

public class LRegOpCode extends BaseOpCode {
    public LRegOpCode(int opCode, String mnemonic, int size) {
        super(opCode, ~Register.LOWER_REG_MASK, mnemonic, size);
    }

    public LRegOpCode(int opCode, String mnemonic) {
        this(opCode, mnemonic, 1);
    }

    @Override
    public String fullMnemonic(byte opCode) {
        var regName = Register.fromLowerOpCode(opCode).name();
        return mnemonic + " " + regName;
    }
}
