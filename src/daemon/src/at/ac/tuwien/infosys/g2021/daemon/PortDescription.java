package at.ac.tuwien.infosys.g2021.daemon;

import java.util.HashMap;
import java.util.Map;

/** The description of a hardware port. */
public class PortDescription {

    // The data
    private String portName;
    private PortClass portClass;
    private Map<String, String> metaData;

    /** No instance creation without data! */
    private PortDescription() {}

    /**
     * Initialization of the data container.
     *
     * @param n the port name
     * @param c the port class
     * @param m the port meta data
     */
    public PortDescription(String n, PortClass c, Map<String, String> m) {

        this();

        if (n == null) throw new NullPointerException("port name is null");

        portName = n;
        portClass = c;
        metaData = m == null ? new HashMap<>() : m;
    }

    /**
     * Returns a hash code value for the object.
     *
     * @return a hash code value for this object.
     */
    @Override
    public int hashCode() {

        return portName.hashCode();
    }

    /**
     * Indicates whether some other object is "equal to" this one. Two <tt>PortDescriptions</tt> are equal if they
     * belongs to the same port.
     *
     * @param obj the reference object with which to compare.
     *
     * @return <tt>true</tt> if this object is the same as the obj argument or <tt>false</tt> otherwise.
     */
    @Override
    public boolean equals(Object obj) {

        try {
            return portName.equals(((PortDescription)obj).portName);
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

        return String.format("%s: '%s', %d metadata entries", super.toString(), portName, metaData == null ? 0 : metaData.size());
    }

    /**
     * This method returns the name of the port, which this object belongs to.
     *
     * @return the name of the port. This name isn't <tt>null</tt>.
     */
    public String getPortName() { return portName; }

    /**
     * This method returns the kind of port, as described in the enumeration <tt>{@link PortClass}</tt>.
     *
     * @return the kind of port
     */
    public PortClass getPortClass() { return portClass; }

    /**
     * This method returns the meta information assigned to a port. The result is a map. The keys are names of
     * topics assigned to the port. They refers to a detailed description of the port features.
     *
     * @return the meta information assigned to the port. The returned map may be empty, if there is no meta information
     *         assigned to the port, but the result will never be <tt>null</tt>.
     */
    public Map<String, String> getPortMetainfo() { return metaData; }
}



