package at.ac.tuwien.infosys.g2021.common.communication;

import at.ac.tuwien.infosys.g2021.common.communication.jaxb.Message;
import at.ac.tuwien.infosys.g2021.common.util.Loggers;
import at.ac.tuwien.infosys.g2021.common.util.PanicError;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

/**
 * This is a TCP/IP connection, which exchanges
 * <tt>{@link at.ac.tuwien.infosys.g2021.common.communication.jaxb.Message}</tt>-objects. This class encapsulates all
 * the handling with byte arrays, XML, schemas and so on.
 */
class Connection {

    // The logger.
    private final static Logger logger = Loggers.getLogger(Connection.class);

    // The XML schema of messages
    private Schema schema;

    // The JAXB-Interface to the messages
    private JAXBInterface jaxb;

    // The socket of the connection
    private Socket socket;
    private final Object senderLock;

    // Every connection has an id for logging reasons
    private int id;
    private static int nextId = 1;
    private final static Object idLock = new Object();

    /** A connection without a socket is not wise. */
    private Connection() {

        senderLock = new Object();

        // Evaluating the id
        synchronized (idLock) {
            id = nextId++;
        }

        // Loading the XML schema
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        try {
            schema = schemaFactory.newSchema(new StreamSource(getClass().getResourceAsStream("communication.xsd")));
        }
        catch (Exception exc) {
            // We cannot find the schema or the schema is erroneous. This is a good reason for getting panic.
            logger.log(Level.SEVERE, "Unable to load the schema 'communication.xsd':", exc);
            throw new PanicError("missing schema 'communication.xsd': ", exc);
        }

        // Creating the JAXB-Interface
        jaxb = new JAXBInterface();
    }

    /**
     * Establishing the connection.
     *
     * @param s the underlying TCP/IP socket
     *
     * @throws java.io.IOException if the connection cannot be initialized
     */
    Connection(Socket s) throws IOException {

        this();
        socket = s;

        if (!isConnected()) throw new IOException("the socket is not connected");
        logger.info(String.format("The connection '%s:%d' has been established as connection #%d.",
                                  socket.getInetAddress().toString(),
                                  socket.getPort(),
                                  id));
    }

    /**
     * Returns the id of this connection.
     *
     * @return the id
     */
    int getId() { return id; }

    /**
     * Reads the underlying socket. This may be null, if the connection is in the disconnected state.
     *
     * @return the underlying socket
     */

    Socket getSocket() { return socket; }

    /**
     * Returns the connection state.
     *
     * @return <tt>true</tt>, if there is a TCP/IP connection established
     */
    boolean isConnected() { return socket != null && socket.isConnected() && !socket.isClosed(); }

    /**
     * Closes the connection. Is the connection is not established, any call to this
     * method is ignored.
     */
    void disconnect() {

        if (socket != null) {
            try {
                socket.close();
                logger.info("The connection '#" + id + " is closed now.");
            }
            catch (Exception e) {
                logger.log(Level.WARNING, "Unable to close the connection #" + id + ":", e);
            }
            finally {
                socket = null;
            }
        }
    }

    /**
     * Sending a message to the corresponding peer.
     *
     * @param message the message to send
     *
     * @throws java.io.IOException if the message cannot be sent
     */
    void send(Message message) throws IOException {

        if (message == null) {
            throw new NullPointerException("message is null");
        }
        else if (!isConnected()) {
            disconnect();
            throw new IOException("sending to a closed connection");
        }
        else {
            // At first we put the message into a byte array stream
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            try {
                jaxb.writeXML(message, stream);
            }
            catch (JAXBException e) {
                logger.log(Level.WARNING, "Cannot create a XML message: ", e);
                throw new ProtocolException(e);
            }

            // Now we append the separator
            stream.write(CommunicationSettings.MESSAGE_SEPARATOR);

            // And write the whole byte array to the socket output stream
            try {
                synchronized (senderLock) {
                    socket.getOutputStream().write(stream.toByteArray());
                    socket.getOutputStream().flush();
                    logger.finer("A XML message has been sent over the connection #" + id + ".");
                }
            }
            catch (Exception e) {
                logger.log(Level.WARNING, "Cannot write to the connection #" + id + ":", e);
                disconnect();
                throw new IOException("sending to a closed connection");
            }
        }
    }

    /**
     * Receiving a message from the corresponding peer. This method may block the current thread, if there is
     * no message available.
     *
     * @return the message received
     *
     * @throws java.io.IOException if the message cannot be received
     */
    Message receive() throws IOException {

        Message result;

        if (!isConnected()) {
            disconnect();
            throw new IOException("reading on a closed connection");
        }
        else {

            byte[] messageBytes = new byte[65536];
            InputStream stream = socket.getInputStream();
            int used = 0;
            int read;

            while (true) {

                read = stream.read();

                if (read < 0 || (byte)read == CommunicationSettings.MESSAGE_SEPARATOR[0]) break;
                messageBytes[used++] = (byte)read;
            }

            // Now the received bytes are interpreted.
            if (read < 0) {
                // An unexpected eof condition on the input stream is occurred.
                disconnect();
                throw new IOException("reading to a closed connection");
            }
            else {
                // There is a message in the message byte array.
                byte[] message = new byte[used];
                System.arraycopy(messageBytes, 0, message, 0, used);
                ByteArrayInputStream inputStream = new ByteArrayInputStream(message);
                try {
                    result = jaxb.readXML(schema, inputStream);
                    logger.finer("A XML message has been read from the connection #" + id + ".");
                }
                catch (JAXBException e) {
                    logger.log(Level.WARNING, "Cannot read a XML message from the connection #" + id + ":", e);
                    throw new ProtocolException(e);
                }
            }
        }

        return result;
    }
}
