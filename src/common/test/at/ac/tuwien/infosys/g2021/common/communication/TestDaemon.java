package at.ac.tuwien.infosys.g2021.common.communication;

import java.io.IOException;
import java.net.ServerSocket;

/** This is a simple implementation of a server socket, which creates a DaemonEndpoint-instance. */
class TestDaemon {

    // The Daemon-Endpoint.
    private DaemonEndpoint endpoint;

    // The server socket.
    private ServerSocket socket;

    // This simple thread listens for incoming connections from the server socket.
    private class ConnectionListener extends Thread {

        /** Initialization. */
        ConnectionListener() {

            super("connection listener thread");
            setDaemon(true);
        }

        /** The thread implementation. */
        @Override
        public void run() {

            ServerSocket serverSocket = socket;
            while (serverSocket != null && !serverSocket.isClosed()) {
                try {
                    if (endpoint == null) {
                        endpoint = new DaemonEndpoint(serverSocket.accept(), new TestDaemonStrategy(TestDaemon.this));
                    }
                    else {
                        serverSocket.accept().close();
                    }
                }
                catch (IOException e) {
                    // The test will fail
                }
                finally {
                    serverSocket = socket;
                }
            }
        }
    }

    /** Start listening for client connections. */
    TestDaemon() throws IOException {

        endpoint = null;

        socket = new ServerSocket(CommunicationSettings.bufferDaemonPort());
        socket.setReuseAddress(true);
        ConnectionListener listener = new ConnectionListener();
        listener.start();
    }

    /** Closes the server socket. */
    void shutdown() {

        try {
            if (socket != null) socket.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            socket = null;
        }
    }

    /**
     * Returns the Daemon-Endpoint.
     *
     * @return the DaemonEndpoint or <tt>null</tt>, if there is no connection established.
     */
    DaemonEndpoint getEndpoint() { return endpoint; }
}
