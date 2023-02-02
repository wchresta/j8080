package li.monoid.j8080;

import li.monoid.j8080.cpu.instrset.Intel8080;
import li.monoid.j8080.system.System;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Hello world!
 *
 */
public class App
{
    public static void main(String[] args) {
        var path = Paths.get("./resources/roms/spaceinvaders/invaders.rom");
        byte[] rom = {};
        try {
            rom = Files.readAllBytes(path);
        } catch (IOException e) {
            java.lang.System.err.println("Could not read rom " + path);
            java.lang.System.exit(1);
        }

        var system = new System(new Intel8080());
        system.loadRom(rom);
        system.run();
    }
}
