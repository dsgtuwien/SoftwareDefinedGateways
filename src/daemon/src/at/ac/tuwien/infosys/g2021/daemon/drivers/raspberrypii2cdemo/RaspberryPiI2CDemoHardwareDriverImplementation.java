package at.ac.tuwien.infosys.g2021.daemon.drivers.raspberrypii2cdemo;

import at.ac.tuwien.infosys.g2021.common.BufferState;
import at.ac.tuwien.infosys.g2021.common.SimpleData;
import at.ac.tuwien.infosys.g2021.common.util.Loggers;
import at.ac.tuwien.infosys.g2021.daemon.HardwareDriverInterface;
import at.ac.tuwien.infosys.g2021.daemon.PortDescription;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/** This is a dummy implementation. It guarantees the existence of at least one usable driver on every hardware. */
public class RaspberryPiI2CDemoHardwareDriverImplementation implements HardwareDriverInterface {

    // The logger.
    private final static Logger logger = Loggers.getLogger(RaspberryPiI2CDemoHardwareDriverImplementation.class);

    // An object for thread synchronization
    private final Object lock;

    // This is the set of the available chips.
    private Map<ChipSet, Chip> chips;

    // The selection of the adc multiplexer.
    private int currentMultiplexerSetting;

    /** Creating the driver instance. */
    public RaspberryPiI2CDemoHardwareDriverImplementation() {

        lock = new Object();
        chips = new HashMap<>();
        currentMultiplexerSetting = -1;
    }

    /**
     * Returns the name of the driver for logging purposes.
     *
     * @return the name of the driver
     */
    @Override
    public String getName() { return "Demo Driver for Raspberry Pi (I\u00B2C interface)"; }

    /**
     * Is this a suitable hardware driver and can this driver work with the current hardware?
     * <p>
     * This method is used to select the current hardware driver of the daemon. It must not throw an exception
     * in any case! If this methods return <tt>false</tt>, no other methods of this driver are called.
     *
     * @return <tt>true</tt>, if this is a suitable driver
     */
    @Override
    public boolean isSuitable() {

        try {
            // This call throws an exception if there is no device.
            I2CFactory.getInstance(I2CBus.BUS_1);
            return true;
        }
        catch (Throwable e) {
            return false;
        }
    }

    /**
     * Is this hardware driver is the best choice for this hardware? If this method returns <tt>false</tt> and another
     * driver is also suitable, the other driver will be used.
     *
     * @return <tt>true</tt>, if this driver is the best choice
     */
    @Override
    public boolean isBestChoice() { return false; }

    /** This method is called, after creating all necessary instances. */
    @Override
    public void initialize() {

        synchronized (lock) {

            // Multiple initializations are ignored.
            if (chips.size() == 0) {

                try {
                    I2CBus bus = I2CFactory.getInstance(I2CBus.BUS_1);

                    for (ChipSet chipSetEntry : ChipSet.values()) {

                        String name = chipSetEntry.name();
                        Chip chip;

                        if (chipSetEntry.isADC()) chip = new ADCChip(bus, chipSetEntry);
                        else chip = new LatchChip(bus, chipSetEntry);

                        // Is this chip exists, it can be used.
                        if (selectADC(chipSetEntry.getMultiplexerValue()) && chip.exists()) {
                            logger.config(String.format("The I\u00B2C device '%s' at address 0x%02X is available.", name, chip.getAddress()));
                            chips.put(chipSetEntry, chip);
                        }
                    }

                    // If there are no chips, we close the bus.
                    if (chips.size() == 0) logger.warning("The I\u00B2C driver is initialized but useless. There are no I\u00B2C devices available.");
                    else logger.info("The I\u00B2C driver is now initialized.");
                }
                catch (Exception e) {
                    // if the allocation of the bus fails, we can return. There are no devices available.
                    logger.log(Level.WARNING, "The initialization of the I\u00B2C driver failed:", e);
                }
            }
        }
    }

    /**
     * Set the multiplexer for the ADCs.
     *
     * @param selection the multiplexer selection
     *
     * @return <tt>true</tt>, if the selection is ok
     */
    private boolean selectADC(int selection) {

        if (selection >= 0 && currentMultiplexerSetting != selection && chips.containsKey(ChipSet.AIMUX)) {
            try {
                chips.get(ChipSet.AIMUX).write(selection);
                logger.fine(String.format("The ADC multiplexer is set to '0x%02X'", selection));
                currentMultiplexerSetting = selection;
            }
            catch (IOException e) {
                // The multiplexer setting failed. We cannot select the chip.
                return false;
            }
        }

        return true;
    }

    /**
     * Read the value of a chip.
     *
     * @param chip the chip from the set of chips
     *
     * @return the raw hardware value or null, if an error occurs
     */
    private Integer readRawValue(ChipSet chip) {

        Integer result = null;

        if (chips.containsKey(chip) && selectADC(chip.getMultiplexerValue())) {
            try {
                result = chips.get(chip).read();
            }
            catch (IOException e) {
                // The chip is faulted, the result is well initialized.
            }
        }

        return result;
    }

    /**
     * Returns the available ports and their properties.
     *
     * @return the port properties
     */
    @Override
    public Collection<PortDescription> getPorts() {

        Collection<PortDescription> result = new ArrayList<>();

        for (PortSet portSetEntry : PortSet.values()) {
            if (chips.containsKey(portSetEntry.fromChip())) {

                Map<String, String> metadata = new HashMap<>();
                metadata.put("type", portSetEntry.portClass().name());
                if (portSetEntry.unit() != null) metadata.put("unit", portSetEntry.unit());

                result.add(new PortDescription(portSetEntry.name(), portSetEntry.portClass(), metadata));
            }
        }

        logger.config(String.format("There are %d I\u00B2C devices known.", result.size()));
        return result;
    }

    /**
     * Returns the states and values of all available ports.
     *
     * @return the state and value of all available ports
     */
    @Override
    public Collection<SimpleData> getAll() {

        Collection<SimpleData> result = new ArrayList<>();
        ChipSet lastChipRead = null;
        Integer lastValueRead = null;
        Date now = new Date();

        synchronized (lock) {
            for (PortSet portSetEntry : PortSet.values()) {

                ChipSet chip = portSetEntry.fromChip();

                if (chips.containsKey(chip)) {

                    String name = portSetEntry.name();

                    // Read the chip value if necessary
                    if (lastChipRead != chip) {
                        lastChipRead = chip;
                        lastValueRead = readRawValue(chip);
                    }

                    // If there was a raise condition, the port is faulted!
                    if (lastValueRead == null) {
                        logger.fine(String.format("The I\u00B2C device '%s' is in faulted state.", name));
                        result.add(new SimpleData(name, now, BufferState.FAULTED));
                    }
                    else {
                        Number value = portSetEntry.rawValueToNumber(lastValueRead);

                        logger.fine(String.format("The I\u00B2C device '%s' offers the value %.3f.", name, value.doubleValue()));
                        result.add(new SimpleData(name, now, BufferState.READY, value));
                    }
                }
            }
        }

        return result;
    }

    /**
     * Sets the output of an actor.
     *
     * @param port  the name of the port
     * @param value the new output value
     *
     * @return <tt>true</tt>, if the port was set correctly
     *
     * @throws UnsupportedOperationException if the port is not known or a sensor port
     */
    @Override
    public boolean set(String port, Number value) throws UnsupportedOperationException {

        try {
            PortSet portSetEntry = PortSet.valueOf(port);
            ChipSet chip = portSetEntry.fromChip();

            if (chips.containsKey(chip) && portSetEntry.portClass().isActor()) {

                synchronized (lock) {

                    // Read the chip value if necessary
                    Integer read = readRawValue(chip);

                    // If we can't read the chip, we also can't write to it
                    if (read == null) {
                        logger.fine(String.format("Reading I\u00B2C device '%s' failed.", port));
                        return false;
                    }

                    try {
                        Integer raw = portSetEntry.numberToRawValue(read, value);

                        if (raw != null) {
                            logger.fine(String.format("The I\u00B2C device '%s' is set to %d.", port, raw));
                            chips.get(chip).write(raw);
                            return true;
                        }
                        else {
                            logger.fine(String.format("Setting I\u00B2C device '%s' failed.", port));
                            return false;
                        }
                    }
                    catch (IOException e) {
                        logger.log(Level.FINE, String.format("Setting I\u00B2C device '%s' failed.", port), e);
                        return false;
                    }
                }
            }
            else {
                throw new UnsupportedOperationException("no suitable I\u00B2C device for set operation: " + port);
            }
        }
        catch (IllegalArgumentException e) {
            throw new UnsupportedOperationException("no such I²C device: " + port, e);
        }
    }

    /** This method is called, whenever a shutdown sequence is initiated. */
    @Override
    public void shutdown() { release(); }

    /** This method is called immediately after a shutdown, immediately before the process is stopped. */
    @Override
    public void release() {

        if (chips.size() > 0) {

            // Now the chips are useless.
            chips.clear();
            logger.info("The I\u00B2C driver is closed now.");
        }
    }
}
