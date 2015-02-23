package at.ac.tuwien.infosys.g2021.daemon.drivers.raspberrypii2cdemo;

import at.ac.tuwien.infosys.g2021.common.util.Loggers;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/** A chip is a hardware device at the I²C bus. */
abstract class Chip {

    // The logger.
    private final static Logger logger = Loggers.getLogger(Chip.class);

    // The bus, where the chip is located
    private I2CBus bus;

    // The device
    private I2CDevice device;

    // The name of the chip
    private String name;

    // The I²C address
    private int address;

    /** No instances without initialization! */
    private Chip() {}

    /**
     * Initialization.
     *
     * @param b    the I²C bus
     * @param n    the name
     * @param addr the port address
     */
    protected Chip(I2CBus b, String n, int addr) {

        this();

        bus = b;
        name = n;
        address = addr;
        try {
            device = bus.getDevice(addr);
        }
        catch (IOException e) {
            // no such device available
            device = null;
            logger.log(Level.FINE, String.format("Unable to access I\u00B2C device '%s' at address 0x%02X.", name, address), e);
        }
    }

    /**
     * Initialization.
     *
     * @param b the I²C bus
     * @param c the device from the chip set
     */
    protected Chip(I2CBus b, ChipSet c) { this(b, c.name(), c.getAddress()); }

    /**
     * Returns the I²C bus.
     *
     * @return the bus
     */
    I2CBus getBus() { return bus; }

    /**
     * Returns the representation of the chip on the I²C bus.
     *
     * @return the chip
     */
    I2CDevice getDevice() { return device; }

    /**
     * Returns the name of the chip.
     *
     * @return the name of the chip
     */
    String getName() { return name; }

    /**
     * Returns the address of the chip on the bus.
     *
     * @return the address of the chip
     */
    int getAddress() { return address; }

    /**
     * Is this chip present on the bus?
     *
     * @return <tt>true</tt>, if the chip is present
     */
    boolean exists() {

        try {
            read();
            return true;
        }
        catch (Exception e) {
            logger.log(Level.FINE, String.format("The I\u00B2C device '%s' at address 0x%02X is not available.", name, address), e);
        }

        return false;
    }

    /**
     * Read the value from the chip.
     *
     * @return the value
     *
     * @throws IOException if the read operation fails
     */
    abstract int read() throws IOException;

    /**
     * Write a value into the chip.
     *
     * @param value the value to be written
     *
     * @throws IOException if the read operation fails
     */
    abstract void write(int value) throws IOException;
}

