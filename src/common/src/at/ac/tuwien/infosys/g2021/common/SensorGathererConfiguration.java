package at.ac.tuwien.infosys.g2021.common;

/** A SENSOR-gatherer works as sensor for a hardware port. */
final public class SensorGathererConfiguration implements GathererConfiguration {

    // The port name.
    private String portName;

    /** Initialization. */
    private SensorGathererConfiguration() {}

    /**
     * Initialization.
     *
     * @param pn the name of the hardware port
     */
    public SensorGathererConfiguration(String pn) {

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
            SensorGathererConfiguration other = (SensorGathererConfiguration)obj;
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
    public GathererClass kindOfGatherer() { return GathererClass.SENSOR; }

    /**
     * Is this buffer class supported by the gatherer?
     *
     * @param bufferClass the buffer class
     *
     * @return <tt>true</tt>, if this kind of buffer is supported by the gatherer
     */
    @Override
    public boolean isBufferClassSupported(BufferClass bufferClass) { return bufferClass == BufferClass.SENSOR; }

    /**
     * Returns the name of the hardware port.
     *
     * @return the port name
     */
    public String getPortName() { return portName; }
}

