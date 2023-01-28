package li.monoid.j8080.disassembler;

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
    public String fullMnemonic(byte opCode) throws InvalidOpCode {
        int rp = (0x30 & opCode) >> 4;
        var rpName = switch (rp) {
            case 0x00 -> "B";
            case 0x01 -> "D";
            case 0x02 -> "H";
            case 0x03 -> "SP";
            default -> throw new InvalidOpCode(opCode);
        };
        return mnemonic + " " + rpName;
    }
}
