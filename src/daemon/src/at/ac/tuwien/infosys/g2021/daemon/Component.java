package at.ac.tuwien.infosys.g2021.daemon;

/**
 * A component is a main part of the buffer daemon. It must implement the methods for initialization and shutdown.
 */
interface Component {

    /**
     * This method is called, after creating all necessary instances.
     */
    public void initialize();

    /**
     * This method is called, whenever a shutdown sequence is initiated.
     */
    public void shutdown();

    /**
     * This method is called immediately after a shutdown, immediately before the process is stopped.
     */
    public void release();
}


