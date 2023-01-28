package li.monoid.j8080.disassembler;

import li.monoid.j8080.cpu.Cpu;
import li.monoid.j8080.cpu.opcodes.OpType;

public class Reader {
    public static class EndOfBuffer extends Exception {
        public EndOfBuffer() { }
    }

    public static class Error extends Exception {
        private final byte opCode;
        private final int address;
        private String disassembly;

        public Error(String message, byte opCode, int address, String disassembly) {
            super(message);
            this.opCode = opCode;
            this.address = address;
            this.disassembly = disassembly;
        }

        public void setDisassembly(String disassembly) {
            this.disassembly = disassembly;
        }

        public String toString() {
            return String.format("%s opCode=%02x addr=%02x\nDisassembly:\n%s", this.getMessage(), this.opCode, this.address, this.disassembly);
        }
    }

    private final byte[] buf;
    private int bufPointer;

    public Reader(byte[] buf) {
        this.buf = buf;
        this.bufPointer = 0;
    }

    public void seek(int addr) {
        this.bufPointer = addr;
    }

    public String readNext() throws Error, EndOfBuffer {
        if (this.bufPointer >= this.buf.length) {
            throw new EndOfBuffer();
        }

        byte opCodeByte = this.buf[this.bufPointer];
        var opCode = Cpu.getOpCode(opCodeByte);

        if (opCode.opType == OpType.INVALID) {
            var unknownCode = String.format("%04x ?(%02x)", this.bufPointer, opCodeByte);
            this.bufPointer += 1;
            return unknownCode;
        }

        var opSize = opCode.kind.getSize();
        String mnemonic = String.format("%04x %s", this.bufPointer, opCode.mnemonic);
        if (this.bufPointer + opSize - 1 > this.buf.length) {
            // Out of bounds
            throw new Error("last opCode has no bytes left", opCodeByte, this.bufPointer, mnemonic);
        }

        var mnemonicWithArgs = switch (opSize) {
            case 1 -> mnemonic;
            case 2 -> String.format("%s %02x", mnemonic, this.buf[this.bufPointer + 1]);
            case 3 ->
                    String.format("%s %02x%02x", mnemonic, this.buf[this.bufPointer + 2], this.buf[this.bufPointer + 1]);
            default -> throw new Error("invalid opCode size", opCodeByte, this.bufPointer, mnemonic);
        };
        this.bufPointer += opSize;

        return mnemonicWithArgs;
    }

    public String readAll() throws Error {
        var sb = new StringBuilder();
        while (true) {
            try {
                sb.append(this.readNext());
            } catch (EndOfBuffer eob) {
                break;
            } catch (Error e) {
                e.setDisassembly(sb.toString());
                throw e;
            }
            sb.append('\n');
        }
        return sb.toString();
    }
}
