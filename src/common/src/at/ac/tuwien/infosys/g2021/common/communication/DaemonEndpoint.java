package at.ac.tuwien.infosys.g2021.common.communication;

import at.ac.tuwien.infosys.g2021.common.BufferConfiguration;
import at.ac.tuwien.infosys.g2021.common.SimpleData;
import at.ac.tuwien.infosys.g2021.common.util.Loggers;
import com.eclipsesource.json.JsonObject;
import java.io.IOException;
import java.net.Socket;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is the connection endpoint at the buffer daemon to a JVM containing GBots with
 * its DataPoint instances. Incoming requests are routed to the daemon implementation
 * over the <tt>{@link ClientRequestExecutionStrategy}</tt>-interface. This allows a loose
 * coupling between the daemon implementation and the protocol implementation
 */
public class DaemonEndpoint {

    /** This inner class is a thread, which shutdowns the daemon. */
    private class Killer extends Thread {

        /** Initialization. */
        Killer() {
            super("remote shutdown thread");
            setDaemon(true);
            start();
        }

        /** The thread implementation it runs unless the socket is closed. It reads all the messages received and interpret them. */
        @Override
        public synchronized void run() {

            try {
                // give the daemon time to finish the shutdown communication correctly
                wait(250L);
            }
            catch (InterruptedException e) {
                // Bad luck, the shutdown is just initiated earlier
            }

            daemon.shutdown(DaemonEndpoint.this);
        }
    }

    /** This inner class is a thread listening for messages from the GBots. */
    private class Receiver extends Thread {

        /** Initialization. */
        Receiver() {
            super("message receiver thread");
            setDaemon(true);
            start();
        }

        /** The thread implementation it runs unless the socket is closed. It reads all the messages received and interpret them. */
        @Override
        public void run() {

            // A local copy of the connection to be thread-safe
            Connection client = connection;
            MessageSender messageSender = sender;

            if (client != null) {
                logger.fine(String.format("The receiver thread for client messages at connection #%d is started.",
                                          client.getId()));
            }

            try {

                while (client != null && messageSender != null && !interrupted()) {

                    JsonObject message = client.receive();
                    JsonObject arguments = message.get(JsonInterface.ARGUMENTS).asObject();
                    JsonInterface json = new JsonInterface();

                    // Interpret the received message and create an answer.
                    switch (message.get(JsonInterface.TYPE).asString()) {
                        case JsonInterface.DISCONNECT:
                            disconnectImmediately();
                            break;

                        case JsonInterface.ESTABLISH:
                            if (arguments.get(JsonInterface.VERSION).asInt() == CommunicationSettings.version()) {
                                messageSender.accepted();
                            }
                            else {
                                messageSender.rejected("illegal version");
                                disconnectImmediately();
                            }
                            break;

                        case JsonInterface.GET:
                            String name = arguments.get(JsonInterface.NAME).asString();
                            if (daemon.bufferExists(DaemonEndpoint.this, name)) buffersToPush.add(name);
                            break;

                        case JsonInterface.GET_BUFFER_CONFIGURATION:
                            BufferConfiguration configuration = daemon.bufferConfiguration(DaemonEndpoint.this, arguments.get(JsonInterface.NAME).asString());
                            if (configuration != null) messageSender.bufferConfiguration(configuration);
                            else messageSender.rejected("unknown buffer");
                            break;

                        case JsonInterface.GET_IMMEDIATE:
                            SimpleData value = daemon.bufferValue(DaemonEndpoint.this, arguments.get(JsonInterface.NAME).asString());
                            if (value != null) messageSender.push(value, false);
                            else messageSender.rejected("unknown buffer");
                            break;

                        case JsonInterface.QUERY_BUFFER_BY_METAINFO:
                            messageSender.bufferNames(daemon.queryBuffersByMetainfo(DaemonEndpoint.this,
                                                                                    arguments.get(JsonInterface.TOPIC).asString(),
                                                                                    arguments.get(JsonInterface.METAINFO).asString()));
                            break;

                        case JsonInterface.QUERY_BUFFER_BY_NAME:
                            messageSender.bufferNames(daemon.queryBuffersByName(DaemonEndpoint.this,
                                                                                arguments.get(JsonInterface.NAME).asString()));
                            break;

                        case JsonInterface.QUERY_METAINFO:
                            name = arguments.get(JsonInterface.NAME).asString();
                            configuration = daemon.bufferConfiguration(DaemonEndpoint.this, name);
                            if (configuration != null) {
                                messageSender.bufferMetainfo(name,
                                                             daemon.isHardwareBuffer(DaemonEndpoint.this, name),
                                                             configuration.getMetainfo());
                            }
                            else {
                                messageSender.rejected("unknown buffer");
                            }
                            break;

                        case JsonInterface.RELEASE_BUFFER:
                            if (daemon.removeBuffer(DaemonEndpoint.this, arguments.get(JsonInterface.NAME).asString())) messageSender.accepted();
                            else messageSender.rejected("unknown buffer");
                            break;

                        case JsonInterface.SET:
                            value = daemon.setBufferValue(DaemonEndpoint.this, arguments.get(JsonInterface.NAME).asString(), arguments.get(JsonInterface.VALUE).asDouble());
                            if (value != null) messageSender.push(value, false);
                            else messageSender.rejected("unknown actor");
                            break;

                        case JsonInterface.SET_BUFFER_CONFIGURATION:
                            if (daemon.setBufferConfiguration(DaemonEndpoint.this,
                                                              arguments.get(JsonInterface.NAME).asString(),
                                                              json.configurationFromJSON(arguments.get(JsonInterface.CONFIGURATION).asObject()),
                                                              arguments.get(JsonInterface.CREATE).asBoolean())) {
                                messageSender.accepted();
                            }
                            else {
                                messageSender.rejected("wrong buffer configuration");
                            }
                            break;

                        case JsonInterface.SHUTDOWN:
                            new Killer();
                            break;

                        default:
                            handleProtocolViolation();
                            break;
                    }

                    client = connection;
                    messageSender = sender;
                }
            }
            catch (Exception e) {

                // An understandable exception, if the connection was closed.
                if (connection != null) handleCommunicationError(e);
            }
        }

        /** Stops the receiver thread. */
        void shutdown() { interrupt(); }
    }

    // The logger.
    private final static Logger logger = Loggers.getLogger(DaemonEndpoint.class);

    // The receiver thread
    private Receiver receiverThread;

    // The connection to the client
    private Connection connection;
    private final Object connectionLock;

    // The message sender.
    private MessageSender sender;

    // The implementation
    private ClientRequestExecutionStrategy daemon;

    // The set of buffer names, whose value changes must communicated.
    private Set<String> buffersToPush;

    /** Initialisation of the endpoint instance. */
    private DaemonEndpoint() {

        receiverThread = null;
        connection = null;
        connectionLock = new Object();
        sender = null;
        buffersToPush = Collections.synchronizedSet(new HashSet<>());
    }

    /**
     * Initialisation of the endpoint instance.
     *
     * @param socket the connection to the client
     * @param impl   the implementation of the request execution
     *
     * @throws java.io.IOException if this operation fails
     */
    public DaemonEndpoint(Socket socket, ClientRequestExecutionStrategy impl) throws IOException {

        this();
        this.daemon = impl;

        connect(socket);
    }

    /**
     * Returns the connection state of this connection.
     *
     * @return <tt>true</tt>, if there is a TCP/IP connection established
     */
    public boolean isConnected() {

        synchronized (connectionLock) {
            return connection != null;
        }
    }

    /**
     * Establishes the connection to a client JVM. Is the connection is established, any call to this
     * method is ignored.
     *
     * @param socket the socket to the client JVM
     *
     * @throws java.io.IOException if this operation fails
     */
    public void connect(Socket socket) throws IOException {

        synchronized (connectionLock) {
            if (!isConnected()) {

                // Establish the connection
                connection = new Connection(socket);
                sender = new MessageSender(connection);

                // Listen for messages
                receiverThread = new Receiver();
            }
        }
    }

    /**
     * Closes the connection to the daemon without sending a disconnect message. Is the connection is not
     * established, any call to this method is ignored.
     */
    public void disconnectImmediately() {

        buffersToPush.clear();

        synchronized (connectionLock) {
            if (isConnected()) {

                // Close the connection
                connection.disconnect();

                // Stop the receiver thread
                receiverThread.shutdown();

                // Resetting all communication components
                receiverThread = null;
                sender = null;
                connection = null;
            }
        }

        daemon.connectionDied(this);
    }

    /**
     * Closes the connection to the daemon. Is the connection is not established, any call to this
     * method is ignored.
     */
    public void disconnect() {

        synchronized (connectionLock) {
            if (isConnected()) {

                // Sending a disconnect - Message
                if (!connection.getSocket().isClosed()) {
                    try {
                        sender.disconnect();
                    }
                    catch (IOException io) {
                        // We are not able to communicate about the connection
                        handleCommunicationError(io);
                    }
                }

                // wait a moment to give the peer a chance to receive this message
                try {
                    Thread.sleep(100L);
                }
                catch (InterruptedException e) {
                    // Ok. Due to the close of the socket, the communication may break down
                    // and IOExceptions are thrown. This may result only in ugly log messages.
                }

                // Close the connection now
                disconnectImmediately();
            }
        }
    }

    /** A protocol violation has occurred. The connection will be closed. */
    private void handleProtocolViolation() {

        if (isConnected()) {
            logger.warning(String.format("The client at connection #%d uses an unknown protocol.", connection.getId()));
            disconnectImmediately();
        }
    }

    /**
     * There is a communication error occurred.
     *
     * @param io the causing exception
     */
    private void handleCommunicationError(Exception io) {

        if (isConnected()) {
            logger.log(Level.WARNING,
                       String.format("Due to an communication error, the connection #%d is not usable.", connection.getId()),
                       io);
            disconnectImmediately();
        }
    }

    /**
     * Removes a buffer from the list of buffers, which are notified about spontaneous value changes.
     *
     * @param name the buffer name
     */
    public void forgetBuffer(String name) { buffersToPush.remove(name); }

    /**
     * A spontaneous value change has occurred.
     *
     * @param value the new value
     */
    public void spontaneousValueChange(SimpleData value) {

        MessageSender currentSender = sender;

        if (currentSender != null && buffersToPush.remove(value.getBufferName())) {
            try {
                currentSender.push(value, true);
            }
            catch (IOException e) {
                // We are not able to communicate about the connection
                handleCommunicationError(e);
            }
        }
    }
}


