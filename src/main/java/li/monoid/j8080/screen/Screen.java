package li.monoid.j8080.screen;

import li.monoid.j8080.bus.Bus;
import li.monoid.j8080.memory.Memory;

import javax.swing.JFrame;
import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Screen implements Runnable {
    private final JFrame frame;
    private final Canvas canvas;

    private final int WIDTH = 224;
    private final int HEIGHT = 256;
    private final BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
    private final int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> lineScannerTask;
    private int lineScanState = 0;
    private final Memory memory;
    private final Bus bus;

    public Screen(Memory memory, Bus bus, KeyListener keyListener) {
        this.memory = memory;
        this.bus = bus;
        canvas = new Canvas();
        frame = new JFrame();
        frame.addKeyListener(keyListener);

        canvas.setPreferredSize(new Dimension(WIDTH*4, HEIGHT*4));
        canvas.setMinimumSize(new Dimension(WIDTH*4, HEIGHT*4));
        canvas.setMaximumSize(new Dimension(WIDTH*4, HEIGHT*4));
        frame.setLayout(new BorderLayout());
        frame.add(canvas);
        frame.pack();
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
    }

    public void turnOn() {
        frame.setVisible(true);
        lineScannerTask = scheduler.scheduleAtFixedRate(this, 500, 1000/120, TimeUnit.MILLISECONDS);
    }

    public void turnOff() {
        lineScannerTask.cancel(false);
    }

    @Override
    public void run() {
        grabScreen();
        render();
    }

    void grabScreen() {
        var screenMem = memory.readBytes(0x2400, 0x1c00);

        // Monitor is turned 90 degrees.
        for (var i = 0; i < WIDTH; ++i) {
            for (var j = 0; j < HEIGHT; ++j) {
                var idx = i * HEIGHT + j;
                pixels[(HEIGHT - j - 1) * WIDTH + i] = ((screenMem[Math.floorDiv(idx, 8)] >> (idx % 8)) & 1) * 0xffffff;
            }
        }
    }

    void render() {
        var bs = canvas.getBufferStrategy();
        if (bs == null) {
            canvas.createBufferStrategy(1);
            return;
        }

        var g = bs.getDrawGraphics();
        if (lineScanState == 0) {
            // We are drawing the upper half of the screen, which corresponds to the left half of the rotated one.
            bus.interrupt(0x01);  // Monitor sends a 1-interrupt whenever we are around the middle.
            g.drawImage(image, 0, 0, canvas.getWidth()/2, canvas.getHeight(), 0, 0, WIDTH/2, HEIGHT, null);
            lineScanState = 1;
        } else {
            bus.interrupt(0x02);  // Monitor sends a 2-interrupt whenever we are done drawing a full screen.
            g.drawImage(image, canvas.getWidth()/2, 0, canvas.getWidth(), canvas.getHeight(), WIDTH/2, 0, WIDTH, HEIGHT, null);
            lineScanState = 0;
        }
        g.dispose();
        bs.show();
    }
}
