package at.ac.tuwien.infosys.g2021.daemon;

import at.ac.tuwien.infosys.g2021.common.BufferState;
import at.ac.tuwien.infosys.g2021.common.FilteringAdapterConfiguration;
import org.junit.Test;

import static org.junit.Assert.assertNull;

/** This is the test of the filtering adapter */
public class FilteringAdapterTest extends AbstractAdapterTester<FilteringAdapter> {

    /**
     * Creates the test object.
     *
     * @return the test object
     */
    @Override
    protected FilteringAdapter initializeTestObject() {

        FilteringAdapter result = new FilteringAdapter(new FilteringAdapterConfiguration(0.1));

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

        // After a state change the first value is not filtered
        simulateInput(2.0);
        checkValueChange(2.0);

        // Marginal value change is ignored
        simulateInput(1.91);
        checkValue(getTestObject().get(), 2.0);
        assertNull(lastValueReceived());

        simulateInput(2.09);
        checkValue(getTestObject().get(), 2.0);
        assertNull(lastValueReceived());

        // Setting a greater value change is distributed
        simulateInput(2.2);
        checkValueChange(2.2);
        simulateInput(2.0);
        checkValueChange(2.0);

        // Simulating a state change
        simulateInput(BufferState.FAULTED);
        checkValueChange(BufferState.FAULTED);

        // Setting the same input state is not distributed
        simulateInput(BufferState.FAULTED);
        checkValue(getTestObject().get(), BufferState.FAULTED);
        assertNull(lastValueReceived());
    }
}



