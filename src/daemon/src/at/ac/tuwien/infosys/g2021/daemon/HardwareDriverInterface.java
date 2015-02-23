package at.ac.tuwien.infosys.g2021.daemon;

import at.ac.tuwien.infosys.g2021.common.SimpleData;
import java.util.Collection;

/** Every driver for hardware devices must implement this interface. */
public interface HardwareDriverInterface extends Component {

    /**
     * Is this a suitable hardware driver and can this driver work with the current hardware?
     * <p>
     * This method is used to select the current hardware driver of the daemon. It must not throw an exception
     * in any case! If this methods return <tt>false</tt>, no other methods of this driver are called.
     *
     * @return <tt>true</tt>, if this is a suitable driver
     */
    public boolean isSuitable();

    /**
     * Is this hardware driver is the best choice for this hardware? If this method returns <tt>false</tt> and another
     * driver is also suitable, the other driver will be used.
     *
     * @return <tt>true</tt>, if this driver is the best choice
     */
    public boolean isBestChoice();

    /**
     * Returns the name of the driver for logging purposes.
     *
     * @return the name of the driver
     */
    public String getName();

    /**
     * Returns the available ports and their properties.
     *
     * @return the port properties
     */
    public Collection<PortDescription> getPorts();

    /**
     * Returns the states and values of all available ports.
     *
     * @return the state and value of all available ports
     */
    public Collection<SimpleData> getAll();

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
    public boolean set(String port, Number value) throws UnsupportedOperationException;
}

