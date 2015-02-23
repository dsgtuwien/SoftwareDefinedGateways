package at.ac.tuwien.infosys.g2021.common.communication;

import at.ac.tuwien.infosys.g2021.common.util.Loggers;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Logger;

/**
 * This class provides all the settings, needed by the communication between
 * DataPoints and the buffer daemon.
 */
public final class CommunicationSettings {

    /** This is the current protocol version. */
    private final static int VERSION = 1;

    /** This is the default port for the TCP/IP-connection to the daemon. */
    private final static int DAEMON_DEFAULT_PORT = 3449;

    /** This is the timeout in milliseconds, the receiver thread will wait for the GBot thread to get ready for receiving an answer. */
    final static long CLIENT_READY_TIMEOUT = 250L;

    /** This is the timeout in milliseconds, a client will wait for an answer of the buffer daemon. */
    final static long DAEMON_ANSWER_TIMEOUT = 2500L;

    /** This is the logger. */
    private static final Logger LOGGER = Loggers.getLogger(CommunicationSettings.class);

    // Daemons host and port.
    private static InetAddress daemonHost = null;
    private static int daemonPort = -1;

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
    public static InetAddress bufferDaemonAddress() {

        if (daemonHost == null) {

            String hostName = null;

            // Checking for an explicit host address.
            String property = System.getProperty("at.ac.tuwien.infosys.g2021.daemon.address");
            if (property != null) hostName = property;

            // Resolving the internet address.
            if (hostName != null) {
                try {
                    daemonHost = InetAddress.getByName(hostName);
                }
                catch (UnknownHostException e) {
                    LOGGER.warning(String.format("The host '%s' is not known. The local host address will be used.", hostName));
                }
            }

            // Using the local host loopback address as default
            if (daemonHost == null) daemonHost = InetAddress.getLoopbackAddress();

            LOGGER.config(String.format("The address '%s' is used as server address.", daemonHost.toString()));
        }
        return daemonHost;
    }

    /**
     * Returns the port number, on which the buffer daemon is listening.
     *
     * @return the server port
     */
    public static int bufferDaemonPort() {

        if (daemonPort < 0) {

            daemonPort = DAEMON_DEFAULT_PORT;

            // Checking for an explicit port number.
            String property = System.getProperty("at.ac.tuwien.infosys.g2021.daemon.port");
            if (property != null) {
                try {
                    daemonPort = Integer.parseInt(property);
                }
                catch (NumberFormatException nfe) {
                    LOGGER.warning(String.format("'%s' is not a valid port number. The default port %d will be used.", property, DAEMON_DEFAULT_PORT));
                }
            }

            // Checking for an valid port number.
            if (daemonPort < 0 || daemonPort > 65535) {
                LOGGER.warning(String.format("'%d' is not a valid port number. The default port %d will be used.", daemonPort, DAEMON_DEFAULT_PORT));
                daemonPort = DAEMON_DEFAULT_PORT;
            }

            LOGGER.config(String.format("The port '%d' is used as server port.", daemonPort));
        }

        return daemonPort;
    }

    /** Instances of this class are not allowed. */
    private CommunicationSettings() {}
}
