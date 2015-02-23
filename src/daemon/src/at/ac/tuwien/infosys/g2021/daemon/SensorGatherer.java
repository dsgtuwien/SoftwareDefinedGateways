package at.ac.tuwien.infosys.g2021.daemon;

import at.ac.tuwien.infosys.g2021.common.BufferState;
import at.ac.tuwien.infosys.g2021.common.SensorGathererConfiguration;
import at.ac.tuwien.infosys.g2021.common.SimpleData;

/** This is a gatherer, that communicate with the driver to read values from a sensor. */
class SensorGatherer extends Gatherer implements ValueChangeConsumer {

    /**
     * Initializing a dummy gatherer.
     *
     * @param config the configuration
     */
    SensorGatherer(SensorGathererConfiguration config) {

        super(config);
        setCurrentState(BufferState.FAULTED);
    }

    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object.
     */
    @Override
    public String toString() {

        SensorGathererConfiguration configuration = getConfiguration();

        return String.format("%s:%s", getClass().getSimpleName(), configuration.getPortName());
    }

    /**
     * Can this gatherer be used as actor?
     *
     * @return <tt>true</tt>, if this gatherer acts as an actor
     */
    @Override
    boolean canUseAsActor() { return false; }

    /**
     * This is the notification of a spontaneous value change.
     *
     * @param newValue the new buffer value
     */
    @Override
    public void valueChanged(SimpleData newValue) {

        if (newValue.getState() == BufferState.READY) setCurrentValue(newValue.getValue());
        else setCurrentState(newValue.getState());
    }
}



