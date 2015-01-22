package at.ac.tuwien.infosys.g2021.daemon;

import at.ac.tuwien.infosys.g2021.common.BufferState;
import at.ac.tuwien.infosys.g2021.common.DummyAdapterConfiguration;
import org.junit.Test;

import static org.junit.Assert.assertNull;

/** This is the test of the dummy adapter */
public class DummyAdapterTest extends AbstractAdapterTester<DummyAdapter> {

    /**
     * Creates the test object.
     *
     * @return the test object
     */
    @Override
    protected DummyAdapter initializeTestObject() {

        DummyAdapter result = new DummyAdapter(new DummyAdapterConfiguration());

        result.addValueChangeConsumer(getValueChangeConsumer());
        return result;
    }

    /** Test setting adapter values. */
    @Test
    public void test() {

        // Initial state
        checkValue(getTestObject().get(), BufferState.INITIALIZING);

        // Simulating a value change
        simulateInput(2.0);
        checkValueChange(2.0);

        // Simulating a state change
        simulateInput(BufferState.FAULTED);
        checkValueChange(BufferState.FAULTED);

        // Simulating another value change
        simulateInput(4.0);
        checkValueChange(4.0);

        // Setting the same input is not distributed
        simulateInput(4.0);
        checkValue(getTestObject().get(), 4.0);
        assertNull(lastValueReceived());

        // Simulating a state change
        simulateInput(BufferState.FAULTED);
        checkValueChange(BufferState.FAULTED);

        // Setting the same input is not distributed
        simulateInput(BufferState.FAULTED);
        checkValue(getTestObject().get(), BufferState.FAULTED);
        assertNull(lastValueReceived());
    }
}
