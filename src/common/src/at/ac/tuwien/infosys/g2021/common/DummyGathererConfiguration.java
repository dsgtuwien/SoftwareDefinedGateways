package at.ac.tuwien.infosys.g2021.common;

/** A DUMMY-gatherer works as actor and accepts any value. This gatherer has no configuration features. */
final public class DummyGathererConfiguration implements GathererConfiguration {

    /** Initialization. */
    public DummyGathererConfiguration() {}

    /**
     * Returns a hash code value for the object.
     *
     * @return a hash code value for this object.
     */
    @Override
    public int hashCode() { return 0; }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param obj the reference object with which to compare.
     *
     * @return {@code true} if this object is the same as the obj
     *         argument; {@code false} otherwise.
     */
    @Override
    public boolean equals(Object obj) {

        return obj != null && obj instanceof DummyGathererConfiguration;
    }

    /**
     * Every adapter configuration can return the kind of gatherer.
     *
     * @return the kind of gatherer
     */
    @Override
    public GathererClass kindOfGatherer() { return GathererClass.DUMMY; }

    /**
     * Is this buffer class supported by the gatherer?
     *
     * @param bufferClass the buffer class
     *
     * @return <tt>true</tt>, if this kind of buffer is supported by the gatherer
     */
    @Override
    public boolean isBufferClassSupported(BufferClass bufferClass) { return true; }
}

