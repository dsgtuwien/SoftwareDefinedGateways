package at.ac.tuwien.infosys.g2021.common;

/** A ACTOR-gatherer works as actor for a hardware port and accepts any value. */
final public class ActorGathererConfiguration implements GathererConfiguration {

    // The port name.
    private String portName;

    /** Initialization. */
    private ActorGathererConfiguration() {}

    /**
     * Initialization.
     *
     * @param pn the name of the hardware port
     */
    public ActorGathererConfiguration(String pn) {

        this();

        if (pn == null) throw new NullPointerException("port name is null");
        portName = pn;
    }

    /**
     * Returns a hash code value for the object.
     *
     * @return a hash code value for this object.
     */
    @Override
    public int hashCode() { return portName.hashCode(); }

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

        try {
            ActorGathererConfiguration other = (ActorGathererConfiguration)obj;
            return portName.equals(other.portName);
        }
        catch (ClassCastException | NullPointerException e) {
            return false;
        }
    }

    /**
     * Every adapter configuration can return the kind of gatherer.
     *
     * @return the kind of gatherer
     */
    @Override
    public GathererClass kindOfGatherer() { return GathererClass.ACTOR; }

    /**
     * Is this buffer class supported by the gatherer?
     *
     * @param bufferClass the buffer class
     *
     * @return <tt>true</tt>, if this kind of buffer is supported by the gatherer
     */
    @Override
    public boolean isBufferClassSupported(BufferClass bufferClass) { return bufferClass == BufferClass.ACTOR; }

    /**
     * Returns the name of the hardware port.
     *
     * @return the port name
     */
    public String getPortName() { return portName; }
}

