package li.monoid.j8080.bus;

import li.monoid.j8080.system.DataRW;

public class Bus implements DataRW {
    private final DataRW dataRW;

    public Bus(DataRW dataRW) {
        this.dataRW = dataRW;
    }

    @Override
    public byte readByte(int address) {
        return dataRW.readByte(address);
    }

    @Override
    public void writeByte(int address, byte data) {
        dataRW.writeByte(address, data);
    }
}
