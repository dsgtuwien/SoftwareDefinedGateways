package at.ac.tuwien.infosys.g2021.daemon.drivers.raspberrypii2cdemo;

import com.pi4j.io.i2c.I2CBus;
import java.io.IOException;

/** An ADC from the I²C bus. */
class ADCChip extends Chip {

    /**
     * Initialization.
     *
     * @param b the I²C bus
     * @param c the device from the chip set
     */
    ADCChip(I2CBus b, ChipSet c) { super(b, c); }

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

        byte[] buffer = new byte[2];
        int read;
        try {
            read = getDevice().read(buffer, 0, 2);
        }
        catch (IOException e) {
            // rethrow the exception
            throw new IOException(e.getMessage(), e);
        }

        // the buffer must filled completely
        if (read != buffer.length) throw new IOException("read error");
        else return ((0x0000000F & buffer[0]) << 4) | ((buffer[1] & 0x000000F0) >> 4);
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

        throw new IOException("write not supported");
    }
}

