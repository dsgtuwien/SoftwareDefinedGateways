package at.ac.tuwien.infosys.g2021.daemon;

import at.ac.tuwien.infosys.g2021.common.ActorGathererConfiguration;
import at.ac.tuwien.infosys.g2021.common.BufferState;
import at.ac.tuwien.infosys.g2021.common.SensorGathererConfiguration;
import at.ac.tuwien.infosys.g2021.common.SimpleData;
import at.ac.tuwien.infosys.g2021.daemon.drivers.unittest.UnitTestDriverImplementation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/** This is the test of the actor gatherer */
public class DriverTest {

    /** Setting up the test object. */
    @Before
    public void setUp() throws Exception {

        System.setProperty("at.ac.tuwien.infosys.g2021.unit.test", "unit-test");

        // We start a daemon, because some the test object may use some other
        // components of a daemon.
        Daemon.main(new String[] {"-<unit-test>: don-t-exit-on-shutdown", "-<unit-test>: don-t-open-socket"});

        // Let the driver some time to initialize
        Thread.sleep(100L);
    }

    /** removing the test object. */
    @After
    public void tearDown() throws Exception {

        // Stop the daemon
        Daemon.get().stop();
        System.setProperty("at.ac.tuwien.infosys.g2021.unit.test", "");
    }

    /** Test the daemon configuration of the driver */
    @Test
    public void testInitialization() {

        // Check the buffers
        Buffers buffers = Daemon.get().buffers();

        Buffer buffer = buffers.bufferByName("AI");
        assertEquals("AI", buffer.getName());
        assertTrue(buffer.isHardwareBuffer());
        assertFalse(buffer.isActor());
        assertEquals("AI", buffer.getMetainfo().get("type"));

        buffer = buffers.bufferByName("AO");
        assertEquals("AO", buffer.getName());
        assertTrue(buffer.isHardwareBuffer());
        assertTrue(buffer.isActor());
        assertEquals("AO", buffer.getMetainfo().get("type"));

        buffer = buffers.bufferByName("DI");
        assertEquals("DI", buffer.getName());
        assertTrue(buffer.isHardwareBuffer());
        assertFalse(buffer.isActor());
        assertEquals("DI", buffer.getMetainfo().get("type"));

        buffer = buffers.bufferByName("DO");
        assertEquals("DO", buffer.getName());
        assertTrue(buffer.isHardwareBuffer());
        assertTrue(buffer.isActor());
        assertEquals("DO", buffer.getMetainfo().get("type"));

        // Check the gatherers
        Gatherers gatherers = Daemon.get().gatherers();
        assertTrue(gatherers.gathererExists(new SensorGathererConfiguration("AI")));
        assertTrue(gatherers.gathererExists(new SensorGathererConfiguration("DI")));
        assertFalse(gatherers.gathererExists(new SensorGathererConfiguration("AO")));
        assertFalse(gatherers.gathererExists(new SensorGathererConfiguration("DO")));
        assertFalse(gatherers.gathererExists(new ActorGathererConfiguration("AI")));
        assertFalse(gatherers.gathererExists(new ActorGathererConfiguration("DI")));
        assertTrue(gatherers.gathererExists(new ActorGathererConfiguration("AO")));
        assertTrue(gatherers.gathererExists(new ActorGathererConfiguration("DO")));

        // Check the driver
        assertTrue(HardwareDriverFactory.select() instanceof UnitTestDriverImplementation);
    }

    /** Test input changes */
    @Test
    public void testInputChanges() throws Exception {

        UnitTestDriverImplementation driver = (UnitTestDriverImplementation)HardwareDriverFactory.select();
        Buffer buffer = Daemon.get().buffers().bufferByName("AI");

        SimpleData value = buffer.get();
        assertEquals("AI", value.getBufferName());
        assertEquals(BufferState.READY, value.getState());
        assertEquals(0.0, value.getValue().doubleValue(), 1.0e-6);

        driver.setPortValue("AI", 33.0);

        Thread.sleep(1200L);

        value = buffer.get();
        assertEquals("AI", value.getBufferName());
        assertEquals(BufferState.READY, value.getState());
        assertEquals(33.0, value.getValue().doubleValue(), 1.0e-6);

        driver.disturbPort("AI");

        Thread.sleep(1200L);

        value = buffer.get();
        assertEquals("AI", value.getBufferName());
        assertEquals(BufferState.FAULTED, value.getState());

        driver.setPortValue("AI", -12.0);

        Thread.sleep(1200L);

        value = buffer.get();
        assertEquals("AI", value.getBufferName());
        assertEquals(BufferState.READY, value.getState());
        assertEquals(-12.0, value.getValue().doubleValue(), 1.0e-6);
    }

    /** Test output changes */
    @Test
    public void testOutputChanges() throws Exception {

        UnitTestDriverImplementation driver = (UnitTestDriverImplementation)HardwareDriverFactory.select();
        Buffer buffer = Daemon.get().buffers().bufferByName("AO");

        SimpleData value = buffer.get();
        assertEquals("AO", value.getBufferName());
        assertEquals(BufferState.INITIALIZING, value.getState());

        buffer.put(33.0);

        Thread.sleep(200L);

        value = buffer.get();
        assertEquals("AO", value.getBufferName());
        assertEquals(BufferState.READY, value.getState());
        assertEquals(33.0, value.getValue().doubleValue(), 1.0e-6);

        driver.disturbPort("AO");
        buffer.put(17.0);

        Thread.sleep(200L);

        value = buffer.get();
        assertEquals("AO", value.getBufferName());
        assertEquals(BufferState.FAULTED, value.getState());

        driver.setPortValue("AO", -0.0);
        buffer.put(-12.0);

        Thread.sleep(200L);

        value = buffer.get();
        assertEquals("AO", value.getBufferName());
        assertEquals(BufferState.READY, value.getState());
        assertEquals(-12.0, value.getValue().doubleValue(), 1.0e-6);
    }
}

