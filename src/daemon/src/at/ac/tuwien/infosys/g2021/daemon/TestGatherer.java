package at.ac.tuwien.infosys.g2021.daemon;

import at.ac.tuwien.infosys.g2021.common.BufferState;
import at.ac.tuwien.infosys.g2021.common.TestGathererConfiguration;
import java.util.TimerTask;

/**
 * This is a test gatherer. This gatherer increments the current value by 0.1 every second. If the current value exceeds 1.0,
 * the current value is decremented by 1.0.
 */
class TestGatherer extends Gatherer {

    /** This internal class makes the updates. */
    private class Updater extends TimerTask {

        /** The action to be performed by this timer task. */
        @Override
        public void run() { update(); }
    }

    // This is the current timer task
    private Updater updater;

    /**
     * Initializing a dummy gatherer.
     *
     * @param config the configuration
     */
    TestGatherer(TestGathererConfiguration config) {

        super(config);
        setCurrentValue(0.0);

        updater = new Updater();
        Daemon.get().timer().scheduleAtFixedRate(updater, 1000L, 1000L);
    }

    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object.
     */
    @Override
    public String toString() { return getClass().getSimpleName(); }

    /** Releases any system resources of the gatherer. */
    @Override
    void shutdown() {

        if (updater != null) {
            updater.cancel();
            updater = null;
        }

        super.shutdown();
    }

    /** A value update caused from the system timer. */
    private void update() {

        synchronized (valueLock) {

            if (get().getState() == BufferState.READY) {

                double value = get().getValue().doubleValue();

                if (value < 0.9999999999) setCurrentValue(value + 0.1);
                else setCurrentValue(value - 1.0);
            }
        }
    }

    /**
     * Can this gatherer can be used as actor?
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
}
