package li.monoid.j8080;

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

        var system = new System();
        system.loadRom(rom);

        for (int i = 0; i < 2000; ++i) {
            java.lang.System.out.print(system);
            system.step();
        }
    }
}
