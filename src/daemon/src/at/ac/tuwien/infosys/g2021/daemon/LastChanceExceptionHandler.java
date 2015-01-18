package at.ac.tuwien.infosys.g2021.daemon;

import at.ac.tuwien.infosys.g2021.common.util.Loggers;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This object handles uncaught exceptions.
 */
class LastChanceExceptionHandler implements Thread.UncaughtExceptionHandler {

    // Is a shutdown requested?
    private boolean alreadyExiting;

    /**
     * Initialization of the exception handling
     */
    LastChanceExceptionHandler() {

        super();
        alreadyExiting = false;
    }

    /**
     * Disables the exception handler while a shutdown is initialed.
     */
    void setShutdownInitiated() { alreadyExiting = true; }

    /**
     * Un uncaught exception was received.
     *
     * @param t der Thread
     * @param e the Exception
     */
    @Override
    public void uncaughtException(Thread t, Throwable e) {

        // In the shutdown phase, uncaught exceptions are ignored.
        if (alreadyExiting) return;

        // Write a log entry
        Logger logger = Loggers.getLogger(this);
        logger.log(Level.SEVERE, "A nonrecoverable system error is occurred:", e);

        // Kill the daemon
        Daemon.get().kill();
    }
}
