package li.monoid.j8080.disassembler;

import li.monoid.j8080.cpu.Register;

public class RegOpCode extends BaseOpCode {
    final int regMask;

    public RegOpCode(int opCode, String mnemonic, int regMask, int size) {
        super(opCode, 0xff & ~regMask, mnemonic, size);
        this.regMask = regMask;
    }

    public RegOpCode(int opCode, String mnemonic, int regMask) {
        this(opCode, mnemonic, regMask, 1);
    }

    String registerName(byte opCode) {
        if (this.regMask == Register.LOWER_REG_MASK) {
            return Register.fromLowerOpCode(opCode).name();
        }
        return Register.fromUpperOpCode(opCode).name();
    }

    @Override
    public String fullMnemonic(byte opCode) {
        var regName = registerName(opCode);
        return mnemonic + " " + regName;
    }

}
