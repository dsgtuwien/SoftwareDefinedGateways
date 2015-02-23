package at.ac.tuwien.infosys.g2021.intf;

import at.ac.tuwien.infosys.g2021.common.BufferClass;
import at.ac.tuwien.infosys.g2021.common.BufferConfiguration;
import at.ac.tuwien.infosys.g2021.common.DummyGathererConfiguration;
import at.ac.tuwien.infosys.g2021.common.ScalingAdapterConfiguration;
import at.ac.tuwien.infosys.g2021.daemon.Daemon;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/** The test of the TimeControl class. */
public class TimeControlTest {

    // The test objects
    private DataPoint dataPoint;
    private TimeControl timeControl;

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
        dataPoint.assign("actor");
        dataPoint.assign("sensor");
        timeControl = dataPoint.getTimeControl();
    }

    /** removing the test objects. */
    @After
    public void tearDown() throws Exception {

        // Releasing the test object
        timeControl.stop();
        timeControl = null;

        dataPoint.release();
        dataPoint = null;

        Daemon.get().stop();
    }

    /** Test a nonzero initial delay. */
    @Test
    public void testInitialDelay() throws Exception {

        timeControl.setInitialDelay(250);
        timeControl.setDelay(100);
        timeControl.setCount(0);

        timeControl.restart();
        assertEquals(0, timeControl.getResultQueue().size());
        assertTrue(timeControl.isRunning());

        Thread.sleep(200);

        assertEquals(0, timeControl.getResultQueue().size());
        assertTrue(timeControl.isRunning());

        Thread.sleep(100);

        assertEquals(1, timeControl.getResultQueue().size());
        assertTrue(timeControl.isRunning());
    }

    /** Tests an initial delay of zero. */
    @Test
    public void testZeroInitialDelay() throws Exception {

        timeControl.setInitialDelay(0);
        timeControl.setDelay(200);
        timeControl.setCount(0);

        timeControl.restart();
        assertEquals(1, timeControl.getResultQueue().size());
        assertTrue(timeControl.isRunning());

        Thread.sleep(150);

        assertEquals(1, timeControl.getResultQueue().size());
        assertTrue(timeControl.isRunning());

        Thread.sleep(100);

        assertEquals(2, timeControl.getResultQueue().size());
        assertTrue(timeControl.isRunning());
    }

    /** Test a nonzero count. */
    @Test
    public void testNonzeroCount() throws Exception {

        timeControl.setInitialDelay(100);
        timeControl.setDelay(100);
        timeControl.setCount(1);

        timeControl.restart();
        assertEquals(0, timeControl.getResultQueue().size());
        assertTrue(timeControl.isRunning());

        Thread.sleep(50);

        assertEquals(0, timeControl.getResultQueue().size());
        assertTrue(timeControl.isRunning());

        Thread.sleep(100);

        assertEquals(1, timeControl.getResultQueue().size());
        assertFalse(timeControl.isRunning());
    }

    /** Test a nonzero count. */
    @Test
    public void testCount1NoInitialDelay() throws Exception {

        timeControl.setInitialDelay(0);
        timeControl.setDelay(100);
        timeControl.setCount(1);

        timeControl.restart();
        assertEquals(1, timeControl.getResultQueue().size());
        assertFalse(timeControl.isRunning());
    }

    /** Test start/stop. */
    @Test
    public void testStartStop() throws Exception {

        timeControl.setInitialDelay(100);
        timeControl.setDelay(100);
        timeControl.setCount(2);

        timeControl.restart();
        assertEquals(0, timeControl.getResultQueue().size());
        assertTrue(timeControl.isRunning());

        Thread.sleep(50);

        assertEquals(0, timeControl.getResultQueue().size());
        assertTrue(timeControl.isRunning());

        timeControl.stop();

        assertEquals(0, timeControl.getResultQueue().size());
        assertFalse(timeControl.isRunning());

        Thread.sleep(200);

        assertEquals(0, timeControl.getResultQueue().size());
        assertFalse(timeControl.isRunning());

        timeControl.start();

        assertEquals(0, timeControl.getResultQueue().size());
        assertTrue(timeControl.isRunning());

        Thread.sleep(150);

        assertEquals(1, timeControl.getResultQueue().size());
        assertTrue(timeControl.isRunning());

        timeControl.stop();

        assertEquals(1, timeControl.getResultQueue().size());
        assertFalse(timeControl.isRunning());

        Thread.sleep(200);

        assertEquals(1, timeControl.getResultQueue().size());
        assertFalse(timeControl.isRunning());

        timeControl.start();

        Thread.sleep(150);

        assertEquals(2, timeControl.getResultQueue().size());
        assertFalse(timeControl.isRunning());
    }
}

