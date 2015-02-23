package at.ac.tuwien.infosys.g2021.daemon;

import at.ac.tuwien.infosys.g2021.common.util.Loggers;
import at.ac.tuwien.infosys.g2021.common.util.PanicError;
import at.ac.tuwien.infosys.g2021.daemon.drivers.raspberrypii2cdemo.RaspberryPiI2CDemoHardwareDriverImplementation;
import at.ac.tuwien.infosys.g2021.daemon.drivers.unittest.UnitTestDriverImplementation;
import java.util.logging.Logger;

/** This class knows all implemented hardware drivers and returns the best choice for the current hardware. */
class HardwareDriverFactory {

    // The logger.
    private final static Logger logger = Loggers.getLogger(HardwareDriverFactory.class);

    /**
     * This array contains all implemented hardware drivers.
     *
     * There are only a dummy driver to ensure a nonnull driver selection, a test driver for the unit test
     * and a demo driver for Raspberry Pi, because the original hardware for the G2021 is no longer available.
     */
    private final static HardwareDriverInterface[] availableDrivers = {
            new DummyHardwareDriverImplementation(),
            new UnitTestDriverImplementation(),
            new RaspberryPiI2CDemoHardwareDriverImplementation(),
    };

    /** There are no instances allowed. */
    private HardwareDriverFactory() {}

    /**
     * Select the best choice of the drive.
     *
     * @return the best suitable driver
     */
    static HardwareDriverInterface select() {

        HardwareDriverInterface result = null;

        for (HardwareDriverInterface driver : availableDrivers) {

            String name = driver.getName();

            if (driver.isSuitable()) {
                result = driver;
                if (driver.isBestChoice()) break;
                else logger.fine("The driver '" + name + "' is suitable, but it may not the best choice.");
            }
            else {
                logger.fine("The driver '" + name + "' is not suitable.");
            }
        }

        // Now a suitable driver must exists.
        if (result == null) throw new PanicError("no driver found");

        logger.config("The driver '" + result.getName() + "' is used.");
        return result;
    }
}

