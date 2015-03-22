package at.ac.tuwien.infosys.g2021.intf;

import at.ac.tuwien.infosys.g2021.common.BufferClass;
import at.ac.tuwien.infosys.g2021.common.BufferConfiguration;
import at.ac.tuwien.infosys.g2021.common.BufferDescription;
import at.ac.tuwien.infosys.g2021.common.BufferState;
import at.ac.tuwien.infosys.g2021.common.DummyGathererConfiguration;
import at.ac.tuwien.infosys.g2021.common.ScalingAdapterConfiguration;
import at.ac.tuwien.infosys.g2021.common.SimpleData;
import at.ac.tuwien.infosys.g2021.daemon.Daemon;
import java.util.Map;
import java.util.Set;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/** The test of the DataPoint class. */
public class DataPointTest {

    // The test object
    private DataPoint dataPoint;

    // A listener for DataPoints
    private class Listener implements DataPointObserver {

        /**
         * A buffer has been assigned to the data point.
         *
         * @param dataPoint  the data point
         * @param bufferName the name of the assigned buffer
         */
        @Override
        public void bufferAssigned(DataPoint dataPoint, String bufferName) { assignedBuffer = bufferName; }

        /**
         * The overall state of the data point has changed.
         *
         * @param dataPoint the affected data point
         * @param oldOne    the state left
         * @param newOne    the current state of the data point
         */
        @Override
        public void dataPointStateChanged(DataPoint dataPoint, BufferState oldOne, BufferState newOne) {

            newState = newOne;
        }

        /**
         * A buffer has been detached from the data point.
         *
         * @param dataPoint  the data point
         * @param bufferName the name of the detached buffer
         */
        @Override
        public void bufferDetached(DataPoint dataPoint, String bufferName) { detachedBuffer = bufferName; }

        /**
         * A buffer has changed its state or value.
         *
         * @param dataPoint the data point
         * @param oldOne    the outdated buffer data. This argument may
         *                  be <tt>null</tt>, if the buffer just has been assigned.
         * @param newOne    the current buffer dataThis argument may
         *                  be <tt>null</tt>, if the buffer just has been detached.
         */
        @Override
        public void bufferChanged(DataPoint dataPoint, SimpleData oldOne, SimpleData newOne) {

            newValue = newOne;
        }
    }

    private class Callback implements SDGCallback {

        @Override
        public void onTimeout(DataPoint dataPoint, Map<String, SimpleData> data) {
            assertNotNull(dataPoint);
            assertNotNull(data);
            cb = true;
        }
    }

    // Variables for the last received information of the listener
    private String assignedBuffer;
    private String detachedBuffer;
    private BufferState newState;
    private SimpleData newValue;
    private boolean cb;

    /** Clearing the observer data. */
    private void resetObserverData() {

        assignedBuffer = null;
        detachedBuffer = null;
        newState = null;
        newValue = null;
    }

    /** Setting up the test objects. */
    @Before
    public void setUp() throws Exception {

        // We start a daemon, because the test object may use some other
        // components of a daemon.
        Daemon.main(new String[] {"-<unit-test>: don-t-exit-on-shutdown"});

        // Creating a buffer manager
        BufferManager bufferManager = new BufferManager();

        // Setting up an actor buffer
        BufferConfiguration actorConfig = new BufferConfiguration();
        actorConfig.setBufferClass(BufferClass.ACTOR);
        actorConfig.getMetainfo().put("sensor", "no");
        actorConfig.getAdapterChain().add(new ScalingAdapterConfiguration(0.0, 2.0, 1.0));
        actorConfig.setGatherer(new DummyGathererConfiguration());
        bufferManager.create("actor", actorConfig);

        // Setting up the sensor
        BufferConfiguration sensorConfig = new BufferConfiguration();
        sensorConfig.setBufferClass(BufferClass.SENSOR);
        sensorConfig.getMetainfo().put("sensor", "yes");
        sensorConfig.getAdapterChain().add(new ScalingAdapterConfiguration(0.0, 0.5, 0.0));
        sensorConfig.setGatherer(new DummyGathererConfiguration());
        bufferManager.create("sensor", sensorConfig);
        bufferManager.release();

        // Setting up the test object
        dataPoint = new DataPoint();
        dataPoint.addDataPointObserver(new Listener());
        resetObserverData();
    }

    /** removing the test objects. */
    @After
    public void tearDown() throws Exception {

        // Releasing the test object
        dataPoint.release();
        dataPoint = null;

        Daemon.get().stop();
    }

    /** Test the initial state. */
    @Test
    public void testInitialState() {

        assertEquals(BufferState.INITIALIZING, dataPoint.getState());
    }

    /** Querying buffer names by name. */
    @Test
    public void testQueriesByName() {

        Set<BufferDescription> found;

        found = dataPoint.queryBuffersByName("x+");
        assertNotNull(found);
        assertEquals(0, found.size());

        found = dataPoint.queryBuffersByName("ct");
        assertNotNull(found);
        assertEquals(1, found.size());

        found = dataPoint.queryBuffersByName("or");
        assertNotNull(found);
        assertEquals(2, found.size());
    }

    /** Querying buffer names by metainfo. */
    @Test
    public void testQueriesByMetainfo() {

        Set<BufferDescription> found;

        found = dataPoint.queryBuffersByMetainfo("unknown", "yes");
        assertNotNull(found);
        assertEquals(0, found.size());

        found = dataPoint.queryBuffersByMetainfo("sensor", "egal");
        assertNotNull(found);
        assertEquals(0, found.size());

        found = dataPoint.queryBuffersByMetainfo("se", "no");
        assertNotNull(found);
        assertEquals(1, found.size());

        found = dataPoint.queryBuffersByMetainfo("se", ".");
        assertNotNull(found);
        assertEquals(2, found.size());
    }

    /** Assigning an unknown buffer. */
    @Test(expected = IllegalArgumentException.class)
    public void testAssignUnknownBuffer() {

        dataPoint.assign("xtor");
    }

    /** Assigning known buffers. */
    @Test
    public void testAssignBuffer() {

        Set<String> bufferNames;

        // There are no buffers known
        bufferNames = dataPoint.getAssignedBufferNames();
        assertFalse(dataPoint.isAssigned("actor"));
        assertFalse(dataPoint.isAssigned("sensor"));
        assertEquals(0, bufferNames.size());

        // Assigning the sensor causes a state change to READY
        dataPoint.assign("sensor");
        bufferNames = dataPoint.getAssignedBufferNames();
        assertFalse(dataPoint.isAssigned("actor"));
        assertTrue(dataPoint.isAssigned("sensor"));
        assertEquals(BufferState.READY, newState);
        assertEquals("sensor", assignedBuffer);
        assertNotNull(newValue);
        assertEquals("sensor", newValue.getBufferName());
        assertEquals(BufferState.READY, newValue.getState());
        assertNotNull(newValue.getValue());
        assertEquals(0.0, newValue.getValue().doubleValue(), 1.0e-6);
        assertEquals(1, bufferNames.size());
        assertTrue(bufferNames.contains("sensor"));
        resetObserverData();

        // Assigning the actor causes a state change to INITIALIZING, because this is
        // the state of the actor
        dataPoint.assign("actor");
        bufferNames = dataPoint.getAssignedBufferNames();
        assertTrue(dataPoint.isAssigned("actor"));
        assertTrue(dataPoint.isAssigned("sensor"));
        assertEquals(BufferState.INITIALIZING, newState);
        assertEquals("actor", assignedBuffer);
        assertNotNull(newValue);
        assertEquals("actor", newValue.getBufferName());
        assertEquals(BufferState.INITIALIZING, newValue.getState());
        assertEquals(2, bufferNames.size());
        assertTrue(bufferNames.contains("actor"));
        assertTrue(bufferNames.contains("sensor"));
        resetObserverData();

        // Assigning the actor a second time has no effect
        dataPoint.assign("actor");
        bufferNames = dataPoint.getAssignedBufferNames();
        assertTrue(dataPoint.isAssigned("actor"));
        assertTrue(dataPoint.isAssigned("sensor"));
        assertNull(newState);
        assertNull(assignedBuffer);
        assertNull(newValue);
        assertEquals(2, bufferNames.size());
        assertTrue(bufferNames.contains("actor"));
        assertTrue(bufferNames.contains("sensor"));
        resetObserverData();
    }

    /** Detaching buffers. */
    @Test
    public void testDetachBuffer() {

        dataPoint.assign("actor");
        dataPoint.assign("sensor");

        // Detaching the actor causes a state change to READY
        dataPoint.detach("actor");
        assertEquals(BufferState.READY, newState);
        assertEquals("actor", detachedBuffer);
        assertNotNull(newValue);
        assertEquals("actor", newValue.getBufferName());
        assertEquals(BufferState.RELEASED, newValue.getState());
        assertNull(newValue.getValue());
        resetObserverData();

        // Detaching the sensor causes a state change to INITIALIZING, because there is
        // no buffer assigned
        dataPoint.detach("sensor");
        assertEquals(BufferState.INITIALIZING, newState);
        assertEquals("sensor", detachedBuffer);
        assertNotNull(newValue);
        assertEquals("sensor", newValue.getBufferName());
        assertEquals(BufferState.RELEASED, newValue.getState());
        resetObserverData();

        // Detaching a detached buffer has no effect
        dataPoint.detach("actor");
        assertNull(newState);
        assertNull(assignedBuffer);
        assertNull(newValue);
        resetObserverData();
    }

    /** Reading buffer values. */
    @Test
    public void testGet() {

        SimpleData data;

        dataPoint.assign("actor");
        dataPoint.assign("sensor");

        data = dataPoint.get("actor");
        assertNotNull(data);
        assertEquals("actor", data.getBufferName());
        assertEquals(BufferState.INITIALIZING, data.getState());

        data = dataPoint.get("sensor");
        assertNotNull(data);
        assertEquals("sensor", data.getBufferName());
        assertEquals(BufferState.READY, data.getState());
        assertEquals(0.0, data.getValue().doubleValue(), 1.0e-6);

        // reading an unknown buffer
        try {
            dataPoint.get("xtor");
            assertNotNull(null);
        }
        catch (IllegalArgumentException e) {
            // Well done
        }
    }

    /** Reading all buffer values. */
    @Test
    public void testGetAll() {

        Map<String, SimpleData> data;

        data = dataPoint.getAll();
        assertNotNull(data);
        assertEquals(0, data.size());

        dataPoint.assign("actor");
        dataPoint.assign("sensor");

        data = dataPoint.getAll();
        assertNotNull(data);
        assertEquals(2, data.size());
        assertNotNull(data.get("sensor"));
        assertEquals("sensor", data.get("sensor").getBufferName());
        assertEquals(BufferState.READY, data.get("sensor").getState());
        assertEquals(0.0, data.get("sensor").getValue().doubleValue(), 1.0e-6);
        assertNotNull(data.get("actor"));
        assertEquals("actor", data.get("actor").getBufferName());
        assertEquals(BufferState.INITIALIZING, data.get("actor").getState());
    }

    /** Setting buffer values. */
    @Test
    public void testSet() {

        dataPoint.assign("actor");
        dataPoint.assign("sensor");
        resetObserverData();

        // Setting the actor to 10.0. Therefore the gatherer value must be
        // 2.0 * 10.0 + 1.0 = 21.0. The sensor gets the gatherer value and
        // its value become 0.5 * 21.0 = 10.5!
        dataPoint.set("actor", 10.0);

        // The data has changed spontaneous
        Map<String, SimpleData> data = dataPoint.getAll();
        assertNotNull(data);
        assertEquals(2, data.size());
        assertNotNull(data.get("sensor"));
        assertEquals("sensor", data.get("sensor").getBufferName());
        assertEquals(BufferState.READY, data.get("sensor").getState());
        assertEquals(10.5, data.get("sensor").getValue().doubleValue(), 1.0e-6);
        assertNotNull(data.get("actor"));
        assertEquals("actor", data.get("actor").getBufferName());
        assertEquals(BufferState.READY, data.get("actor").getState());
        assertEquals(10.0, data.get("actor").getValue().doubleValue(), 1.0e-6);

        // The state of the data point is now ready
        assertEquals(BufferState.READY, dataPoint.getState());

        // Some notifications has been received
        assertEquals(BufferState.READY, newState);
        assertNotNull(newValue);
        assertEquals("actor", newValue.getBufferName());
        assertEquals(BufferState.READY, newValue.getState());
        assertNotNull(newValue.getValue());
        assertEquals(10.0, newValue.getValue().doubleValue(), 1.0e-6);
    }

    @Test
    public void testCallbacks() throws Exception {

        Callback callback = new Callback();

        cb = false;
        dataPoint.addCallback(250L, callback);
        assertFalse(cb);

        Thread.sleep(125L);

        assertFalse(cb);

        Thread.sleep(250L);

        assertTrue(cb);
        cb = false;

        Thread.sleep(250L);

        assertTrue(cb);
        cb = false;
        dataPoint.removeCallback(callback);

        Thread.sleep(250L);

        assertFalse(cb);

        Thread.sleep(250L);

        assertFalse(cb);
    }
}
