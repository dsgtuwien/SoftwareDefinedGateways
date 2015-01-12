package at.ac.tuwien.infosys.g2021.common;

/**
 * A Test-gatherer has no configuration features.
 */
public class TestGathererConfiguration implements GathererConfiguration {

    /**
     * Initialization.
     */
    public TestGathererConfiguration() {}

    /**
     * Every adapter configuration can return the kind of gatherer.
     *
     * @return the kind of gatherer
     */
    @Override
    public GathererClass kindOfGatherer() { return GathererClass.TEST; }
}

