package at.ac.tuwien.infosys.g2021.common;

/**
 * A DUMMY-gatherer has no configuration features.
 */
public class DummyGathererConfiguration implements GathererConfiguration {

    /**
     * Initialization.
     */
    public DummyGathererConfiguration() {}

    /**
     * Every adapter configuration can return the kind of gatherer.
     *
     * @return the kind of gatherer
     */
    @Override
    public GathererClass kindOfGatherer() { return GathererClass.DUMMY; }
}

