package li.monoid.j8080.cpu;

/*
 * A Condition is a bit pattern within an opCode that represents a condition.
 * Examples are NZ (not zero) and C (carry).
 * The condition appears in a specific part of the opCode:
 * 11CCCxxx (note, the condition is always prefixed with 11 in the 8080 instruction set).
 */
public enum Condition {
    NZ, Z, NC, C, PO, PE, P, M;

    public static Condition fromOpCode(byte opCode) {
        assert (0xc0 & opCode) == 0xc0;  // Ensure opCode is prefixed with 11.
        return switch ((0b00111000 & opCode) >> 3) {
            case 0b000 -> NZ;
            case 0b001 -> Z;
            case 0b010 -> NC;
            case 0b011 -> C;
            case 0b100 -> PO;
            case 0b101 -> PE;
            case 0b110 -> P;
            case 0b111 -> M;
            default -> throw new IllegalStateException("Unexpected condition opCode: " + opCode);
        };
    }
}
