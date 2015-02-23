package at.ac.tuwien.infosys.g2021.common;

/** This is the base interface of all configurations, describing the implemented gatherers. */
public interface GathererConfiguration {

    /**
     * Every gatherer configuration can return the kind of gatherer.
     *
     * @return the kind of gatherer
     */
    public GathererClass kindOfGatherer();

    /**
     * Is this buffer class supported by the gatherer?
     *
     * @param bufferClass the buffer class
     *
     * @return <tt>true</tt>, if this kind of buffer is supported by the gatherer
     */
    public boolean isBufferClassSupported(BufferClass bufferClass);
}

