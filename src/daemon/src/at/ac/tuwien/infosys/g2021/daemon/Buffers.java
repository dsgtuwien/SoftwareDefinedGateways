package at.ac.tuwien.infosys.g2021.daemon;

import at.ac.tuwien.infosys.g2021.common.BufferConfiguration;
import at.ac.tuwien.infosys.g2021.common.SimpleData;
import at.ac.tuwien.infosys.g2021.common.util.Loggers;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/** The set of all known buffers. */
class Buffers extends ValueChangeProducer implements Component {

    // The logger.
    private final static Logger logger = Loggers.getLogger(Buffers.class);

    // The listener for value changes
    private class Listener implements ValueChangeConsumer {

        /**
         * This is the notification of a spontaneous value change.
         *
         * @param newValue the new buffer value
         */
        @Override
        public void valueChanged(SimpleData newValue) { fireValueChange(newValue); }
    }

    // A map of all wellknown buffers
    private Map<String, Buffer> buffers;
    private final Object bufferLock;

    // The value change listener
    private Listener listener;

    /** Initializing the buffers. */
    Buffers() {

        buffers = new HashMap<>();
        bufferLock = new Object();
        listener = new Listener();
    }

    /** This method is called, after creating all necessary instances. */
    @Override
    public void initialize() { /* There is no need for more initialization. */ }

    /** This method is called, whenever a shutdown sequence is initiated. */
    @Override
    public void shutdown() {

        synchronized (bufferLock) {
            for (Buffer buffer : buffers.values()) buffer.shutdown();
            buffers.clear();
        }
    }

    /** This method is called immediately after a shutdown, immediately before the process is stopped. */
    @Override
    public void release() { shutdown(); }

    /**
     * This method creates a new buffer. If a buffer with the given name exists, this buffer is released
     * before the new buffer is created.
     *
     * @param name   the buffer name
     * @param config the buffer configuration
     *
     * @return the buffer or <tt>null</tt>, if the new buffer configuration is null
     */
    Buffer create(String name, BufferConfiguration config) { return create(name, config, false); }

    /**
     * This method creates a new buffer. If a buffer with the given name exists, this buffer is released
     * before the new buffer is created.
     *
     * @param name       the buffer name
     * @param config     the buffer configuration
     * @param isHardware is this a hardwarebuffer?
     *
     * @return the buffer or <tt>null</tt>, if the new buffer configuration is null
     */
    Buffer create(String name, BufferConfiguration config, boolean isHardware) {

        synchronized (bufferLock) {

            try {
                // Creating a new buffer. If the creation fails, the IllegalArgumentException
                // is thrown. This is done BEFORE an existing buffer is removed, because the
                // gatherer should remain opened, if both buffers use the same gatherer.
                Buffer result = new Buffer(name, config, isHardware);

                // Removes an existing buffer
                remove(name);

                // Adding the new buffer and distribute its initial value
                buffers.put(name, result);
                logger.info(String.format("The buffer '%s' has been created.", name));

                result.addValueChangeConsumer(listener);
                fireValueChange(result.get());

                return result;
            }
            catch (IllegalArgumentException e) {

                // This is a wrong buffer configuration. No changes are done.
                return null;
            }
        }
    }

    /**
     * This method removes a buffer. If no buffer with the given name exists, this method has no effect.
     *
     * @param name the buffer name
     *
     * @return the removed buffer
     */
    Buffer remove(String name) {

        synchronized (bufferLock) {

            Buffer result = buffers.remove(name);

            if (result != null) {

                logger.info(String.format("The buffer '%s' has been removed.", name));

                // Shutting down the buffer will cause a state change to BufferState.RELEASED and this
                // state change is distributed using the listener.
                result.shutdown();
            }

            return result;
        }
    }

    /**
     * This method returns a wellknown buffer.
     *
     * @param name the buffer name
     *
     * @return the buffer with the given name or <tt>null</tt>, if no buffer with this name exists
     */
    Buffer bufferByName(String name) {

        synchronized (bufferLock) {
            return buffers.get(name);
        }
    }

    /**
     * This method looks for available buffers.
     *
     * @param name a regular expression specifying the buffer name, which should be scanned. A simple match satisfy
     *             the search condition, the regular expression must not match the whole feature description.
     *
     * @return a collection of all the buffer names matching this query
     */
    Set<String> queryBuffersByName(String name) {

        Set<String> result = new TreeSet<>();

        synchronized (bufferLock) {

            try {

                Pattern pattern = Pattern.compile(name, Pattern.CASE_INSENSITIVE);

                for (String bufferName : buffers.keySet()) {

                    Matcher match = pattern.matcher(bufferName);

                    if (match.find()) result.add(bufferName);
                }
            }
            catch (PatternSyntaxException e) {
                // We just return an empty result set.
            }
        }

        return result;
    }

    /**
     * This method looks for available buffers.
     *
     * @param topic   a regular expression specifying the buffer topics, which should be scanned for the wanted features.
     *                These are keys of the buffer meta information. A simple match satisfy
     *                the search condition, the regular expression must not match the whole topic name.
     * @param feature a regular expression specifying the buffer features, which should be scanned. A simple match satisfy
     *                the search condition, the regular expression must not match the whole feature description.
     *
     * @return a collection of all the names of those buffers, which matches this query
     */
    Set<String> queryBuffersByMetainfo(String topic, String feature) {

        Set<String> result = new TreeSet<>();

        synchronized (bufferLock) {
            try {
                Pattern topicPattern = Pattern.compile(topic, Pattern.CASE_INSENSITIVE);
                Pattern featurePattern = Pattern.compile(feature, Pattern.CASE_INSENSITIVE);

                for (Buffer buffer : buffers.values()) {
                    for (Map.Entry<String, String> metainfo : buffer.getMetainfo().entrySet()) {

                        Matcher topicMatch = topicPattern.matcher(metainfo.getKey());
                        Matcher featureMatch = featurePattern.matcher(metainfo.getValue());

                        if (topicMatch.find() && featureMatch.find()) {
                            result.add(buffer.getName());
                            break;
                        }
                    }
                }
            }
            catch (PatternSyntaxException e) {
                // We return an empty result set.
            }
        }

        return result;
    }
}

