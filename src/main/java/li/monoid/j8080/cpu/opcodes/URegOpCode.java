package li.monoid.j8080.cpu.opcodes;

import li.monoid.j8080.cpu.registers.Register;

public class URegOpCode extends RegOpCode {
    public URegOpCode(int opCode, String mnemonic, int baseCycles, int mFetchCycles, int size) {
        super(opCode, ~Register.UPPER_REG_MASK, mnemonic, baseCycles, mFetchCycles, size);
    }

    public URegOpCode(int opCode, String mnemonic, int baseCycles, int mFetchCycles) {
        this(opCode, mnemonic, baseCycles, mFetchCycles, 1);
    }


    String registerName(byte opCode) {
        return Register.fromUpperOpCode(opCode).name();
    }

    @Override
    public String fullMnemonic(byte opCode) {
        var regName = registerName(opCode);
        return mnemonic + " " + regName;
    }

    @Override
    boolean doesFetchM(byte opCode) {
        return (opCode & Register.UPPER_REG_MASK) == UPPER_M;
    }
}