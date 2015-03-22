package at.ac.tuwien.infosys.g2021.daemon;

import at.ac.tuwien.infosys.g2021.common.BufferState;
import at.ac.tuwien.infosys.g2021.common.SimpleData;
import at.ac.tuwien.infosys.g2021.common.TriggeringAdapterConfiguration;

/**
 * A triggering adapter toggle between the two output values "lowerOutput" and "upperOutput". The upper and the lower
 * input threshold values ("lowerThreshold" and "upperThreshold") and the output values are configuration items.
 * <p>
 * If "upperThreshold" is lower than the "lowerThreshold", this adapter works as comparator. The input value is
 * compared against "lowerThreshold" and the configuration item "upperThreshold" is ignored.
 * <p>
 * "upperOutput" may be smaller than "lowerOutput". In this way an inverting trigger is configured.
 */
class TriggeringAdapter extends Adapter {

    /** Initialization
     * @param configuration The configuration
     */
    TriggeringAdapter(TriggeringAdapterConfiguration configuration) { super(configuration); }

    /**
     * This is the notification of a spontaneous value change. This adapter
     * routes the value change to all of its value change customers.
     *
     * @param newValue the new buffer value
     */
    @Override
    public void valueChanged(SimpleData newValue) {

        synchronized (valueLock) {

            SimpleData current = get();
            TriggeringAdapterConfiguration config = getConfiguration();

            // Adjusting the threshold values
            double lowerThreshold = config.getLowerThreshold();
            double upperThreshold = Math.max(lowerThreshold, config.getUpperThreshold());

            // Distributing a not ready state
            if (newValue.getState() != BufferState.READY) {
                set(newValue);
            }
            else {

                double in = newValue.getValue().doubleValue();

                // After the input is getting ready, a lower output value is assumed. Therefore
                // the upper output value is only set, if the input value is greater than the upper threshold.
                // In every cases an input value change is done.
                if (current.getState() != BufferState.READY) {
                    set(new SimpleData(newValue.getTimestamp(),
                                       BufferState.READY,
                                       in >= upperThreshold ? config.getUpperOutput() : config.getLowerOutput()));
                }

                // If the input is greater then the upper threshold, the output value is the upper value.
                // This may cause a value change sent to the consumers.
                else if (in >= upperThreshold) {
                    set(new SimpleData(newValue.getTimestamp(),
                                       BufferState.READY,
                                       config.getUpperOutput()));
                }

                // If the input is less then the lower threshold, the output value is the lower value.
                // This may cause a value change sent to the consumers.
                else if (in <= lowerThreshold) {
                    set(new SimpleData(newValue.getTimestamp(),
                                       BufferState.READY,
                                       config.getLowerOutput()));
                }

                // If the input value is between upper and lower threshold, no changes are communicated!
            }
        }
    }
}

