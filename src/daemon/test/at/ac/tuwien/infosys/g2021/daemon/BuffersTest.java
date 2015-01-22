package at.ac.tuwien.infosys.g2021.daemon;

import at.ac.tuwien.infosys.g2021.common.BufferClass;
import at.ac.tuwien.infosys.g2021.common.BufferConfiguration;
import at.ac.tuwien.infosys.g2021.common.BufferState;
import at.ac.tuwien.infosys.g2021.common.DummyGathererConfiguration;
import at.ac.tuwien.infosys.g2021.common.ScalingAdapterConfiguration;
import at.ac.tuwien.infosys.g2021.common.SimpleData;
import java.util.Set;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/** This class tests the functionality of the collection of buffers. */
public class BuffersTest {

    // The test object
    private Buffers buffers;

    // Two buffer configurations
    private BufferConfiguration actorConfig;
    private BufferConfiguration sensorConfig;

    // This inner class is a ValueChangeConsumer.
    private class Listener implements ValueChangeConsumer {

        @Override
        public void valueChanged(SimpleData newValue) { lastValue = newValue; }
    }

    // This is the last value change consumed.
    private SimpleData lastValue;

    /** Setting up the test objects. */
    @Before
    public void setUp() throws Exception {

        // We start a daemon, because some the test object may use some other
        // components of a daemon.
        Daemon.main(new String[] {"-<unit-test>: don-t-exit-on-shutdown", "-<unit-test>: don-t-open-socket"});

        // Creating the test object
        buffers = new Buffers();
        buffers.initialize();
        buffers.addValueChangeConsumer(new Listener());

        // Setting up an actor buffer
        actorConfig = new BufferConfiguration();
        actorConfig.setBufferClass(BufferClass.ACTOR);
        actorConfig.getMetainfo().put("sensor", "no");
        actorConfig.getAdapterChain().add(new ScalingAdapterConfiguration(0.0, 2.0, 1.0));
        actorConfig.setGatherer(new DummyGathererConfiguration());

        // Setting up the sensor
        sensorConfig = new BufferConfiguration();
        sensorConfig.setBufferClass(BufferClass.SENSOR);
        sensorConfig.getMetainfo().put("sensor", "yes");
        sensorConfig.getAdapterChain().add(new ScalingAdapterConfiguration(0.0, 0.5, 0.0));
        sensorConfig.setGatherer(new DummyGathererConfiguration());
    }

    /** removing the test objects. */
    @After
    public void tearDown() throws Exception {

        // Releasing the test object
        buffers.shutdown();
        buffers = null;

        Daemon.get().stop();
    }

    /** Inserting buffers. */
    @Test
    public void testInsert() {

        String name = "actor";

        // There is no buffer named "actor"
        assertNull(buffers.bufferByName(name));

        // Creating an actor buffer
        buffers.create(name, actorConfig);
        assertNotNull(buffers.bufferByName(name));

        // Creating a new actor buffer with the same configuration will
        // release the old buffer and create a new one.
        Buffer wellknown = buffers.bufferByName(name);
        buffers.create(name, actorConfig);
        assertNotNull(buffers.bufferByName(name));
        assertTrue(buffers.bufferByName(name) != wellknown);
    }

    /** Removing buffers. */
    @Test
    public void testRemove() {

        String name = "actor";

        // There is no buffer named "actor"
        assertNull(buffers.bufferByName(name));

        // Creating an actor buffer
        buffers.create(name, actorConfig);
        Buffer created = buffers.bufferByName(name);
        assertNotNull(created);

        // Remove this buffer
        assertTrue(buffers.remove(name) == created);
        assertNull(buffers.bufferByName(name));

        // Remove this buffer again
        assertNull(buffers.remove(name));
    }

    /** Querying buffer names by name. */
    @Test
    public void testQueriesByName() {

        Set<String> found;

        buffers.create("actor", actorConfig);
        buffers.create("sensor", sensorConfig);

        found = buffers.queryBuffersByName("x+");
        assertNotNull(found);
        assertEquals(0, found.size());

        found = buffers.queryBuffersByName("ct");
        assertNotNull(found);
        assertEquals(1, found.size());
        assertTrue(found.contains("actor"));

        found = buffers.queryBuffersByName("or");
        assertNotNull(found);
        assertEquals(2, found.size());
        assertTrue(found.contains("actor"));
        assertTrue(found.contains("sensor"));
    }

    /** Querying buffer names by metainfo. */
    @Test
    public void testQueriesByMetainfo() {

        Set<String> found;

        buffers.create("actor", actorConfig);
        buffers.create("sensor", sensorConfig);

        found = buffers.queryBuffersByMetainfo("unknown", "yes");
        assertNotNull(found);
        assertEquals(0, found.size());

        found = buffers.queryBuffersByMetainfo("sensor", "egal");
        assertNotNull(found);
        assertEquals(0, found.size());

        found = buffers.queryBuffersByMetainfo("se", "no");
        assertNotNull(found);
        assertEquals(1, found.size());
        assertTrue(found.contains("actor"));

        found = buffers.queryBuffersByMetainfo("se", ".");
        assertNotNull(found);
        assertEquals(2, found.size());
        assertTrue(found.contains("actor"));
        assertTrue(found.contains("sensor"));
    }

    /** Tests the notifications of value changes. */
    @Test
    public void testNotifications() {

        buffers.create("actor", actorConfig);
        buffers.create("sensor", sensorConfig);

        // Setting the actor to 10.0. Therefore the gatherer value must be
        // 2.0 * 10.0 + 1.0 = 21.0. The sensor gets the gatherer value and
        // its value become 0.5 * 21.0 = 10.5!
        buffers.bufferByName("actor").put(10.0);
        assertEquals("sensor", lastValue.getBufferName());
        assertEquals(BufferState.READY, lastValue.getState());
        assertEquals(10.5, lastValue.getValue().doubleValue(), 1.0e-6);
    }
}

