package at.ac.tuwien.infosys.g2021.common;

import java.util.Date;

/**
 * A simple data container, containing the current information about a single buffer. This may be the newest value
 * gathered from a sensor or the last value sent to an actor.
 */
public class SimpleData {

    // The data
    private String buffer;
    private Date timestamp;
    private BufferState state;
    private Number value;

    /** Instances without data make no sense. */
    public SimpleData() { this(null, null, null, null); }

    /**
     * Initialisation of the data container.
     *
     * @param t a timestamp
     * @param s the buffer state
     */
    public SimpleData(Date t, BufferState s) { this("???", t, s, null); }

    /**
     * Initialisation of the data container.
     *
     * @param b the buffer name
     * @param t a timestamp
     * @param s the buffer state
     */
    public SimpleData(String b, Date t, BufferState s) { this(b, t, s, null); }

    /**
     * Initialisation of the data container.
     *
     * @param t a timestamp
     * @param s the buffer state
     * @param v the buffer value
     */
    public SimpleData(Date t, BufferState s, Number v) { this("???", t, s, v); }

    /**
     * Initialisation of the data container.
     *
     * @param b the buffer name
     * @param d the buffer data as SimpleData object
     */
    public SimpleData(String b, SimpleData d) { this(b, d.timestamp, d.state, d.value); }

    /**
     * Initialisation of the data container.
     *
     * @param b the buffer name
     * @param t a timestamp
     * @param s the buffer state
     * @param v the buffer value
     */
    public SimpleData(String b, Date t, BufferState s, Number v) {

        if (v == null && s == BufferState.READY) throw new NullPointerException("buffer value is null");

        buffer = b;
        timestamp = t;
        state = s;
        value = v;
    }

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
            SimpleData other = (SimpleData)obj;

            return state == other.getState()
                   && (value == null && other.getValue() == null
                       || value != null && Math.abs(value.doubleValue() - other.getValue().doubleValue()) < 1.0e-8);
        }
        catch (ClassCastException | NullPointerException e) {
            // This exceptions mean, that the other object reference is null
            // or not an instance of this class.
            return false;
        }
    }

    /**
     * Returns a hash code value for the object.
     *
     * @return a hash code value for this object.
     */
    @Override
    public int hashCode() {

        return state.hashCode() ^ (value == null ? -42 : value.hashCode()) << 3;
    }

    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object.
     */
    @Override
    public String toString() {

        return String.format("%s: '%s': %s (%s)", super.toString(), buffer, value == null ? "null" : value.toString(), state.name());
    }

    /**
     * Is this a dummy value?
     *
     * @return <tt>true</tt>, if is is a dummy value
     */
    public boolean isDummy() { return buffer == null; }

    /**
     * This method returns the name of the buffer, which this object belongs to.
     *
     * @return the name of the buffer. This name isn't <tt>null</tt>.
     */
    public String getBufferName() { return buffer; }

    /**
     * This method Sets the name of the buffer, which this object belongs to.
     *
     * @param name the name of the buffer. This name isn't <tt>null</tt>.
     */
    public void setBufferName(String name) { buffer = name; }

    /**
     * This method returns state of the buffer.
     *
     * @return the buffer state (it isn't <tt>null</tt>)
     */
    public BufferState getState() { return state; }

    /**
     * This method returns the gathered value of the buffer.
     *
     * @return the gathered value. It is nonnull if the buffer state
     *         is <tt>{@link BufferState#READY}</tt> and
     *         <tt>null</tt> in any other cases.
     */
    public Number getValue() { return value; }

    /**
     * <p>
     * This method returns the date and time, when the buffer reached this state or gathered the value.
     * </p>
     * <p>
     * This timestamp is created in the buffer process, which doesn't run in this JVM. Therefore the timestamp may not
     * be meaningful, if the JVM of the buffer process runs on an other hardware as this JVM. The
     * content of the timestamp depends on the kind of buffer and the buffer state, but it is never
     * <tt>null</tt>.
     * </p>
     *
     * @return the timestamp as date and time (it isn't <tt>null</tt> in any case)
     */
    public Date getTimestamp() { return timestamp; }
}



