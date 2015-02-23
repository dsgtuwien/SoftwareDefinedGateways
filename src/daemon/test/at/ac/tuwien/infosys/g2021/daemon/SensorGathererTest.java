package at.ac.tuwien.infosys.g2021.daemon;

import at.ac.tuwien.infosys.g2021.common.BufferState;
import at.ac.tuwien.infosys.g2021.common.SensorGathererConfiguration;
import at.ac.tuwien.infosys.g2021.common.SimpleData;
import java.util.Date;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

/** This is the test of the sensor gatherer */
public class SensorGathererTest extends AbstractGathererTester<SensorGatherer> {

    /**
     * Creates the test object.
     *
     * @return the test object
     */
    @Override
    protected SensorGatherer initializeTestObject() {

        SensorGatherer result = new SensorGatherer(new SensorGathererConfiguration("XXX"));

        result.addValueChangeConsumer(getValueChangeConsumer());
        return result;
    }

    /** Is a sensor gatherer an actor? */
    @Test
    public void testActor() {

        assertFalse(getTestObject().canUseAsActor());
    }

    /** Test the initial values. */
    @Test
    public void testInitialValue() {

        SimpleData answer = getTestObject().get();
        checkState(answer, BufferState.FAULTED);

        // No value change
        assertNull(lastValueReceived());
    }

    /** Test reading gatherer values. */
    @Test
    public void testGet() {

        // Test working port
        SimpleData driverValue = new SimpleData("XXX", new Date(), BufferState.READY, 17.0);
        getTestObject().valueChanged(driverValue);
        SimpleData answer = getTestObject().get();
        assertEquals(driverValue, answer);

        // Test faulted port
        driverValue = new SimpleData("XXX", new Date(), BufferState.FAULTED);
        getTestObject().valueChanged(driverValue);
        answer = getTestObject().get();
        assertEquals(driverValue, answer);
    }

    /** Test setting gatherer values. */
    @Test
    public void testSet() {

        assertFalse(getTestObject().set(1.0));
    }
}
