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

public class Cpu implements Runnable, InterruptHandler {
    public static final int CYCLES_PER_TICK = 8;
    private final Registers registers;
    private final Alu alu;
    private final Bus bus;
    private final InstrSet instrSet;

    private final Set<Short> debugPoints = new HashSet<>();

    private final List<Short> debugMemAddresses = new LinkedList<>();
    private boolean debugInstructions = false;

    private boolean interruptsEnabling = false;
    private boolean interruptsEnabled = false;
    private int interruptSet = -1;

    private boolean isHalted = false;
    private int accumulatedCycles = 0;

    public Cpu(InstrSet instrSet, Registers registers, Alu alu, Bus bus) {
        this.instrSet = instrSet;
        this.registers = registers;
        this.bus = bus;
        this.alu = alu;
    }

    public Cpu(InstrSet instrSet, Bus bus) {
        this(instrSet, new Registers(), new Alu(), bus);
    }

    public boolean isHalted() {
        return isHalted;
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

    @Override
    public void run() {
        accumulatedCycles -= CYCLES_PER_TICK; // Small optimization
        if (accumulatedCycles <= 0 && !isHalted) {
            accumulatedCycles += processInstruction();
        }
        if (accumulatedCycles < 0) {
            accumulatedCycles = 0;
        }
    }

    public int processInstruction() {
        if (interruptSet >= 0 && interruptsEnabled) {
            var nnn = interruptSet;
            interruptsEnabled = false;
            interruptSet = -1;
            return interrupt(nnn);
        }


        var opCodeAddress = registers.getPC();
        var opCodeByte = bus.readByte(opCodeAddress);
        var opCode = instrSet.getOpCode(opCodeByte);

        var debugStr = "";
        if (debugInstructions) {
            debugStr = showInstr(opCode, opCodeAddress);
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

        registers.incPC();

        // When EI is called, interrupts are not immediately activated, but only after on the next instruction.
        if (interruptsEnabling) {
            interruptsEnabled = true;
            interruptsEnabling = false;
        }

        var argNum = opCode.kind.getSize() - 1;
        short arg = 0x0000;
        if (argNum == 1) {
            arg |= 0xff & bus.readByte(registers.incPC());
        } else if (argNum == 2) {
            arg = bus.readShort(registers.incPC(2));
        }

        if (debugInstructions) {
            System.out.printf("%-14s %4x%n", debugStr, arg);
        }

        var kind = opCode.kind;
        var cycles = opCode.kind.getCycles(opCode.opCode);

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
            System.err.printf("Unsupported CPU instruction %s(%02x) @ %04x", opCode.mnemonic, opCodeByte, opCodeAddress);
            System.exit(1);
            cycles += 1;
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

    @Override
    public void handleInterrupt(int nnn) {
        interruptSet = nnn;
    }

    private int interrupt(int nnn) {
        pushShort(registers.getPC());
        registers.setPC(8 * nnn);
        return 11;
    }

    private int stepRegKind(OpCode opCode, Register reg, short arg) {
        switch (opCode.opType) {
            case ADC -> alu.addC(readFromReg(reg));
            case ADD -> alu.add(readFromReg(reg));
            case ANA -> alu.and(readFromReg(reg));
            case CMP -> alu.cmp(readFromReg(reg));
            case DCR -> {
                byte val = Cast.toByte(readFromReg(reg) - 1);
                writeToReg(reg, val);
                alu.setZSPFrom(val);
            }
            case INR -> {
                var val = readFromReg(reg) + 1;
                writeToReg(reg, Cast.toByte(val));
                alu.setZSPFrom(val);
            }
            case MVI -> writeToReg(reg, Cast.toByte(arg));
            case ORA -> alu.or(readFromReg(reg));
            case RST -> interrupt((0b00111000 & opCode.opCode) >> 3);
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
            case DI -> interruptsEnabled = false;
            case EI -> interruptsEnabling = true; // Not immediately effective
            case HLT -> isHalted = true;
            case IN -> alu.setAcc(bus.readFromDevice(Cast.toByte(arg)));
            case JMP -> registers.setPC(arg);
            case LDA -> alu.setAcc(bus.readByte(arg));
            case LHLD -> registers.setHL(bus.readShort(arg));
            case NOP -> {
            }
            case ORI -> alu.or(arg);
            case OUT -> bus.writeToDevice(Cast.toByte(arg), alu.getAcc());
            case PCHL -> registers.setPC(registers.getHL());
            case RAL -> alu.rotateLeftC();
            case RAR -> alu.rotateRightC();
            case RET -> registers.setPC(popShort());
            case RLC -> alu.rotateLeft();
            case RRC -> alu.rotateRight();
            case SBI -> alu.subC(arg);
            case SHLD -> bus.writeShort(arg, registers.getHL());
            case SPHL -> registers.setSP(registers.getHL());
            case STA -> bus.writeByte(arg, alu.getAcc());
            case STC -> alu.setCarry(1);
            case SUI -> alu.sub(arg);
            case XCHG -> {
                var de = registers.getDE();
                registers.setDE(registers.getHL());
                registers.setHL(de);
            }
            case XRI -> alu.xor(arg);
            case XTHL -> {
                var sp = registers.getSP();
                var dataOnStack = bus.readShort(sp);
                bus.writeShort(sp, registers.getHL());
                registers.setHL(dataOnStack);
            }
            default -> System.err.println("Unsupported CPU instruction unar: " + opCode.mnemonic);
        }
        return 0;
    }

    public String showStack(int size) {
        var sb = new StringBuilder(String.format("  Stack: %04x%n", bus.readShort(registers.getSP() > 1 ? registers.getSP() - 2 : 0)));
        sb.append(String.format("  %04x > %04x%n", registers.getSP(), bus.readShort(registers.getSP())));
        for (int i = 1; i < size; ++i) {
            sb.append(
                    String.format("  %04x   %04x%n",
                            Cast.toShort(registers.getSP()+2*i),
                            bus.readShort(Cast.toShort(registers.getSP() + 2*i))));
        }
        return sb.toString();
    }

    public String showRegisters() {
        return String.format("  Acc: %02x%n", alu.getAcc()) +
                String.format("  BC: %04x%n", registers.getBC()) +
                String.format("  DE: %04x%n", registers.getDE()) +
                String.format("  HL: %04x%n", registers.getHL()) +
                String.format("  SP: %04x%n", registers.getSP()) +
                String.format("  PC: %04x%n", registers.getPC()) +
                String.format("  Instr: %02x%n", bus.readByte(registers.getPC())) +
                String.format("  Flags: ZSPC %x%x%x%x%n", alu.isZ() ? 1 : 0, alu.isS() ? 1 : 0, alu.isP() ? 1 : 0, alu.isCY() ? 1 : 0);
    }

    public String coreDump(int context) {
        return showRegisters() + showStack(context);
    }

    public String showInstr(OpCode opCode, short opCodeAddress) {
        return String.format("%04x: %s", opCodeAddress, opCode.kind.fullMnemonic(opCode.opCode));
    }

    public String toString() {
        var opCodeAddress = registers.getPC();
        var opCodeByte = bus.readByte(opCodeAddress);
        var opCode = instrSet.getOpCode(opCodeByte);
        return showInstr(opCode, opCodeAddress) + showRegisters() + showStack(4);
    }
}
