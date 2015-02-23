package at.ac.tuwien.infosys.g2021.common;

import java.util.HashMap;
import java.util.Map;

/** A buffer description shows the meta information assigned to a buffer. */
public class BufferDescription {

    // The data
    private String bufferName;
    private boolean hardwareBuffer;
    private Map<String, String> metaData;

    /** No instance creation without data! */
    private BufferDescription() {}

    /**
     * Initialization of the data container.
     *
     * @param n  the buffer name
     * @param hw is this a hardware buffer
     * @param m  the buffer meta data
     */
    public BufferDescription(String n, boolean hw, Map<String, String> m) {

        this();

        if (n == null) throw new NullPointerException("buffer name is null");

        bufferName = n;
        hardwareBuffer = hw;
        metaData = m == null ? new HashMap<>() : m;
    }

    /**
     * Returns a hash code value for the object.
     *
     * @return a hash code value for this object.
     */
    @Override
    public int hashCode() {

        return bufferName.hashCode();
    }

    /**
     * Indicates whether some other object is "equal to" this one. Two <tt>BufferDescriptions</tt> are equal if they
     * belongs to the same buffer.
     *
     * @param obj the reference object with which to compare.
     *
     * @return <tt>true</tt> if this object is the same as the obj argument or <tt>false</tt> otherwise.
     */
    @Override
    public boolean equals(Object obj) {

        try {
            return bufferName.equals(((BufferDescription)obj).bufferName);
        }
        catch (NullPointerException | ClassCastException e) {
            return false;
        }
    }

    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object.
     */
    @Override
    public String toString() {

        return String.format("%s: '%s' (%s), %d metadata entries", super.toString(), bufferName, hardwareBuffer ? "HW" : "SW", metaData == null ? 0 : metaData.size());
    }

    /**
     * This method returns the name of the buffer, which this object belongs to.
     *
     * @return the name of the buffer. This name isn't <tt>null</tt>.
     */
    public String getBufferName() { return bufferName; }

    /**
     * Represents this buffer a hardware port? This kind of buffer cannot be updated or removed.
     *
     * @return <tt>true</tt>, if this buffer represents a hardware port
     */
    public boolean isHardwareBuffer() { return hardwareBuffer; }

    /**
     * This method returns the meta information assigned to a buffer. The result is a map. The keys are names of
     * topics assigned to the buffer. They refers to a detailed description of the buffer features.
     *
     * @return the meta information assigned to the buffer. The returned map may be empty, if there is no meta information
     *         assigned to the buffer, but the result will never be <tt>null</tt>.
     */
    public Map<String, String> getBufferMetainfo() { return metaData; }
}



