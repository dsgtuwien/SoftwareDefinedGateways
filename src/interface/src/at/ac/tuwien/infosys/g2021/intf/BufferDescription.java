package at.ac.tuwien.infosys.g2021.intf;

import java.util.HashMap;
import java.util.Map;

/**
 * A buffer description shows the meta information assigned to a buffer.
 */
public class BufferDescription {

    // The data
    private String bufferName;
    private Map<String, String> metaData;

    /**
     * No instance creation without data!
     */
    private BufferDescription() {}

    /**
     * Initialization of the data container.
     *
     * @param n the buffer name
     * @param m the buffer meta data
     */
    BufferDescription(String n, Map<String, String> m) {

        if (n == null) throw new NullPointerException("buffer name is null");

        bufferName = n;
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
     * This method returns the name of the buffer, which this object belongs to.
     *
     * @return the name of the buffer. This name isn't <tt>null</tt>.
     */
    public String getBufferName() { return bufferName; }

    /**
     * This method returns the meta information assigned to a buffer. The result is a map. The keys are names of
     * topics assigned to the buffer. They refers to a detailed description of the buffer features.
     *
     * @return the meta information assigned to the buffer. The returned map may be empty, if there is no meta information
     * assigned to the buffer, but the result will never be <tt>null</tt>.
     */
    public Map<String, String> getBufferMetainfo() { return metaData; }
}



