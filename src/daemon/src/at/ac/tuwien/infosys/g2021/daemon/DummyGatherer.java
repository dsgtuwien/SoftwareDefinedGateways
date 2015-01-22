package at.ac.tuwien.infosys.g2021.daemon;

import at.ac.tuwien.infosys.g2021.common.BufferState;
import at.ac.tuwien.infosys.g2021.common.DummyGathererConfiguration;

/** This is a dummy gatherer. This gatherer works as actor and accepts any value. */
class DummyGatherer extends Gatherer {

    /**
     * Initializing a dummy gatherer.
     *
     * @param config the configuration
     */
    DummyGatherer(DummyGathererConfiguration config) {

        super(config);
        setCurrentValue(0.0);
    }

    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object.
     */
    @Override
    public String toString() { return getClass().getSimpleName(); }

    /**
     * Can this gatherer be used as actor?
     *
     * @return <tt>true</tt>, if this gatherer acts as an actor
     */
    @Override
    boolean canUseAsActor() { return true; }

    /**
     * Sets the output value of actors. For sensors, this method has no effect and always returns <tt>false</tt>.
     *
     * @param value the new output value
     *
     * @return <tt>true</tt>, if the hardware value successfully changed
     */
    @Override
    boolean set(Number value) {

        synchronized (valueLock) {

            if (get().getState() == BufferState.READY && value != null) {
                setCurrentValue(value);
                return true;
            }
            else {
                return false;
            }
        }
    }

    /**
     * Sets an error state. This method is implemented for testing reasons only.
     *
     * @param state the new state
     */
    void set(BufferState state) {

        synchronized (valueLock) {

            if (get().getState() != BufferState.READY && state == BufferState.READY) {
                setCurrentValue(0.0);
            }
            else if (get().getState() != state) {
                setCurrentState(state);
            }
        }
    }
}

