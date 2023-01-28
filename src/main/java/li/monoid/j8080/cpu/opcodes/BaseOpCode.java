package li.monoid.j8080.cpu.opcodes;

public class BaseOpCode {
    final int opCode;
    final int opMask;
    final String mnemonic;
    final int size;

    public BaseOpCode(int opCode, int opMask, String mnemonic, int size) {
        this.opCode = opCode;
        this.opMask = opMask;
        this.mnemonic = mnemonic;
        this.size = size;
    }

    public int getOpCode() {
        return opCode;
    }

    public int getOpMask() {
        return opMask;
    }

    public String getMnemonic() {
        return mnemonic;
    }

    public int getSize() {
        return size;
    }

    public boolean match(byte opCode) {
        return (opCode & opMask) == this.opCode;
    }

    public String toString() {
        return mnemonic;
    }

    public String fullMnemonic(byte opCode) {
        return mnemonic;
    }
}