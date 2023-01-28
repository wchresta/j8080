package li.monoid.j8080.disassembler;

public class RegOpCode extends BaseOpCode {
    final int regMask;

    public RegOpCode(int opCode, String mnemonic, int regMask, int size) {
        super(opCode, 0xff & ~regMask, mnemonic, size);
        this.regMask = regMask;
    }

    public RegOpCode(int opCode, String mnemonic, int regMask) {
        this(opCode, mnemonic, regMask, 1);
    }

    byte getRegValue(byte opCode) {
        int rm = regMask;
        int reg = (regMask & opCode);
        while ((rm & 1) == 0) {
            reg >>= 1;
            rm >>= 1;
        }
        return (byte) reg;
    }
    String getRegName(byte reg) throws InvalidFlag {
        return switch (reg) {
            case 0b111 -> "A";
            case 0b000 -> "B";
            case 0b001 -> "C";
            case 0b010 -> "D";
            case 0b011 -> "E";
            case 0b100 -> "H";
            case 0b101 -> "L";
            case 0b110 -> "M";
            default -> throw new InvalidFlag(reg);
        };
    }

    @Override
    public String fullMnemonic(byte opCode) throws InvalidOpCode {
        var regValue = this.getRegValue(opCode);
        var regName = "";
        try {
            regName = this.getRegName(regValue);
        } catch (InvalidFlag e) {
            throw new InvalidOpCode(opCode, String.format("invalid flag %3b", e.getFlag()));
        }
        return mnemonic + " " + regName;
    }

}
