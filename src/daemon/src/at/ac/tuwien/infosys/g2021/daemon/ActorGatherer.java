package at.ac.tuwien.infosys.g2021.daemon;

import at.ac.tuwien.infosys.g2021.common.ActorGathererConfiguration;
import at.ac.tuwien.infosys.g2021.common.BufferState;

/** This is a gatherer, that communicate with the driver to control an actor. */
class ActorGatherer extends Gatherer {

    /**
     * Initializing a dummy gatherer.
     *
     * @param config the configuration
     */
    ActorGatherer(ActorGathererConfiguration config) {

        super(config);
        setCurrentState(BufferState.INITIALIZING);
    }

    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object.
     */
    @Override
    public String toString() {

        ActorGathererConfiguration configuration = getConfiguration();

        return String.format("%s:%s", getClass().getSimpleName(), configuration.getPortName());
    }

    /**
     * Can this gatherer be used as actor?
     *
     * @return <tt>true</tt>, if this gatherer acts as an actor
     */
    @Override
    boolean canUseAsActor() { return true; }

    /**
     * Sets the output value of actors.
     *
     * @param value the new output value
     *
     * @return <tt>true</tt>, if the hardware value successfully changed
     */
    @Override
    boolean set(Number value) {

        boolean result = false;

        synchronized (valueLock) {

            if (value != null) {

                ActorGathererConfiguration configuration = getConfiguration();

                if (Daemon.get().driver().set(configuration.getPortName(), value)) {
                    setCurrentValue(value);
                    result = true;
                }
                else {
                    setCurrentState(BufferState.FAULTED);
                }
            }

            return result;
        }
    }
}


