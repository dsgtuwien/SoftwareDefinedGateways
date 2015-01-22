package at.ac.tuwien.infosys.g2021.daemon;

import at.ac.tuwien.infosys.g2021.common.SimpleData;
import org.junit.After;
import org.junit.Before;

/** This is an abstract tester common for the components and classes of a daemon. */
abstract class AbstractComponentTester<T> {

    // This inner class is a ValueChangeConsumer.
    private class Listener implements ValueChangeConsumer {

        @Override
        public void valueChanged(SimpleData newValue) { lastValue = newValue; }
    }

    // This classes can act as ValueChangeConsumer. This is the last value change consumed.
    private SimpleData lastValue;

    // This class is the test object.
    private T testObject;

    /** Setting up the test object. */
    @Before
    public void setUp() throws Exception {

        // We start a daemon, because some the test object may use some other
        // components of a daemon.
        Daemon.main(new String[] {"-<unit-test>: don-t-exit-on-shutdown", "-<unit-test>: don-t-open-socket"});

        testObject = initializeTestObject();
        lastValue = null;
    }

    /** removing the test object. */
    @After
    public void tearDown() throws Exception {

        tearDownTestObject(testObject);
        testObject = null;

        Daemon.get().stop();
    }

    /**
     * Creates the test object.
     *
     * @return the test object
     */
    abstract protected T initializeTestObject();

    /**
     * Returns the test object.
     *
     * @return the test object
     */
    protected T getTestObject() { return testObject; }

    /**
     * Shutting down the test object.
     *
     * @param testObject the test object
     */
    abstract protected void tearDownTestObject(T testObject);

    /**
     * Returns a ValueChangeConsumer instance.
     *
     * @return the value change consumer
     */
    protected ValueChangeConsumer getValueChangeConsumer() { return new Listener(); }

    /**
     * Reads the last value change.
     *
     * @return the last value received
     */
    protected SimpleData lastValueReceived() { return lastValue; }

    /** Resets the last value change. */
    protected void clearLastValueReceived() { lastValue = null; }
}

