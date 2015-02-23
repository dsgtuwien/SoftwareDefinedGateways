package at.ac.tuwien.infosys.g2021.intf;

import at.ac.tuwien.infosys.g2021.common.BufferConfiguration;
import at.ac.tuwien.infosys.g2021.common.BufferDescription;
import at.ac.tuwien.infosys.g2021.common.communication.ClientEndpoint;
import at.ac.tuwien.infosys.g2021.common.communication.ValueChangeObserver;
import at.ac.tuwien.infosys.g2021.common.util.Loggers;
import java.util.Set;
import java.util.logging.Logger;

/** The BufferManager is used to create, configure and delete buffers. */
public class BufferManager {

    // The logger
    private final static Logger logger = Loggers.getLogger(BufferManager.class);

    // Every buffer manager has an own id for logging reasons
    private int id;
    private static int nextId = 1;
    private final static Object idLock = new Object();

    /**
     * Due to information hiding issues, the functionality of a buffer manager isn't implemented in the class
     * <tt>{@link at.ac.tuwien.infosys.g2021.intf.BufferManager}</tt> itself. This inner class is the real implementation
     * of a buffer manager.
     * <p>
     * To prevent useless instances of this class - not regular released objects - there must not exist any
     * hard reference to this class except the only one in the <tt>{@link at.ac.tuwien.infosys.g2021.intf.BufferManager}</tt>
     * class. The <tt>{@link at.ac.tuwien.infosys.g2021.common.communication.ClientEndpoint}</tt> class
     * uses weak references only, to notify used data points about spontaneous value changes.
     */
    private class Implementation extends AbstractClientImplementation implements ValueChangeObserver {

        /**
         * Initializes a new instance of <tt>DataPoint</tt>. After initialization, there are no buffers
         * assigned.
         */
        Implementation() { super(); }

        /**
         * This method returns the configuration of a buffer.
         *
         * @param name the name of the buffer
         *
         * @return the configuration of this buffer. The result may be <tt>null</tt>, if no communication with the buffer
         *         daemon is possible.
         *
         * @throws java.lang.IllegalArgumentException
         *          if the buffer is unknown
         */
        BufferConfiguration get(String name) throws IllegalArgumentException {

            ClientEndpoint endpoint = getConnectedClientEndpoint();

            if (endpoint != null) return endpoint.getBufferConfiguration(name);
            else return null;
        }

        /**
         * This method makes an update of the buffer configuration.
         *
         * @param name          the name of the buffer
         * @param configuration the buffer configuration
         * @param createAllowed if the creation of a new buffer is allowed
         *
         * @return <tt>true</tt>, if the configuration was successfully changed in the daemon
         */
        boolean update(String name, BufferConfiguration configuration, boolean createAllowed) {

            ClientEndpoint endpoint = getConnectedClientEndpoint();

            if (endpoint != null) return endpoint.setBufferConfiguration(name, configuration, createAllowed);
            else return false;
        }

        /**
         * This method releases the buffer with the given name and removes it from the set of known buffers.
         *
         * @param name the name of the buffer
         *
         * @return <tt>true</tt>, if the configuration was successfully changed in the daemon
         *
         * @throws java.lang.IllegalArgumentException
         *          if the buffer is unknown
         */
        public boolean remove(String name) {

            ClientEndpoint endpoint = getConnectedClientEndpoint();

            if (endpoint != null) return endpoint.releaseBuffer(name);
            else return false;
        }
    }

    // The implementation of the data point
    private Implementation implementation;

    /** Initialization of the buffer manager. */
    public BufferManager() {

        // Evaluating the id
        synchronized (idLock) {
            id = nextId++;
        }

        // Initializing the implementation
        implementation = new Implementation();

        logger.info("The buffer manager #" + getId() + " has been created.");
    }

    /**
     * Returns the id of this connection.
     *
     * @return the id
     */
    private int getId() { return id; }

    /**
     * Called by the garbage collector on this object when garbage collection
     * determines that there are no more references to the object. All used
     * system resources are released.
     *
     * @throws Throwable the {@code Exception} raised by this method
     */
    @Override
    protected void finalize() throws Throwable {

        release();
        super.finalize();
    }

    /**
     * Releases all system resources. This method may be called more than once. If the buffer manager is already released,
     * the method call will have no effect.
     */
    public void release() {

        implementation.release();
        logger.info("The buffer manager #" + getId() + " has been released.");
    }

    /**
     * This method looks for available buffers.
     *
     * @param bufferName a regular expression specifying the buffer name. A simple match satisfy
     *                   the search condition, the regular expression must not match the whole buffer name.
     *
     * @return a collection of all the buffers matching this query
     */
    public Set<BufferDescription> queryBuffersByName(String bufferName) { return implementation.queryBuffersByName(bufferName); }

    /**
     * This method looks for available buffers.
     *
     * @param topic   a regular expression specifying the buffer topics, which should be scanned for the wanted features.
     *                These are keys of the buffer meta information. A simple match satisfy
     *                the search condition, the regular expression must not match the whole topic name.
     * @param feature a regular expression specifying the buffer features, which should be scanned. A simple match satisfy
     *                the search condition, the regular expression must not match the whole feature description.
     *
     * @return a collection of all the buffers matching this query
     */
    public Set<BufferDescription> queryBuffersByMetainfo(String topic, String feature) {

        return implementation.queryBuffersByMetainfo(topic, feature);
    }

    /**
     * This method looks for available buffers. This query scans all topics of the buffer for the requested feature.
     *
     * @param feature a regular expression specifying the buffer features, which should be scanned. A simple match satisfy
     *                the search condition, the regular expression must not match the whole feature description.
     *
     * @return a collection of all the buffers matching this query
     */
    public Set<BufferDescription> queryBuffersByMetainfo(String feature) { return queryBuffersByMetainfo(".*", feature); }

    /**
     * This method returns the configuration of a buffer.
     *
     * @param name the name of the buffer
     *
     * @return the configuration of this buffer. The result may be <tt>null</tt>, if no communication with the buffer
     *         daemon is possible.
     *
     * @throws java.lang.IllegalArgumentException
     *          if the buffer is unknown
     */
    public BufferConfiguration get(String name) throws IllegalArgumentException { return implementation.get(name); }

    /**
     * This method creates a new buffer with a given buffer configuration. If the buffer already exists, the
     * buffer configuration is updated.
     *
     * @param name          the name of the buffer
     * @param configuration the buffer configuration
     *
     * @return <tt>true</tt>, if the configuration was successfully changed in the daemon
     */
    public boolean create(String name, BufferConfiguration configuration) { return implementation.update(name, configuration, true); }

    /**
     * This method makes an update of the buffer configuration.
     *
     * @param name          the name of the buffer
     * @param configuration the buffer configuration
     *
     * @return <tt>true</tt>, if the configuration was successfully changed in the daemon
     */
    public boolean update(String name, BufferConfiguration configuration) {

        return implementation.update(name, configuration, false);
    }

    /**
     * This method releases the buffer with the given name and removes it from the set of known buffers.
     *
     * @param name the name of the buffer
     *
     * @return <tt>true</tt>, if the configuration was successfully changed in the daemon
     *
     * @throws java.lang.IllegalArgumentException
     *          if the buffer is unknown
     */
    public boolean release(String name) { return implementation.remove(name); }
}
