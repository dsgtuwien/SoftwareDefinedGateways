package at.ac.tuwien.infosys.g2021.common;

/**
 * A lowpass adapter acts as lowpass filter. LowpassAdapter instances will be triggered
 * by an internal 1 sec time ticker and will generate new output values every second. The
 * output value is estimated by a linear interpolation between the current input value and
 * the last output value. The interpolation factor acts as time constant. An interpolation factor
 * of 0.0 (this is the default) means, that the current input value is sent every second, an
 * interpolation factor of 1.0 means, that the output value will become the input value after
 * infinite time.
 *
 * @see #setInterpolationFactor(double)
 */
public class LowpassAdapterConfiguration implements AdapterConfiguration {

    // The configuration items
    private double interpolationFactor;

    /** Initialization with the default values. */
    public LowpassAdapterConfiguration() {}

    /**
     * Initialization with configuration items.
     *
     * @param interpolationFactor the interpolation factor
     *
     * @see #setInterpolationFactor(double)
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
     * Sets the interpolation factor. The interpolation factor acts as time constant. An
     * interpolation factor of 0.0 (this is the default) means, that the current input value
     * is sent every second, an interpolation factor of 1.0 means, that the output value
     * will become the input value after infinite time.
     * <p>Therefore a value update forced from the system timer will cause an output value y,
     * where
     * <pre>
     *      y = x * e^(-t/T)
     * </pre>
     * The following interpolation-factors correspond to the time-constants T in seconds
     * <pre>
     *  T   I-fac         T    I-fac         T    I-fac         T    I-fac         T    I-fac
     * ---  -----        ---   -----        ---   -----        ---   -----        ---   -----
     * 1,0  0,368        5,0   0,819        9,0   0,895       12,5   0,923       16,5   0,941
     * 1,5  0,513        5,5   0,834        9,5   0,900       13,0   0,926       17,0   0,943
     * 2,0  0,607        6,0   0,846       10,0   0,905       13,5   0,929       17,5   0,944
     * 2,5  0,670        6,5   0,857       10,5   0,909       14,0   0,931       18,0   0,946
     * 3,0  0,717        7,0   0,867       10,5   0,909       14,5   0,933       18,5   0,947
     * 3,5  0,751        7,5   0,875       11,0   0,913       15,0   0,936       19,0   0,949
     * 4,0  0,779        8,0   0,882       11,5   0,917       15,5   0,938       19,5   0,950
     * 4,5  0,801        8,5   0,889       12,0   0,920       16,0   0,939       20,0   0,951
     * </pre>
     *
     * @param interpolationFactor the new interpolation factor
     */
    public void setInterpolationFactor(double interpolationFactor) {

        this.interpolationFactor = Math.max(0.0, Math.min(interpolationFactor, 1.0));
    }
}


