package at.ac.tuwien.infosys.g2021.daemon;

import at.ac.tuwien.infosys.g2021.common.BufferState;
import at.ac.tuwien.infosys.g2021.common.SimpleData;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/** This class is designed for testing gatherers. */
abstract class AbstractGathererTester<T extends Gatherer> extends AbstractComponentTester<T> {

    /**
     * Shutting down the test object.
     *
     * @param testObject the test object
     */
    @Override
    protected void tearDownTestObject(T testObject) { testObject.shutdown(); }

    /** Verifying gatherer values. */
    protected void checkValue(SimpleData data, double expected) {

        assertNotNull(data);
        assertNotNull(data.getTimestamp());
        assertEquals(BufferState.READY, data.getState());
        assertNotNull(data.getValue());
        assertEquals(expected, data.getValue().doubleValue(), 1.0e-6);
    }

    /** Verifying gatherer states. */
    protected void checkState(SimpleData data, BufferState state) {

        assertNotNull(data);
        assertNotNull(data.getTimestamp());
        assertEquals(state, data.getState());
    }
}


