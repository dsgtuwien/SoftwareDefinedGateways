package at.ac.tuwien.infosys.g2021.common.communication;

import at.ac.tuwien.infosys.g2021.common.util.Loggers;
import com.eclipsesource.json.JsonObject;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/** This is a TCP/IP connection, which exchanges Strings containing JSON objects. */
class Connection {

    // The logger.
    private final static Logger logger = Loggers.getLogger(Connection.class);

    // The socket of the connection
    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
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

        inputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        outputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

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
                inputStream = null;
                outputStream = null;
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
    void send(String message) throws IOException {

        if (message == null) {
            throw new NullPointerException("message is null");
        }
        else if (!isConnected()) {
            disconnect();
            throw new IOException("sending to a closed connection");
        }
        else {
            try {
                synchronized (senderLock) {
                    outputStream.writeUTF(message);
                    outputStream.flush();
                    logger.fine(String.format("The message '%s' has been sent over the connection #%d.", message, getId()));
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
    JsonObject receive() throws IOException {

        JsonObject result;

        if (!isConnected()) {
            disconnect();
            throw new IOException("reading on a closed connection");
        }
        else {

            try {
                String message = inputStream.readUTF();
                JsonInterface json = new JsonInterface();

                result = json.stringToJSON(message);
                logger.fine(String.format("The JSON object '%s' has been read from the connection #%d.", message, getId()));
            }
            catch (IOException e) {
                // IOExceptions are rethrown
                throw new IOException(e.getMessage(), e);
            }
            catch (Exception e) {
                // All other exceptions are treated as protocol violations
                logger.log(Level.WARNING, "Cannot read a JSON object from the connection #" + id + ":", e);
                throw new ProtocolException(e);
            }
        }

        return result;
    }
}
