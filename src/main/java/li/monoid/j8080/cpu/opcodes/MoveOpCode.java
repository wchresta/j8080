package li.monoid.j8080.cpu.opcodes;

import li.monoid.j8080.cpu.registers.Register;

public class MoveOpCode extends RegOpCode {
    public MoveOpCode(int opCode, String mnemonic, int baseCycles) {
        super(opCode, 0xc0, mnemonic, baseCycles, 2, 1);
    }

    public boolean doesFetchM(byte opCode) {
        return (opCode & Register.UPPER_REG_MASK) == UPPER_M || (opCode & Register.LOWER_REG_MASK) == LOWER_M;
    }
}
