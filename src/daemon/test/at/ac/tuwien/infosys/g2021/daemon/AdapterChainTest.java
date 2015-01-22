package at.ac.tuwien.infosys.g2021.daemon;

import at.ac.tuwien.infosys.g2021.common.BufferConfiguration;
import at.ac.tuwien.infosys.g2021.common.BufferState;
import at.ac.tuwien.infosys.g2021.common.FilteringAdapterConfiguration;
import at.ac.tuwien.infosys.g2021.common.ScalingAdapterConfiguration;
import at.ac.tuwien.infosys.g2021.common.SimpleData;
import java.util.Date;
import org.junit.Test;

import static org.junit.Assert.assertNull;

/**
 * This is the test of the adapter chain. Our test chain consists of two adapters.
 * A scaling adapter is followed by a filtering adapter.
 */
public class AdapterChainTest extends AbstractAdapterTester<AdapterChain> {

    /**
     * Creates the test object.
     *
     * @return the test object
     */
    @Override
    protected AdapterChain initializeTestObject() {

        BufferConfiguration bufferConfiguration = new BufferConfiguration();
        bufferConfiguration.getAdapterChain().add(new ScalingAdapterConfiguration(1.0, 2.0, 0.0));
        bufferConfiguration.getAdapterChain().add(new FilteringAdapterConfiguration(1.0));

        AdapterChain result = new AdapterChain(bufferConfiguration);

        result.addValueChangeConsumer(getValueChangeConsumer());
        return result;
    }

    /** Test setting adapter values. */
    @Test
    public void test() {

        // Initial state
        checkValue(getTestObject().get(), BufferState.INITIALIZING);

        // Simulating a value change
        getTestObject().put(new SimpleData(new Date(), BufferState.READY, 2.0));
        checkValueChange(8.0);

        // Simulating a state change
        getTestObject().put(new SimpleData(new Date(), BufferState.FAULTED));
        checkValueChange(BufferState.FAULTED);

        // Simulating another value change
        getTestObject().put(new SimpleData(new Date(), BufferState.READY, 4.0));
        checkValueChange(24.0);

        // Simulating a marginal input change -> is not distributed
        getTestObject().put(new SimpleData(new Date(), BufferState.READY, 4.05));
        checkValue(getTestObject().get(), 24.0);
        assertNull(lastValueReceived());

        // Simulating a state change
        getTestObject().put(new SimpleData(new Date(), BufferState.FAULTED));
        checkValueChange(BufferState.FAULTED);

        // Setting the same input is not distributed
        getTestObject().put(new SimpleData(new Date(), BufferState.FAULTED));
        checkValue(getTestObject().get(), BufferState.FAULTED);
        assertNull(lastValueReceived());
    }
}
