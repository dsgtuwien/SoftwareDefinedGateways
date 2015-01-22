package at.ac.tuwien.infosys.g2021.daemon;

import at.ac.tuwien.infosys.g2021.common.BufferState;
import at.ac.tuwien.infosys.g2021.common.TriggeringAdapterConfiguration;
import org.junit.Test;

import static org.junit.Assert.assertNull;

/** This is the test of the triggering adapter */
public class TriggeringAdapterTest extends AbstractAdapterTester<TriggeringAdapter> {

    /**
     * Creates the test object.
     *
     * @return the test object
     */
    @Override
    protected TriggeringAdapter initializeTestObject() {

        TriggeringAdapter result = new TriggeringAdapter(new TriggeringAdapterConfiguration(0.33, 0.67, 1.0, 0.0));

        result.addValueChangeConsumer(getValueChangeConsumer());
        return result;
    }

    /** Test setting adapter values. */
    @Test
    public void test() {

        // Initial state
        checkValue(getTestObject().get(), BufferState.INITIALIZING);

        // Simulating a value change greater than the upper threshold -> output is upper value
        simulateInput(2.0);
        checkValueChange(0.0);

        // Simulating a state change
        simulateInput(BufferState.FAULTED);
        checkValueChange(BufferState.FAULTED);

        // Simulating another value change, lower than the upper threshold -> output is lower value
        simulateInput(0.6);
        checkValueChange(1.0);

        // Setting the value below the upper threshold -> no effect
        simulateInput(0.2);
        checkValue(getTestObject().get(), 1.0);
        assertNull(lastValueReceived());
        simulateInput(0.65);
        checkValue(getTestObject().get(), 1.0);
        assertNull(lastValueReceived());

        // Setting the value greater than the upper threshold -> output is upper value
        simulateInput(0.7);
        checkValueChange(0.0);

        // Setting the value above the lower threshold -> no effect
        simulateInput(0.34);
        checkValue(getTestObject().get(), 0.0);
        assertNull(lastValueReceived());
        simulateInput(0.42);
        checkValue(getTestObject().get(), 0.0);
        assertNull(lastValueReceived());

        // Setting the value lower than the lower threshold -> output is lower value
        simulateInput(0.32);
        checkValueChange(1.0);

        // Simulating a state change
        simulateInput(BufferState.FAULTED);
        checkValueChange(BufferState.FAULTED);

        // Setting the same input is not distributed
        simulateInput(BufferState.FAULTED);
        checkValue(getTestObject().get(), BufferState.FAULTED);
        assertNull(lastValueReceived());
    }
}


