package at.ac.tuwien.infosys.g2021.daemon;

import at.ac.tuwien.infosys.g2021.common.ActorGathererConfiguration;
import at.ac.tuwien.infosys.g2021.common.BufferConfiguration;
import at.ac.tuwien.infosys.g2021.common.GathererConfiguration;
import at.ac.tuwien.infosys.g2021.common.SensorGathererConfiguration;
import at.ac.tuwien.infosys.g2021.common.SimpleData;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;

/**
 * Here is the logical driver for the G2021. It uses a hardware dependent hardware driver to access the
 * hardware devices. The main tasks of this class are
 * <ul>
 * <li>to create the hardware buffers in the initialization phase,</li>
 * <li>to poll the hardware ports and</li>
 * <li>to route the value changes to the gatherers.</li>
 * </ul>
 */
class Driver implements Component {

    // The used hardware driver
    private HardwareDriverInterface driver;

    // The current timer task
    private TimerTask ticker;

    // The last values distributed
    private Map<String, SimpleData> currentValues;

    // The map of gatherers with the port name as key
    private Map<String, Collection<ValueChangeConsumer>> listeners;

    // This inner class is the implementation of the timer task
    private class Ticker extends TimerTask {

        /** The action to be performed by this timer task. */
        @Override
        public void run() { poll(); }
    }

    /** Initialization of the driver. The suitable hardware driver is selected. */
    Driver() {

        driver = HardwareDriverFactory.select();
        ticker = new Ticker();
        listeners = new HashMap<>();
        currentValues = new HashMap<>();
    }

    /**
     * This method is called, after creating all necessary instances. Here the
     * hardware buffer for all available ports are created.
     */
    @Override
    public void initialize() {

        driver.initialize();

        // Now the available hardware is scanned and buffers are generated
        driver.getPorts().forEach(this::configureHardwareBuffer);

        // at last a timer task is started, to poll all the values.
        Daemon.get().timer().scheduleAtFixedRate(ticker, 0L, 1000L);
    }

    /**
     * Configure all the hardware buffers and their gatherers.
     *
     * @param port the information about hardware ports from the driver
     */
    private void configureHardwareBuffer(PortDescription port) {

        String portName = port.getPortName();
        PortClass portClass = port.getPortClass();

        // Create a configuration for the gatherer
        GathererConfiguration gathererConfiguration;
        if (portClass.isActor()) gathererConfiguration = new ActorGathererConfiguration(portName);
        else gathererConfiguration = new SensorGathererConfiguration(portName);

        // generate a buffer description of the needed buffer
        BufferConfiguration bufferConfiguration = new BufferConfiguration();
        bufferConfiguration.setBufferClass(portClass.bufferClassForPort());
        bufferConfiguration.setGatherer(gathererConfiguration);
        bufferConfiguration.getMetainfo().putAll(port.getPortMetainfo());

        // Now we create all the stuff
        Daemon.get().buffers().create(portName, bufferConfiguration, true);

        // If this buffer is a sensor, the gatherer will listen to value changes
        if (portClass.isSensor()) {

            SensorGatherer gatherer = (SensorGatherer)Daemon.get().gatherers().gathererForConfiguration(gathererConfiguration);
            Collection<ValueChangeConsumer> listener = listeners.get(portName);

            if (listener == null) {
                listener = new ArrayList<>();
                listeners.put(portName, listener);
            }

            listener.add(gatherer);
        }
    }

    /**
     * Distribute value changes to the gatherers.
     *
     * @param value the current value
     */
    private void distributeValue(SimpleData value) {

        String port = value.getBufferName();

        if (listeners.containsKey(port) && !value.equals(currentValues.get(port))) {
            listeners.get(port).forEach(listener -> listener.valueChanged(value));
            currentValues.put(port, value);
        }
    }

    /** Polls the hardware changes and distributes value changes to the gatherers. */
    private void poll() { driver.getAll().forEach(this::distributeValue); }

    /**
     * Sets the output value of actors.
     *
     * @param portName the name of the output port
     * @param value    the new output value
     *
     * @return <tt>true</tt>, if the hardware value successfully changed
     */
    boolean set(String portName, Number value) {

        try {
            return driver.set(portName, value);
        }
        catch (UnsupportedOperationException e) {
            return false;
        }
    }

    /** This method is called, whenever a shutdown sequence is initiated. */
    @Override
    public void shutdown() {

        // polling is not necessary any more
        ticker.cancel();

        // close the hardware driver
        driver.shutdown();
    }

    /** This method is called immediately after a shutdown, immediately before the process is stopped. */
    @Override
    public void release() {

        // At first no more value changes are delivered
        listeners.clear();

        // polling is not necessary any more
        ticker.cancel();

        // close the hardware driver
        driver.release();
    }
}

