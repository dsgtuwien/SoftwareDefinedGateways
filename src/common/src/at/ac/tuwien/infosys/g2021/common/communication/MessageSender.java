package at.ac.tuwien.infosys.g2021.common.communication;

import at.ac.tuwien.infosys.g2021.common.BufferConfiguration;
import at.ac.tuwien.infosys.g2021.common.SimpleData;
import at.ac.tuwien.infosys.g2021.common.util.Loggers;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.logging.Logger;

/** This class transforms a method interface into message objects and sends the message objects to the communication partner. */
class MessageSender {

    // The connection to the peer
    private Connection connection;

    // The logger
    private final static Logger logger = Loggers.getLogger(MessageSender.class);

    /** Initialize an instance with no communication partner assigned. */
    MessageSender() {}

    /** Initialize an instance and assigns a connection to it. */
    MessageSender(Connection c) {

        this();
        setConnection(c);
    }

    /**
     * Reads the connection to the communication partner.
     *
     * @return the connection
     */
    Connection getConnection() { return connection; }

    /**
     * Assigns a connection to the communication partner.
     *
     * @param connection the new connection to the communication partner
     */
    void setConnection(Connection connection) { this.connection = connection; }

    /**
     * Assigns a connection to the communication partner.
     *
     * @throws java.io.IOException if sending is not possible
     */
    private void checkConnection() throws IOException {

        if (connection == null) throw new IOException("no connection");
        else if (!connection.isConnected()) throw new IOException("connection is not connected");
    }

    /**
     * Sending a Message object over the connection and handle logging.
     *
     * @param message the message
     *
     * @throws IOException if the send operation fails
     */
    private void sendMessage(JsonObject message) throws IOException {

        JsonInterface json = new JsonInterface();
        String msg = json.stringFromJSON(message);
        connection.send(msg);
    }

    /**
     * Sends an "establish" message to the communication partner.
     *
     * @throws IOException if the send operation fails
     */
    void establish() throws IOException {

        checkConnection();

        JsonObject arguments = new JsonObject();
        arguments.add(JsonInterface.VERSION, CommunicationSettings.version());

        JsonObject message = new JsonObject();
        message.add(JsonInterface.TYPE, JsonInterface.ESTABLISH);
        message.add(JsonInterface.ARGUMENTS, arguments);

        sendMessage(message);
    }

    /**
     * Sends an "accepted" message to the communication partner.
     *
     * @throws IOException if the send operation fails
     */
    void accepted() throws IOException {

        checkConnection();

        JsonObject message = new JsonObject();
        message.add(JsonInterface.TYPE, JsonInterface.ACCEPTED);
        message.add(JsonInterface.ARGUMENTS, new JsonObject());

        sendMessage(message);
    }

    /**
     * Sends a "rejected" message to the communication partner.
     *
     * @param reason the reason of rejection
     *
     * @throws IOException if the send operation fails
     */
    void rejected(String reason) throws IOException {

        checkConnection();

        JsonObject arguments = new JsonObject();
        arguments.add(JsonInterface.REASON, reason);

        JsonObject message = new JsonObject();
        message.add(JsonInterface.TYPE, JsonInterface.REJECTED);
        message.add(JsonInterface.ARGUMENTS, arguments);

        sendMessage(message);
    }

    /**
     * Sends a "disconnect" message to the communication partner.
     *
     * @throws IOException if the send operation fails
     */
    void disconnect() throws IOException {

        checkConnection();

        JsonObject message = new JsonObject();
        message.add(JsonInterface.TYPE, JsonInterface.DISCONNECT);
        message.add(JsonInterface.ARGUMENTS, new JsonObject());

        sendMessage(message);
    }

    /**
     * Sends a "shutdown" message to the communication partner.
     *
     * @throws IOException if the send operation fails
     */
    void shutdown() throws IOException {

        checkConnection();

        JsonObject message = new JsonObject();
        message.add(JsonInterface.TYPE, JsonInterface.SHUTDOWN);
        message.add(JsonInterface.ARGUMENTS, new JsonObject());

        sendMessage(message);
    }

    /**
     * Sends a "queryBuffersByName" message to the communication partner.
     *
     * @param name a regular expression for the buffer name
     *
     * @throws IOException if the send operation fails
     */
    void queryBuffersByName(String name) throws IOException {

        checkConnection();

        JsonObject arguments = new JsonObject();
        arguments.add(JsonInterface.NAME, name);

        JsonObject message = new JsonObject();
        message.add(JsonInterface.TYPE, JsonInterface.QUERY_BUFFER_BY_NAME);
        message.add(JsonInterface.ARGUMENTS, arguments);

        sendMessage(message);
    }

    /**
     * Sends a "queryBuffersByMetainfo" message to the communication partner.
     *
     * @param topic           a regular expression specifying the buffer topics
     * @param metainfoPattern a regular expression for the buffer meta info
     *
     * @throws IOException if the send operation fails
     */
    void queryBuffersByMetainfo(String topic, String metainfoPattern) throws IOException {

        checkConnection();

        JsonObject arguments = new JsonObject();
        arguments.add(JsonInterface.TOPIC, topic);
        arguments.add(JsonInterface.METAINFO, metainfoPattern);

        JsonObject message = new JsonObject();
        message.add(JsonInterface.TYPE, JsonInterface.QUERY_BUFFER_BY_METAINFO);
        message.add(JsonInterface.ARGUMENTS, arguments);

        sendMessage(message);
    }

    /**
     * Sends a "bufferNames" message to the communication partner.
     *
     * @param names the buffer names
     *
     * @throws IOException if the send operation fails
     */
    void bufferNames(Collection<String> names) throws IOException {

        checkConnection();

        JsonObject arguments = new JsonObject();
        JsonArray nameArray = new JsonArray();
        for (String n : names) nameArray.add(n);
        arguments.add(JsonInterface.NAME, nameArray);

        JsonObject message = new JsonObject();
        message.add(JsonInterface.TYPE, JsonInterface.BUFFER_NAMES);
        message.add(JsonInterface.ARGUMENTS, arguments);

        sendMessage(message);
    }

    /**
     * Sends a "queryMetaInfo" message to the communication partner.
     *
     * @param bufferName the buffer name
     *
     * @throws IOException if the send operation fails
     */
    void queryMetainfo(String bufferName) throws IOException {

        checkConnection();

        JsonObject arguments = new JsonObject();
        arguments.add(JsonInterface.NAME, bufferName);

        JsonObject message = new JsonObject();
        message.add(JsonInterface.TYPE, JsonInterface.QUERY_METAINFO);
        message.add(JsonInterface.ARGUMENTS, arguments);

        sendMessage(message);
    }

    /**
     * Sends a "bufferMetaInfo" message to the communication partner.
     *
     * @param bufferName       the name of the buffer
     * @param isHardwareBuffer represents this buffer a hardware port and cannot be modified or deleted
     * @param metainfo         the assigned meta info
     *
     * @throws IOException if the send operation fails
     */
    void bufferMetainfo(String bufferName, boolean isHardwareBuffer, Map<String, String> metainfo) throws IOException {

        checkConnection();

        JsonObject arguments = new JsonObject();
        JsonInterface json = new JsonInterface();

        arguments.add(JsonInterface.NAME, bufferName);
        arguments.add(JsonInterface.IS_HARDWARE, isHardwareBuffer);
        arguments.add(JsonInterface.METAINFO, json.metainfoToJSON(metainfo));

        JsonObject message = new JsonObject();
        message.add(JsonInterface.TYPE, JsonInterface.BUFFER_METAINFO);
        message.add(JsonInterface.ARGUMENTS, arguments);

        sendMessage(message);
    }

    /**
     * Sends a "bufferConfiguration" message to the communication partner.
     *
     * @param configuration the buffer configuration
     *
     * @throws IOException if the send operation fails
     */
    void bufferConfiguration(BufferConfiguration configuration) throws IOException {

        checkConnection();

        JsonObject arguments = new JsonObject();
        JsonInterface json = new JsonInterface();

        arguments.add(JsonInterface.CONFIGURATION, json.configurationToJSON(configuration));

        JsonObject message = new JsonObject();
        message.add(JsonInterface.TYPE, JsonInterface.BUFFER_CONFIGURATION);
        message.add(JsonInterface.ARGUMENTS, arguments);

        sendMessage(message);
    }

    /**
     * Sends a "getBufferConfiguration" message to the communication partner.
     *
     * @param bufferName the name of the buffer
     *
     * @throws IOException if the send operation fails
     */
    void getBufferConfiguration(String bufferName) throws IOException {

        checkConnection();

        JsonObject arguments = new JsonObject();
        arguments.add(JsonInterface.NAME, bufferName);

        JsonObject message = new JsonObject();
        message.add(JsonInterface.TYPE, JsonInterface.GET_BUFFER_CONFIGURATION);
        message.add(JsonInterface.ARGUMENTS, arguments);

        sendMessage(message);
    }

    /**
     * Sends a "setBufferConfiguration" message to the communication partner.
     *
     * @param bufferName    the name of the buffer
     * @param configuration the buffer configuration
     * @param createAllowed is the creation of a new buffer allowed
     *
     * @throws IOException if the send operation fails
     */
    void setBufferConfiguration(String bufferName, BufferConfiguration configuration, boolean createAllowed) throws IOException {

        checkConnection();

        JsonInterface json = new JsonInterface();

        JsonObject arguments = new JsonObject();
        arguments.add(JsonInterface.NAME, bufferName);
        arguments.add(JsonInterface.CREATE, createAllowed);
        arguments.add(JsonInterface.CONFIGURATION, json.configurationToJSON(configuration));

        JsonObject message = new JsonObject();
        message.add(JsonInterface.TYPE, JsonInterface.SET_BUFFER_CONFIGURATION);
        message.add(JsonInterface.ARGUMENTS, arguments);

        sendMessage(message);
    }

    /**
     * Sends a "releaseBuffer" message to the communication partner.
     *
     * @param bufferName the name of the buffer
     *
     * @throws IOException if the send operation fails
     */
    void releaseBuffer(String bufferName) throws IOException {

        checkConnection();

        JsonObject arguments = new JsonObject();
        arguments.add(JsonInterface.NAME, bufferName);

        JsonObject message = new JsonObject();
        message.add(JsonInterface.TYPE, JsonInterface.RELEASE_BUFFER);
        message.add(JsonInterface.ARGUMENTS, arguments);

        sendMessage(message);
    }

    /**
     * Sends a "getImmediate" message to the communication partner.
     *
     * @param bufferName the buffer name
     *
     * @throws IOException if the send operation fails
     */
    void getImmediate(String bufferName) throws IOException {

        checkConnection();

        JsonObject arguments = new JsonObject();
        arguments.add(JsonInterface.NAME, bufferName);

        JsonObject message = new JsonObject();
        message.add(JsonInterface.TYPE, JsonInterface.GET_IMMEDIATE);
        message.add(JsonInterface.ARGUMENTS, arguments);

        sendMessage(message);
    }

    /**
     * Sends a "get" message to the communication partner.
     *
     * @param bufferName the buffer name
     *
     * @throws IOException if the send operation fails
     */
    void get(String bufferName) throws IOException {

        checkConnection();

        JsonObject arguments = new JsonObject();
        arguments.add(JsonInterface.NAME, bufferName);

        JsonObject message = new JsonObject();
        message.add(JsonInterface.TYPE, JsonInterface.GET);
        message.add(JsonInterface.ARGUMENTS, arguments);

        sendMessage(message);
    }

    /**
     * Sends a "set" message to the communication partner.
     *
     * @param bufferName the buffer name
     * @param value      the new buffer value
     *
     * @throws IOException if the send operation fails
     */
    void set(String bufferName, double value) throws IOException {

        checkConnection();

        JsonObject arguments = new JsonObject();
        arguments.add(JsonInterface.NAME, bufferName);
        arguments.add(JsonInterface.VALUE, value);

        JsonObject message = new JsonObject();
        message.add(JsonInterface.TYPE, JsonInterface.SET);
        message.add(JsonInterface.ARGUMENTS, arguments);

        sendMessage(message);
    }

    /**
     * Sends a "push" message to the communication partner.
     *
     * @param value       the buffer value
     * @param spontaneous is this message spontaneous sent
     *
     * @throws IOException if the send operation fails
     */
    void push(SimpleData value, boolean spontaneous) throws IOException {

        Double doubleValue = value.getValue() == null ? null : value.getValue().doubleValue();
        push(value.getBufferName(), value.getState().name(), doubleValue, value.getTimestamp(), spontaneous);
    }

    /**
     * Sends a "push" message to the communication partner.
     *
     * @param bufferName  the buffer name
     * @param state       the buffer state
     * @param value       the buffer value
     * @param timestamp   the timestamp of last change
     * @param spontaneous is this message spontaneous sent
     *
     * @throws IOException if the send operation fails
     */
    void push(String bufferName, String state, Double value, Date timestamp, boolean spontaneous) throws IOException {

        checkConnection();

        JsonObject arguments = new JsonObject();
        arguments.add(JsonInterface.NAME, bufferName);
        arguments.add(JsonInterface.TIMESTAMP, timestamp.getTime());
        arguments.add(JsonInterface.STATE, state);
        if (value != null) arguments.add(JsonInterface.VALUE, value);
        arguments.add(JsonInterface.SPONTANEOUS, spontaneous);

        JsonObject message = new JsonObject();
        message.add(JsonInterface.TYPE, JsonInterface.PUSH);
        message.add(JsonInterface.ARGUMENTS, arguments);

        sendMessage(message);
    }
}



