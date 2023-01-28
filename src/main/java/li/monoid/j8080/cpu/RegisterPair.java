package li.monoid.j8080.cpu;

public enum RegisterPair {
    BC, DE, HL, SP;

    public static RegisterPair fromOpCode(byte opCode) {
        // In OpCodes, the register pair always appears in the following bit pattern:
        // xxRPxxxx
        return switch ((opCode & 0b00110000) >> 4) {
            case 0 -> BC;
            case 1 -> DE;
            case 2 -> HL;
            case 3 -> SP;
            default -> throw new IllegalStateException("Invalid register opCode: " + opCode);
        };
    }
}
