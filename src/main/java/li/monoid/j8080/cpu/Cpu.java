package li.monoid.j8080.cpu;

import li.monoid.j8080.cpu.opcodes.OpCode;
import li.monoid.j8080.cpu.opcodes.kinds.*;

import static li.monoid.j8080.cpu.opcodes.OpType.*;

public class Cpu {
    public static final BaseKind[] OP_KINDS = {
            new MoveKind(0x40, MOV, 5),
            new URegKind(0x06, MVI, 7, 3, 2),
            new RegPKind(0x01, LXI, 10, 3),
            new UnarKind(0x3a, LDA, 13, 3),
            new UnarKind(0x32, STA, 13, 3),
            new UnarKind(0x2a, LHLD, 16, 3),
            new UnarKind(0x22, SHLD, 16, 3),
            new RegPKind(0x0a, LDAX, 7),
            new RegPKind(0x02, STAX, 7),
            new UnarKind(0xeb, XCHG, 4),
            new LRegKind(0x80, ADD, 4),
            new UnarKind(0xc6, ADI, 7, 2),
            new LRegKind(0x88, ADC, 4),
            new UnarKind(0xce, ACI, 7, 2),
            new LRegKind(0x90, SUB, 4),
            new UnarKind(0xd6, SUI, 7, 2),
            new LRegKind(0x98, SBB, 4),
            new UnarKind(0xde, SBI, 7, 2),
            new URegKind(0x04, INR, 5, 5),
            new URegKind(0x05, DCR, 5, 5),
            new RegPKind(0x03, INX, 5),
            new RegPKind(0x0b, DCX, 5),
            new RegPKind(0x09, DAD, 10),
            new UnarKind(0x27, DAA, 4),
            new LRegKind(0xa0, ANA, 4),
            new UnarKind(0xe6, ANI, 7, 2),
            new LRegKind(0xa8, XRA, 4),
            new UnarKind(0xee, XRI, 7, 2),
            new LRegKind(0xb0, ORA, 4),
            new UnarKind(0xf6, ORI, 7, 2),
            new LRegKind(0xb8, CMP, 4),
            new UnarKind(0xfe, CPI, 7, 2),
            new UnarKind(0x07, RLC, 4),
            new UnarKind(0x0f, RRC, 4),
            new UnarKind(0x17, RAL, 4),
            new UnarKind(0x1f, RAR, 4),
            new UnarKind(0x2f, CMA, 4),
            new UnarKind(0x3f, CMC, 4),
            new UnarKind(0x37, STC, 4),
            new UnarKind(0xc3, JMP, 10, 3),
            new CondKind(0xc2, J, 10, 3),
            new UnarKind(0xcd, CALL, 17, 3),
            new CondKind(0xc4, C, 11, 3),
            new UnarKind(0xc9, RET, 10),
            new CondKind(0xc0, R, 5),
            new CondKind(0xc7, RST, 11),
            new UnarKind(0xe9, PCHL, 5),
            new RegPKind(0xc5, PUSH, 11),
            new RegPKind(0xc1, POP, 10),
            new UnarKind(0xe3, XTHL, 18),
            new UnarKind(0xf9, SPHL, 5),
            new UnarKind(0xdb, IN, 10, 2),
            new UnarKind(0xd3, OUT, 10, 2),
            new UnarKind(0xfb, EI, 4),
            new UnarKind(0xf3, DI, 4),
            new UnarKind(0x76, HLT, 7),
            new UnarKind(0x00, NOP, 4),
    };

    public static final OpCode[] OP_CODES;

    static {
        OP_CODES = new OpCode[256];
        OpCodes:
        for (int opCode = 0; opCode <= 255; ++opCode) {
            for (BaseKind opKind : OP_KINDS) {
                if (!opKind.match((byte) opCode)) {
                    continue;
                }
                OP_CODES[opCode] = new OpCode((byte) opCode, opKind.getOpType(), opKind, opKind.fullMnemonic((byte) opCode));
                continue OpCodes;
            }
            OP_CODES[opCode] = new OpCode((byte) opCode, INVALID, new InvalidKind(opCode), "INVALID");
        }
    }

    public static OpCode getOpCode(byte opCode) {
        return OP_CODES[opCode & 0xff];
    }
}
