package at.ac.tuwien.infosys.g2021.common;

/**
 * This is the base interface of all configurations, describing the implemented adapters.
 */
public interface GathererConfiguration {

    /**
     * Every gatherer configuration can return the kind of gatherer.
     *
     * @return the kind of gatherer
     */
    public GathererClass kindOfGatherer();
}
