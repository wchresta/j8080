package li.monoid.j8080.cpu.opcodes;

public enum OpType {
    MOV, MVI, LXI, LDA, STA, LHLD, SHLD, LDAX, STAX, XCHG, ADD, ADI, ADC, ACI, SUB, SUI, SBB, SBI, INR, DCR, INX, DCX,
    DAD, DAA, ANA, ANI, XRA, XRI, ORA, ORI, CMP, CPI, RLC, RRC, RAL, RAR, CMA, CMC, STC, JMP, J, CALL, C, RET, R, RST,
    PCHL, PUSH, POP, XTHL, SPHL, IN, OUT, EI, DI, HLT, NOP,
    INVALID
}
