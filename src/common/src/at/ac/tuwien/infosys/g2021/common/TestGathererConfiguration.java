package at.ac.tuwien.infosys.g2021.common;

/**
 * This gatherer increments the current value by 0.1 every second. If the current value exceeds 1.0,
 * the current value is then decremented by 1.0. This gatherer has no configuration features.
 */
final public class TestGathererConfiguration implements GathererConfiguration {

    /** Initialization. */
    public TestGathererConfiguration() {}

    /**
     * Returns a hash code value for the object.
     *
     * @return a hash code value for this object.
     */
    @Override
    public int hashCode() { return 1; }

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

        return obj != null && obj instanceof TestGathererConfiguration;
    }

    /**
     * Every adapter configuration can return the kind of gatherer.
     *
     * @return the kind of gatherer
     */
    @Override
    public GathererClass kindOfGatherer() { return GathererClass.TEST; }

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

