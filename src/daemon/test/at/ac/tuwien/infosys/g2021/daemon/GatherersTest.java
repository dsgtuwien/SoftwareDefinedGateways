package at.ac.tuwien.infosys.g2021.daemon;

import at.ac.tuwien.infosys.g2021.common.DummyGathererConfiguration;
import at.ac.tuwien.infosys.g2021.common.TestGathererConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/** This tester checks the set of gatherers. */
public class GatherersTest {

    // The test object
    private Gatherers testObject;

    /** Setting up the test object. */
    @Before
    public void setUp() throws Exception {

        // We start a daemon, because some the test object may use some other
        // components of a daemon.
        Daemon.main(new String[] {"-<unit-test>: don-t-exit-on-shutdown", "-<unit-test>: don-t-open-socket"});

        testObject = new Gatherers();
    }

    /** removing up the test object. */
    @After
    public void tearDown() throws Exception {

        testObject.shutdown();
        testObject.release();
        testObject = null;

        Daemon.get().stop();
    }

    /** Test gatherer creation. */
    @Test
    public void testGathererCreation() {

        DummyGathererConfiguration dummyGathererConfiguration = new DummyGathererConfiguration();
        TestGathererConfiguration testGathererConfiguration = new TestGathererConfiguration();

        assertFalse(testObject.gathererExists(dummyGathererConfiguration));
        assertFalse(testObject.gathererExists(testGathererConfiguration));

        // Creating a gatherer
        Gatherer gatherer = testObject.gathererForConfiguration(dummyGathererConfiguration);

        assertNotNull(gatherer);
        assertTrue(gatherer instanceof DummyGatherer);
        assertTrue(testObject.gathererExists(dummyGathererConfiguration));
        assertFalse(testObject.gathererExists(testGathererConfiguration));

        // A second create call will return the same gatherer instance.
        assertNotNull(gatherer);
        assertTrue(testObject.gathererForConfiguration(dummyGathererConfiguration) == gatherer);
        assertTrue(testObject.gathererExists(dummyGathererConfiguration));
        assertFalse(testObject.gathererExists(testGathererConfiguration));
    }

    /** Test gatherer deletion. */
    @Test
    public void testGathererDeletion() {

        DummyGathererConfiguration dummyGathererConfiguration = new DummyGathererConfiguration();
        TestGathererConfiguration testGathererConfiguration = new TestGathererConfiguration();

        assertFalse(testObject.gathererExists(dummyGathererConfiguration));
        assertFalse(testObject.gathererExists(testGathererConfiguration));

        // Creating a gatherer
        Gatherer gatherer = testObject.gathererForConfiguration(dummyGathererConfiguration);

        assertNotNull(gatherer);
        assertTrue(gatherer instanceof DummyGatherer);
        assertTrue(testObject.gathererExists(dummyGathererConfiguration));
        assertFalse(testObject.gathererExists(testGathererConfiguration));

        // Removing the gatherer
        testObject.remove(gatherer);
        assertFalse(testObject.gathererExists(dummyGathererConfiguration));
        assertFalse(testObject.gathererExists(testGathererConfiguration));
    }
}

