package li.monoid.j8080.cpu.opcodes.kinds;

import li.monoid.j8080.cpu.opcodes.OpType;

public class InvalidKind extends BaseKind {
    public InvalidKind(int opCode) {
        super(opCode, 0xff, OpType.INVALID, 0, 1);
    }

    @Override
    public int getCycles(byte opCode) {
        return 0;
    }
}
