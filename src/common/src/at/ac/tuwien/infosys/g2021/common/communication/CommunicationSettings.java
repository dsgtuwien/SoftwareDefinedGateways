package at.ac.tuwien.infosys.g2021.common.communication;

import at.ac.tuwien.infosys.g2021.common.util.Loggers;
import at.ac.tuwien.infosys.g2021.common.util.PanicError;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Logger;

/**
 * This class provides all the settings, needed by the communication between
 * DataPoints and the buffer daemon.
 */
final class CommunicationSettings {

    /** This is the current protocol version. */
    private final static int VERSION = 1;

    /** This is the default port for the TCP/IP-connection to the daemon. */
    private final static int DAEMON_DEFAULT_PORT = 3449;

    /** This is the timeout in milliseconds, the receiver thread will wait for the GBot thread to get ready for receiving an answer. */
    final static long CLIENT_READY_TIMEOUT = 250L;

    /** This is the timeout in milliseconds, a client will wait for an answer of the buffer daemon. */
    final static long DAEMON_ANSWER_TIMEOUT = 2500L;

    /** The separator string between messages */
    final static byte[] MESSAGE_SEPARATOR = {'\r', '\n', '<', '>', '\r', '\n'};

    /**
     * This is the logger.
     */
    private static final Logger LOGGER = Loggers.getLogger(CommunicationSettings.class);

    /**
     * Returns the current protocol version.
     *
     * @return the server port
     */
    static int version() { return VERSION; }

    /**
     * Returns the host address of the buffer daemon.
     *
     * @return the server address
     */
    static InetAddress bufferDaemonAddress() {

        String hostName = null;
        InetAddress result = null;

        // Checking for an explicit host address.
        String property = System.getProperty("at.ac.tuwien.infosys.g2021.daemon.address");
        if (property != null) hostName = property;

        // Resolving the internet address.
        if (hostName != null) {
            try {
                result = InetAddress.getByName(hostName);
            }
            catch (UnknownHostException e) {
                LOGGER.warning(String.format("The host '%s' is not known. The local host address will be used.", hostName));
            }
        }

        // Using the local host as default
        if (result == null) {
            try {
                result = InetAddress.getLocalHost();
            }
            catch (UnknownHostException e) {
                throw new PanicError("unable to get the local host name", e);
            }
        }

        LOGGER.config(String.format("The address '%s' is used as server address.", result.toString()));
        return result;
    }

    /**
     * Returns the port number, on which the buffer daemon is listening.
     *
     * @return the server port
     */
    static int bufferDaemonPort() {

        int result = DAEMON_DEFAULT_PORT;

        // Checking for an explicit port number.
        String property = System.getProperty("at.ac.tuwien.infosys.g2021.daemon.port");
        if (property != null) {
            try {
                result = Integer.parseInt(property);
            }
            catch (NumberFormatException nfe) {
                LOGGER.warning(String.format("'%s' is not a valid port number. The default port %d will be used.", property, DAEMON_DEFAULT_PORT));
            }
        }

        // Checking for an valid port number.
        if (result < 0 || result > 65535) {
            LOGGER.warning(String.format("'%d' is not a valid port number. The default port %d will be used.", result, DAEMON_DEFAULT_PORT));
            result = DAEMON_DEFAULT_PORT;
        }

        LOGGER.config(String.format("The port '%d' is used as server port.", result));
        return result;
    }

    /**
     * Instances of this class are not allowed.
     */
    private CommunicationSettings() {}
}
