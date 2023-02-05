package li.monoid.j8080.cpu;

import li.monoid.j8080.bus.Bus;
import li.monoid.j8080.cpu.instrset.Intel8080;
import li.monoid.j8080.cpu.registers.Registers;
import li.monoid.j8080.memory.Cast;
import li.monoid.j8080.memory.Memory;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Assert;
import org.junit.Test;

import org.json.simple.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class FunctionalTest {
    public void runFunctionalTest(Path romPath) {
        byte[] rom = {};
        try {
            rom = Files.readAllBytes(romPath);
        } catch (IOException e) {
            java.lang.System.err.println("Could not read rom " + romPath);
            java.lang.System.exit(1);
        }

        var mem = new Memory(0x10000);
        var registers = new Registers();
        var alu = new Alu();
        var bus = new Bus(mem);
        var cpu = new Cpu(new Intel8080(), registers, alu, bus);

        mem.loadRom(rom, 0x100); // org 100h
        mem.setRomSize(0);  // Some code is self-modifying, do not protect the rom.
        //cpu.setDebugInstructions(true);
        /*
        cpu.addDebugPoint((short) 0x0c63);
        cpu.addDebugPoint((short) 0x0c64);
        cpu.addDebugPoint((short) 0x0c65);
        cpu.addDebugPoint((short) 0x0c68);
        cpu.addDebugPoint((short) 0x0c6d);
        */
        registers.setPC(0x100);
        //mem.unsafeWriteByte(368, (byte) 0x07); // Bugfix?
        while (true) {
            if (registers.getPC() == 0x0000) {
                Assert.fail("Found PC at 0, one of the early tests failed.");
            }
            if ((mem.readByte(registers.getPC()) == (byte) 0xcd /*CALL*/)
            && (mem.readShort(registers.getPC()+1) == (short) 0x005)) {
                // This is supposed to be a call to PRINT
                // We intercept this call instead.
                switch (registers.getC()) {
                    case 9 -> {
                        var addr = registers.getDE();
                        System.out.printf("%04x: ", registers.getPC());
                        while (true) {
                            byte chr = mem.readByte(addr);
                            if (chr == '$') {
                                break;
                            }
                            System.out.printf("%c", chr & 0xff);
                            ++addr;
                        }
                        System.out.println();
                        registers.incPC(3);
                    }
                    case 2 -> {
                        registers.incPC(3);
                        var arg = alu.getAcc();
                        System.out.printf("%c", arg);
                    }
                    default -> System.err.printf("Unknown print call %x", registers.getC());
                }
            }
            try {
                cpu.run();
            } catch (RuntimeException e) {
                System.err.println(e);
                System.err.println(cpu);
                Assert.fail();
            }
        }
    }

    @Test
    public void jsmooSuite() {
        var jsMooTestFiles = new File("./resources/tests/z80/v1").listFiles();
        assert jsMooTestFiles != null;
        for (File jsTestFile : jsMooTestFiles) {
            var testName = jsTestFile.getName();
            System.out.println("Testing " + jsTestFile.getName());
            jsmooTest(jsTestFile.toPath());
        }
    }

    public void jsmooTest(Path path) {
        // Test the jsmoo suite downloaded from
        // https://github.com/raddad772/jsmoo

        var parser = new JSONParser();
        JSONArray tests;
        try {
            tests = (JSONArray) parser.parse(Files.readString(path));
        } catch (ParseException | IOException e) {
            throw new RuntimeException(e);
        }


        var mem = new Memory(0x10000);
        var registers = new Registers();
        var alu = new Alu();
        var bus = new Bus(mem);
        var cpu = new Cpu(new Intel8080(), registers, alu, bus);
        var groupName = path.getFileName();

        for (Object oTest : tests) {
            var test = (JSONObject) oTest;
            var initial = (JSONObject) test.get("initial");
            var finals = (JSONObject) test.get("final");
            var testName = groupName + ", " + test.get("name");

            Function<String, Short> iShort = (String name) ->
                ((Number) initial.get(name)).shortValue();
            Function<String, Byte> iByte = (String name) ->
                ((Number) initial.get(name)).byteValue();

            registers.setPC(iShort.apply("pc"));
            registers.setSP(iShort.apply("sp"));
            alu.setAcc(iByte.apply("a"));
            registers.setB(iByte.apply("b"));
            registers.setC(iByte.apply("c"));
            registers.setD(iByte.apply("d"));
            registers.setE(iByte.apply("e"));
            alu.setFlagsFromByte(iByte.apply("f"));
            registers.setH(iByte.apply("h"));
            registers.setL(iByte.apply("l"));

            mem.clear();
            for (Object oRamVal : (JSONArray) initial.get("ram")) {
                var ramVal = (JSONArray) oRamVal;
                var addr = ((Number) ramVal.get(0)).shortValue();
                var val = ((Number) ramVal.get(1)).byteValue();
                mem.writeByte(addr, val);
            }

            // Initial state ready, execute!
            cpu.processInstruction();

            // Now let's test!
            BiConsumer<Short, String> assertShort = (Short got, String name) -> {
                Assert.assertEquals(testName + ": " + name, String.format("%04x", ((Number) finals.get(name)).shortValue()), String.format("%04x", got));
            };
            BiConsumer<Byte, String> assertByte = (Byte got, String name) -> {
                Assert.assertEquals(testName + ": " + name, String.format("%02x", ((Number) finals.get(name)).byteValue()), String.format("%02x", got));
            };
            assertShort.accept(registers.getPC(), "pc");
            assertShort.accept(registers.getSP(), "sp");
            assertByte.accept(registers.getB(), "b");
            assertByte.accept(registers.getC(), "c");
            assertByte.accept(registers.getD(), "d");
            assertByte.accept(registers.getE(), "e");
            assertByte.accept(registers.getH(), "h");
            assertByte.accept(registers.getL(), "l");
            assertByte.accept(alu.getAcc(), "a");
            // We only care about the non-static bits, we also ignore AC
            /*
            Assert.assertEquals(testName + ": f",
                    Integer.toBinaryString(0xc5 & ((Number) finals.get("f")).byteValue()),
                    Integer.toBinaryString(0xc5 & alu.getFlagByte()));
            */

        }
    }

    @Test
    public void runPreTest() {
        runFunctionalTest(Paths.get("./resources/8080PRE.COM"));
    }

    @Test
    public void runMainTest() {
        runFunctionalTest(Paths.get("./resources/8080EXER.COM"));
    }
}
