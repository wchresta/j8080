package li.monoid.j8080.cpu;

import li.monoid.j8080.bus.Bus;
import li.monoid.j8080.cpu.instrset.InstrSet;
import li.monoid.j8080.cpu.opcodes.OpCode;
import li.monoid.j8080.cpu.opcodes.kinds.*;
import li.monoid.j8080.cpu.registers.Condition;
import li.monoid.j8080.cpu.registers.Register;
import li.monoid.j8080.cpu.registers.RegisterPair;
import li.monoid.j8080.cpu.registers.Registers;
import li.monoid.j8080.memory.Cast;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static li.monoid.j8080.cpu.opcodes.OpType.MOV;

public class Cpu {
    private final Registers registers;
    private final Alu alu;
    private final Bus bus;
    private final InstrSet instrSet;

    private final Set<Short> debugPoints = new HashSet<>();

    private final List<Short> debugMemAddresses = new LinkedList<>();
    private boolean debugInstructions = false;

    public Cpu(InstrSet instrSet, Registers registers, Alu alu, Bus bus) {
        this.instrSet = instrSet;
        this.registers = registers;
        this.bus = bus;
        this.alu = alu;
    }

    public Cpu(InstrSet instrSet, Bus bus) {
        this(instrSet, new Registers(), new Alu(), bus);
    }

    public void addDebugPoint(short address) {
        debugPoints.add(address);
    }

    public void addDebugMemAddress(short address) {
        debugMemAddresses.add(address);
    }

    public void setDebugInstructions(boolean debugInstructions) {
        this.debugInstructions = debugInstructions;
    }

    private void pushByte(byte val) {
        bus.writeByte(registers.decSP(), val);
    }

    private void pushShort(short val) {
        pushByte(Cast.toByte(val >> 8));
        pushByte(Cast.toByte(val));
    }

    private byte popByte() {
        return bus.readByte(registers.incSP());
    }

    private short popShort() {
        var lo = popByte();
        var hi = popByte();
        return Cast.toShort((hi << 8) | (lo & 0xff));
    }

    public int step() {
        var opCodeAddress = registers.getPC();
        var opCodeByte = bus.readByte(opCodeAddress);
        var opCode = instrSet.getOpCode(opCodeByte);
        registers.incPC();

        var argNum = opCode.kind.getSize() - 1;
        short arg = 0x0000;
        if (argNum == 1) {
            arg |= 0xff & bus.readByte(registers.incPC());
        } else if (argNum == 2) {
            arg = bus.readShort(registers.incPC(2));
        }

        var kind = opCode.kind;
        var cycles = opCode.kind.getCycles(opCode.opCode);
        if (debugInstructions) {
            System.out.printf("%04x: %s %04x%n", opCodeAddress, kind.fullMnemonic(opCodeByte), arg);
        }
        if (kind instanceof UnarKind) {
            cycles += stepUnarKind(opCode, arg);
        } else if (kind instanceof MoveKind) {
            var dst = Register.fromUpperOpCode(opCodeByte);
            var src = Register.fromLowerOpCode(opCodeByte);
            cycles += stepMoveKind(opCode, src, dst);
        } else if (kind instanceof CondKind) {
            var cond = Condition.fromOpCode(opCodeByte);
            cycles += stepCondKind(opCode, cond, arg);
        } else if (kind instanceof URegKind) {
            var reg = Register.fromUpperOpCode(opCodeByte);
            cycles += stepRegKind(opCode, reg, arg);
        } else if (kind instanceof LRegKind) {
            var reg = Register.fromLowerOpCode(opCodeByte);
            cycles += stepRegKind(opCode, reg, arg);
        } else if (kind instanceof RegPKind) {
            var rp = RegisterPair.fromOpCode(opCodeByte);
            cycles += stepRegPKind(opCode, rp, arg);
        } else {
            System.err.println("Unsupported CPU instruction kind: " + opCode.mnemonic);
            cycles += 1;
        }

        if (debugPoints.contains(opCodeAddress)) {
            System.out.print(this);
            if (debugMemAddresses.size() > 0) {
                System.out.println("Debug memory:");
                for (short memAddr : debugMemAddresses) {
                    System.out.printf("  %04x: %02x%n", memAddr, bus.readByte(memAddr));
                }
            }
        }

        return cycles;
    }

    private int stepRegPKind(OpCode opCode, RegisterPair rp, short arg) {
        switch (opCode.opType) {
            case LXI -> registers.setRegPair(rp, arg);
            case LDAX -> alu.setAcc(bus.readShort(registers.getRegPair(rp)));
            case STAX -> bus.writeByte(registers.getRegPair(rp), alu.getAcc());
            case INX -> registers.setRegPair(rp, Cast.toShort(Short.toUnsignedInt(registers.getRegPair(rp)) + 1));
            case DCX -> registers.setRegPair(rp, Cast.toShort(Short.toUnsignedInt(registers.getRegPair(rp)) - 1));
            case DAD -> {
                var sum = registers.getHL() + registers.getRegPair(rp);
                alu.setCarryFrom(sum);
                registers.setHL(Cast.toShort(sum));
            }
            case PUSH -> {
                if (rp == RegisterPair.SP) { // PSW
                    pushByte(alu.getAcc());
                    pushByte(alu.getFlagByte());
                    break;
                }
                pushShort(registers.getRegPair(rp));
            }
            case POP -> {
                if (rp == RegisterPair.SP) { // PSW
                    alu.setFlagsFromByte(popByte());
                    alu.setAcc(popByte());
                    break;
                }
                registers.setRegPair(rp, popShort());
            }
            default -> System.err.println("Unsupported CPU instruction rp: " + opCode.mnemonic);
        }
        return 0;
    }


    private int stepCondKind(OpCode opCode, Condition cond, short arg) {
        var additionalCycleCost = 0;
        var conditonTrue = switch (cond) {
            case NZ -> !alu.isZ();
            case Z -> alu.isZ();
            case NC -> !alu.isCY();
            case C -> alu.isCY();
            case PO -> !alu.isP();
            case PE -> alu.isP();
            case P -> !alu.isS();
            case M -> alu.isS();
        };
        if (conditonTrue) {
            switch (opCode.opType) {
                case J -> registers.setPC(arg);
                case C -> {
                    call(arg);
                    additionalCycleCost += 6;
                }
                case R -> {
                    ret();
                    additionalCycleCost += 6;
                }
                default -> System.err.println("Unsupported CPU instruction cond: " + opCode.mnemonic);
            }
        }
        return additionalCycleCost;
    }

    private byte readFromReg(Register reg) {
        return switch (reg) {
            case M -> bus.readByte(registers.getHL());
            case A -> alu.getAcc();
            default -> registers.getReg(reg);
        };
    }

    private void writeToReg(Register reg, byte val) {
        switch (reg) {
            case A -> alu.setAcc(val);
            case M -> bus.writeByte(registers.getHL(), val);
            default -> registers.setReg(reg, val);
        }
    }

    private int stepMoveKind(OpCode opCode, Register src, Register dst) {
        if (opCode.opType != MOV) {
            System.err.println("Unsupported CPU instruction move: " + opCode.mnemonic);
            return 0;
        }
        writeToReg(dst, readFromReg(src));
        return 0;
    }

    private int stepRegKind(OpCode opCode, Register reg, short arg) {
        switch (opCode.opType) {
            case ADC -> alu.addC(readFromReg(reg));
            case ADD -> alu.add(readFromReg(reg));
            case ANA -> alu.and(readFromReg(reg));
            case DCR -> {
                var val = readFromReg(reg) - 1;
                writeToReg(reg, Cast.toByte(val));
                alu.setZSPFrom(val);
            }
            case INR -> {
                var val = readFromReg(reg) + 1;
                writeToReg(reg, Cast.toByte(val));
                alu.setZSPFrom(val);
            }
            case CMP -> alu.cmp(readFromReg(reg));
            case MVI -> writeToReg(reg, Cast.toByte(arg));
            case ORA -> alu.or(readFromReg(reg));
            case SBB -> alu.subC(readFromReg(reg));
            case SUB -> alu.sub(readFromReg(reg));
            case XRA -> alu.xor(readFromReg(reg));
            default -> System.err.println("Unsupported CPU instruction reg: " + opCode.mnemonic);
        }

        if (reg == Register.M) {
            // Reading to memory costs an additional 3 cycles.
            return 3;
        }
        return 0;
    }

    private void call(short address) {
        pushShort(registers.getPC());
        registers.setPC(address);
    }

    private void ret() {
        registers.setPC(popShort());
    }

    private int stepUnarKind(OpCode opCode, short arg) {
        switch (opCode.opType) {
            case ACI -> alu.addC(arg);
            case ADI -> alu.add(arg);
            case ANI -> alu.and(arg);
            case CALL -> call(arg);
            case CMA -> alu.not();
            case CMC -> alu.setCarry(1 ^ alu.getCarry());
            case CPI -> alu.cmp(0xff & arg);
            case DAA -> alu.daa();
            case IN -> alu.setAcc(bus.readFromDevice(Cast.toByte(arg)));
            case JMP -> registers.setPC(arg);
            case LDA -> alu.setAcc(bus.readByte(arg));
            case LHLD -> registers.setHL(bus.readShort(arg));
            case NOP -> {
            }
            case ORI -> alu.or(arg);
            case OUT -> bus.writeToDevice(Cast.toByte(arg), alu.getAcc());
            case PCHL -> registers.setPC(registers.getHL());
            case RAL -> alu.rotateLeft();
            case RAR -> alu.rotateRight();
            case RET -> registers.setPC(popShort());
            case RLC -> alu.rotateLeftC();
            case RRC -> alu.rotateRightC();
            case SBI -> alu.subC(arg);
            case SHLD -> bus.writeShort(arg, registers.getHL());
            case SPHL -> registers.setSP(registers.getHL());
            case STA -> bus.writeByte(arg, alu.getAcc());
            case SUI -> alu.sub(arg);
            case XCHG -> {
                var de = registers.getDE();
                registers.setDE(registers.getHL());
                registers.setHL(de);
            }
            case XRI -> alu.xor(arg);
            case XTHL -> {
                var sp = registers.getSP();
                registers.setSP(registers.getHL());
                registers.setHL(sp);
            }
            default -> System.err.println("Unsupported CPU instruction unar: " + opCode.mnemonic);
        }
        return 0;
    }

    public String toString() {
        return String.format("  Acc: %02x\n", alu.getAcc()) +
                String.format("  BC: %04x\n", registers.getBC()) +
                String.format("  DE: %04x\n", registers.getDE()) +
                String.format("  HL: %04x\n", registers.getHL()) +
                String.format("  SP: %04x\n", registers.getSP()) +
                String.format("  PC: %04x\n", registers.getPC()) +
                String.format("  Instr: %02x\n", bus.readByte(registers.getPC())) +
                String.format("  Flags: ZSPC %x%x%x%x\n", alu.isZ() ? 1 : 0, alu.isS() ? 1 : 0, alu.isP() ? 1 : 0, alu.isCY() ? 1 : 0) +
                String.format("  Stack: %04x%n", bus.readShort(registers.getSP() > 3 ? registers.getSP() - 4 : 0)) +
                String.format("         %04x%n", bus.readShort(registers.getSP() > 1 ? registers.getSP() - 2 : 0)) +
                String.format("       > %04x%n", bus.readShort(registers.getSP())) +
                String.format("         %04x%n", bus.readShort(registers.getSP() + 2));
    }
}
