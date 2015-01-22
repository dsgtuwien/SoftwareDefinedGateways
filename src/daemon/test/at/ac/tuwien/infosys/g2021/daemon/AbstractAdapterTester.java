package at.ac.tuwien.infosys.g2021.daemon;

import at.ac.tuwien.infosys.g2021.common.BufferState;
import at.ac.tuwien.infosys.g2021.common.SimpleData;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/** This class is designed for testing adapters. */
abstract class AbstractAdapterTester<T extends Adapter> extends AbstractComponentTester<T> {

    /**
     * Shutting down the test object.
     *
     * @param testObject the test object
     */
    @Override
    protected void tearDownTestObject(T testObject) { testObject.shutdown(); }

    /** Setting adapter input. */
    void simulateInput(BufferState state) { getTestObject().valueChanged(new SimpleData(new Date(), state, null)); }

    /** Setting adapter input. */
    void simulateInput(double value) { getTestObject().valueChanged(new SimpleData(new Date(), BufferState.READY, value)); }

    /** Verifying adapter values. */
    protected void checkValue(SimpleData data, BufferState expectedState, Double expectedValue) {

        assertNotNull(data);
        assertNotNull(data.getBufferName());
        assertNotNull(data.getTimestamp());
        assertEquals(expectedState, data.getState());
        if (expectedState == BufferState.READY) {
            assertNotNull(data.getValue());
            assertNotNull(expectedValue);
            assertEquals(expectedValue, data.getValue().doubleValue(), 1.0e-6);
        }
    }

    /** Verifying adapter values. */
    protected void checkValue(SimpleData data, double expectedValue) {

        checkValue(data, BufferState.READY, expectedValue);
    }

    /** Verifying adapter states. */
    protected void checkValue(SimpleData data, BufferState expectedState) {

        checkValue(data, expectedState, null);
    }

    /** Verifying a value change. */
    protected void checkValueChange(double value) {

        checkValue(getTestObject().get(), BufferState.READY, value);
        checkValue(lastValueReceived(), BufferState.READY, value);
        clearLastValueReceived();
    }

    /** Verifying a value change. */
    protected void checkValueChange(BufferState expectedState) {

        checkValue(getTestObject().get(), expectedState, null);
        checkValue(lastValueReceived(), expectedState, null);
        clearLastValueReceived();
    }
}





