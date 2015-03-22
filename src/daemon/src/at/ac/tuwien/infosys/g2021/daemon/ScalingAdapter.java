package at.ac.tuwien.infosys.g2021.daemon;

import at.ac.tuwien.infosys.g2021.common.BufferState;
import at.ac.tuwien.infosys.g2021.common.ScalingAdapterConfiguration;
import at.ac.tuwien.infosys.g2021.common.SimpleData;

/**
 * A scaling adapter calculates an output value y based on the current
 * input value x using the equation:
 * <pre>
 *     y = a * x**2 + b * x + c
 * </pre>
 * The factors a, b and c are found in the configuration.
 */
class ScalingAdapter extends Adapter {

    /** Initialization
     * @param configuration The configuration
     */
    ScalingAdapter(ScalingAdapterConfiguration configuration) { super(configuration); }

    /**
     * This is the notification of a spontaneous value change. This adapter
     * routes the value change to all of its value change customers.
     *
     * @param newValue the new buffer value
     */
    @Override
    public void valueChanged(SimpleData newValue) {

        synchronized (valueLock) {

            if (newValue.getState() != BufferState.READY) {
                set(newValue);
            }
            else {

                ScalingAdapterConfiguration config = getConfiguration();
                SimpleData current = get();
                double in = newValue.getValue().doubleValue();

                set(new SimpleData(newValue.getTimestamp(),
                                   BufferState.READY,
                                   in * in * config.getA() + in * config.getB() + config.getC()));
            }
        }
    }
}

