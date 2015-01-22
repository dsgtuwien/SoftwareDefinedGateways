package at.ac.tuwien.infosys.g2021.daemon;

import at.ac.tuwien.infosys.g2021.common.BufferClass;
import at.ac.tuwien.infosys.g2021.common.BufferConfiguration;
import at.ac.tuwien.infosys.g2021.common.BufferState;
import at.ac.tuwien.infosys.g2021.common.DummyGathererConfiguration;
import at.ac.tuwien.infosys.g2021.common.ScalingAdapterConfiguration;
import at.ac.tuwien.infosys.g2021.common.SimpleData;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * This class tests the functionality of buffers. For this test, we uses two buffers
 * acting with a dummy gatherer. The first buffer is an actor and writes the value
 * 2 * n + 1 to the gatherer. The second buffer is configured as sensor and returns
 * 0.5 * n, where n is the gatherer value.
 */
public class BufferTest {

    // Both test objects and their configurations
    private Buffer actor;
    private Buffer sensor;

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

        // Setting up the actor
        actorConfig = new BufferConfiguration();
        actorConfig.setBufferClass(BufferClass.ACTOR);
        actorConfig.getMetainfo().put("sensor", "no");
        actorConfig.getAdapterChain().add(new ScalingAdapterConfiguration(0.0, 2.0, 1.0));
        actorConfig.setGatherer(new DummyGathererConfiguration());
        actor = new Buffer("actor", actorConfig);

        // Setting up the sensor
        sensorConfig = new BufferConfiguration();
        sensorConfig.setBufferClass(BufferClass.SENSOR);
        sensorConfig.getMetainfo().put("sensor", "yes");
        sensorConfig.getAdapterChain().add(new ScalingAdapterConfiguration(0.0, 0.5, 0.0));
        sensorConfig.setGatherer(new DummyGathererConfiguration());
        sensor = new Buffer("sensor", sensorConfig);
        sensor.addValueChangeConsumer(new Listener());
    }

    /** removing the test objects. */
    @After
    public void tearDown() throws Exception {

        actor.shutdown();
        actor = null;

        sensor.shutdown();
        sensor = null;

        Daemon.get().stop();
    }

    /** Uses the buffers the right name? */
    @Test
    public void testNames() {

        assertEquals("actor", actor.getName());
        assertEquals("sensor", sensor.getName());
    }

    /** Tests the buffer configurations */
    @Test
    public void testConfigurations() {

        assertTrue(actor.getConfiguration() == actorConfig);
        assertTrue(sensor.getConfiguration() == sensorConfig);
    }

    /** Tests the buffer metadata */
    @Test
    public void testMetadata() {

        assertEquals("no", actor.getMetainfo().get("sensor"));
        assertEquals("yes", sensor.getMetainfo().get("sensor"));
    }

    /** Tests the buffer functionality. */
    @Test
    public void test() {

        // Setting the actor to 10.0. Therefore the gatherer value must be
        // 2.0 * 10.0 + 1.0 = 21.0. The sensor gets the gatherer value and
        // its value become 0.5 * 21.0 = 10.5!
        actor.put(10.0);
        assertEquals(10.0, actor.get().getValue().doubleValue(), 1.0e-6);
        assertEquals(10.5, sensor.get().getValue().doubleValue(), 1.0e-6);
        assertEquals(10.5, lastValue.getValue().doubleValue(), 1.0e-6);

        // Now we simulate a hardware problem
        DummyGatherer gatherer = (DummyGatherer)Daemon.get().gatherers().gathererForConfiguration(new DummyGathererConfiguration());
        gatherer.set(BufferState.FAULTED);
        assertEquals(BufferState.FAULTED, actor.get().getState());
        assertEquals(BufferState.FAULTED, sensor.get().getState());
        assertEquals(BufferState.FAULTED, lastValue.getState());

        // The fault has been repaired
        gatherer.set(BufferState.READY);
        assertEquals(BufferState.FAULTED, actor.get().getState());
        assertEquals(BufferState.READY, sensor.get().getState());
        assertEquals(BufferState.READY, lastValue.getState());

        // Setting the same old value
        actor.put(10.0);
        assertEquals(10.0, actor.get().getValue().doubleValue(), 1.0e-6);
        assertEquals(10.5, sensor.get().getValue().doubleValue(), 1.0e-6);
        assertEquals(10.5, lastValue.getValue().doubleValue(), 1.0e-6);
    }
}

