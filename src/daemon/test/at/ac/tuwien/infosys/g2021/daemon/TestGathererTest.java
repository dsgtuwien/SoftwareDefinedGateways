package at.ac.tuwien.infosys.g2021.daemon;

import at.ac.tuwien.infosys.g2021.common.TestGathererConfiguration;
import org.junit.Test;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/** This is the test of the test gatherer */
public class TestGathererTest extends AbstractGathererTester<TestGatherer> {

    /**
     * Creates the test object.
     *
     * @return the test object
     */
    @Override
    protected TestGatherer initializeTestObject() {

        TestGatherer result = new TestGatherer(new TestGathererConfiguration());

        result.addValueChangeConsumer(getValueChangeConsumer());
        return result;
    }

    /** Is a test gatherer an actor? */
    @Test
    public void testActor() {

        assertTrue(getTestObject().canUseAsActor());
    }

    /** Test getting gatherer values. */
    @Test
    public void testGet() throws InterruptedException {

        double expected = 0.0;
        Thread.sleep(500L);

        checkValue(getTestObject().get(), expected);
        assertNull(lastValueReceived());
        Thread.sleep(1000L);
        expected += 0.1;

        for (int i = 0; i < 10; i++) {
            checkValue(getTestObject().get(), expected);
            checkValue(lastValueReceived(), expected);
            clearLastValueReceived();
            Thread.sleep(1000L);
            expected += 0.1;
        }

        expected = 0.0;
        checkValue(getTestObject().get(), expected);
        checkValue(lastValueReceived(), expected);
    }

    /** Test setting gatherer values. */
    @Test
    public void testSet() throws InterruptedException {

        Thread.sleep(500L);

        // Check initial value
        checkValue(getTestObject().get(), 0.0);
        assertNull(lastValueReceived());

        // Check first automatic increment
        Thread.sleep(1000L);
        checkValue(getTestObject().get(), 0.1);
        checkValue(lastValueReceived(), 0.1);
        clearLastValueReceived();

        // Set value to 2.0
        getTestObject().set(2.0);
        checkValue(getTestObject().get(), 2.0);
        checkValue(lastValueReceived(), 2.0);
        clearLastValueReceived();

        // Check automatic decrement
        Thread.sleep(1000L);
        checkValue(getTestObject().get(), 1.0);
        checkValue(lastValueReceived(), 1.0);
        clearLastValueReceived();

        // Check automatic decrement
        Thread.sleep(1000L);
        checkValue(getTestObject().get(), 0.0);
        checkValue(lastValueReceived(), 0.0);
        clearLastValueReceived();

        // Set value to -2.0
        getTestObject().set(-2.0);
        checkValue(getTestObject().get(), -2.0);
        checkValue(lastValueReceived(), -2.0);
        clearLastValueReceived();

        // Check automatic increment
        Thread.sleep(1000L);
        checkValue(getTestObject().get(), -1.9);
        checkValue(lastValueReceived(), -1.9);
        clearLastValueReceived();
    }
}
