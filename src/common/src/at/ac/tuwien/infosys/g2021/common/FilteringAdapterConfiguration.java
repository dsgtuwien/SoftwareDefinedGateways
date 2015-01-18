package at.ac.tuwien.infosys.g2021.common;

/**
 * A filtering adapter suppresses marginal input value changes. The maximum difference between
 * the last sent output value and the current input value is a configuration item.
 */
public class FilteringAdapterConfiguration implements AdapterConfiguration {

    // The configuration items
    private double minimumDifference;

    /** Initialization with the default values. */
    public FilteringAdapterConfiguration() {}

    /**
     * Initialization with given configuration items.
     *
     * @param minimumDifference the minimum difference between the current input value and the last output value sent
     */
    public FilteringAdapterConfiguration(double minimumDifference) {

        this();

        this.minimumDifference = minimumDifference;
    }

    /**
     * Every adapter configuration can return the kind of adapter.
     *
     * @return the kind of adapter
     */
    @Override
    public AdapterClass kindOfAdapter() { return AdapterClass.FILTER; }

    /**
     * Returns the minimum difference between the current input value and the last output value sent.
     *
     * @return the minimum difference between the current input value and the last output value sent
     */
    public double getMinimumDifference() { return minimumDifference; }

    /**
     * Sets the minimum difference between the current input value and the last output value sent.
     *
     * @param minimumDifference the new minimum difference between the current input value and the last output value sent
     */
    public void setMinimumDifference(double minimumDifference) { this.minimumDifference = Math.abs(minimumDifference); }
}


