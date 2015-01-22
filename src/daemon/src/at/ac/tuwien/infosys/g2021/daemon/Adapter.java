package at.ac.tuwien.infosys.g2021.daemon;

import at.ac.tuwien.infosys.g2021.common.BufferState;
import at.ac.tuwien.infosys.g2021.common.SimpleData;
import at.ac.tuwien.infosys.g2021.common.util.Loggers;
import java.util.Date;
import java.util.logging.Logger;

/**
 * An adapter is an object, that transforms a value to another. Value changes may be initiated
 * by signalling a value change over the <tt>{@link ValueChangeConsumer}</tt>-interface or by any asynchronous
 * other event (eg. the system timer). Changes of the adapter value are signalled over the
 * <tt>{@link ValueChangeConsumer}</tt>-interface.
 */
abstract class Adapter extends ValueChangeProducer implements ValueChangeConsumer {

    // The logger.
    private final static Logger logger = Loggers.getLogger(Adapter.class);

    // The used configuration
    private Object configuration;

    // The current gatherer value
    private SimpleData currentValue;

    // This is the lock object for changing values
    protected final Object valueLock;

    /** Initializing the instance variables. */
    private Adapter() {

        valueLock = new Object();
        currentValue = new SimpleData(new Date(), BufferState.INITIALIZING);
    }

    /**
     * Initializing the adapter.
     *
     * @param config the configuration of the adapter
     */
    protected Adapter(Object config) {

        this();
        this.configuration = config;
    }

    /**
     * Returns the configuration of the adapter.
     *
     * @return the configuration
     */
    @SuppressWarnings(value = "unchecked")
    <T> T getConfiguration() { return (T)configuration; }

    /**
     * Returns the current value of the adapter
     *
     * @return the current value
     */
    SimpleData get() {

        synchronized (valueLock) {
            return currentValue;
        }
    }

    /**
     * Sets the current value of the adapter
     *
     * @param value the new current value
     */
    void set(SimpleData value) {

        synchronized (valueLock) {

            if (!currentValue.equals(value)) {
                currentValue = value;
                if (currentValue.getValue() == null) {
                    logger.fine(String.format("An instance of '%s' distributes the new state %s.",
                                              getClass().getSimpleName(), value.getState().name()));
                }
                else {
                    logger.fine(String.format("An instance of '%s' distributes the new value %.3f.",
                                              getClass().getSimpleName(), value.getValue().doubleValue()));
                }
                fireValueChange(currentValue);
            }
        }
    }
}

