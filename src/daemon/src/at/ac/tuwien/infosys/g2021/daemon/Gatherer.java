package at.ac.tuwien.infosys.g2021.daemon;

import at.ac.tuwien.infosys.g2021.common.BufferState;
import at.ac.tuwien.infosys.g2021.common.GathererConfiguration;
import at.ac.tuwien.infosys.g2021.common.SimpleData;
import at.ac.tuwien.infosys.g2021.common.util.Loggers;
import java.util.Date;
import java.util.logging.Logger;

/**
 * A gatherer represents a single value of a hardware interface. This may be an actor or a sensor. Gatherers are identified
 * by a gatherer configuration. For a single hardware value, there exists only a single gatherer.
 */
abstract class Gatherer extends ValueChangeProducer {

    // The logger.
    private final static Logger logger = Loggers.getLogger(Gatherer.class);

    // The used gatherer configuration
    private GathererConfiguration configuration;

    // The current gatherer value
    private SimpleData currentValue;

    // This is the lock object for changing values
    protected final Object valueLock;

    /** There exists no gatherers without configuration. */
    private Gatherer() {

        valueLock = new Object();
    }

    /**
     * Initializing the gatherer.
     *
     * @param config the configuration of the gatherer
     */
    protected Gatherer(GathererConfiguration config) {

        this();
        this.configuration = config;
    }

    /** Releases any system resources of the gatherer. */
    void shutdown() {

        setCurrentState(BufferState.RELEASED);
        super.shutdown();
    }

    /**
     * Returns the configuration of the gatherer.
     *
     * @return the configuration
     */
    @SuppressWarnings(value = "unchecked")
    <T extends GathererConfiguration> T getConfiguration() { return (T)configuration; }

    /**
     * Returns the current value of the gatherer. Gatherers representing sensor values returns
     * the hardware sensor value. Actors returns the last value set by GBots.
     *
     * @return the current value. Be careful, this value contains NO usable buffer name.
     */
    SimpleData get() {

        synchronized (valueLock) {
            return currentValue;
        }
    }

    /**
     * Sets the current value of this gatherer. As state, BufferState.READY is assumed.
     *
     * @param value the current value of this gatherer
     */
    protected void setCurrentValue(Number value) {

        synchronized (valueLock) {
            if (currentValue == null || currentValue.getState() != BufferState.READY || !currentValue.getValue().equals(value)) {
                currentValue = new SimpleData(new Date(), BufferState.READY, value);
                logger.fine(String.format("An instance of '%s' distributes the new value %.3f.",
                                          getClass().getSimpleName(), value.doubleValue()));
                fireValueChange(currentValue);
            }
        }
    }

    /**
     * Sets the current state of this gatherer. As value <tt>null</tt> is assumed.
     *
     * @param state the current state of this gatherer
     */
    protected void setCurrentState(BufferState state) {

        synchronized (valueLock) {
            if (currentValue == null || currentValue.getState() != state) {
                currentValue = new SimpleData(new Date(), state);
                logger.fine(String.format("An instance of '%s' distributes the new state %s.",
                                          getClass().getSimpleName(), state.name()));
                fireValueChange(currentValue);
            }
        }
    }

    /**
     * Can this gatherer can be used as actor?
     *
     * @return <tt>true</tt>, if this gatherer acts as an actor
     */
    boolean canUseAsActor() { return false; }

    /**
     * Sets the output value of actors. For sensors, this method has no effect and always returns <tt>false</tt>.
     *
     * @param value the new output value
     *
     * @return <tt>true</tt>, if the hardware value successfully changed
     *
     * @see #canUseAsActor()
     */
    boolean set(Number value) { return false; }
}

