package at.ac.tuwien.infosys.g2021.intf;

import at.ac.tuwien.infosys.g2021.common.BufferClass;
import at.ac.tuwien.infosys.g2021.common.BufferConfiguration;
import at.ac.tuwien.infosys.g2021.common.BufferDescription;
import at.ac.tuwien.infosys.g2021.common.DummyAdapterConfiguration;
import at.ac.tuwien.infosys.g2021.common.DummyGathererConfiguration;
import at.ac.tuwien.infosys.g2021.common.ScalingAdapterConfiguration;
import at.ac.tuwien.infosys.g2021.daemon.Daemon;
import java.util.Set;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/** The test of the buffer manager. */
public class BufferManagerTest {

    // The test object
    private BufferManager bufferManager;

    // Two buffer configurations
    private BufferConfiguration actorConfig;
    private BufferConfiguration sensorConfig;

    /** Setting up the test objects. */
    @Before
    public void setUp() throws Exception {

        // We start a daemon, because some the test object may use some other
        // components of a daemon.
        Daemon.main(new String[] {"-<unit-test>: don-t-exit-on-shutdown"});

        // Creating the test object
        bufferManager = new BufferManager();

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
        bufferManager.release();
        bufferManager = null;

        Daemon.get().stop();
    }

    /** Tests the query of an unknown buffer. */
    @Test(expected = IllegalArgumentException.class)
    public void testQueryUnknownBufferByName() {

        bufferManager.get("actor");
    }

    /** Tests the query of an known buffer. */
    @Test
    public void testQueryKnownBufferByName() {

        bufferManager.create("actor", actorConfig);

        BufferConfiguration config = bufferManager.get("actor");
        assertNotNull(config);
        assertEquals(BufferClass.ACTOR, config.getBufferClass());
        assertNotNull(config.getMetainfo());
        assertEquals(1, config.getMetainfo().size());
        assertEquals("no", config.getMetainfo().get("sensor"));
        assertNotNull(config.getAdapterChain());
        assertEquals(1, config.getAdapterChain().size());
        assertTrue(config.getAdapterChain().get(0) instanceof ScalingAdapterConfiguration);
        assertNotNull(config.getGatherer());
        assertTrue(config.getGatherer() instanceof DummyGathererConfiguration);
    }

    /** Tests the setup of a new buffer. */
    @Test
    public void testCreation() {

        // Creation of a buffer. The buffer is now known in the daemon
        assertTrue(bufferManager.create("actor", actorConfig));
        assertNotNull(bufferManager.get("actor"));

        // Update an existing buffer with "create(...)
        assertTrue(bufferManager.create("actor", actorConfig));
        assertNotNull(bufferManager.get("actor"));
    }

    /** Tests the update of an existing buffer. */
    @Test
    public void testUpdateExisting() {

        // Creation of a buffer. The buffer is now known in the daemon
        assertTrue(bufferManager.create("actor", actorConfig));
        assertNotNull(bufferManager.get("actor"));

        // Update an existing buffer
        BufferConfiguration configuration = bufferManager.get("actor");
        assertNotNull(configuration);
        configuration.getAdapterChain().add(new DummyAdapterConfiguration());
        assertTrue(bufferManager.update("actor", configuration));
        configuration = bufferManager.get("actor");
        assertNotNull(configuration);
        assertEquals(2, configuration.getAdapterChain().size());
    }

    /** Tests the update of an existing buffer. */
    @Test
    public void testUpdateUnknown() {

        assertFalse(bufferManager.update("actor", actorConfig));
    }

    /** Querying buffer names by name. */
    @Test
    public void testQueriesByName() {

        Set<BufferDescription> found;

        assertTrue(bufferManager.create("actor", actorConfig));
        assertTrue(bufferManager.create("sensor", sensorConfig));

        found = bufferManager.queryBuffersByName("x+");
        assertNotNull(found);
        assertEquals(0, found.size());

        found = bufferManager.queryBuffersByName("ct");
        assertNotNull(found);
        assertEquals(1, found.size());

        found = bufferManager.queryBuffersByName("or");
        assertNotNull(found);
        assertEquals(2, found.size());
    }

    /** Querying buffer names by metainfo. */
    @Test
    public void testQueriesByMetainfo() {

        Set<BufferDescription> found;

        assertTrue(bufferManager.create("actor", actorConfig));
        assertTrue(bufferManager.create("sensor", sensorConfig));

        found = bufferManager.queryBuffersByMetainfo("unknown", "yes");
        assertNotNull(found);
        assertEquals(0, found.size());

        found = bufferManager.queryBuffersByMetainfo("sensor", "egal");
        assertNotNull(found);
        assertEquals(0, found.size());

        found = bufferManager.queryBuffersByMetainfo("se", "no");
        assertNotNull(found);
        assertEquals(1, found.size());

        found = bufferManager.queryBuffersByMetainfo("se", ".");
        assertNotNull(found);
        assertEquals(2, found.size());
    }
}
