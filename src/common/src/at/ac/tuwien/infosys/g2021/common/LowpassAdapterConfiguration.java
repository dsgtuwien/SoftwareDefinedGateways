package at.ac.tuwien.infosys.g2021.common;

/**
 * A lowpass adapter acts as lowpass filter. LowpassAdapter instances will be triggered
 * by an internal 1 sec time ticker and will generate new output values every second. The
 * output value is estimated by a linear interpolation between the current input value and
 * the last output value. The interpolation factor acts as time constant. An interpolation factor
 * of 0.0 (this is the default) means, that the current input value is sent every second, an
 * interpolation factor of 1.0 means, that the output value will become the input value after
 * infinite time.
 */
public class LowpassAdapterConfiguration implements AdapterConfiguration {

    // The configuration items
    private double interpolationFactor;

    /**
     * Initialization with the default values.
     */
    public LowpassAdapterConfiguration() {}

    /**
     * Initialization with configuration items.
     *
     * @param interpolationFactor the interpolation factor
     */
    public LowpassAdapterConfiguration(double interpolationFactor) {

        this();

        this.interpolationFactor = interpolationFactor;
    }

    /**
     * Every adapter configuration can return the kind of adapter.
     *
     * @return the kind of adapter
     */
    @Override
    public AdapterClass kindOfAdapter() { return AdapterClass.LOWPASS; }

    /**
     * Returns the interpolation factor.
     *
     * @return the interpolation factor
     */
    public double getInterpolationFactor() { return interpolationFactor; }

    /**
     * Sets the interpolation factor.
     *
     * @param interpolationFactor the new interpolation factor
     */
    public void setInterpolationFactor(double interpolationFactor) {

        this.interpolationFactor = Math.max(0.0, Math.min(interpolationFactor, 1.0));
    }
}


