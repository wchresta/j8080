package li.monoid.j8080.cpu;

import li.monoid.j8080.cpu.opcodes.*;

public class Cpu {
    public static final BaseOpCode[] OP_CODES = {
            new MoveOpCode(0x40, "MOV "),
            new UnarOpCode(0xf9, "SPHL"),
            new URegOpCode(0x06, "MVI ", 2),
            new RegPOpCode(0x01, "LXI ", 3),
            new UnarOpCode(0x3a, "LDA ", 3),
            new UnarOpCode(0x32, "STA ", 3),
            new UnarOpCode(0x2a, "LHLD", 3),
            new UnarOpCode(0x22, "SHLD", 3),
            new RegPOpCode(0x0a, "LDAX"),
            new RegPOpCode(0x02, "STAX"),
            new UnarOpCode(0xeb, "XCHG"),
            new LRegOpCode(0x80, "ADD "),
            new UnarOpCode(0xc6, "ADI ", 2),
            new LRegOpCode(0x88, "ADC "),
            new UnarOpCode(0xce, "ACI ", 2),
            new LRegOpCode(0x90, "SUB "),
            new UnarOpCode(0xd6, "SUI ", 2),
            new LRegOpCode(0x98, "SBB "),
            new UnarOpCode(0xde, "SBI ", 2),
            new URegOpCode(0x04, "INR "),
            new URegOpCode(0x05, "DCR "),
            new RegPOpCode(0x03, "IDX "),
            new RegPOpCode(0x0b, "DCX "),
            new RegPOpCode(0x09, "DAD "),
            new UnarOpCode(0x27, "DAA "),
            new LRegOpCode(0xa0, "ANA "),
            new UnarOpCode(0xe6, "ANI ", 2),
            new LRegOpCode(0xa8, "XRA "),
            new UnarOpCode(0xee, "XRI ", 2),
            new LRegOpCode(0xb0, "ORA "),
            new UnarOpCode(0xf6, "ORI ", 2),
            new LRegOpCode(0xb8, "CMP "),
            new UnarOpCode(0xfe, "CPI ", 2),
            new UnarOpCode(0x07, "RLC "),
            new UnarOpCode(0x0f, "RRC "),
            new UnarOpCode(0x17, "RAL "),
            new UnarOpCode(0x1f, "RAR "),
            new UnarOpCode(0x2f, "CMA "),
            new UnarOpCode(0x3f, "CMC "),
            new UnarOpCode(0x37, "STC "),
            new UnarOpCode(0xc3, "JMP ", 3),
            new CondOpCode(0xc2, "J   ", 3),
            new UnarOpCode(0xcd, "CALL", 3),
            new CondOpCode(0xc4, "C   ", 3),
            new UnarOpCode(0xc9, "RET "),
            new CondOpCode(0xc0, "R   "),
            new CondOpCode(0xc7, "RST "),
            new UnarOpCode(0xe9, "PCHL"),
            new RegPOpCode(0xc5, "PUSH"),
            new RegPOpCode(0xc1, "POP "),
            new UnarOpCode(0xe3, "XTHL"),
            new UnarOpCode(0xdb, "IN  ", 2),
            new UnarOpCode(0xd3, "OUT ", 2),
            new UnarOpCode(0xfb, "EI  "),
            new UnarOpCode(0xf3, "DI  "),
            new UnarOpCode(0x76, "HLT "),
            new UnarOpCode(0x00, "NOP "),
    };
}
