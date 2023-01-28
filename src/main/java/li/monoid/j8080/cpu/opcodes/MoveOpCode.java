package li.monoid.j8080.cpu.opcodes;

public class MoveOpCode extends BaseOpCode {
    public MoveOpCode(int opCode, String mnemonic) {
        super(opCode, 0xc0, mnemonic, 1);
    }
}
