package at.ac.tuwien.infosys.g2021.daemon.drivers.unittest;

import at.ac.tuwien.infosys.g2021.common.BufferState;
import at.ac.tuwien.infosys.g2021.common.SimpleData;
import at.ac.tuwien.infosys.g2021.daemon.HardwareDriverInterface;
import at.ac.tuwien.infosys.g2021.daemon.PortClass;
import at.ac.tuwien.infosys.g2021.daemon.PortDescription;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/** This is a driver implementation used for unit tests. */
public class UnitTestDriverImplementation implements HardwareDriverInterface {

    // All the port values.
    private Map<String, SimpleData> values;

    /** Creating the driver instance. */
    public UnitTestDriverImplementation() {

        values = new HashMap<>();
    }

    /**
     * Returns the name of the driver for logging purposes.
     *
     * @return the name of the driver
     */
    @Override
    public String getName() { return "Hardware Driver for Unit Tests"; }

    /**
     * Is this a suitable hardware driver and can this driver work with the current hardware?
     * <p>
     * This method is used to select the current hardware driver of the daemon. It must not throw an exception
     * in any case! If this methods return <tt>false</tt>, no other methods of this driver are called.
     * <p>
     * This driver is used, if the system property "at.ac.tuwien.infosys.g2021.unit.test" is set to "unit-test".
     *
     * @return <tt>true</tt>, if this is a suitable driver
     */
    @Override
    public boolean isSuitable() { return "unit-test".equals(System.getProperty("at.ac.tuwien.infosys.g2021.unit.test")); }

    /**
     * Is this hardware driver is the best choice for this hardware? If this method returns <tt>false</tt> and another
     * driver is also suitable, the other driver will be used.
     *
     * @return <tt>true</tt>, if this driver is the best choice
     */
    @Override
    public boolean isBestChoice() { return true; }

    /** This method is called, after creating all necessary instances. */
    @Override
    public void initialize() {

        values.put("AI", new SimpleData("AI", new Date(), BufferState.READY, 0.0));
        values.put("AO", new SimpleData("AO", new Date(), BufferState.READY, 0.0));
        values.put("DI", new SimpleData("DI", new Date(), BufferState.READY, 0));
        values.put("DO", new SimpleData("DO", new Date(), BufferState.READY, 0));
    }

    /**
     * Returns the available ports and their properties.
     *
     * @return the port properties
     */
    @Override
    public Collection<PortDescription> getPorts() {

        Collection<PortDescription> result = new ArrayList<>();
        PortDescription port;
        HashMap<String, String> metainfo;

        metainfo = new HashMap<>();
        metainfo.put("type", "DI");
        port = new PortDescription("DI", PortClass.DIGITAL_INPUT, metainfo);
        result.add(port);

        metainfo = new HashMap<>();
        metainfo.put("type", "AI");
        port = new PortDescription("AI", PortClass.ANALOG_INPUT, metainfo);
        result.add(port);

        metainfo = new HashMap<>();
        metainfo.put("type", "DO");
        port = new PortDescription("DO", PortClass.DIGITAL_OUTPUT, metainfo);
        result.add(port);

        metainfo = new HashMap<>();
        metainfo.put("type", "AO");
        port = new PortDescription("AO", PortClass.ANALOG_OUTPUT, metainfo);
        result.add(port);

        return result;
    }

    /**
     * Returns the states and values of all available ports.
     *
     * @return the state and value of all available ports
     */
    @Override
    public Collection<SimpleData> getAll() { return values.values(); }

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

        switch (port) {
            case "AO":
            case "DO":
                if (values.get(port).getState() != BufferState.READY) return false;
                else setPortValue(port, value);
                break;

            default:
                throw new UnsupportedOperationException("no such port");
        }

        return true;
    }

    /** This method is called, whenever a shutdown sequence is initiated. */
    @Override
    public void shutdown() {}

    /** This method is called immediately after a shutdown, immediately before the process is stopped. */
    @Override
    public void release() {}

    /** Returns a port value.
     * @param port For this port the value is desired.
     * @return the desired value.
     * */
    public SimpleData getPortValue(String port) { return values.get(port); }

    /** Returns a port value.
     * @param port One of <tt>AI</tt> or <tt>AO</tt> or <tt>DI</tt> or <tt>DO</tt>.
     * */
    public void disturbPort(String port) {

        switch (port) {
            case "AI":
            case "AO":
            case "DI":
            case "DO":
                values.put(port, new SimpleData(port, new Date(), BufferState.FAULTED));
                break;
        }
    }

    /**
     * Sets the value of a port.
     *
     * @param port  the name of the port
     * @param value the new output value
     */
    public void setPortValue(String port, Number value) {

        switch (port) {
            case "AI":
            case "AO":
                values.put(port, new SimpleData(port, new Date(), BufferState.READY, Math.max(-100.0, Math.min(value.doubleValue(), 100.0))));
                break;

            case "DI":
            case "DO":
                values.put(port, new SimpleData(port, new Date(), BufferState.READY, Math.round(value.floatValue()) == 0 ? 0 : 1));
                break;
        }
    }
}
