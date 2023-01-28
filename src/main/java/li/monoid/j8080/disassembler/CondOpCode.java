package li.monoid.j8080.disassembler;

public class CondOpCode extends RegOpCode {
    public CondOpCode(int opCode, String mnemonic, int size) {
        super(opCode, mnemonic, 0x38, size);
    }

    public CondOpCode(int opCode, String mnemonic) {
        this(opCode, mnemonic, 1);
    }

    @Override
    String getRegName(byte reg) throws InvalidFlag {
        return switch (reg) {
            case 0b000 -> "NZ";
            case 0b001 -> "Z";
            case 0b010 -> "NC";
            case 0b011 -> "C";
            case 0b100 -> "PO";
            case 0b101 -> "PE";
            case 0b110 -> "P";
            case 0b111 -> "M";
            default -> throw new InvalidFlag(reg);
        };
    }
}
