package li.monoid.j8080.cpu.instrset;

import li.monoid.j8080.cpu.opcodes.OpCode;

public interface InstrSet {
    OpCode getOpCode(byte opCode);
}
