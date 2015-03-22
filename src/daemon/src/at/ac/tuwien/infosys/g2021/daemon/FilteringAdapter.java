package at.ac.tuwien.infosys.g2021.daemon;

import at.ac.tuwien.infosys.g2021.common.BufferState;
import at.ac.tuwien.infosys.g2021.common.FilteringAdapterConfiguration;
import at.ac.tuwien.infosys.g2021.common.SimpleData;

/**
 * A filtering adapter suppresses marginal input value changes. The maximum difference between
 * the last sent output value and the current input value is a configuration item.
 */
class FilteringAdapter extends Adapter {

    /** Initialization
     * @param configuration The configuration
     */
    FilteringAdapter(FilteringAdapterConfiguration configuration) { super(configuration); }

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

            if (newValue.getState() != BufferState.READY || current.getState() != BufferState.READY) {
                set(newValue);
            }
            else {
                FilteringAdapterConfiguration config = getConfiguration();
                double delta = Math.abs(newValue.getValue().doubleValue() - current.getValue().doubleValue());

                if (delta >= config.getMinimumDifference()) set(newValue);
            }
        }
    }
}


