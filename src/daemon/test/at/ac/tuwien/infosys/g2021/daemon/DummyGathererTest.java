package at.ac.tuwien.infosys.g2021.daemon;

import at.ac.tuwien.infosys.g2021.common.DummyGathererConfiguration;
import at.ac.tuwien.infosys.g2021.common.SimpleData;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/** This is the test of the dummy gatherer */
public class DummyGathererTest extends AbstractGathererTester<DummyGatherer> {

    /**
     * Creates the test object.
     *
     * @return the test object
     */
    @Override
    protected DummyGatherer initializeTestObject() {

        DummyGatherer result = new DummyGatherer(new DummyGathererConfiguration());

        result.addValueChangeConsumer(getValueChangeConsumer());
        return result;
    }

    /** Is a dummy gatherer an actor? */
    @Test
    public void testActor() {

        assertTrue(getTestObject().canUseAsActor());
    }

    /** Test the initial values. */
    @Test
    public void testInitialValue() {

        SimpleData answer = getTestObject().get();
        checkValue(answer, 0.0);

        // No value change
        assertNull(lastValueReceived());
    }

    /** Test setting gatherer values. */
    @Test
    public void testGetSet() {

        assertTrue(getTestObject().set(17.0));

        SimpleData answer = getTestObject().get();
        checkValue(answer, 17.0);

        // value change received
        assertNotNull(lastValueReceived());
        checkValue(lastValueReceived(), 17.0);
        clearLastValueReceived();

        // Set to another value
        assertTrue(getTestObject().set(-11.0));

        answer = getTestObject().get();
        checkValue(answer, -11.0);

        // value change received
        assertNotNull(lastValueReceived());
        checkValue(lastValueReceived(), -11.0);
    }
}
