package at.ac.tuwien.infosys.g2021.common.communication;

import at.ac.tuwien.infosys.g2021.common.BufferConfiguration;
import at.ac.tuwien.infosys.g2021.common.SimpleData;
import at.ac.tuwien.infosys.g2021.common.communication.jaxb.AcceptedTag;
import at.ac.tuwien.infosys.g2021.common.communication.jaxb.BufferConfigurationTag;
import at.ac.tuwien.infosys.g2021.common.communication.jaxb.BufferMetainfoTag;
import at.ac.tuwien.infosys.g2021.common.communication.jaxb.BufferNamesTag;
import at.ac.tuwien.infosys.g2021.common.communication.jaxb.DisconnectTag;
import at.ac.tuwien.infosys.g2021.common.communication.jaxb.EstablishTag;
import at.ac.tuwien.infosys.g2021.common.communication.jaxb.GetBufferConfigurationTag;
import at.ac.tuwien.infosys.g2021.common.communication.jaxb.GetImmediateTag;
import at.ac.tuwien.infosys.g2021.common.communication.jaxb.GetTag;
import at.ac.tuwien.infosys.g2021.common.communication.jaxb.Message;
import at.ac.tuwien.infosys.g2021.common.communication.jaxb.ObjectFactory;
import at.ac.tuwien.infosys.g2021.common.communication.jaxb.PushTag;
import at.ac.tuwien.infosys.g2021.common.communication.jaxb.QueryBuffersByMetainfoTag;
import at.ac.tuwien.infosys.g2021.common.communication.jaxb.QueryBuffersByNameTag;
import at.ac.tuwien.infosys.g2021.common.communication.jaxb.QueryMetainfoTag;
import at.ac.tuwien.infosys.g2021.common.communication.jaxb.RejectedTag;
import at.ac.tuwien.infosys.g2021.common.communication.jaxb.ReleaseBufferTag;
import at.ac.tuwien.infosys.g2021.common.communication.jaxb.SetBufferConfigurationTag;
import at.ac.tuwien.infosys.g2021.common.communication.jaxb.SetTag;
import at.ac.tuwien.infosys.g2021.common.communication.jaxb.ShutdownTag;
import at.ac.tuwien.infosys.g2021.common.util.Loggers;
import at.ac.tuwien.infosys.g2021.common.util.PanicError;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.logging.Logger;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

/** This class transforms a method interface into message objects and sends the message objects to the communication partner. */
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

    /** Initialize an instance with no communication partner assigned. */
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
     * @param name    the name of the message in the log
     * @param message the message
     *
     * @throws IOException if the send operation fails
     */
    private void sendMessage(String name, Message message) throws IOException {

        connection.send(message);
        logger.fine(String.format("The message '%s' has been sent over the connection #%d.", name, connection.getId()));
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

        sendMessage(String.format("rejected(%s)", reason), message);
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
     * Sends a "shutdown" message to the communication partner.
     *
     * @throws IOException if the send operation fails
     */
    void shutdown() throws IOException {

        checkConnection();

        Message message = objectFactory.createMessage();
        ShutdownTag shutdownTag = objectFactory.createShutdownTag();
        message.setShutdown(shutdownTag);

        sendMessage("shutdown()", message);
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

        Message message = objectFactory.createMessage();
        QueryBuffersByNameTag queryBuffersTag = objectFactory.createQueryBuffersByNameTag();

        queryBuffersTag.setName(name);
        message.setQueryBuffersByName(queryBuffersTag);

        sendMessage(String.format("queryBuffersByName(%s)", name), message);
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

        Message message = objectFactory.createMessage();
        QueryBuffersByMetainfoTag queryBuffersTag = objectFactory.createQueryBuffersByMetainfoTag();

        queryBuffersTag.setTopic(topic);
        queryBuffersTag.setMetainfo(metainfoPattern);
        message.setQueryBuffersByMetainfo(queryBuffersTag);

        sendMessage(String.format("queryBuffersByMetainfo(%s, %s)", topic, metainfoPattern), message);
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
    void queryMetainfo(String bufferName) throws IOException {

        checkConnection();

        Message message = objectFactory.createMessage();
        QueryMetainfoTag queryMetainfoTag = objectFactory.createQueryMetainfoTag();

        queryMetainfoTag.setName(bufferName);
        message.setQueryMetainfo(queryMetainfoTag);

        sendMessage(String.format("queryMetainfo(%s)", bufferName), message);
    }

    /**
     * Sends a "bufferMetaInfo" message to the communication partner.
     *
     * @param bufferName the name of the buffer
     * @param metainfo   the assigned meta info
     *
     * @throws IOException if the send operation fails
     */
    void bufferMetainfo(String bufferName, Map<String, String> metainfo) throws IOException {

        checkConnection();

        Message message = objectFactory.createMessage();
        JAXBInterface jaxb = new JAXBInterface();
        BufferMetainfoTag bufferMetainfoTag = objectFactory.createBufferMetainfoTag();

        bufferMetainfoTag.setName(bufferName);
        jaxb.metainfoToXML(metainfo, bufferMetainfoTag.getMetainfo());

        message.setBufferMetainfo(bufferMetainfoTag);

        sendMessage(String.format("bufferMetainfo(%s, %d entries)", bufferName, metainfo.size()), message);
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

        Message message = objectFactory.createMessage();
        JAXBInterface jaxb = new JAXBInterface();
        BufferConfigurationTag bufferConfigurationTag = jaxb.configurationToXML(configuration);

        message.setBufferConfiguration(bufferConfigurationTag);

        sendMessage("bufferConfiguration()", message);
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

        Message message = objectFactory.createMessage();
        GetBufferConfigurationTag getBufferConfigurationTag = objectFactory.createGetBufferConfigurationTag();

        getBufferConfigurationTag.setName(bufferName);

        message.setGetBufferConfiguration(getBufferConfigurationTag);

        sendMessage(String.format("getBufferConfiguration(%s)", bufferName), message);
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

        Message message = objectFactory.createMessage();
        JAXBInterface jaxb = new JAXBInterface();
        SetBufferConfigurationTag setBufferConfigurationTag = objectFactory.createSetBufferConfigurationTag();

        setBufferConfigurationTag.setName(bufferName);
        setBufferConfigurationTag.setCreate(createAllowed);
        setBufferConfigurationTag.setBufferConfiguration(jaxb.configurationToXML(configuration));

        message.setSetBufferConfiguration(setBufferConfigurationTag);

        sendMessage(String.format("setBufferConfiguration(%s, configuration, %s)", bufferName, Boolean.valueOf(createAllowed).toString()),
                    message);
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

        Message message = objectFactory.createMessage();
        ReleaseBufferTag releaseBufferTag = objectFactory.createReleaseBufferTag();

        releaseBufferTag.setName(bufferName);

        message.setReleaseBuffer(releaseBufferTag);

        sendMessage(String.format("releaseBuffer(%s)", bufferName), message);
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


