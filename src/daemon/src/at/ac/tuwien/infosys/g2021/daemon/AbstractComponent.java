package at.ac.tuwien.infosys.g2021.daemon;

/**
 * This is a dummy implementation of the Component interface for convenience.
 */
class AbstractComponent implements Component {

    /**
     * There is nothing to initialize.
     */
    protected AbstractComponent() {}

    /**
     * This method is called, after creating all necessary instances.
     */
    @Override
    public void initialize() {}

    /**
     * This method is called, whenever a shutdown sequence is initiated.
     */
    @Override
    public void shutdown() {}

    /**
     * This method is called immediately after a shutdown, immediately before the process is stopped.
     */
    @Override
    public void release() {}
}
