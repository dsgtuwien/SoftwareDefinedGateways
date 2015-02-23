package at.ac.tuwien.infosys.g2021.daemon;

import at.ac.tuwien.infosys.g2021.common.SimpleData;
import java.util.ArrayList;
import java.util.Collection;

/** This is a dummy implementation. It guarantees the existence of at least one usable driver on every hardware. */
class DummyHardwareDriverImplementation implements HardwareDriverInterface {

    /** Creating the driver instance. */
    DummyHardwareDriverImplementation() {}

    /**
     * Returns the name of the driver for logging purposes.
     *
     * @return the name of the driver
     */
    @Override
    public String getName() { return "dummy driver"; }

    /**
     * Is this a suitable hardware driver and can this driver work with the current hardware?
     * <p>
     * This method is used to select the current hardware driver of the daemon. It must not throw an exception
     * in any case! If this methods return <tt>false</tt>, no other methods of this driver are called.
     *
     * @return <tt>true</tt>, if this is a suitable driver
     */
    @Override
    public boolean isSuitable() { return true; }

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
    public void initialize() {}

    /**
     * Returns the available ports and their properties.
     *
     * @return the port properties
     */
    @Override
    public Collection<PortDescription> getPorts() { return new ArrayList<>(); }

    /**
     * Returns the states and values of all available ports.
     *
     * @return the state and value of all available ports
     */
    @Override
    public Collection<SimpleData> getAll() { return new ArrayList<>(); }

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
    public boolean set(String port, Number value) throws UnsupportedOperationException { throw new UnsupportedOperationException("no such port"); }

    /** This method is called, whenever a shutdown sequence is initiated. */
    @Override
    public void shutdown() {}

    /** This method is called immediately after a shutdown, immediately before the process is stopped. */
    @Override
    public void release() {}
}
