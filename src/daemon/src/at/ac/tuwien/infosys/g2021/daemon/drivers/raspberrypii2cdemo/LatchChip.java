package at.ac.tuwien.infosys.g2021.daemon.drivers.raspberrypii2cdemo;

import com.pi4j.io.i2c.I2CBus;
import java.io.IOException;

/** A 8 Bit latch on the I²C bus. */
class LatchChip extends Chip {

    /**
     * Initialization.
     *
     * @param b the I²C bus
     * @param c the device from the chip set
     */
    LatchChip(I2CBus b, ChipSet c) { super(b, c); }

    /**
     * Read the value from the chip.
     *
     * @return the value
     *
     * @throws java.io.IOException if the read operation fails
     */
    @Override
    int read() throws IOException {

        if (getDevice() == null) throw new IOException("I\u00B2C device doesn't exists");
        return getDevice().read();
    }

    /**
     * Write a value into the chip.
     *
     * @param value the value to be written
     *
     * @throws java.io.IOException if the read operation fails
     */
    @Override
    void write(int value) throws IOException {

        if (getDevice() == null) throw new IOException("I\u00B2C device doesn't exists");
        getDevice().write((byte)value);
    }
}

