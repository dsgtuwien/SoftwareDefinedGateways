package at.ac.tuwien.infosys.g2021.common.communication;

import at.ac.tuwien.infosys.g2021.common.communication.jaxb.AcceptedTag;
import at.ac.tuwien.infosys.g2021.common.communication.jaxb.BufferMetaInfoTag;
import at.ac.tuwien.infosys.g2021.common.communication.jaxb.BufferNamesTag;
import at.ac.tuwien.infosys.g2021.common.communication.jaxb.DisconnectTag;
import at.ac.tuwien.infosys.g2021.common.communication.jaxb.EstablishTag;
import at.ac.tuwien.infosys.g2021.common.communication.jaxb.GetImmediateTag;
import at.ac.tuwien.infosys.g2021.common.communication.jaxb.GetTag;
import at.ac.tuwien.infosys.g2021.common.communication.jaxb.Message;
import at.ac.tuwien.infosys.g2021.common.communication.jaxb.MetaInfoTag;
import at.ac.tuwien.infosys.g2021.common.communication.jaxb.ObjectFactory;
import at.ac.tuwien.infosys.g2021.common.communication.jaxb.PushTag;
import at.ac.tuwien.infosys.g2021.common.communication.jaxb.QueryBuffersTag;
import at.ac.tuwien.infosys.g2021.common.communication.jaxb.QueryMetaInfoTag;
import at.ac.tuwien.infosys.g2021.common.communication.jaxb.RejectedTag;
import at.ac.tuwien.infosys.g2021.common.communication.jaxb.SetTag;
import at.ac.tuwien.infosys.g2021.common.util.Loggers;
import at.ac.tuwien.infosys.g2021.common.util.PanicError;
import java.io.IOException;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * This class transforms a method interface into message objects and sends the message objects to the communication partner.
 */
class MessageSender {

    // The factory to create messages
    private ObjectFactory objectFactory;

    // The connection to the peer
    private Connection connection;

    // The calendars for sending date values
    private DatatypeFactory dataTypeFactory;
    private GregorianCalendar calendar;

    // The logger
    private final static Logger logger = Loggers.getLogger(MessageSender.class);

    /**
     * Initialize an instance with no communication partner assigned.
     */
    MessageSender() {

        try {
            objectFactory = new ObjectFactory();
            dataTypeFactory = DatatypeFactory.newInstance();
            calendar = new GregorianCalendar();
        }
        catch (DatatypeConfigurationException e) {
            throw new PanicError(e);
        }
    }

    /**
     * Initialize an instance and assigns a connection to it.
     */
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
     * @param name    the name of the message in the log
     * @param message the message
     *
     * @throws IOException if the send operation fails
     */
    private void sendMessage(String name, Message message) throws IOException {

        connection.send(message);
        logger.fine(String.format("The message '%s' has been sent over the connection #%d", name, connection.getId()));
    }

    /**
     * Sends an "establish" message to the communication partner.
     *
     * @throws IOException if the send operation fails
     */
    void establish() throws IOException {

        checkConnection();

        Message message = objectFactory.createMessage();
        EstablishTag establishTag = objectFactory.createEstablishTag();

        establishTag.setVersion(CommunicationSettings.version());
        message.setEstablish(establishTag);

        sendMessage(String.format("establish(%d)", CommunicationSettings.version()), message);
    }

    /**
     * Sends an "accepted" message to the communication partner.
     *
     * @throws IOException if the send operation fails
     */
    void accepted() throws IOException {

        checkConnection();

        Message message = objectFactory.createMessage();
        AcceptedTag acceptedTag = objectFactory.createAcceptedTag();
        message.setAccepted(acceptedTag);

        sendMessage("accepted()", message);
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

        Message message = objectFactory.createMessage();
        RejectedTag rejectedTag = objectFactory.createRejectedTag();

        rejectedTag.setReason(reason);
        message.setRejected(rejectedTag);

        sendMessage(String.format("establish(%s)", reason), message);
    }

    /**
     * Sends a "disconnect" message to the communication partner.
     *
     * @throws IOException if the send operation fails
     */
    void disconnect() throws IOException {

        checkConnection();

        Message message = objectFactory.createMessage();
        DisconnectTag disconnectTag = objectFactory.createDisconnectTag();
        message.setDisconnect(disconnectTag);

        sendMessage("disconnect()", message);
    }

    /**
     * Sends a "queryBuffers" message to the communication partner.
     *
     * @param namePattern     a regular expression for the buffer name
     * @param metaInfoPattern a regular expression for the buffer meta info
     *
     * @throws IOException if the send operation fails
     */
    void queryBuffers(String namePattern, String metaInfoPattern) throws IOException {

        checkConnection();

        Message message = objectFactory.createMessage();
        QueryBuffersTag queryBuffersTag = objectFactory.createQueryBuffersTag();

        queryBuffersTag.setName(namePattern);
        queryBuffersTag.setMetaInfo(metaInfoPattern);
        message.setQueryBuffers(queryBuffersTag);

        sendMessage(String.format("queryBuffers(%s, %s)", namePattern, metaInfoPattern), message);
    }

    /**
     * Sends a "bufferNames" message to the communication partner.
     *
     * @param names the buffer names
     *
     * @throws IOException if the send operation fails
     */
    void bufferNames(List<String> names) throws IOException {

        checkConnection();

        Message message = objectFactory.createMessage();
        BufferNamesTag bufferNamesTag = objectFactory.createBufferNamesTag();

        bufferNamesTag.getName().addAll(names);
        message.setBufferNames(bufferNamesTag);

        sendMessage(String.format("bufferNames(%d entries)", names.size()), message);
    }

    /**
     * Sends a "queryMetaInfo" message to the communication partner.
     *
     * @param bufferName the buffer name
     *
     * @throws IOException if the send operation fails
     */
    void queryMetaInfo(String bufferName) throws IOException {

        checkConnection();

        Message message = objectFactory.createMessage();
        QueryMetaInfoTag queryMetaInfoTag = objectFactory.createQueryMetaInfoTag();

        queryMetaInfoTag.setName(bufferName);
        message.setQueryMetaInfo(queryMetaInfoTag);

        sendMessage(String.format("queryMetaInfo(%s)", bufferName), message);
    }

    /**
     * Sends a "bufferMetaInfo" message to the communication partner.
     *
     * @param bufferName the name of the buffer
     * @param metaInfo   the assigned meta info
     *
     * @throws IOException if the send operation fails
     */
    void bufferMetaInfo(String bufferName, Map<String, String> metaInfo) throws IOException {

        checkConnection();

        Message message = objectFactory.createMessage();
        BufferMetaInfoTag bufferMetaInfoTag = objectFactory.createBufferMetaInfoTag();

        bufferMetaInfoTag.setName(bufferName);
        for (Map.Entry<String, String> entry : metaInfo.entrySet()) {

            MetaInfoTag metaInfoTag = objectFactory.createMetaInfoTag();

            metaInfoTag.setName(entry.getKey());
            metaInfoTag.setInfo(entry.getValue());

            bufferMetaInfoTag.getMetaInfo().add(metaInfoTag);
        }
        message.setBufferMetaInfo(bufferMetaInfoTag);

        sendMessage(String.format("bufferMetaInfo(%s, %d entries)", bufferName, metaInfo.size()), message);
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

        Message message = objectFactory.createMessage();
        GetImmediateTag getImmediateTag = objectFactory.createGetImmediateTag();

        getImmediateTag.setName(bufferName);
        message.setGetImmediate(getImmediateTag);

        sendMessage(String.format("getImmediate(%s)", bufferName), message);
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

        Message message = objectFactory.createMessage();
        GetTag getTag = objectFactory.createGetTag();

        getTag.setName(bufferName);
        message.setGet(getTag);

        sendMessage(String.format("get(%s)", bufferName), message);
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

        Message message = objectFactory.createMessage();
        SetTag setTag = objectFactory.createSetTag();

        setTag.setName(bufferName);
        setTag.setValue(value);
        message.setSet(setTag);

        sendMessage(String.format("set(%s, %g)", bufferName, value), message);
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

        Message message = objectFactory.createMessage();
        PushTag pushTag = objectFactory.createPushTag();

        calendar.setTime(timestamp);
        XMLGregorianCalendar at = dataTypeFactory.newXMLGregorianCalendar(calendar);

        pushTag.setName(bufferName);
        pushTag.setState(state);
        pushTag.setValue(value);
        pushTag.setTimestamp(at);
        pushTag.setSpontaneous(spontaneous);

        message.setPush(pushTag);

        if (value == null) sendMessage(String.format("push(%s, %tc, %s, null)", bufferName, timestamp, state), message);
        else sendMessage(String.format("push(%s, %tc, %s, %g)", bufferName, timestamp, state, value), message);
    }
}


