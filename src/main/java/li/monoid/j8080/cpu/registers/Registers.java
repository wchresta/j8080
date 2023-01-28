package li.monoid.j8080.cpu.registers;

public class Registers {
    private int a, b, c, d, e, h, l;
    private int sp, pc;

    public int getA() {
        return a;
    }

    public void setA(int a) {
        this.a = a;
    }

    public int getB() {
        return b;
    }

    public void setB(int b) {
        this.b = b;
    }

    public int getC() {
        return c;
    }

    public void setC(int c) {
        this.c = c;
    }

    public int getD() {
        return d;
    }

    public void setD(int d) {
        this.d = d;
    }

    public int getE() {
        return e;
    }

    public void setE(int e) {
        this.e = e;
    }

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }

    public int getL() {
        return l;
    }

    public void setL(int l) {
        this.l = l;
    }

    public int getBC() {
        return b << 8 | c;
    }

    public int getDE() {
        return d << 8 | e;
    }

    public int getHL() {
        return h << 8 | l;
    }

    public int getSP() {
        return sp;
    }

    public void setSP(int sp) {
        this.sp = sp;
    }

    public int getPC() {
        return pc;
    }

    public void setPC(int pc) {
        this.pc = pc;
    }

    public void incPC() {
        this.pc += 1;
    }

    public int getReg(Register r) {
        return switch (r) {
            case A -> a;
            case B -> b;
            case C -> c;
            case D -> d;
            case E -> e;
            case H -> h;
            case L -> l;
            default -> throw new IllegalStateException("Invalid register: " + r);
        };
    }

    public int getRegPair(RegisterPair rp) {
        return switch (rp) {
            case BC -> getBC();
            case DE -> getDE();
            case HL -> getHL();
            case SP -> getSP();
        };
    }
}
