package at.ac.tuwien.infosys.g2021.common.communication;

import at.ac.tuwien.infosys.g2021.common.BufferConfiguration;
import at.ac.tuwien.infosys.g2021.common.SimpleData;
import at.ac.tuwien.infosys.g2021.common.communication.jaxb.EstablishTag;
import at.ac.tuwien.infosys.g2021.common.communication.jaxb.GetBufferConfigurationTag;
import at.ac.tuwien.infosys.g2021.common.communication.jaxb.GetImmediateTag;
import at.ac.tuwien.infosys.g2021.common.communication.jaxb.GetTag;
import at.ac.tuwien.infosys.g2021.common.communication.jaxb.Message;
import at.ac.tuwien.infosys.g2021.common.communication.jaxb.QueryBuffersByMetainfoTag;
import at.ac.tuwien.infosys.g2021.common.communication.jaxb.QueryBuffersByNameTag;
import at.ac.tuwien.infosys.g2021.common.communication.jaxb.QueryMetainfoTag;
import at.ac.tuwien.infosys.g2021.common.communication.jaxb.ReleaseBufferTag;
import at.ac.tuwien.infosys.g2021.common.communication.jaxb.SetBufferConfigurationTag;
import at.ac.tuwien.infosys.g2021.common.communication.jaxb.SetTag;
import at.ac.tuwien.infosys.g2021.common.util.Loggers;
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

                    Message message = client.receive();

                    // Interpret the received message and send an answer.
                    if (message.getAccepted() != null) {
                        logger.warning(String.format("An 'Accept' message has been received from the client connection #%d.",
                                                     client.getId()));
                        handleProtocolViolation();
                    }
                    else if (message.getBufferConfiguration() != null) {
                        logger.warning(String.format("An 'Accept' message has been received from the client connection #%d.",
                                                     client.getId()));
                        handleProtocolViolation();
                    }
                    else if (message.getBufferMetainfo() != null) {
                        logger.warning(String.format("A 'BufferMetaInfo' message has been received from the client connection #%d.",
                                                     client.getId()));
                        handleProtocolViolation();
                    }
                    else if (message.getBufferNames() != null) {
                        logger.warning(String.format("A 'BufferNames' message has been received from the client connection #%d.",
                                                     client.getId()));
                        handleProtocolViolation();
                    }
                    else if (message.getDisconnect() != null) {
                        logger.info(String.format("A 'Disconnect' message has been received from the client connection #%d.",
                                                  client.getId()));
                        disconnectImmediately();
                    }
                    else if (message.getEstablish() != null) {

                        EstablishTag tag = message.getEstablish();

                        if (tag.getVersion() == CommunicationSettings.version()) {
                            logger.fine(String.format("An 'Establish' message has been received from the client connection #%d.",
                                                      client.getId()));
                            messageSender.accepted();
                        }
                        else {
                            logger.warning(String.format("An 'Establish' message with an illegal version id has been received from the client " +
                                                         "connection #%d. The required version is %d, the current daemon version is %d.",
                                                         client.getId(), tag.getVersion(), CommunicationSettings.version()));
                            messageSender.rejected("illegal version");
                            disconnectImmediately();
                        }
                    }
                    else if (message.getGet() != null) {

                        GetTag tag = message.getGet();

                        if (daemon.bufferExists(DaemonEndpoint.this, tag.getName())) {
                            logger.fine(String.format("A 'Get' message for buffer '%s' has been received from the client connection #%d.",
                                                      tag.getName(), client.getId()));
                            buffersToPush.add(tag.getName());
                        }
                        else {
                            logger.info(String.format("A 'Get' message for an unknown buffer '%s' has been received from the client connection #%d.",
                                                      tag.getName(), client.getId()));
                        }
                    }
                    else if (message.getGetBufferConfiguration() != null) {

                        GetBufferConfigurationTag tag = message.getGetBufferConfiguration();
                        BufferConfiguration configuration = daemon.bufferConfiguration(DaemonEndpoint.this, tag.getName());

                        if (configuration != null) {
                            logger.fine(String.format("A 'GetBufferConfiguration' message for buffer '%s' has been received from " +
                                                      "the client connection #%d.",
                                                      tag.getName(), client.getId()));
                            messageSender.bufferConfiguration(configuration);
                        }
                        else {
                            logger.info(String.format("A 'GetBufferConfiguration' message for an unknown buffer '%s' has been recei" +
                                                      "ved from the client connection #%d.",
                                                      tag.getName(), client.getId()));
                            messageSender.rejected("unknown buffer: " + tag.getName());
                        }
                    }
                    else if (message.getGetImmediate() != null) {

                        GetImmediateTag tag = message.getGetImmediate();
                        SimpleData value = daemon.bufferValue(DaemonEndpoint.this, tag.getName());

                        if (value != null) {
                            logger.fine(String.format("A 'GetImmediate' message for buffer '%s' has been received from the " +
                                                      "client connection #%d.",
                                                      tag.getName(), client.getId()));
                            messageSender.push(value, false);
                        }
                        else {
                            logger.info(String.format("A 'GetImmediate' message for an unknown buffer '%s' has been received from " +
                                                      "the client connection #%d.",
                                                      tag.getName(), client.getId()));
                            messageSender.rejected("unknown buffer: " + tag.getName());
                        }
                    }
                    else if (message.getPush() != null) {
                        logger.warning(String.format("A 'Push' message has been received from the client connection #%d.",
                                                     client.getId()));
                        handleProtocolViolation();
                    }
                    else if (message.getQueryBuffersByMetainfo() != null) {

                        QueryBuffersByMetainfoTag tag = message.getQueryBuffersByMetainfo();

                        logger.fine(String.format("A 'QueryBuffersByMetainfo' message for buffers with topics '%s' and features " +
                                                  "'%s' has been received from the client connection #%d.",
                                                  tag.getTopic(), tag.getMetainfo(), client.getId()));
                        messageSender.bufferNames(daemon.queryBuffersByMetainfo(DaemonEndpoint.this, tag.getTopic(), tag.getMetainfo()));
                    }
                    else if (message.getQueryBuffersByName() != null) {

                        QueryBuffersByNameTag tag = message.getQueryBuffersByName();

                        logger.fine(String.format("A 'QueryBuffersByName' message for buffers with names " +
                                                  "'%s' has been received from the client connection #%d.",
                                                  tag.getName(), client.getId()));
                        messageSender.bufferNames(daemon.queryBuffersByName(DaemonEndpoint.this, tag.getName()));
                    }
                    else if (message.getQueryMetainfo() != null) {

                        QueryMetainfoTag tag = message.getQueryMetainfo();
                        BufferConfiguration configuration = daemon.bufferConfiguration(DaemonEndpoint.this, tag.getName());

                        if (configuration != null) {
                            logger.fine(String.format("A 'QueryMetainfo' message for buffer '%s' has been received " +
                                                      "from the client connection #%d.",
                                                      tag.getName(), client.getId()));
                            messageSender.bufferMetainfo(tag.getName(), configuration.getMetainfo());
                        }
                        else {
                            logger.info(String.format("A 'QueryMetainfo' message for an unknown buffer '%s' has been " +
                                                      "received from the client connection #%d.",
                                                      tag.getName(), client.getId()));
                            messageSender.rejected("unknown buffer: " + tag.getName());
                        }
                    }
                    else if (message.getRejected() != null) {
                        logger.warning(String.format("A 'Rejected' message has been received from the client connection #%d.",
                                                     client.getId()));
                        handleProtocolViolation();
                    }
                    else if (message.getReleaseBuffer() != null) {

                        ReleaseBufferTag tag = message.getReleaseBuffer();

                        if (daemon.releaseBuffer(DaemonEndpoint.this, tag.getName())) {
                            logger.fine(String.format("A 'ReleaseBuffer' message for buffer '%s' has been received " +
                                                      "from the client connection #%d.",
                                                      tag.getName(), client.getId()));
                            messageSender.accepted();
                        }
                        else {
                            logger.info(String.format("A 'ReleaseBuffer' message for an unknown buffer '%s' has been " +
                                                      "received from the client connection #%d.",
                                                      tag.getName(), client.getId()));
                            messageSender.rejected("unknown buffer: " + tag.getName());
                        }
                    }
                    else if (message.getSet() != null) {

                        SetTag tag = message.getSet();
                        SimpleData value = daemon.setBufferValue(DaemonEndpoint.this, tag.getName(), tag.getValue());

                        if (value != null) {
                            logger.fine(String.format("A 'Set' message for buffer '%s' has been received from the " +
                                                      "client connection #%d.",
                                                      tag.getName(), client.getId()));
                            messageSender.push(value, false);
                        }
                        else {
                            logger.info(String.format("A 'Set' message for an unknown buffer '%s' has been received from " +
                                                      "the client connection #%d.",
                                                      tag.getName(), client.getId()));
                            messageSender.rejected("unknown buffer: " + tag.getName());
                        }
                    }
                    else if (message.getSetBufferConfiguration() != null) {

                        SetBufferConfigurationTag tag = message.getSetBufferConfiguration();
                        JAXBInterface jaxb = new JAXBInterface();

                        if (daemon.setBufferConfiguration(DaemonEndpoint.this, tag.getName(), jaxb.configurationFromXML(tag.getBufferConfiguration()), tag.isCreate())) {
                            logger.fine(String.format("A 'SetBufferConfiguration' message for buffer '%s' has been received from " +
                                                      "the client connection #%d.",
                                                      tag.getName(), client.getId()));
                            messageSender.accepted();
                        }
                        else {
                            logger.info(String.format("A 'SetBufferConfiguration' message for the buffer '%s' with a wrong buffer configuration " +
                                                      "has been received from the client connection #%d.",
                                                      tag.getName(), client.getId()));
                            messageSender.rejected("wrong buffer configuration: " + tag.getName());
                        }
                    }
                    else if (message.getShutdown() != null) {
                        logger.info(String.format("A 'Shutdown' message has been received from the client connection #%d.",
                                                  client.getId()));
                        new Killer();
                    }
                    else {
                        logger.warning("An unknown message has been received from buffer daemon.");
                        handleProtocolViolation();
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

        logger.warning(String.format("The client at connection #%d uses an unknown protocol.", connection.getId()));
        disconnectImmediately();
    }

    /**
     * There is a communication error occurred.
     *
     * @param io the causing exception
     */
    private void handleCommunicationError(Exception io) {

        logger.log(Level.WARNING,
                   String.format("Cannot communicate with the client at connection #%d.", connection.getId()),
                   io);
        disconnectImmediately();
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


