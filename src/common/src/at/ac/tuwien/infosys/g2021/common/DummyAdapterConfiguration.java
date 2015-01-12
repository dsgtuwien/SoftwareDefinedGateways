package at.ac.tuwien.infosys.g2021.common;

/**
 * A DUMMY-adapter has no configuration features.
 */
public class DummyAdapterConfiguration implements AdapterConfiguration {

    /**
     * Initialization.
     */
    public DummyAdapterConfiguration() {}

    /**
     * Every adapter configuration can return the kind of adapter.
     *
     * @return the kind of adapter
     */
    @Override
    public AdapterClass kindOfAdapter() { return AdapterClass.DUMMY; }
}

