package at.ac.tuwien.infosys.g2021.common;

/**
 * A triggering adapter toggle between the two output values "lowerOutput" and "upperOutput". The upper and the lower
 * input threshold values ("lowerThreshold" and "upperThreshold") and the output values are configuration items.
 * <p>
 * If "upperThreshold" is lower than the "lowerThreshold", this adapter works as comparator. The input value is
 * compared against "lowerThreshold" and the configuration item "upperThreshold" is ignored.
 * <p>
 * "upperOutput" may be smaller than "lowerOutput". In this way an inverting trigger is configured.
 */
public class TriggeringAdapterConfiguration implements AdapterConfiguration {

    // The configuration items
    private double lowerThreshold;
    private double upperThreshold;
    private double lowerOutput;
    private double upperOutput;

    /** Initialization with the default values. */
    public TriggeringAdapterConfiguration() {}

    /**
     * Initialization with configuration items.
     *
     * @param lowerThreshold the lower threshold value
     * @param upperThreshold the upper threshold value
     * @param lowerOutput    the lower output value
     * @param upperOutput    the upper output value
     */
    public TriggeringAdapterConfiguration(double lowerThreshold, double upperThreshold, double lowerOutput, double upperOutput) {

        this();

        this.lowerThreshold = lowerThreshold;
        this.upperThreshold = upperThreshold;
        this.lowerOutput = lowerOutput;
        this.upperOutput = upperOutput;
    }

    /**
     * Every adapter configuration can return the kind of adapter.
     *
     * @return the kind of adapter
     */
    @Override
    public AdapterClass kindOfAdapter() { return AdapterClass.TRIGGER; }

    /**
     * Returns the lower threshold value.
     *
     * @return the lower threshold value
     */
    public double getLowerThreshold() { return lowerThreshold; }

    /**
     * Sets the lower threshold value.
     *
     * @param lowerThreshold the new lower threshold value
     */
    public void setLowerThreshold(double lowerThreshold) { this.lowerThreshold = lowerThreshold; }

    /**
     * Returns the upper threshold value.
     *
     * @return the upper threshold value
     */
    public double getUpperThreshold() { return upperThreshold; }

    /**
     * Sets the upper threshold value.
     *
     * @param upperThreshold the new upper threshold value
     */
    public void setUpperThreshold(double upperThreshold) { this.upperThreshold = upperThreshold; }

    /**
     * Returns the lower output value.
     *
     * @return the lower output value
     */
    public double getLowerOutput() { return lowerOutput; }

    /**
     * Sets the lower output value.
     *
     * @param lowerOutput the new lower output value
     */
    public void setLowerOutput(double lowerOutput) { this.lowerOutput = lowerOutput; }

    /**
     * Returns the upper output value.
     *
     * @return the upper output value
     */
    public double getUpperOutput() { return upperOutput; }

    /**
     * Sets the upper output value.
     *
     * @param upperOutput the new  upper output value
     */
    public void setUpperOutput(double upperOutput) { this.upperOutput = upperOutput; }
}


