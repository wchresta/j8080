package li.monoid.j8080.cpu.opcodes;

public class URegOpCode extends BaseOpCode {
    public URegOpCode(int opCode, String mnemonic, int size) {
        super(opCode, ~Register.UPPER_REG_MASK, mnemonic, size);
    }

    public URegOpCode(int opCode, String mnemonic) {
        this(opCode, mnemonic, 1);
    }


    String registerName(byte opCode) {
        return Register.fromUpperOpCode(opCode).name();
    }

    @Override
    public String fullMnemonic(byte opCode) {
        var regName = registerName(opCode);
        return mnemonic + " " + regName;
    }
}