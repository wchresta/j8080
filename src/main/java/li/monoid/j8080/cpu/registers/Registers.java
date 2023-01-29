package li.monoid.j8080.cpu.registers;

import li.monoid.j8080.memory.Cast;

public class Registers {
    private byte b, c, d, e, h, l;
    private short sp, pc;

    public byte getB() {
        return b;
    }

    public void setB(int b) {
        this.b = Cast.toByte(b);
    }

    public byte getC() {
        return c;
    }

    public void setC(int c) {
        this.c = Cast.toByte(c);
    }

    public byte getD() {
        return d;
    }

    public void setD(int d) {
        this.d = Cast.toByte(d);
    }

    public byte getE() {
        return e;
    }

    public void setE(int e) {
        this.e = Cast.toByte(e);
    }

    public byte getH() {
        return h;
    }

    public void setH(int h) {
        this.h = Cast.toByte(h);
    }

    public byte getL() {
        return l;
    }

    public void setL(int l) {
        this.l = Cast.toByte(l);
    }

    public short getBC() {
        return Cast.toShort((b << 8) | c & 0xff);
    }

    public void setBC(short val) {
        b = Cast.toByte(val >> 8);
        c = Cast.toByte(val);
    }

    public short getDE() {
        return Cast.toShort((d << 8) | e & 0xff);
    }

    public void setDE(short val) {
        d = Cast.toByte(val >> 8);
        e = Cast.toByte(val);
    }

    public short getHL() {
        return Cast.toShort((h << 8) | l & 0xff);
    }

    public void setHL(short val) {
        h = Cast.toByte((val & 0xff00) >> 8);
        l = Cast.toByte(val & 0x00ff);
    }

    public short getSP() {
        return sp;
    }

    public void setSP(int sp) {
        this.sp = Cast.toShort(sp);
    }

    public short decSP() {
        return Cast.toShort(--sp);
    }

    public short incSP() {
        return Cast.toShort(sp++);
    }

    public short getPC() {
        return pc;
    }

    public void setPC(int pc) {
        this.pc = Cast.toShort(pc);
    }

    public short incPC() {
        return Cast.toShort(pc++);
    }

    public short incPC(int inc) {
        var oldPc = pc;
        pc += inc;
        return oldPc;
    }

    public byte getReg(Register r) {
        return switch (r) {
            case B -> b;
            case C -> c;
            case D -> d;
            case E -> e;
            case H -> h;
            case L -> l;
            default -> throw new IllegalStateException("Invalid register: " + r);
        };
    }

    public void setReg(Register r, byte val) {
        switch (r) {
            case B -> b = val;
            case C -> c = val;
            case D -> d = val;
            case E -> e = val;
            case H -> h = val;
            case L -> l = val;
            default -> throw new IllegalStateException("Invalid register: " + r);
        }
    }

    public short getRegPair(RegisterPair rp) {
        return switch (rp) {
            case BC -> getBC();
            case DE -> getDE();
            case HL -> getHL();
            case SP -> sp;
        };
    }

    public void setRegPair(RegisterPair rp, short val) {
        switch (rp) {
            case BC -> setBC(val);
            case DE -> setDE(val);
            case HL -> setHL(val);
            case SP -> sp = val;
        }
    }
}
