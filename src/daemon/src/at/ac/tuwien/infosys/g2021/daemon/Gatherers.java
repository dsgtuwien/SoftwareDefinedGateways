package at.ac.tuwien.infosys.g2021.daemon;

import at.ac.tuwien.infosys.g2021.common.ActorGathererConfiguration;
import at.ac.tuwien.infosys.g2021.common.DummyGathererConfiguration;
import at.ac.tuwien.infosys.g2021.common.GathererConfiguration;
import at.ac.tuwien.infosys.g2021.common.SensorGathererConfiguration;
import at.ac.tuwien.infosys.g2021.common.TestGathererConfiguration;
import at.ac.tuwien.infosys.g2021.common.util.Loggers;
import at.ac.tuwien.infosys.g2021.common.util.NotYetImplementedError;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/** This is a container, containing all gatherers currently working. There exists only one gatherer per gatherer configuration. */
class Gatherers implements Component {

    // The logger.
    private final static Logger logger = Loggers.getLogger(Gatherers.class);

    // This are all the gatherers, accessible with their configuration as key.
    private Map<GathererConfiguration, Gatherer> gatherers;
    private final Object lock;

    /** Initializing an empty set of gatherers. */
    Gatherers() {

        lock = new Object();
        gatherers = new HashMap<>();
    }

    /** This method is called, after creating all necessary instances. */
    @Override
    public void initialize() { /* There is no extra initialization needed */ }

    /**
     * This method is called, whenever a shutdown sequence is initiated. The
     * shutdown is delegated to all the gatherers and then all gatherers are
     * deleted.
     */
    @Override
    public void shutdown() {

        synchronized (lock) {
            for (Gatherer gatherer : gatherers.values()) gatherer.shutdown();
            gatherers.clear();
        }
    }

    /** This method is called immediately after a shutdown, immediately before the process is stopped. */
    @Override
    public void release() { shutdown(); }

    /**
     * Exists a gatherer with this configuration?
     *
     * @param config the gatherer configuration
     *
     * @return <tt>true</tt> if a gatherer with this configuration exists
     */
    boolean gathererExists(GathererConfiguration config) {

        synchronized (lock) {
            return gatherers.containsKey(config);
        }
    }

    /**
     * Returns a gatherer with this configuration. If the gatherer doesn't exists, it
     * will be created.
     *
     * @param config the gatherer configuration
     *
     * @return the gatherer, which is not <tt>null</tt>
     *
     * @throws IllegalArgumentException if the argument is a wrong configuration
     */
    Gatherer gathererForConfiguration(GathererConfiguration config) throws IllegalArgumentException {

        synchronized (lock) {

            Gatherer result = gatherers.get(config);

            // If no matching gatherer is known, a new one is created.
            if (result == null) {

                switch (config.kindOfGatherer()) {
                    case DUMMY:
                        result = new DummyGatherer((DummyGathererConfiguration)config);
                        break;

                    case TEST:
                        result = new TestGatherer((TestGathererConfiguration)config);
                        break;

                    case ACTOR:
                        result = new ActorGatherer((ActorGathererConfiguration)config);
                        break;

                    case SENSOR:
                        result = new SensorGatherer((SensorGathererConfiguration)config);
                        break;

                    default:
                        throw new NotYetImplementedError("unknown gatherer implementation for gatherer class: " +
                                                         config.kindOfGatherer().name());
                }

                gatherers.put(config, result);
                logger.info(String.format("The gatherer '%s' has been created.", result.toString()));
            }

            return result;
        }
    }

    /**
     * Removes a gatherer from this set of gatherers. If this gatherer isn't known, the method call has no effect.
     *
     * @param gatherer the gatherer to remove
     */
    void remove(Gatherer gatherer) {

        if (gatherer != null) {
            synchronized (lock) {

                GathererConfiguration config = gatherer.getConfiguration();

                if (gatherers.containsKey(config)) {
                    gatherer.shutdown();
                    gatherers.remove(config);
                    logger.info(String.format("The gatherer '%s' has been destroyed.", gatherer.toString()));
                }
            }
        }
    }
}



