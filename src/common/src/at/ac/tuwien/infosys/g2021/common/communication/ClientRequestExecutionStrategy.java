package at.ac.tuwien.infosys.g2021.common.communication;

import at.ac.tuwien.infosys.g2021.common.BufferConfiguration;
import at.ac.tuwien.infosys.g2021.common.SimpleData;
import java.util.Set;

/**
 * This interface defines all the necessary methods to handle incoming requests
 * from the clients and their GBots in the daemon.
 */
public interface ClientRequestExecutionStrategy {

    /**
     * This method must handle a communication error at the client connection.
     *
     * @param conn the connection
     */
    public void connectionDied(DaemonEndpoint conn);

    /**
     * A shutdown request has been received.
     *
     * @param conn the connection
     */
    public void shutdown(DaemonEndpoint conn);

    /**
     * Exists a buffer with a given name?
     *
     * @param conn       the connection
     * @param bufferName the name of the buffer
     *
     * @return <tt>true</tt>, if a buffer with this name is known
     */
    public boolean bufferExists(DaemonEndpoint conn, String bufferName);

    /**
     * This method looks for available buffers.
     *
     * @param conn the connection
     * @param name a regular expression specifying the buffer name, which should be scanned. A simple match satisfy
     *             the search condition, the regular expression must not match the whole name.
     *
     * @return a collection of all the buffers matching this query
     */
    public Set<String> queryBuffersByName(DaemonEndpoint conn, String name);

    /**
     * This method looks for available buffers.
     *
     * @param conn    the connection
     * @param topic   a regular expression specifying the buffer topics, which should be scanned for the wanted features.
     *                These are keys of the buffer meta information. A simple match satisfy
     *                the search condition, the regular expression must not match the whole topic name.
     * @param feature a regular expression specifying the buffer features, which should be scanned. A simple match satisfy
     *                the search condition, the regular expression must not match the whole feature description.
     *
     * @return a collection of all the buffers matching this query
     */
    public Set<String> queryBuffersByMetainfo(DaemonEndpoint conn, String topic, String feature);

    /**
     * Returns the configuration of a buffer.
     *
     * @param conn       the connection
     * @param bufferName the name of the buffer
     *
     * @return the buffer configuration or <tt>null</tt>, is no buffer with this name is known
     */
    public BufferConfiguration bufferConfiguration(DaemonEndpoint conn, String bufferName);

    /**
     * Represents this buffer a hardware port? This kind of buffer cannot be updated or removed.
     *
     * @param conn       the connection
     * @param bufferName the name of the buffer
     *
     * @return <tt>true</tt>, if this buffer represents a hardware port
     */
    public boolean isHardwareBuffer(DaemonEndpoint conn, String bufferName);

    /**
     * Changes the configuration of a buffer. If the buffer doesn't exists and the
     * <tt>create</tt>-argument is set to <tt>true</tt>, a new buffer is created.
     *
     * @param conn       the connection
     * @param bufferName the name of the buffer
     * @param config     the buffer configuration
     * @param create     is buffer creation allowed
     *
     * @return the buffer configuration or <tt>null</tt>, is no buffer with this name is known
     */
    public boolean setBufferConfiguration(DaemonEndpoint conn, String bufferName, BufferConfiguration config, boolean create);

    /**
     * Returns the value of a buffer.
     *
     * @param conn       the connection
     * @param bufferName the name of the buffer
     *
     * @return the current value or <tt>null</tt>, is no buffer with this name is known
     */
    public SimpleData bufferValue(DaemonEndpoint conn, String bufferName);

    /**
     * Sets the value of a buffer.
     *
     * @param conn       the connection
     * @param bufferName the name of the buffer
     * @param value      the new buffer value
     *
     * @return the new current value of the buffer or <tt>null</tt>, is no buffer with this name is known
     */
    public SimpleData setBufferValue(DaemonEndpoint conn, String bufferName, double value);

    /**
     * Releases a buffer.
     *
     * @param conn       the connection
     * @param bufferName the name of the buffer
     *
     * @return <tt>true</tt>, if the buffer is now released
     */
    public boolean removeBuffer(DaemonEndpoint conn, String bufferName);
}


