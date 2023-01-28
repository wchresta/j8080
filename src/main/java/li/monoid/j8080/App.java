package li.monoid.j8080;

import li.monoid.j8080.disassembler.Reader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Hello world!
 *
 */
public class App
{
    public static void main( String[] args )
    {
        var path = Paths.get("./resources/roms/spaceinvaders/invaders.rom");
        byte[] rom = {};
        try {
            rom = Files.readAllBytes(path);
        } catch (IOException e) {
            System.err.println("Could not read rom " + path);
            System.exit(1);
        }

        var reader = new Reader(rom);
        try {
            System.out.print(reader.readAll());
        } catch (Reader.Error e) {
            System.err.println(e);
        }
    }
}
