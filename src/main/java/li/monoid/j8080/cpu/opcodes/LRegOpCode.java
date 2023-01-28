package li.monoid.j8080.cpu.opcodes;

import li.monoid.j8080.cpu.registers.Register;

public class LRegOpCode extends RegOpCode {
    public LRegOpCode(int opCode, String mnemonic, int baseCycles, int size) {
        super(opCode, ~Register.LOWER_REG_MASK, mnemonic, baseCycles, 3, size);
    }

    public LRegOpCode(int opCode, String mnemonic, int baseCycles) {
        this(opCode, mnemonic, baseCycles, 1);
    }

    @Override
    public String fullMnemonic(byte opCode) {
        var regName = Register.fromLowerOpCode(opCode).name();
        return mnemonic + " " + regName;
    }

    @Override
    boolean doesFetchM(byte opCode) {
        return (opCode & Register.LOWER_REG_MASK) == LOWER_M;
    }
}
