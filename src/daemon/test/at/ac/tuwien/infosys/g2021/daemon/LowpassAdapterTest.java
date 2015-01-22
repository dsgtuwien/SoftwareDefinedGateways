package at.ac.tuwien.infosys.g2021.daemon;

import at.ac.tuwien.infosys.g2021.common.BufferState;
import at.ac.tuwien.infosys.g2021.common.LowpassAdapterConfiguration;
import org.junit.Test;

import static org.junit.Assert.assertNull;

/** This is the test of the lowpass adapter */
public class LowpassAdapterTest extends AbstractAdapterTester<LowpassAdapter> {

    /**
     * Creates the test object.
     *
     * @return the test object
     */
    @Override
    protected LowpassAdapter initializeTestObject() {

        LowpassAdapter result = new LowpassAdapter(new LowpassAdapterConfiguration(0.9));

        result.addValueChangeConsumer(getValueChangeConsumer());
        return result;
    }

    /** Test setting adapter values. */
    @Test
    public void test() throws InterruptedException {

        // Initial state
        checkValue(getTestObject().get(), BufferState.INITIALIZING);
        Thread.sleep(500L);

        // A state change to the READY-State -> the default output is assumed.
        simulateInput(2.0);
        checkValueChange(0.0);

        // Every second, an interpolation is done.
        Thread.sleep(1000L);
        checkValueChange(0.2); // 0.9 * 0.0 + (1.0 - 0.9) * 2.0
        Thread.sleep(1000L);
        checkValueChange(0.38); // 0.9 * 0.2 + (1.0 - 0.9) * 2.0

        // A change of the input value has no effect to the output
        simulateInput(10.0);
        checkValue(getTestObject().get(), 0.38);
        assertNull(lastValueReceived());

        // Any input change within the interpolation interval are ignored
        simulateInput(1000000.0);
        simulateInput(-1000000.0);
        simulateInput(10.0);
        checkValue(getTestObject().get(), 0.38);
        assertNull(lastValueReceived());

        // The change of the input value is taken at the next interpolation
        Thread.sleep(1000L);
        checkValueChange(1.342); // 0.9 * 0.38 + (1.0 - 0.9) * 10.0

        // Any change to another state is distributed immediately
        simulateInput(BufferState.FAULTED);
        checkValueChange(BufferState.FAULTED);

        // Setting the same input is not distributed
        simulateInput(BufferState.FAULTED);
        checkValue(getTestObject().get(), BufferState.FAULTED);
        assertNull(lastValueReceived());
    }
}


