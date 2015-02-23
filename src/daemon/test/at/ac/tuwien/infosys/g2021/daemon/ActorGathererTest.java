package at.ac.tuwien.infosys.g2021.daemon;

import at.ac.tuwien.infosys.g2021.common.ActorGathererConfiguration;
import at.ac.tuwien.infosys.g2021.common.BufferState;
import at.ac.tuwien.infosys.g2021.common.SimpleData;
import java.util.Date;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/** This is the test of the actor gatherer */
public class ActorGathererTest extends AbstractGathererTester<ActorGatherer> {

    /** Setting up the test object. */
    @Override
    @Before
    public void setUp() throws Exception {

        System.setProperty("at.ac.tuwien.infosys.g2021.unit.test", "unit-test");
        super.setUp();
        Thread.sleep(100L);
    }

    /** removing the test object. */
    @Override
    @After
    public void tearDown() throws Exception {

        super.tearDown();
        System.setProperty("at.ac.tuwien.infosys.g2021.unit.test", "");
    }

    /**
     * Creates the test object.
     *
     * @return the test object
     */
    @Override
    protected ActorGatherer initializeTestObject() {

        ActorGatherer result = new ActorGatherer(new ActorGathererConfiguration("AO"));

        result.addValueChangeConsumer(getValueChangeConsumer());
        return result;
    }

    /** Is a actor gatherer an actor? */
    @Test
    public void testActor() {

        assertTrue(getTestObject().canUseAsActor());
    }

    /** Test the initial values. */
    @Test
    public void testInitialValue() {

        SimpleData answer = getTestObject().get();
        checkState(answer, BufferState.INITIALIZING);

        // No value change
        assertNull(lastValueReceived());
    }

    /** Test setting gatherer values. */
    @Test
    public void testSet() {

        assertTrue(getTestObject().set(1.0));
        assertEquals(new SimpleData("XXX", new Date(), BufferState.READY, 1.0), getTestObject().get());
    }
}
