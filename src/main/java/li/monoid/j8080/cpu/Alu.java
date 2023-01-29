package li.monoid.j8080.cpu;

import li.monoid.j8080.memory.Cast;

public class Alu {
    private static final int VALUE_MASK = 0xff;
    private static final int SIGN_MASK = 0x80;
    private static final int PARITY_MASK = 0x01;
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
        s = (val & SIGN_MASK) > 0 ? 1 : 0;
    }

    private void setParityFrom(int val) {
        p = (val & PARITY_MASK) == 0 ? 1 : 0;
    }

    public void setCarryFrom(int val) {
        cy = (val & CARRY_MASK) == 0 ? 1 : 0;
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
        var tmp = getAcc();
        sub(val);
        setAcc(tmp);
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
