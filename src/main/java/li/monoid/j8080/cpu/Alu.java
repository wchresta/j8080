package li.monoid.j8080.cpu;

import li.monoid.j8080.memory.Cast;

public class Alu {
    private static final int VALUE_MASK = 0xff;
    private static final int SIGN_MASK = 0x80;
    private static final int CARRY_MASK = 0x100;
    private static final int VALUE_CARRY_MASK = VALUE_MASK | CARRY_MASK;
    private int acc = 0;  // Size 8

    private int z = 0, s = 0, p = 0, cy = 0;
    // TODO: Add support for auxiliary carry

    public Alu() {
    }

    public boolean isZ() {
        return z > 0;
    }

    public boolean isS() {
        return s > 0;
    }

    public boolean isP() {
        return p > 0;
    }

    public boolean isCY() {
        return cy > 0;
    }

    public byte getAcc() {
        return Cast.toByte(acc);
    }


    public void setAcc(int acc) {
        this.acc = 0xff & acc;
    }

    public void setAcc(byte acc) {
        this.acc = acc;
    }

    private void setZeroFrom(int val) {
        z = (val & VALUE_MASK) == 0 ? 1 : 0;
    }

    private void setSignFrom(int val) {
        s = (val & SIGN_MASK) >> 7;
    }

    private void setParityFrom(int val) {
        p = 1;  // Parity even --> p=1
        if ((val & 0x1) > 0) ++p;
        if ((val & 0x2) > 0) ++p;
        if ((val & 0x4) > 0) ++p;
        if ((val & 0x8) > 0) ++p;
        if ((val & 0x10) > 0) ++p;
        if ((val & 0x20) > 0) ++p;
        if ((val & 0x40) > 0) ++p;
        if ((val & 0x80) > 0) ++p;
        p &= 1;
    }

    public void setCarryFrom(int val) {
        cy = (val & CARRY_MASK) >> 8;
    }

    private void setFlagsFrom(int val) {
        setZSPFrom(val);
        setCarryFrom(val);
    }

    public void setZSPFrom(int val) {
        setZeroFrom(val);
        setSignFrom(val);
        setParityFrom(val);
    }

    /**
     * getCarry returns 1 if CY is set, 0 otherwise
     */
    public int getCarry() {
        return cy;
    }

    public void setCarry(int val) {
        cy = val > 0 ? 1 : 0;
    }

    public byte getFlagByte() {
        var flags = 0b00000010;  // Bit 1 is always 1
        flags |= cy;
        flags |= p << 2;
        flags |= z << 6;
        flags |= s << 7;
        return Cast.toByte(flags);
    }

    public void setFlagsFromByte(byte flags) {
        int iFlags = Byte.toUnsignedInt(flags);
        cy = iFlags & 1;
        p = (iFlags >> 2) & 1;
        z = (iFlags >> 6) & 1;
        s = (iFlags >> 7) & 1;
    }

    /**
     * Acc = Acc + val
     */
    public void add(int val) {
        acc = (acc + val & VALUE_MASK) & VALUE_CARRY_MASK;
        setFlagsFrom(acc);
    }

    /**
     * Acc = Acc + val + CY
     */
    public void addC(int val) {
        acc += (val & VALUE_MASK) + getCarry();
        setFlagsFrom(acc);
    }

    /**
     * Acc = Acc - val
     */
    public void sub(int val) {
        acc = -val & VALUE_MASK;
        setFlagsFrom(acc);
    }

    /**
     * Acc = Acc - val - CY
     */
    public void subC(int val) {
        acc -= (val & VALUE_MASK) + getCarry();
        setFlagsFrom(acc);
    }

    public void daa() {
        if ((0x0f & acc) > 9) { // TODO: or AC flag is set
            add(6);
        }
        if ((0xf0 & acc) > 0x90 | cy > 0) {
            add(6 << 4);
        }
    }

    public void and(int val) {
        // Carry flag is always cleared by this
        setAcc(acc & val);
        setFlagsFrom(acc);
    }

    public void or(int val) {
        // Carry flag is always cleared by this
        setAcc(acc | val);
        setFlagsFrom(acc);
    }

    public void xor(int val) {
        // Carry flag is always cleared by this
        setAcc(acc ^ val);
        setFlagsFrom(acc);
    }

    public void not() {
        // Carry flag is always cleared by this
        setAcc(~acc);
        setFlagsFrom(acc);
    }

    public void cmp(int val) {
        // This is like sub, but the accumulator remains unchanged.
        setFlagsFrom((0xff & acc) - (val & 0xff));
    }

    public void rotateLeft() {
        acc <<= 1;
        setCarryFrom(acc);
        acc |= cy;
    }

    public void rotateLeftC() {
        acc <<= 1;
        acc |= cy;
        setCarryFrom(acc);
    }

    public void rotateRight() {
        cy = acc & 1;
        acc = (acc >> 1) | (cy << 7);
    }

    public void rotateRightC() {
        var newCy = acc & 1;
        acc = (acc >> 1) | (cy << 7);
        cy = newCy;
    }
}
