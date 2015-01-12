package at.ac.tuwien.infosys.g2021.intf;

import at.ac.tuwien.infosys.g2021.common.SimpleData;
import at.ac.tuwien.infosys.g2021.common.communication.ClientEndpoint;
import at.ac.tuwien.infosys.g2021.common.communication.ValueChangeObserver;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * This is an abstract implementation of classes communicating with a client endpoint. It manages the connection with the
 * client endpoint and implements the buffer queries.
 */
abstract class AbstractClientImplementation implements ValueChangeObserver {

    // The communication object to the daemon
    private ClientEndpoint endpoint;
    private final Object endpointLock;

    /**
     * Initializes a new instance of <tt>DataPoint</tt>. After initialization, there are no buffers
     * assigned.
     */
    protected AbstractClientImplementation() {

        endpointLock = new Object();
        assignClientEndpoint();
    }

    /**
     * Get a thread-safe snapshot of the client endpoint.
     *
     * @return the current client endpoint
     */
    protected ClientEndpoint getClientEndpoint() {

        synchronized (endpointLock) {
            assignClientEndpoint();
            return endpoint;
        }
    }

    /**
     * Opens a connection to the daemon und registers this data point.
     */
    protected ClientEndpoint assignClientEndpoint() {

        ClientEndpoint connected = null;

        synchronized (endpointLock) {
            if (endpoint == null) {
                endpoint = ClientEndpoint.get();
                try {
                    if (!endpoint.isConnected()) {
                        endpoint.connect();
                        endpoint.addValueChangeObserver(this);
                        connected = endpoint;
                    }
                }
                catch (IOException e) {
                    releaseClientEndpoint();
                }
            }
        }

        return connected;
    }

    /**
     * Release the client endpoint.
     */
    protected void releaseClientEndpoint() {

        synchronized (endpointLock) {
            if (endpoint != null) {
                endpoint.removeValueChangeObserver(this);
                endpoint = null;
            }
        }
    }

    /**
     * Closes the connection to the client endpoint.
     */
    void release() {

        releaseClientEndpoint();
    }

    /**
     * This method looks for available buffers.
     *
     * @param bufferName a regular expression specifying the buffer name, which should be scanned. A simple match satisfy
     *                   the search condition, the regular expression must not match the whole feature description.
     *
     * @return a collection of all the buffers matching this query
     */
    Set<BufferDescription> queryBuffersByName(String bufferName) {

        Set<BufferDescription> result = new HashSet<>();
        ClientEndpoint endpoint = getClientEndpoint();

        if (endpoint != null) {

            Set<String> names = endpoint.queryBuffersByName(bufferName);

            for (String name : names) {
                try {
                    result.add(getBufferDescription(name));
                }
                catch (IllegalArgumentException e) {
                    // This buffer may be removed by the buffer manager. Therefore the exception is ignored.
                }
            }
        }

        return result;
    }

    /**
     * This method looks for available buffers.
     *
     * @param topic   a regular expression specifying the buffer topics, which should be scanned for the wanted features. A simple match satisfy
     *                the search condition, the regular expression must not match the whole topic name.
     * @param feature a regular expression specifying the buffer features, which should be scanned. A simple match satisfy
     *                the search condition, the regular expression must not match the whole feature description.
     *
     * @return a collection of all the buffers matching this query
     */
    Set<BufferDescription> queryBuffersByMetainfo(String topic, String feature) {

        Set<BufferDescription> result = new HashSet<>();
        ClientEndpoint endpoint = getClientEndpoint();

        if (endpoint != null) {

            Set<String> names = endpoint.queryBuffersByMetainfo(topic, feature);

            for (String name : names) {
                try {
                    result.add(getBufferDescription(name));
                }
                catch (IllegalArgumentException e) {
                    // This buffer may be removed by the buffer manager. Therefore the exception is ignored.
                }
            }
        }

        return result;
    }

    /**
     * This method returns the meta information of a buffer.
     *
     * @param name the name of a buffer
     *
     * @return the buffer meta information, which is never <tt>null</tt>
     *
     * @throws IllegalArgumentException if there exists no buffer with the given buffer name
     */
    BufferDescription getBufferDescription(String name) throws IllegalArgumentException {

        ClientEndpoint endpoint = getClientEndpoint();

        if (endpoint != null) {

            Map<String, String> metaData = endpoint.queryMetainfo(name);
            if (metaData != null) return new BufferDescription(name, new TreeMap<>(metaData));
        }

        throw new IllegalArgumentException("unknown buffer '" + name + "'");
    }

    /**
     * This is the notification of a spontaneous value change.
     *
     * @param newValue the new buffer value
     */
    @Override
    public void valueChanged(SimpleData newValue) {}

    /**
     * This is the notification about the lost connection to the daemon.
     */
    @Override
    public void communicationLost() { releaseClientEndpoint(); }
}
