package at.ac.tuwien.infosys.g2021.daemon;

import at.ac.tuwien.infosys.g2021.common.BufferState;
import at.ac.tuwien.infosys.g2021.common.ScalingAdapterConfiguration;
import org.junit.Test;

import static org.junit.Assert.assertNull;

/** This is the test of the scaling adapter */
public class ScalingAdapterTest extends AbstractAdapterTester<ScalingAdapter> {

    /**
     * Creates the test object.
     *
     * @return the test object
     */
    @Override
    protected ScalingAdapter initializeTestObject() {

        ScalingAdapter result = new ScalingAdapter(new ScalingAdapterConfiguration(0.5, 2.0, -1.0));

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
        checkValueChange(5.0);  // 0.5 * 2.0 * 2.0 + 2.0 * 2.0 - 1.0 = 5.0

        // Simulating a state change
        simulateInput(BufferState.FAULTED);
        checkValueChange(BufferState.FAULTED);

        // Simulating another value change
        simulateInput(1.0);
        checkValueChange(1.5); // 0.5 * 1.0 * 1.0 + 2.0 * 1.0 - 1.0 = 1.5

        // Setting the same input is not distributed
        simulateInput(1.0);
        checkValue(getTestObject().get(), 1.5);
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

