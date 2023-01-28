package li.monoid.j8080.cpu.opcodes;

public enum Register {
    /* In opCodes, register bit pattern appear in the following form:
    * xxDDDSSS
    * We denote DDD as the upper bit pattern, SSS as the lower bit pattern
    */
    A, B, C, D, E, H, L, M;

    public static final int UPPER_REG_MASK = 0b00111000;
    public static final int LOWER_REG_MASK = 0b00000111;


    public static Register fromLowerOpCode(byte opCode) {
        return fromBitPattern((byte) (opCode & LOWER_REG_MASK));
    }

    public static Register fromUpperOpCode(byte opCode) {
        return fromBitPattern((byte) ((opCode & UPPER_REG_MASK) >> 3));
    }
    private static Register fromBitPattern(byte pattern) {
        return switch (pattern) {
            case 0b000 -> B;
            case 0b001 -> C;
            case 0b010 -> D;
            case 0b011 -> E;
            case 0b100 -> H;
            case 0b101 -> L;
            case 0b110 -> M;
            case 0b111 -> A;
            default -> throw new IllegalStateException("Invalid register pattern: " + pattern);
        };
    }
}

