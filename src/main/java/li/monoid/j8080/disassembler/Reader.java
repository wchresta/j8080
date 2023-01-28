package li.monoid.j8080.disassembler;

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

    public static final int DST_MASK = 0b00111000;
    public static final int SRC_MASK = 0b00000111;

    public static final BaseOpCode[] OP_CODES = {
            new MoveOpCode(0x40, "MOV"),
            new OpCode(0xf9, "SPHL"),
            new RegOpCode(0x06, "MVI", DST_MASK, 2),
            new RPOpCode(0x01, "LXI", 3),
            new OpCode(0x3a, "LDA", 3),
            new OpCode(0x32, "STA", 3),
            new OpCode(0x2a, "LHLD", 3),
            new OpCode(0x22, "SHLD", 3),
            new RPOpCode(0x0a, "LDAX"),
            new RPOpCode(0x02, "STAX"),
            new OpCode(0xeb, "XCHG"),
            new RegOpCode(0x80, "ADD", SRC_MASK),
            new OpCode(0xc6, "ADI", 2),
            new RegOpCode(0x88, "ADC", SRC_MASK),
            new OpCode(0xce, "ACI", 2),
            new RegOpCode(0x90, "SUB", SRC_MASK),
            new OpCode(0xd6, "SUI", 2),
            new RegOpCode(0x98, "SBB", SRC_MASK),
            new OpCode(0xde, "SBI", 2),
            new RegOpCode(0x04, "INR", DST_MASK),
            new RegOpCode(0x05, "DCR", DST_MASK),
            new RPOpCode(0x03, "IDX"),
            new RPOpCode(0x0b, "DCX"),
            new RPOpCode(0x09, "DAD"),
            new OpCode(0x27, "DAA"),
            new RegOpCode(0xa0, "ANA", SRC_MASK),
            new OpCode(0xe6, "ANI", 2),
            new RegOpCode(0xa8, "XRA", SRC_MASK),
            new OpCode(0xee, "XRI", 2),
            new RegOpCode(0xb0, "ORA", SRC_MASK),
            new OpCode(0xf6, "ORI", 2),
            new RegOpCode(0xb8, "CMP", SRC_MASK),
            new OpCode(0xfe, "CPI", 2),
            new OpCode(0x07, "RLC"),
            new OpCode(0x0f, "RRC"),
            new OpCode(0x17, "RAL"),
            new OpCode(0x1f, "RAR"),
            new OpCode(0x2f, "CMA"),
            new OpCode(0x3f, "CMC"),
            new OpCode(0x37, "STC"),
            new OpCode(0xc3, "JMP", 3),
            new CondOpCode(0xc2, "J", 3),
            new OpCode(0xcd, "CALL", 3),
            new CondOpCode(0xc4, "C", 3),
            new OpCode(0xc9, "RET"),
            new CondOpCode(0xc0, "R"),
            new CondOpCode(0xc7, "RST"),
            new OpCode(0xe9, "PCHL"),
            new RPOpCode(0xc5, "PUSH"),
            new RPOpCode(0xc1, "POP"),
            new OpCode(0xe3, "XTHL"),
            new OpCode(0xdb, "IN", 2),
            new OpCode(0xd3, "OUT", 2),
            new OpCode(0xfb, "EI"),
            new OpCode(0xf3, "DI"),
            new OpCode(0x76, "HLT"),
            new OpCode(0x00, "NOP"),
    };

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

        byte thisOpCode = this.buf[this.bufPointer];
        for (BaseOpCode opCode : OP_CODES) {
            if (!opCode.match(thisOpCode)) {
                continue;
            }

            var opSize = opCode.getSize();
            String mnemonic = String.format("%04x ", this.bufPointer);
            try {
                mnemonic += opCode.fullMnemonic(thisOpCode);
            } catch (BaseOpCode.InvalidOpCode e) {
                throw new Error("concrete opCode is invalid", thisOpCode, this.bufPointer, opCode.getMnemonic());
            }
            if (this.bufPointer + opSize - 1 > this.buf.length) {
                // Out of bounds
                throw new Error("last opCode has no bytes left", thisOpCode, this.bufPointer, mnemonic);
            }

            var mnemonicWithArgs = switch (opSize) {
                case 1 -> mnemonic;
                case 2 -> String.format("%s %02x", mnemonic, this.buf[this.bufPointer+1]);
                case 3 -> String.format("%s %02x%02x", mnemonic, this.buf[this.bufPointer+2], this.buf[this.bufPointer+1]);
                default -> throw new Error("invalid opCode size", thisOpCode, this.bufPointer, mnemonic);
            };
            this.bufPointer += opSize;

            return mnemonicWithArgs;
        }
        var unknownCode = String.format("%04x ?(%02x)", this.bufPointer, thisOpCode);
        this.bufPointer += 1;
        return unknownCode;
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
