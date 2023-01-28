package li.monoid.j8080.disassembler;

public class BaseOpCode {
    public static class InvalidOpCode extends Exception {
        private final byte opCode;

        public InvalidOpCode(byte opCode) {
            super(String.format("invalid OpCode %02x", opCode));
            this.opCode = opCode;
        }

        public InvalidOpCode(byte opCode, String details) {
            super(String.format("invalid OpCode %02x: %s", opCode, details));
            this.opCode = opCode;
        }

        public byte getOpCode() {
            return opCode;
        }
    }

    public static class InvalidFlag extends Exception {
        private final byte flag;

        public InvalidFlag(byte flag) {
            this.flag = flag;
        }

        public byte getFlag() {
            return flag;
        }
    }

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

    public String fullMnemonic(byte opCode) throws InvalidOpCode {
        return mnemonic;
    }
}