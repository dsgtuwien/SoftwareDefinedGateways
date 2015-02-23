package at.ac.tuwien.infosys.g2021.common.communication;

import at.ac.tuwien.infosys.g2021.common.BufferConfiguration;
import at.ac.tuwien.infosys.g2021.common.BufferDescription;
import at.ac.tuwien.infosys.g2021.common.BufferState;
import at.ac.tuwien.infosys.g2021.common.SimpleData;
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * This is the unit-test for the g2021 communication between the daemon and the
 * client. All the communication scenarios between a <tt>{@link ClientEndpoint}</tt>
 * and a <tt>{@link DaemonEndpoint}</tt> are tested.
 */
public class CommunicationTest implements ValueChangeObserver {

    // The client endpoint.
    private ClientEndpoint client;

    // The daemon endpoint
    private DaemonEndpoint daemon;

    // The dummy daemon.
    private TestDaemon server;

    // The last buffer value received.
    private SimpleData lastValue;

    /** This is the notification about the lost connection to the daemon. */
    @Override
    public void communicationLost() {}

    /**
     * This is the notification of a spontaneous value change.
     *
     * @param newValue the new buffer value
     */
    @Override
    public void valueChanged(SimpleData newValue) { lastValue = newValue; }

    /** Setting up the connection. */
    @Before
    public void setUp() throws IOException {

        server = new TestDaemon();

        client = ClientEndpoint.get();
        client.addValueChangeObserver(this);
        client.connect();

        daemon = server.getEndpoint();
        if (daemon == null) throw new IOException("unable to establish connection");
    }

    /** Close the connection. */
    @After
    public void tearDown() {

        client.removeValueChangeObserver(this);
        client.disconnect();
        daemon.disconnect();
        server.shutdown();

        client = null;
        daemon = null;
        server = null;
    }

    /* The communication setup (establish() -> accepted()) is tested implicitly within every
     * test case. Therefore no testcases are written to check this.
     */

    /** This test case tests querying buffers by name. */
    @Test
    public void testQueryBuffersByName() {

        Set<String> bufferNames = new HashSet<>();

        // Query for unknown buffers
        bufferNames.clear();
        bufferNames.addAll(client.queryBuffersByName("<.*>"));
        assertEquals(0, bufferNames.size());

        // Query for some buffers
        bufferNames.clear();
        bufferNames.addAll(client.queryBuffersByName("[cd]"));
        assertEquals(1, bufferNames.size());
        assertTrue(bufferNames.contains("buffer-c"));

        // Query for all buffers
        bufferNames.clear();
        bufferNames.addAll(client.queryBuffersByName("[abc]"));
        assertEquals(3, bufferNames.size());
        assertTrue(bufferNames.contains("buffer-a"));
        assertTrue(bufferNames.contains("buffer-b"));
        assertTrue(bufferNames.contains("buffer-c"));
    }

    /** This test case tests querying buffers by meta info. */
    @Test
    public void testQueryBuffersByMetainfo() {

        Set<String> bufferNames = new HashSet<>();

        // Query for unknown buffer topics
        bufferNames.clear();
        bufferNames.addAll(client.queryBuffersByMetainfo("[xy]", "da"));
        assertEquals(0, bufferNames.size());

        // Query for unknown buffer features
        bufferNames.clear();
        bufferNames.addAll(client.queryBuffersByMetainfo("topic", "wurscht"));
        assertEquals(0, bufferNames.size());

        // Query for some buffers
        bufferNames.clear();
        bufferNames.addAll(client.queryBuffersByMetainfo("-c", "da"));
        assertEquals(2, bufferNames.size());
        assertTrue(bufferNames.contains("buffer-a"));
        assertTrue(bufferNames.contains("buffer-b"));

        // Query for all buffers
        bufferNames.clear();
        bufferNames.addAll(client.queryBuffersByMetainfo("topic", "a"));
        assertEquals(3, bufferNames.size());
        assertTrue(bufferNames.contains("buffer-a"));
        assertTrue(bufferNames.contains("buffer-b"));
        assertTrue(bufferNames.contains("buffer-c"));
    }

    /** This test case tests querying meta info from a buffer. */
    @Test
    public void testQueryMetainfo() {

        BufferDescription metainfo;

        // Query for unknown buffer
        metainfo = client.queryMetainfo("buffer-n");
        assertNull(metainfo);

        // Query for known buffer
        metainfo = client.queryMetainfo("buffer-b");
        assertNotNull(metainfo);
        assertEquals("buffer-b", metainfo.getBufferName());
        assertFalse(metainfo.isHardwareBuffer());
        assertEquals(2, metainfo.getBufferMetainfo().size());
        assertEquals("egal", metainfo.getBufferMetainfo().get("topic-b"));
        assertEquals("nischt da", metainfo.getBufferMetainfo().get("topic-c"));
    }

    /** This test case queries buffer configurations. */
    @Test
    public void testGetBufferConfiguration() {

        BufferConfiguration configuration;

        // Query for unknown buffer
        try {
            configuration = client.getBufferConfiguration("buffer-n");

            // This method must throw an exception!
            assertTrue(false);
        }
        catch (IllegalArgumentException e) {
            configuration = null;
        }
        assertNull(configuration);

        // Query for known buffer
        configuration = client.getBufferConfiguration("buffer-b");
        assertNotNull(configuration);
        assertEquals(2, configuration.getMetainfo().size());
        assertEquals("egal", configuration.getMetainfo().get("topic-b"));
        assertEquals("nischt da", configuration.getMetainfo().get("topic-c"));
    }

    /** This test case changes buffer configurations. */
    @Test
    public void testSetBufferConfiguration() {

        // Updating an unknown buffer
        assertFalse(client.setBufferConfiguration("buffer-n", new BufferConfiguration(), false));

        // setting configuration for a known buffer
        assertTrue(client.setBufferConfiguration("buffer-b", new BufferConfiguration(), false));

        // creating a new buffer
        assertTrue(client.setBufferConfiguration("buffer-n", new BufferConfiguration(), true));
    }

    /** This test case releases buffers. */
    @Test
    public void testReleaseBuffer() {

        // Releasing an unknown buffer
        assertFalse(client.releaseBuffer("buffer-n"));

        // Releasing a known buffer
        assertTrue(client.releaseBuffer("buffer-a"));
    }

    /** This test case queries buffer values. */
    @Test
    public void testGet() {

        SimpleData answer;

        // Querying buffer values for an unknown buffer
        try {
            client.getImmediate("buffer-n");
            assertTrue(false);
        }
        catch (IllegalArgumentException e) { /* well done ! */ }

        // Querying buffer values for a faulted known buffer
        answer = client.getImmediate("buffer-b");
        assertEquals("buffer-b", answer.getBufferName());
        assertNotNull(answer.getTimestamp());
        assertEquals(BufferState.FAULTED, answer.getState());
        assertNull(answer.getValue());

        // Querying buffer values for a ready known buffer
        answer = client.getImmediate("buffer-c");
        assertEquals("buffer-c", answer.getBufferName());
        assertNotNull(answer.getTimestamp());
        assertEquals(BufferState.READY, answer.getState());
        assertNotNull(answer.getValue());
        assertEquals(16.0, answer.getValue().doubleValue(), 1.0e-10);
    }

    /** This test case sets buffer values. */
    @Test
    public void testSet() {

        // Setting the value of an unknown buffer
        try {
            client.set("buffer-n", 1.0);
            assertTrue(false);
        }
        catch (IllegalArgumentException e) { /* well done ! */ }

        // Setting the value of a sensor
        try {
            client.set("buffer-a", 1.0);
            assertTrue(false);
        }
        catch (IllegalArgumentException e) { /* well done ! */ }

        // Setting an actor value.
        SimpleData answer = client.set("buffer-c", 12.0);
        assertEquals("buffer-c", answer.getBufferName());
        assertNotNull(answer.getTimestamp());
        assertEquals(BufferState.READY, answer.getState());
        assertNotNull(answer.getValue());
        assertEquals(12.0, answer.getValue().doubleValue(), 1.0e-10);
    }

    /** This test case tests spontaneous value changes. */
    @Test
    public void testValueChanges() throws InterruptedException {

        SimpleData change = new SimpleData("buffer-b", new Date(), BufferState.INITIALIZING);

        // Firing a not expected value change
        lastValue = null;
        daemon.spontaneousValueChange(change);
        Thread.sleep(100L);
        assertNull(lastValue);

        // Requesting changes for a wrong buffer
        client.getOnChange("buffer-a");
        Thread.sleep(100L);
        lastValue = null;
        daemon.spontaneousValueChange(change);
        Thread.sleep(100L);
        assertNull(lastValue);

        // Request changes and receive one of them
        client.getOnChange("buffer-b");
        Thread.sleep(100L);
        lastValue = null;
        daemon.spontaneousValueChange(change);
        Thread.sleep(100L);
        assertNotNull(lastValue);
        assertEquals(change.getBufferName(), lastValue.getBufferName());
        assertEquals(change.getTimestamp(), lastValue.getTimestamp());
        assertEquals(change.getState(), lastValue.getState());
        assertEquals(change.getValue(), lastValue.getValue());

        // Test, that the request is now outdated
        lastValue = null;
        daemon.spontaneousValueChange(change);
        Thread.sleep(100L);
        assertNull(lastValue);
    }
}


