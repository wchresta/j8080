package li.monoid.j8080.cpu.opcodes.kinds;

import li.monoid.j8080.cpu.opcodes.OpType;
import li.monoid.j8080.cpu.registers.Register;

public class MoveKind extends RegKind {
    public MoveKind(int opCode, OpType opType, int baseCycles) {
        super(opCode, 0xc0, opType, baseCycles, 2, 1);
    }

    public boolean doesFetchM(byte opCode) {
        return (opCode & Register.UPPER_REG_MASK) == UPPER_M || (opCode & Register.LOWER_REG_MASK) == LOWER_M;
    }

    @Override
    public String fullMnemonic(byte opCode) {
        var upperReg = Register.fromUpperOpCode(opCode);
        var lowerReg = Register.fromLowerOpCode(opCode);
        return String.format("%s %s,%s", super.fullMnemonic(opCode), upperReg.name(), lowerReg.name());
    }
}
