package at.ac.tuwien.infosys.g2021.intf;

import at.ac.tuwien.infosys.g2021.common.BufferDescription;
import at.ac.tuwien.infosys.g2021.common.BufferState;
import at.ac.tuwien.infosys.g2021.common.SimpleData;
import at.ac.tuwien.infosys.g2021.common.communication.ClientEndpoint;
import at.ac.tuwien.infosys.g2021.common.communication.ValueChangeObserver;
import at.ac.tuwien.infosys.g2021.common.util.Loggers;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.WeakHashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * <p>
 * This is the basic interface to actor and sensor data, which can be used by GBots. It contains all the
 * methods for data point configuration and an observer for signalling data and state changes.
 * </p>
 * <p>
 * This class ist thread-safe. That means that concurrent access to the methods of this class by multiple threads
 * cannot corrupt the own internal data structures. Data and state changes are signalled by the
 * <tt>{@link DataPointObserver}</tt> in an own thread, so be careful.
 * </p>
 * <p>
 * The following example shows, how <tt>DataPoints</tt> can be used:
 * </p>
 * <pre>
 *
 *    // At first we need a DataPoint instance.
 *    DataPoint dataPoint = new DataPoint();
 *
 *    // Now we can query available buffers
 *    Collection&lt;BufferDescription&gt; availableBuffers = dataPoint.queryBuffersByMetainfo("^oufen$", "(keen)|(nischt)");
 *
 *    // Wow, now we can select the best matching buffers.
 *    ...
 *
 *    // And then we assign the buffers to the data point
 *    dataPoint.assign(myFavoriteBuffer);
 *    dataPoint.assign(anotherPrettyBuffer);
 *    ...
 *
 *    // A short time later, the state should be BufferState.READY and we can work with buffers.
 *    Map&lt;String, SimpleData&gt; allData = dataPoint.getAll();
 *    ...
 *    dataPoint.addObserver(myDataPointObserver);
 *    ...
 *
 *    // All work is done. Now we detach the assigned buffers.
 *    dataPoint.detach(myFavoriteBuffer);
 *    dataPoint.detach(anotherPrettyBuffer);
 *    ...
 *
 *    // Finally we should release the system resources used by the data point.
 *    dataPoint.release();
 *
 * </pre>
 */
public class DataPoint {

    // Every data point has an id for logging reasons
    private int id;
    private static int nextId = 1;
    private final static Object idLock = new Object();

    // The logger
    private final static Logger logger = Loggers.getLogger(DataPoint.class);

    /**
     * Due to information hiding issues, the functionality of a data point isn't implemented in the class
     * <tt>{@link at.ac.tuwien.infosys.g2021.intf.DataPoint}</tt> itself. This inner class is the real implementation
     * of data points and implements all important methods of data points and all methods for receiving
     * value changes from the client endpoint too
     * (interface <tt>{@link at.ac.tuwien.infosys.g2021.common.communication.ValueChangeObserver}</tt>).
     * <p>
     * To prevent useless instances of this class - not regular released objects - there must not exist any
     * hard reference to this class except the only one in the <tt>{@link at.ac.tuwien.infosys.g2021.intf.DataPoint}</tt>
     * class. The <tt>{@link at.ac.tuwien.infosys.g2021.common.communication.ClientEndpoint}</tt> class
     * uses weak references only, to notify used data points about spontaneous value changes.
     */
    private class Implementation extends AbstractClientImplementation implements ValueChangeObserver {

        // The set of assigned buffers and their values.
        private Map<String, SimpleData> buffers;
        private final Object bufferLock;

        // The list of observers.
        private List<DataPointObserver> observers;

        // All the existing streams
        private WeakHashMap<BlockingQueue<SimpleData>, Object> streams;

        // The state of this data point
        private BufferState state;

        /**
         * Initializes a new instance of <tt>DataPoint</tt>. After initialization, there are no buffers
         * assigned.
         */
        Implementation() {

            super();

            bufferLock = new Object();
            buffers = new HashMap<>();
            observers = new CopyOnWriteArrayList<>();
            streams = new WeakHashMap<>();
            state = ClientEndpoint.get().isConnected() ? BufferState.INITIALIZING : BufferState.ISOLATED;
            callbacks = Collections.synchronizedMap(new HashMap<>());
        }

        /** Opens a connection to the daemon und registers this data point. */
        @Override
        protected ClientEndpoint assignClientEndpoint() {

            ClientEndpoint connected = super.assignClientEndpoint();

            // After reinstallation of a connection, all known buffers must be updated
            if (connected != null) {
                if (buffers != null) {

                    Collection<String> allBufferNames = new ArrayList<>();

                    synchronized (bufferLock) {
                        allBufferNames.addAll(buffers.keySet());
                    }

                    for (String bufferName : allBufferNames) {
                        try {
                            valueChanged(connected.getImmediate(bufferName));
                        }
                        catch (IllegalArgumentException e) {

                            // This buffer has been removed!
                            valueChanged(new SimpleData(bufferName, new Date(), BufferState.RELEASED));
                        }
                    }

                    updateState();
                }
            }

            return connected;
        }

        /** Release the client endpoint. */
        @Override
        protected void releaseClientEndpoint() {

            super.releaseClientEndpoint();

            // Here, there is no connection to the daemon. All of the buffers are in a faulted state!
            // This must not be done, if there is no daemon connection in the initialization phase.
            if (buffers != null) {
                synchronized (bufferLock) {

                    Collection<SimpleData> allBufferStates = buffers.values();
                    Date now = new Date();

                    for (SimpleData oldState : allBufferStates) valueChanged(new SimpleData(oldState.getBufferName(), now, BufferState.FAULTED));
                }
            }
        }

        /**
         * The overall state of the data point has changed.
         *
         * @param oldOne the state left
         * @param newOne the current state of the data point
         */
        private void fireStateChanged(BufferState oldOne, BufferState newOne) {

            logger.info(String.format("The data point #%d changed its state from '%s' to '%s'.", getId(), oldOne.name(), newOne.name()));
            for (DataPointObserver observer : observers) observer.dataPointStateChanged(DataPoint.this, oldOne, newOne);
        }

        /**
         * A buffer has been assigned to the data point.
         *
         * @param bufferName the name of the assigned buffer
         */
        private void fireBufferAssigned(String bufferName) {

            for (DataPointObserver observer : observers) observer.bufferAssigned(DataPoint.this, bufferName);
            logger.info(String.format("The buffer '%s' has been assigned to the data point #%d.", bufferName, getId()));
        }

        /**
         * A buffer has been detached from the data point.
         *
         * @param bufferName the name of the detached buffer
         */
        private void fireBufferDetached(String bufferName) {

            for (DataPointObserver observer : observers) observer.bufferDetached(DataPoint.this, bufferName);
            logger.info(String.format("The buffer '%s' has been detached from the data point #%d.", bufferName, getId()));
        }

        /**
         * A buffer has changed its state or value.
         *
         * @param oldOne the outdated buffer data. This argument may
         *               be <tt>null</tt>, if the buffer just has been assigned.
         * @param newOne the current buffer dataThis argument may
         *               be <tt>null</tt>, if the buffer just has been detached.
         */
        private void fireBufferChanged(SimpleData oldOne, SimpleData newOne) {

            // We don't communicate about dummy values.
            if (oldOne != null && oldOne.isDummy()) oldOne = null;
            if (newOne != null && newOne.isDummy()) newOne = null;
            if (newOne == oldOne) return;

            for (DataPointObserver observer : observers) observer.bufferChanged(DataPoint.this, oldOne, newOne);
        }

        /**
         * Detaches all assigned buffers and sets the state of this data point to <tt>{@link at.ac.tuwien.infosys.g2021.common.BufferState#RELEASED}</tt>.
         * This method may be called more than once. If the state of this data point is <tt>{@link at.ac.tuwien.infosys.g2021.common.BufferState#RELEASED}</tt>,
         * the method call will have no effect.
         */
        @Override
        void release() {

            synchronized (bufferLock) {

                // Detaching all buffers.
                for (String name : new HashSet<>(buffers.keySet())) detach(name);
                buffers.clear();

                // No more value changes are sent to streams
                for (BlockingQueue<SimpleData> queue : streams.keySet()) {
                    try {
                        queue.put(new SimpleData());
                    }
                    catch (InterruptedException e) {
                        // We are shutting down and we delegate the interrupt flag to the caller thread.
                        Thread.currentThread().interrupt();
                    }
                }
                streams.clear();
            }

            // Now the final state change is done
            setState(BufferState.RELEASED);

            // No more notification will be sent
            observers.clear();
            callbacks.values().forEach(Task::stop);
            callbacks.clear();

            super.release();
        }

        /**
         * Evaluates the overall state of this data point.
         *
         * @return the current state of the data point, which is never <tt>null</tt>.
         */
        private BufferState evaluateState() {

            // A released buffer is in RELEASED state.
            if (getState() == BufferState.RELEASED) return BufferState.RELEASED;

            // If there is no connection to the daemon, the state is ISOLATED!
            if (!ClientEndpoint.get().isConnected()) return BufferState.ISOLATED;

            // Evaluating the states of all assigned buffers
            boolean faulted = false;
            boolean initializing = false;

            synchronized (bufferLock) {

                // No buffers assigned
                if (buffers.size() == 0) return BufferState.INITIALIZING;

                for (SimpleData data : buffers.values()) {
                    switch (data.getState()) {
                        case FAULTED:
                        case RELEASED:
                            faulted = true;
                            break;

                        case INITIALIZING:
                            initializing = true;
                            break;
                    }
                }
            }

            // Is any buffer not ready?
            if (faulted) return BufferState.FAULTED;

            // Is any buffer initializing?
            if (initializing) return BufferState.INITIALIZING;

            // Ok, we are ready
            return BufferState.READY;
        }

        /**
         * Sets the overall state of this data point.
         *
         * @param newState the state of the data point, which must not be <tt>null</tt>.
         */
        private void setState(BufferState newState) {

            if (newState == null) throw new NullPointerException("new state is null");

            if (state != newState) {

                BufferState oldState = state;

                state = newState;
                fireStateChanged(oldState, newState);
            }
        }

        /** Sets the overall state of this data point. */
        private void updateState() { setState(evaluateState()); }

        /**
         * Gets the overall state of this data point. The state of the data point is derived from the states
         * of the assigned buffers in such way:
         * <ul>
         * <li>After calling the method <tt>{@link #release()}</tt>, the state is <tt>{@link at.ac.tuwien.infosys.g2021.common.BufferState#RELEASED}</tt>.</li>
         * <li>If the buffer daemon isn't reachable, the state is <tt>{@link at.ac.tuwien.infosys.g2021.common.BufferState#ISOLATED}</tt>.</li>
         * <li>If no buffer is assigned, the state is <tt>{@link at.ac.tuwien.infosys.g2021.common.BufferState#INITIALIZING}</tt>.</li>
         * <li>If an assigned buffer is in <tt>{@link at.ac.tuwien.infosys.g2021.common.BufferState#FAULTED}</tt> state, the state is <tt>{@link at.ac.tuwien.infosys.g2021.common.BufferState#FAULTED}</tt>.</li>
         * <li>If an assigned buffer is in <tt>{@link at.ac.tuwien.infosys.g2021.common.BufferState#RELEASED}</tt> state, the state is <tt>{@link at.ac.tuwien.infosys.g2021.common.BufferState#FAULTED}</tt>.</li>
         * <li>If an assigned buffer is in <tt>{@link at.ac.tuwien.infosys.g2021.common.BufferState#INITIALIZING}</tt> state, the state is <tt>{@link at.ac.tuwien.infosys.g2021.common.BufferState#INITIALIZING}</tt>.</li>
         * <li>Otherwise the state is <tt>{@link at.ac.tuwien.infosys.g2021.common.BufferState#READY}</tt>.</li>
         * </ul>
         *
         * @return the state of the data point, which is never <tt>null</tt>.
         */
        BufferState getState() { return state; }

        /**
         * Assigns a buffer for read or write access to this data point. This method will have no effect, if the buffer is currently
         * assigned to this data point.
         *
         * @param bufferName the name of the buffer
         *
         * @throws IllegalArgumentException if there exists no buffer with the given buffer name
         */
        void assign(String bufferName) throws IllegalArgumentException {

            ClientEndpoint endpoint = getConnectedClientEndpoint();
            boolean initialized;

            // Daemon not reachable -> there is no buffer to assign to
            if (endpoint == null) {
                throw new IllegalArgumentException("unknown buffer '" + bufferName + "'");
            }
            else {

                // At first we try insert the buffer into the set of wellknown buffers.
                synchronized (bufferLock) {
                    initialized = buffers.putIfAbsent(bufferName, new SimpleData()) == null;
                }

                // If the buffer was unknown, we must evaluate the buffer value
                if (initialized) {
                    try {
                        SimpleData initialValue = endpoint.getImmediate(bufferName);

                        // Ok, the buffer is assigned now
                        fireBufferAssigned(bufferName);
                        valueChanged(initialValue);
                        updateState();
                    }
                    catch (IllegalArgumentException e) {

                        // In the meantime, the buffer was removed with the buffer manager. We remove it now.
                        synchronized (bufferLock) {
                            buffers.remove(bufferName);
                        }

                        throw new IllegalArgumentException("unknown buffer '" + bufferName + "'");
                    }
                }
            }
        }

        /**
         * Read the names of all buffers assigned to this data point.
         *
         * @return a set of buffer names which may be empty, if there are no buffers assigned to this data point.
         *
         * @see #assign(String)
         * @see #detach(String)
         */
        Set<String> getAssignedBufferNames() {

            synchronized (bufferLock) {
                return new TreeSet<>(buffers.keySet());
            }
        }

        /**
         * Deassigns a buffer from this data point. This method will have no effect, if the buffer is currently
         * not assigned to this data point.
         *
         * @param bufferName the name of the buffer
         */
        void detach(String bufferName) {

            synchronized (bufferLock) {
                if (buffers.get(bufferName) != null) {
                    valueChanged(new SimpleData(bufferName, new Date(), BufferState.RELEASED));
                    buffers.remove(bufferName);
                    fireBufferDetached(bufferName);
                    updateState();
                }
            }
        }

        /**
         * Get the current information about all the buffers attached to this data point.
         *
         * @return a map with the names of all currently attached buffers as key and their values as data. The result may be
         *         empty, but is never <tt>null</tt>.
         */
        Map<String, SimpleData> getAll() {

            synchronized (bufferLock) {
                return new TreeMap<>(buffers);
            }
        }

        /**
         * Sets the value to an actor assigned to this data point.
         *
         * @param bufferName the name of the actor buffer
         * @param value      the new value
         *
         * @throws java.lang.IllegalArgumentException
         *          if the buffer isn't assigned to this data point or is not changable (e.g. an actor)
         */
        void set(String bufferName, Number value) throws IllegalArgumentException {

            ClientEndpoint endpoint = getConnectedClientEndpoint();
            SimpleData currentValue;

            synchronized (bufferLock) {
                currentValue = buffers.get(bufferName);
            }

            if (value == null) {
                throw new NullPointerException("buffer value is null");
            }
            else if (currentValue == null) {
                throw new IllegalArgumentException("unknown buffer '" + bufferName + "'");
            }
            else if (endpoint == null) {
                throw new IllegalStateException("buffer '" + bufferName + "' is not reachable.");
            }
            else {
                currentValue = endpoint.set(bufferName, value);
                valueChanged(currentValue);
                updateState();
                if (currentValue.getState() == BufferState.READY) {
                    logger.info(String.format("The value of buffer '%s' is set to %.3f from data point #%d.", bufferName, value.doubleValue(), getId()));
                }
                else {
                    logger.info(String.format("The state of buffer '%s' from data point #%d is now '%s'.", bufferName, getId(), currentValue.getState().name()));
                }
            }
        }

        /**
         * <p>
         * Adds a new data point observer to the set of well known observers. If the observer argument is <tt>null</tt> or the
         * observer argument is currently registered in this object, the method call will have no effect. Note that the registered
         * observers are a set. Multiple registration will not cause multiple notifications about state or value changes.
         * </p>
         * <p>
         * The observer instance will be notified about changes in a separate thread!
         * </p>
         *
         * @param observer the observer
         */
        void addDataPointObserver(DataPointObserver observer) {

            removeDataPointObserver(observer);
            observers.add(observer);
        }

        /**
         * <p>
         * Removes a data point observer from the set of well known observers. If the observer argument is <tt>null</tt> or the
         * observer argument is currently not registered in this object, the method call will have no effect.
         * </p>
         *
         * @param observer the observer
         */
        void removeDataPointObserver(DataPointObserver observer) {

            observers.remove(observer);
        }

        /** This is the notification about the lost connection to the daemon. */
        @Override
        public void communicationLost() {

            super.communicationLost();
            if (state != BufferState.RELEASED) setState(BufferState.ISOLATED);
        }

        /**
         * This is the notification of a spontaneous value change.
         *
         * @param newValue the new buffer value
         */
        @Override
        public void valueChanged(SimpleData newValue) {

            SimpleData oldValue = null;

            // Register the value change in the map of buffers
            synchronized (bufferLock) {
                if (buffers.containsKey(newValue.getBufferName())) {
                    oldValue = buffers.get(newValue.getBufferName());
                    buffers.put(newValue.getBufferName(), newValue);
                }
            }

            // Distribute the value change
            if (oldValue != null) {

                // Is the buffer changed?
                if (!oldValue.equals(newValue)) {

                    // Queue the new value for the listening streams
                    for (BlockingQueue<SimpleData> queue : streams.keySet()) {
                        try {
                            queue.put(newValue);
                        }
                        catch (InterruptedException e) {
                            // This exception must not be thrown, because this blocking queue has no limit.
                            // We just set the interrupted flag of the calling thread.
                            Thread.currentThread().interrupt();
                        }
                    }

                    // Notify the listeners
                    fireBufferChanged(oldValue, newValue);
                }

                // Order the next change
                ClientEndpoint endpoint = getClientEndpoint();
                if (endpoint != null && endpoint.isConnected() && newValue.getState() != BufferState.RELEASED) {
                    endpoint.getOnChange(newValue.getBufferName());
                }
            }
        }

        /**
         * Returns a stream of all buffer value changes of this data point.
         *
         * @return a stream of value changes
         */
        Stream<SimpleData> getStream() {

            // This must be an atomic operation with respect to the buffer values.
            synchronized (bufferLock) {

                BlockingQueue<SimpleData> queue = new ValueQueue();

                // Put all current buffer values into the queue
                for (SimpleData value : buffers.values()) {
                    try {
                        queue.put(value);
                    }
                    catch (InterruptedException e) {
                        // This exception must not be thrown, because this blocking queue has no limit.
                        // We just set the interrupted flag of the calling thread.
                        Thread.currentThread().interrupt();
                    }
                }

                // Register this queue for further value changes
                streams.put(queue, new Object());
                return queue.stream();
            }
        }
    }

    // The implementation of the data point
    private Implementation implementation;

    /**
     * Initializes a new instance of <tt>DataPoint</tt>. After initialization, there are no buffers
     * assigned.
     */
    public DataPoint() {

        // Evaluating the id
        synchronized (idLock) {
            id = nextId++;
        }

        // Initializing the implementation
        implementation = new Implementation();

        logger.info("The data point #" + getId() + " has been created.");
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
     * Detaches all assigned buffers and sets the state of this data point to <tt>{@link BufferState#RELEASED}</tt>.
     * This method may be called more than once. If the state of this data point is <tt>{@link BufferState#RELEASED}</tt>,
     * the method call will have no effect.
     */
    public void release() {

        implementation.release();
        logger.info("The data point #" + getId() + " has been released.");
    }

    /**
     * Gets the overall state of this data point. The state of the data point is derived from the states
     * of the assigned buffers in such way:
     * <ul>
     * <li>After calling the method <tt>{@link #release()}</tt>, the state is <tt>{@link at.ac.tuwien.infosys.g2021.common.BufferState#RELEASED}</tt>.</li>
     * <li>If the connection to the buffer daemon is broken, the state is <tt>{@link at.ac.tuwien.infosys.g2021.common.BufferState#ISOLATED}</tt>.</li>
     * <li>If no buffer is assigned, the state is <tt>{@link at.ac.tuwien.infosys.g2021.common.BufferState#INITIALIZING}</tt>.</li>
     * <li>If an assigned buffer is in <tt>{@link BufferState#FAULTED}</tt> state, the state is <tt>{@link BufferState#FAULTED}</tt>.</li>
     * <li>If an assigned buffer is in <tt>{@link BufferState#RELEASED}</tt> state, the state is <tt>{@link at.ac.tuwien.infosys.g2021.common.BufferState#FAULTED}</tt>.</li>
     * <li>If an assigned buffer is in <tt>{@link at.ac.tuwien.infosys.g2021.common.BufferState#INITIALIZING}</tt> state, the state is <tt>{@link at.ac.tuwien.infosys.g2021.common.BufferState#INITIALIZING}</tt>.</li>
     * <li>Otherwise the state is <tt>{@link at.ac.tuwien.infosys.g2021.common.BufferState#READY}</tt>.</li>
     * </ul>
     *
     * @return the state of the data point, which is never <tt>null</tt>.
     */
    public BufferState getState() { return implementation.getState(); }

    /**
     * This method looks for available buffers.
     *
     * @param name a regular expression specifying the buffer name, which should be scanned. A simple match satisfy
     *             the search condition, the regular expression must not match the whole name.
     *
     * @return a collection of all the buffers matching this query
     */
    public Set<BufferDescription> queryBuffersByName(String name) { return implementation.queryBuffersByName(name); }

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
    public Set<BufferDescription> queryBuffersByMetainfo(String topic, String feature) { return implementation.queryBuffersByMetainfo(topic, feature); }

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
     * This method returns the meta information of a buffer.
     *
     * @param name the name of a buffer
     *
     * @return the buffer meta information, which is never <tt>null</tt>
     *
     * @throws java.lang.IllegalArgumentException
     *          if there exists no buffer with the given buffer name
     */
    public BufferDescription getBufferDescription(String name) throws IllegalArgumentException { return implementation.getBufferDescription(name); }

    /**
     * Assigns a buffer for read or write access to this data point. This method will have no effect, if the buffer is currently
     * assigned to this data point.
     *
     * @param bufferName the name of the buffer
     *
     * @throws java.lang.IllegalArgumentException
     *          if there exists no buffer with the given buffer name
     */
    public void assign(String bufferName) throws IllegalArgumentException { implementation.assign(bufferName); }

    /**
     * Is a buffer assigned to this data point?
     *
     * @param bufferName the name of the buffer
     *
     * @return <tt>true</tt>, if the buffer is currently assigned to this data point
     *
     * @see #assign(String)
     * @see #detach(String)
     */
    public boolean isAssigned(String bufferName) { return getAssignedBufferNames().contains(bufferName); }

    /**
     * Read the names of all buffers assigned to this data point.
     *
     * @return a set of buffer names which may be empty, if there are no buffers assigned to this data point.
     *
     * @see #assign(String)
     * @see #detach(String)
     */
    public Set<String> getAssignedBufferNames() { return implementation.getAssignedBufferNames(); }

    /**
     * Detaches a buffer from this data point. This method will have no effect, if the buffer is currently
     * not assigned to this data point.
     *
     * @param bufferName the name of the buffer
     */
    public void detach(String bufferName) { implementation.detach(bufferName); }

    /**
     * Get the current information about a buffer attached to this data point. Sensor buffers return the
     * value read from the hardware. Actors return the last value set to the buffer. For convenience actor
     * buffers return an error state, if the hardware is or was in this state and no value was set.
     *
     * @param bufferName the name of the buffer
     *
     * @return the current buffer data
     *
     * @throws java.lang.IllegalArgumentException
     *          there is no buffer with the given buffer name assigned to this data point
     */
    public SimpleData get(String bufferName) throws IllegalArgumentException {

        Map<String, SimpleData> bufferData = getAll();
        SimpleData result = bufferData.get(bufferName);

        if (result == null) throw new IllegalArgumentException("unknown buffer '" + bufferName + "'");
        else return result;
    }

    /**
     * Get the current information about all the buffers attached to this data point.
     *
     * @return a map with the names of all currently attached buffers as key and their values as data. The result may be
     *         empty, but is never <tt>null</tt>.
     */
    public Map<String, SimpleData> getAll() { return implementation.getAll(); }

    /**
     * Sets the value to an actor assigned to this data point.
     *
     * @param bufferName the name of the actor buffer
     * @param value      the new value
     *
     * @throws java.lang.IllegalArgumentException
     *          if the buffer isn't assigned to this data point or is not changable (e.g. an actor)
     */
    public void set(String bufferName, Number value) throws IllegalArgumentException {

        implementation.set(bufferName, value);
    }

    /**
     * <p>
     * Adds a new data point observer to the set of well known observers. If the observer argument is <tt>null</tt> or the
     * observer argument is currently registered in this object, the method call will have no effect. Note that the registered
     * observers are a set. Multiple registration will not cause multiple notifications about state or value changes.
     * </p>
     * <p>
     * The observer instance will be notified about changes in a separate thread!
     * </p>
     *
     * @param observer the observer
     */
    public void addDataPointObserver(DataPointObserver observer) { implementation.addDataPointObserver(observer); }

    /**
     * <p>
     * Removes a data point observer from the set of well known observers. If the observer argument is <tt>null</tt> or the
     * observer argument is currently not registered in this object, the method call will have no effect.
     * </p>
     *
     * @param observer the observer
     */
    public void removeDataPointObserver(DataPointObserver observer) { implementation.removeDataPointObserver(observer); }

    /**
     * Get an object, which allows time triggered retrieval of buffer data.
     *
     * @return a new instance for time triggered retrieval of buffer data
     */
    public TimeControl getTimeControl() { return new TimeControl(this); }

    /**
     * Returns a stream of all buffer value changes of this data point. After calling this method, all current buffer
     * values are put into this stream. Then value changes are signalled by putting the new buffer value into the stream.
     * If there are no buffer value changes available, the calling thread is blocked. Detaching a buffer will cause a value
     * change to the buffer state <tt>{@link at.ac.tuwien.infosys.g2021.common.BufferState#RELEASED}</tt>. Releasing this
     * data point will automatically detach all assigned buffers. No more data changes are signalled with this stream.
     *
     * @return a stream of value changes
     */
    public Stream<SimpleData> getStream() { return implementation.getStream(); }

    // *** CALLBACKS ***
    private class Task {

        private long delay;
        private Timer timer;
        private SDGCallback callback;

        Task(long d, SDGCallback cb) {

            super();

            delay = d;
            callback = cb;
            start();
        }

        void start() {

            timer = new Timer(true);
            timer.schedule(new TimerTask() {
                public void run() {
                    stop();
                    start();
                    try {
                        callback.onTimeout(DataPoint.this, DataPoint.this.getAll());
                    }
                    catch (Exception e) {
                        logger.log(Level.WARNING, "The callback '" + callback + "' has thrown an exception:", e);
                    }
                }
            },
                           delay);
        }

        void stop() { timer.cancel(); }
    }

    private Map<SDGCallback, Task> callbacks;

    /**
     * Adds SDGCallbacks to this data point. If some callbacks are already registered, the callbacks are removed and
     * scheduled with the new delay.
     *
     * @param delay the delay in milliseconds. At least 100msec are assumed.
     * @param cb    the callback
     */
    public void addCallback(long delay, SDGCallback... cb) {

        if (cb != null) {
            removeCallback(cb);
            for (SDGCallback callback : cb) {
                if (callback != null) callbacks.put(callback, new Task(Math.max(100L, delay), callback));
            }
        }
    }

    /**
     * Removes SDGCallback from this data point. If some callbacks are not registered, the method has no effect for
     * these callbacks.
     *
     * @param cb the callback
     */
    public void removeCallback(SDGCallback... cb) {

        if (cb != null) {
            for (SDGCallback callback : cb) {
                if (callback != null) {
                    Task t = callbacks.remove(callback);
                    if (t != null) t.stop();
                }
            }
        }
    }
}


