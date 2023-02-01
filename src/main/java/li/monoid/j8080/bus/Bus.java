package li.monoid.j8080.bus;

import li.monoid.j8080.cpu.InterruptHandler;
import li.monoid.j8080.devices.InputDevice;
import li.monoid.j8080.devices.OutputDevice;
import li.monoid.j8080.system.DeviceIO;
import li.monoid.j8080.system.MemoryRW;

import java.util.HashMap;
import java.util.Map;

public class Bus implements MemoryRW, DeviceIO {
    private final MemoryRW memoryRW;
    private InterruptHandler interruptHandler;
    private final Map<Byte, InputDevice> inputDeviceMap = new HashMap<>();
    private final Map<Byte, OutputDevice> outputDeviceMap = new HashMap<>();

    public Bus(MemoryRW memoryRW) {
        this.memoryRW = memoryRW;
    }

    public void setInterruptHandler(InterruptHandler interruptHandler) {
        this.interruptHandler = interruptHandler;
    }

    public void registerInputDevice(byte deviceNo, InputDevice device) {
        inputDeviceMap.put(deviceNo, device);
    }

    public void registerOutputDevice(byte deviceNo, OutputDevice device) {
        outputDeviceMap.put(deviceNo, device);
    }

    public void interrupt(int nnn) {
        if (interruptHandler == null) {
            return;
        }
        interruptHandler.handleInterrupt(nnn);
    }

    @Override
    public byte readByte(int address) {
        return memoryRW.readByte(address);
    }

    @Override
    public void writeByte(int address, byte data) {
        memoryRW.writeByte(address, data);
    }

    @Override
    public void writeToDevice(byte deviceNo, byte data) {
        var device = outputDeviceMap.get(deviceNo);
        if (device == null) {
            System.err.printf("Cannot send data to unknown device: %02x%n", deviceNo);
            return;
        }

        device.receiveData(deviceNo, data);
    }

    @Override
    public byte readFromDevice(byte deviceNo) {
        var device = inputDeviceMap.get(deviceNo);
        if (device == null) {
            System.err.printf("Cannot receive data from unknown device: %02x%n", deviceNo);
            return 0x00;
        }
        return device.sendData(deviceNo);
    }
}
