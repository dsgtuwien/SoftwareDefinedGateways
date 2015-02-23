package at.ac.tuwien.infosys.g2021.common.communication;

import at.ac.tuwien.infosys.g2021.common.BufferClass;
import at.ac.tuwien.infosys.g2021.common.BufferConfiguration;
import at.ac.tuwien.infosys.g2021.common.BufferState;
import at.ac.tuwien.infosys.g2021.common.SimpleData;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * This is a dummy implementation of a g2021 daemon with a fixed behaviour
 * for test reasons.
 */
class TestDaemonStrategy implements ClientRequestExecutionStrategy {

    // This is a set of buffer names and the according buffer data
    private Map<String, SimpleData> values;

    // This is a set of buffer names and the according configurations
    private Map<String, BufferConfiguration> configurations;

    // The test daemon
    private TestDaemon daemon;

    /** Setting up the dummy. */
    TestDaemonStrategy(TestDaemon daemon) {

        this.daemon = daemon;

        configurations = new HashMap<>();
        values = new HashMap<>();

        configurations.put("buffer-a", new BufferConfiguration());
        configurations.get("buffer-a").setBufferClass(BufferClass.SENSOR);
        configurations.get("buffer-a").getMetainfo().put("topic-a", "egal");
        configurations.get("buffer-a").getMetainfo().put("topic-b", "da");
        configurations.get("buffer-a").getMetainfo().put("topic-c", "nischt da");
        values.put("buffer-a", new SimpleData("buffer-a", new Date(), BufferState.READY, 4.0));

        configurations.put("buffer-b", new BufferConfiguration());
        configurations.get("buffer-b").setBufferClass(BufferClass.SENSOR);
        configurations.get("buffer-b").getMetainfo().put("topic-b", "egal");
        configurations.get("buffer-b").getMetainfo().put("topic-c", "nischt da");
        values.put("buffer-b", new SimpleData("buffer-b", new Date(), BufferState.FAULTED));

        configurations.put("buffer-c", new BufferConfiguration());
        configurations.get("buffer-c").setBufferClass(BufferClass.ACTOR);
        configurations.get("buffer-c").getMetainfo().put("topic-a", "da");
        configurations.get("buffer-c").getMetainfo().put("topic-c", "egal");
        values.put("buffer-c", new SimpleData("buffer-c", new Date(), BufferState.READY, 16.0));
    }

    /**
     * This method must handle a communication error at the client connection.
     *
     * @param conn the connection
     */
    @Override
    public void connectionDied(DaemonEndpoint conn) { shutdown(conn); }

    /**
     * A shutdown request has been received.
     *
     * @param conn the connection
     */
    @Override
    public void shutdown(DaemonEndpoint conn) {

        conn.disconnect();
        daemon.shutdown();
    }

    /**
     * Exists a buffer with a given name?
     *
     * @param conn       the connection
     * @param bufferName the name of the buffer
     *
     * @return <tt>true</tt>, if a buffer with this name is known
     */
    @Override
    public boolean bufferExists(DaemonEndpoint conn, String bufferName) { return configurations.containsKey(bufferName); }

    /**
     * This method looks for available configurations.
     *
     * @param conn the connection
     * @param name a regular expression specifying the buffer name, which should be scanned. A simple match satisfy
     *             the search condition, the regular expression must not match the whole name.
     *
     * @return a collection of all the buffers matching this query
     */
    @Override
    public Set<String> queryBuffersByName(DaemonEndpoint conn, String name) {

        Set<String> result = new TreeSet<>();

        try {
            Pattern pattern = Pattern.compile(name, Pattern.CASE_INSENSITIVE);

            for (String bufferName : configurations.keySet()) {

                Matcher match = pattern.matcher(bufferName);

                if (match.find()) result.add(bufferName);
            }
        }
        catch (PatternSyntaxException e) {
            // We return an empty result set.
        }

        return result;
    }

    /**
     * This method looks for available configurations.
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
    @Override
    public Set<String> queryBuffersByMetainfo(DaemonEndpoint conn, String topic, String feature) {

        Set<String> result = new TreeSet<>();

        try {
            Pattern topicPattern = Pattern.compile(topic, Pattern.CASE_INSENSITIVE);
            Pattern featurePattern = Pattern.compile(feature, Pattern.CASE_INSENSITIVE);

            for (Map.Entry<String, BufferConfiguration> buffer : configurations.entrySet()) {
                for (Map.Entry<String, String> metainfo : buffer.getValue().getMetainfo().entrySet()) {

                    Matcher topicMatch = topicPattern.matcher(metainfo.getKey());
                    Matcher featureMatch = featurePattern.matcher(metainfo.getValue());

                    if (topicMatch.find() && featureMatch.find()) {
                        result.add(buffer.getKey());
                        break;
                    }
                }
            }
        }
        catch (PatternSyntaxException e) {
            // We return an empty result set.
        }

        return result;
    }

    /**
     * Returns the configuration of a buffer.
     *
     * @param conn       the connection
     * @param bufferName the name of the buffer
     *
     * @return the buffer configuration or <tt>null</tt>, is no buffer with this name is known
     */
    @Override
    public BufferConfiguration bufferConfiguration(DaemonEndpoint conn, String bufferName) {

        if (configurations.containsKey(bufferName)) return configurations.get(bufferName);
        else return null;
    }

    /**
     * Represents this buffer a hardware port? This kind of buffer cannot be updated or removed.
     *
     * @param conn       the connection
     * @param bufferName the name of the buffer
     *
     * @return <tt>true</tt>, if this buffer represents a hardware port
     */
    @Override
    public boolean isHardwareBuffer(DaemonEndpoint conn, String bufferName) { return false; }

    /**
     * Changes the configuration of a buffer. If the buffer doesn't exists and the
     * <tt>create</tt>-argument is set to <tt>true</tt>, a new buffer is created.
     *
     * @param conn       the connection
     * @param bufferName the name of the buffer
     *
     * @return the buffer configuration or <tt>null</tt>, is no buffer with this name is known
     */
    @Override
    public boolean setBufferConfiguration(DaemonEndpoint conn, String bufferName, BufferConfiguration config, boolean create) {

        if (create || configurations.containsKey(bufferName)) {
            configurations.put(bufferName, config);
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Returns the value of a buffer.
     *
     * @param conn       the connection
     * @param bufferName the name of the buffer
     *
     * @return the current value or <tt>null</tt>, is no buffer with this name is known
     */
    @Override
    public SimpleData bufferValue(DaemonEndpoint conn, String bufferName) { return values.get(bufferName); }

    /**
     * Sets the value of a buffer.
     *
     * @param conn       the connection
     * @param bufferName the name of the buffer
     * @param value      the new buffer value
     *
     * @return the new current value of the buffer or <tt>null</tt>, is no buffer with this name is known
     */
    @Override
    public SimpleData setBufferValue(DaemonEndpoint conn, String bufferName, double value) {

        SimpleData result = null;

        if (configurations.containsKey(bufferName) && configurations.get(bufferName).getBufferClass() == BufferClass.ACTOR) {
            result = new SimpleData(bufferName, new Date(), BufferState.READY, value);
            values.put(bufferName, result);
        }

        return result;
    }

    /**
     * Releases a buffer.
     *
     * @param conn       the connection
     * @param bufferName the name of the buffer
     *
     * @return <tt>true</tt>, if the buffer is now released
     */
    @Override
    public boolean removeBuffer(DaemonEndpoint conn, String bufferName) { return configurations.containsKey(bufferName); }
}
