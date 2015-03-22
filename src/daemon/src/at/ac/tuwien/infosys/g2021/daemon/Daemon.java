package at.ac.tuwien.infosys.g2021.daemon;

import at.ac.tuwien.infosys.g2021.common.BufferConfiguration;
import at.ac.tuwien.infosys.g2021.common.SimpleData;
import at.ac.tuwien.infosys.g2021.common.communication.ClientEndpoint;
import at.ac.tuwien.infosys.g2021.common.communication.ClientRequestExecutionStrategy;
import at.ac.tuwien.infosys.g2021.common.communication.CommunicationSettings;
import at.ac.tuwien.infosys.g2021.common.communication.DaemonEndpoint;
import at.ac.tuwien.infosys.g2021.common.util.Loggers;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Set;
import java.util.Timer;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * This is the main class of the G2021 buffer daemon. The behaviour of the daemon can be controlled by
 * command line options. The following options ara available:
 * <ul>
 * <li><b>-stop</b> sends a "shutdown"-message to a running daemon and shuts it down</li>
 * <li><b>-version</b> displays the version and terminates immediately</li>
 * </ul>
 * The class daemon is also the data structure to keep all important parts of the daemon together,
 * initialize them and shut them down. It is designed as singleton, because there must not exist some
 * duplicates.
 */
public class Daemon {

    // The version of the daemon:
    private final static String VERSION = "1.0.1";

    // The logger.
    private final static Logger logger = Loggers.getLogger(Daemon.class);

    // This simple thread listens for incoming connections from the server socket.
    private class ConnectionListener extends Thread {

        /** Initialization. */
        ConnectionListener() { super("connection listener thread"); }

        /** The thread implementation. */
        @Override
        public void run() {

            // Make a copy to be thread safe
            ServerSocket serverSocket = socket;
            while (serverSocket != null && !serverSocket.isClosed()) {
                try {
                    addConnection(new DaemonEndpoint(serverSocket.accept(), new ClientRequestImplementation()));
                }
                catch (IOException e) {
                    if (!serverSocket.isClosed()) {
                        logger.log(Level.WARNING, "Establishing an incoming connection has failed.", e);
                    }
                }
                finally {
                    serverSocket = socket;
                }
            }
        }
    }

    /** This is the implementation of the client request handling. */
    private class ClientRequestImplementation implements ClientRequestExecutionStrategy {

        /**
         * A shutdown request has been received.
         *
         * @param conn the connection
         */
        @Override
        public void shutdown(DaemonEndpoint conn) { stop(); }

        /**
         * This method must handle a communication error at the client connection.
         *
         * @param conn the connection
         */
        @Override
        public void connectionDied(DaemonEndpoint conn) { removeConnection(conn); }

        /**
         * Exists a buffer with a given name?
         *
         * @param conn       the connection
         * @param bufferName the name of the buffer
         *
         * @return <tt>true</tt>, if a buffer with this name is known
         */
        @Override
        public boolean bufferExists(DaemonEndpoint conn, String bufferName) { return buffers.bufferByName(bufferName) != null; }

        /**
         * Returns the configuration of a named buffer.
         *
         * @param conn       the connection
         * @param bufferName the name of the buffer
         *
         * @return the buffer configuration or <tt>null</tt>, is no buffer with this name is known
         */
        @Override
        public BufferConfiguration bufferConfiguration(DaemonEndpoint conn, String bufferName) {

            Buffer buffer = buffers.bufferByName(bufferName);

            return buffer == null ? null : buffer.getConfiguration();
        }

        /**
         * Represents this buffer a hardware port? This kind of buffer cannot be updated or removed.
         *
         * @param conn       the connection
         * @param bufferName the name of the buffer
         *
         * @return <tt>true</tt>, if this buffer represents a hardware port
         */
        @Override
        public boolean isHardwareBuffer(DaemonEndpoint conn, String bufferName) {

            Buffer buffer = buffers.bufferByName(bufferName);

            return buffer != null && buffer.isHardwareBuffer();
        }

        /**
         * Changes the configuration of a buffer. If the buffer doesn't exists and the
         * <tt>create</tt>-argument is set to <tt>true</tt>, a new buffer is created.
         *
         * @param conn       the connection
         * @param bufferName the name of the buffer
         * @param config     the buffer configuration
         * @param create     is the creation of a new buffer allowed
         *
         * @return <tt>true</tt>, if the buffer configuration is changed
         */
        @Override
        public boolean setBufferConfiguration(DaemonEndpoint conn, String bufferName, BufferConfiguration config, boolean create) {

            boolean result = false;

            Buffer existing = buffers.bufferByName(bufferName);

            if (existing != null && !existing.isHardwareBuffer() || existing == null && create) {
                if (buffers.create(bufferName, config) != null) result = true;
            }

            return result;
        }

        /**
         * Returns the value of a named buffer.
         *
         * @param conn       the connection
         * @param bufferName the name of the buffer
         *
         * @return the current value or <tt>null</tt>, is no buffer with this name is known
         */
        @Override
        public SimpleData bufferValue(DaemonEndpoint conn, String bufferName) {

            Buffer buffer = buffers.bufferByName(bufferName);

            return buffer == null ? null : buffer.get();
        }

        /**
         * Sets the value of a buffer.
         *
         * @param conn       the connection
         * @param bufferName the name of the buffer
         * @param value      the new buffer value
         *
         * @return the new current value of the buffer or <tt>null</tt>, is no buffer with this name is known
         */
        @Override
        public SimpleData setBufferValue(DaemonEndpoint conn, String bufferName, double value) {

            Buffer buffer = buffers.bufferByName(bufferName);
            SimpleData result = null;

            if (buffer != null && buffer.put(value)) result = buffer.get();

            return result;
        }

        /**
         * This method looks for available buffers.
         *
         * @param conn    the connection
         * @param topic   a regular expression specifying the buffer topics, which should be scanned for the wanted features.
         *                These are keys of the buffer meta information. A simple match satisfy
         *                the search condition, the regular expression must not match the whole topic name.
         * @param feature a regular expression specifying the buffer features, which should be scanned. A simple match satisfy
         *                the search condition, the regular expression must not match the whole feature description.
         *
         * @return a collection of all the buffers matching this query
         */
        @Override
        public Set<String> queryBuffersByMetainfo(DaemonEndpoint conn, String topic, String feature) {

            return buffers.queryBuffersByMetainfo(topic, feature);
        }

        /**
         * This method looks for available buffers.
         *
         * @param conn the connection
         * @param name a regular expression specifying the buffer name, which should be scanned. A simple match satisfy
         *             the search condition, the regular expression must not match the whole name.
         *
         * @return a collection of all the buffers matching this query
         */
        @Override
        public Set<String> queryBuffersByName(DaemonEndpoint conn, String name) {

            return buffers.queryBuffersByName(name);
        }

        /**
         * Releases a buffer.
         *
         * @param conn       the connection
         * @param bufferName the name of the buffer
         *
         * @return <tt>true</tt>, if the buffer is now released
         */
        @Override
        public boolean removeBuffer(DaemonEndpoint conn, String bufferName) {

            Buffer existing = buffers.bufferByName(bufferName);
            return existing != null && !existing.isHardwareBuffer() && buffers.remove(bufferName) != null;
        }
    }

    /**
     * This is the listener, that receives value changes from all the buffers. They are routed
     * to the client connections.
     */
    private class ValueChangeListener implements ValueChangeConsumer {

        /**
         * This is the notification of a spontaneous value change.
         *
         * @param newValue the new buffer value
         */
        @Override
        public void valueChanged(SimpleData newValue) {

            for (DaemonEndpoint connection : connections) connection.spontaneousValueChange(newValue);
        }
    }

    // The only instance of this class.
    private static Daemon instance;

    // The exception handler
    private LastChanceExceptionHandler lastChanceExceptionHandler;

    // The system timer
    private Timer timer;

    // All known gatherers
    private Gatherers gatherers;

    // All known buffers
    private Buffers buffers;

    // The hardware driver
    private Driver driver;

    // A set of connection to the clients.
    private final Set<DaemonEndpoint> connections;

    // The server socket and the thread listening for new connection.
    private ServerSocket socket;
    private ConnectionListener connectionListener;

    // Some test flags set by command line options
    private boolean exitOnShutdown;
    private boolean openSocket;

    /** Initialization of the instance variables. */
    private Daemon() {

        connections = new CopyOnWriteArraySet<>();
    }

    /**
     * Initialization of the daemon.
     *
     * @param args the arguments from the command line
     */
    private void initialize(String[] args) {

        exitOnShutdown = true;
        openSocket = true;

        // At first the command line arguments are evaluated and handled.
        handleCommandLineArguments(args);

        // Setting up an uncaught exception handler
        lastChanceExceptionHandler = new LastChanceExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(lastChanceExceptionHandler);

        // Write the configuration items to the logger
        logger.info(String.format("This is the G2021 buffer daemon version '%s'.", VERSION));
        logger.config(String.format("Operating system: %s (%s) version %s",
                                    System.getProperty("os.name"), System.getProperty("os.arch"), System.getProperty("os.version")));
        logger.config(String.format("Java virtual machine: %s %s version %s (java %s)",
                                    System.getProperty("java.vm.vendor"), System.getProperty("java.vm.name"),
                                    System.getProperty("java.vm.version"), System.getProperty("java.version")));
        logger.config(String.format("Default directory: %s", System.getProperty("user.dir")));

        // Now we create all the necessary main parts
        timer = new Timer(true);
        gatherers = new Gatherers();
        buffers = new Buffers();
        driver = new Driver();
        logger.info("Initialization of all components is started.");

        // Now the main parts can work together
        gatherers.initialize();
        buffers.initialize();
        buffers.addValueChangeConsumer(new ValueChangeListener());
        driver.initialize();

        // At last the server socket is initialized
        if (openSocket) {
            try {
                socket = new ServerSocket(CommunicationSettings.bufferDaemonPort());
                socket.setReuseAddress(true);
                connectionListener = new ConnectionListener();
                connectionListener.start();
                logger.info(String.format("The G2021 buffer daemon listen for incoming connections at port %d.",
                                          CommunicationSettings.bufferDaemonPort()));
            }
            catch (IOException e) {
                logger.log(Level.SEVERE,
                           String.format("The G2021 buffer daemon cannot listen for incoming connections " +
                                         "at port %d. An emergency shutdown is initiated.",
                                         CommunicationSettings.bufferDaemonPort()),
                           e);
                kill();
            }
        }

        logger.info("The G2021 buffer daemon is now up and running.");
    }

    /** Stops the daemon. This is a request for a regular shutdown.
     * @param exitCode the exit code
     */
    private void stop(int exitCode) {

        // We disable the uncaught exception handler, because we are already shutting down
        lastChanceExceptionHandler.setShutdownInitiated();

        logger.info("Initiating the shutdown sequence now.");

        // At first we close the server socket and accept no more client connections.
        if (socket != null) {
            try {
                socket.close();
            }
            catch (IOException e) {
                logger.log(Level.WARNING, "Unable to close the server socket.", e);
            }
            finally {
                socket = null;
            }
        }

        // Now the listener thread for incoming connections must die
        try {
            if (connectionListener != null) connectionListener.join();
        }
        catch (InterruptedException e) {
            // All right. The thread is killed by the System.exit - call.
            Thread.currentThread().interrupt();
        }
        finally {
            connectionListener = null;
        }

        // Now we shutdown gracefully, if it's a regular shutdown.
        if (exitCode == 0) {

            // Disconnecting all known connections.
            if (connections != null) {
                for (DaemonEndpoint connection : connections) connection.disconnect();
            }

            // Shutdown all components
            if (driver != null) driver.shutdown();
            if (buffers != null) buffers.shutdown();
            if (gatherers != null) gatherers.shutdown();
            logger.info("All components have performed a shutdown.");
        }

        // Disconnecting all remaining connections immediately.
        if (connections != null) {
            for (DaemonEndpoint connection : connections) connection.disconnectImmediately();
        }

        // Now release the system resources
        if (driver != null) {
            driver.release();
            driver = null;
        }
        if (buffers != null) {
            buffers.release();
            buffers = null;
        }
        if (gatherers != null) {
            gatherers.release();
            gatherers = null;
        }

        // Now the timer thread is stopped
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        logger.info("All system resources have been released.");

        // And finally exit the process
        if (exitOnShutdown) {
            logger.info(String.format("Exiting the operation system process with exit code %d.", exitCode));
            LogManager.getLogManager().reset();
            System.exit(exitCode);
        }
        else {
            logger.info(String.format("The shutdown sequence is done."));
        }
    }

    /** Stops the daemon. This is a request for a regular shutdown. */
    public void stop() { stop(0); }

    /** Kills the daemon. This is a request for emergency shutdown. */
    public void kill() { stop(1); }

    /**
     * Returns the system timer,
     *
     * @return the timer
     */
    public Timer timer() { return timer; }

    /**
     * Returns the set of gatherers.
     *
     * @return the gatherers
     */
    public Gatherers gatherers() { return gatherers; }

    /**
     * Returns the set of buffers.
     *
     * @return the buffers
     */
    public Buffers buffers() { return buffers; }

    /**
     * Returns the driver.
     *
     * @return the driver
     */
    public Driver driver() { return driver; }

    /**
     * Adds a connection to the set of wellknown connections.
     *
     * @param connection the new connection
     */
    void addConnection(DaemonEndpoint connection) { connections.add(connection); }

    /**
     * Removes a connection from the set of wellknown connections.
     *
     * @param connection the obsolete connection
     */
    public void removeConnection(DaemonEndpoint connection) {

        connection.disconnect();
        connections.remove(connection);
    }

    /**
     * Interprets the command line arguments if given.
     *
     * @param args the arguments from the command line
     */
    private void handleCommandLineArguments(String[] args) {

        for (String arg : args) {
            switch (arg) {
                case "-version":
                    System.out.println(VERSION);
                    System.exit(0);
                    break;

                case "-stop":
                    if (shutdownRemoteDaemon()) System.exit(0);
                    else System.exit(1);
                    break;

                case "-<unit-test>: don-t-exit-on-shutdown":
                    exitOnShutdown = false;
                    break;

                case "-<unit-test>: don-t-open-socket":
                    openSocket = false;
                    break;

                default:
                    System.err.println("An unexpected command line argument was found: " + arg);
                    System.exit(1);
                    break;
            }
        }
    }

    /** This method stops a running daemon.
     * @return <tt>true</tt>: the shutdown was successful<br>
     *     <tt>false</tt>: not successful
     */
    private boolean shutdownRemoteDaemon() {

        ClientEndpoint endpoint = ClientEndpoint.get();

        try {
            endpoint.connect();
            endpoint.shutdown();
            return true;
        }
        catch (IOException ioex) {
            System.err.println("Unable to send a shutdown request to the G2021 daemon.");
            ioex.printStackTrace(System.err);
            return false;
        }
        finally {
            endpoint.disconnect();
        }
    }

    /**
     * Returns the only instance of this class.
     *
     * @return the daemon object
     */
    public static Daemon get() { return instance; }

    /**
     * This is the main entry of the G2021 buffer daemon.
     *
     * @param args the arguments from the command line
     */
    public static void main(String[] args) {

        instance = new Daemon();
        instance.initialize(args);
    }
}
