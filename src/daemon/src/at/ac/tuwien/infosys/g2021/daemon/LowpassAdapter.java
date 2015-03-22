package at.ac.tuwien.infosys.g2021.daemon;

import at.ac.tuwien.infosys.g2021.common.BufferState;
import at.ac.tuwien.infosys.g2021.common.LowpassAdapterConfiguration;
import at.ac.tuwien.infosys.g2021.common.SimpleData;
import java.util.Date;
import java.util.TimerTask;

/**
 * A lowpass adapter acts as lowpass filter. LowpassAdapter instances will be triggered
 * by an internal 1 sec time ticker and will generate new output values every second. The
 * output value is estimated by a linear interpolation between the current input value and
 * the last output value. The interpolation factor acts as time constant. An interpolation factor
 * of 0.0 (this is the default) means, that the current input value is sent every second, an
 * interpolation factor of 1.0 means, that the output value will become the input value after
 * infinite time.
 */
class LowpassAdapter extends Adapter {

    /** This internal class makes the updates. */
    private class Updater extends TimerTask {

        /** The action to be performed by this timer task. */
        @Override
        public void run() { update(); }
    }

    // This is the current timer task
    private Updater updater;

    // The last value sent
    private double lastValueSent;

    // The current input value
    private SimpleData currentInputValue;

    /** Initialization
     * @param configuration The configuration
     */
    LowpassAdapter(LowpassAdapterConfiguration configuration) {

        super(configuration);

        lastValueSent = 0.0;
        currentInputValue = new SimpleData(new Date(), BufferState.INITIALIZING);

        updater = new Updater();
        Daemon.get().timer().scheduleAtFixedRate(updater, 1000L, 1000L);
    }

    /** Releases any system resources of the adapter. */
    @Override
    void shutdown() {

        if (updater != null) {
            updater.cancel();
            updater = null;
        }

        super.shutdown();
    }

    /** A value update forced from the system timer. */
    private void update() {

        synchronized (valueLock) {

            if (currentInputValue.getState() == BufferState.READY) {

                LowpassAdapterConfiguration config = getConfiguration();
                double currentValue = currentInputValue.getValue().doubleValue();

                lastValueSent = currentValue + config.getInterpolationFactor() * (lastValueSent - currentValue);

                set(new SimpleData(new Date(), BufferState.READY, lastValueSent));
            }
        }
    }

    /**
     * This is the notification of a spontaneous value change. This adapter
     * routes the value change to all of its value change customers.
     *
     * @param newValue the new buffer value
     */
    @Override
    public void valueChanged(SimpleData newValue) {

        synchronized (valueLock) {
            currentInputValue = newValue;
            if (currentInputValue.getState() != BufferState.READY) {
                set(currentInputValue);
            }
            else if (get().getState() != BufferState.READY) {
                lastValueSent = 0.0;
                set(new SimpleData(newValue.getTimestamp(), BufferState.READY, lastValueSent));
            }
        }
    }
}


