package at.ac.tuwien.infosys.g2021.common;

/**
 * This is the base interface of all configurations, describing the implemented adapters.
 */
public interface AdapterConfiguration {

    /**
     * Every adapter configuration can return the kind of adapter.
     *
     * @return the kind of adapter
     */
    public AdapterClass kindOfAdapter();
}

