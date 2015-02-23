/**
 * This package implements the communication between the buffer daemon and the GBot
 * interfaces. As transport mechanism TCP/IP is used. A server socket in the
 * buffer daemon listens for new connections. Every JVM containing GBots with
 * DataPoints establish one single connection to the server socket in the buffer daemon.
 * As server address and port "localhost:3449" is assumed. This defaults can be
 * overridden with the system properties "<tt>at.ac.tuwien.infosys.g2021.daemon.address</tt>"
 * and  "<tt>at.ac.tuwien.infosys.g2021.daemon.port</tt>".
 *
 * <h3>Object Model</h3>
 *
 * At the client side, there is a singleton object called <tt>{@link at.ac.tuwien.infosys.g2021.common.communication.ClientEndpoint}</tt> within every
 * JVM containing GBots. This singleton instance is created during the instantiation of the first <tt>DataPoint</tt> object.
 * The <tt>ClientEndpoint</tt> maintains one TCP/IP connection to the buffer daemon as long as minimal one <tt>DataPoint</tt> exists.
 * <p>
 * At the TCP/IP server side within the daemon, a <tt>{@link at.ac.tuwien.infosys.g2021.common.communication.DaemonEndpoint}</tt>
 * object receives all messages from all <tt>ClientEndpoint</tt>s. It interprets the received messages, do the necessary labour
 * and sends the result as another message back to the <tt>ClientEndpoint</tt>. For every TCP/IP client connection there exists
 * a separate thread within the daemon. At the client side the calling thread is suspended until the answer from the daemon is
 * received. If there is no answer from the daemon within 2 seconds the <tt>ClientEndpoint</tt> assumes, that the daemon has died.
 * It closes the connection to the daemon and changes to the <tt>ISOLATED</tt> state.
 *
 * <h3>Protocol</h3>
 *
 * Due to simpler debugging, all messages are sent as JSON objects. They are sent on a permanent open TCP/IP stream. Therefore
 * every message is sent as an UTF-8 encoded byte array (look for the <tt>DataOutputStream</tt> documentation of Java).
 * <p>
 * All messages have the same structure. The string property 'type' describes the kind of message. These types are shown
 * in the following scenarios. The 'arguments' object contains any additional data to the message.
 *
 * <h4>Establishing a Connection</h4>
 *
 * The following scenario shows the messages of a successfully connection to the daemon.
 * <pre>
 *     GBot                                    daemon
 *       |
 *       |--------------------------------------&gt; Establishing the TCP/IP connection
 *       |                                       |
 *       |&lt;establish&gt;                            |
 *       |--------------------------------------&gt;| The daemon checks the client protocol version.
 *       |                                       |
 *       |                                       | The version is ok, the daemon accepts the
 *       |                          &lt;accepted&gt;   | connection.
 *       |&lt;--------------------------------------|
 *       |                                       |
 *       |                                       |
 *       |                                       | Now the connection can be used.
 * </pre>
 *
 * If client and daemon uses a different protocol, the client cannot connect to the daemon.
 *
 * <pre>
 *     GBot                                    daemon
 *       |
 *       |--------------------------------------&gt; Establishing the TCP/IP connection
 *       |                                       |
 *       |&lt;establish&gt;                            |
 *       |--------------------------------------&gt;| The daemon checks the client protocol version.
 *       |                                       |
 *       |                          &lt;rejected&gt;   | Its a wrong version.
 *       |&lt;--------------------------------------|
 *       |                                       | The daemon closes the connection.
 *       |The client closes the connection too.
 * </pre>
 *
 * <h4>Closing a Connection</h4>
 *
 * Client or daemon can close the connection at any time sending a &lt;disconnect&gt; message. After sending or
 * receiving this message, the socket is closed immediately. The client <tt>DataPoint</tt>s changes to the
 * <tt>DISCONNECTED</tt> state.
 *
 * <h4>Querying Buffer Names and Meta Information</h4>
 *
 * <pre>
 *     GBot                                    daemon
 *       |                                       |
 *       |                                       |
 *       |&lt;queryBuffersByName&gt;                   |
 *       | or                                    |
 *       |&lt;queryBuffersByMetainfo&gt;               |
 *       |--------------------------------------&gt;| Here the daemon looks for matching buffers
 *       |                                       | and answers with a list of buffer names.
 *       |                       &lt;bufferNames&gt;   |
 *       |&lt;--------------------------------------|
 *       |                                       |
 *       |                                       |
 *       |                                       |
 *       |&lt;queryMetaInfo&gt;                        |
 *       |--------------------------------------&gt;| Now the GBot wants to read all the meta info
 *       |                                       | of a buffer. The daemon sends a list of meta info
 *       |                                       | assigned to the buffer.
 *       |                    &lt;bufferMetaInfo&gt;   |
 *       |&lt;--------------------------------------|
 *       |                                       |
 *       |                                       |
 *       |                                       |
 *       |&lt;queryMetaInfo&gt;                        |
 *       |--------------------------------------&gt;| Reading the meta info of an unknown buffer
 *       |                                       | causes a rejection.
 *       |                          &lt;rejected&gt;   |
 *       |&lt;--------------------------------------|
 *       |                                       |
 *</pre>
 *
 * <h4>Reading Buffer State and Value</h4>
 *
 * <pre>
 *     GBot                                    daemon
 *       |                                       |
 *       |                                       |
 *       |&lt;getImmediate&gt;                         |
 *       |--------------------------------------&gt;| This is a client request for reading buffer info immediately.
 *       |                                       | The daemon answers as requested.
 *       |                              &lt;push&gt;   |
 *       |&lt;--------------------------------------|
 *       |                                       |
 *       |                                       |
 *       |                                       |
 *       |&lt;get&gt;                                  |
 *       |--------------------------------------&gt;| Now the GBot knows the current buffer info. New buffer info
 *       |                                       | is necessary, if the buffer value or state changes. The daemon
 *       |                                       | sends no answer! If there is no such buffer known by the daemon,
 *       |                                       | this message is ignored at all.
 *       |                                       |
 *       |                                       | ...
 *       |                                       |
 *       |                                       | Here the buffer value changes. The daemon sends the updated
 *       |                              &lt;push&gt;   | buffer info to the client.
 *       |&lt;--------------------------------------|
 *       |                                       |
 *       |                                       | Another change of the buffer information! The daemon does
 *       |                                       | nothing. There was no &lt;get&gt; message received.
 *       |                                       |
 *       |&lt;getImmediate&gt;                         |
 *       |--------------------------------------&gt;| At last the GBot wants to know buffer info about an
 *       |                                       | unknown buffer. The daemon rejects this request.
 *       |                          &lt;rejected&gt;   |
 *       |&lt;--------------------------------------|
 *       |                                       |
 *</pre>
 *
 * <h4>Setting the Buffer Value</h4>
 *
 * <pre>
 *     GBot                                    daemon
 *       |                                       |
 *       |&lt;set&gt;                                  | The GBot changes a buffer value.
 *       |--------------------------------------&gt;| If this is a well known buffer, the daemon sets the
 *       |                              &lt;push&gt;   | hardware ports as requested and pushes the new
 *       |&lt;--------------------------------------| port info.
 *       |                                       |
 *       |                                       |
 *       |&lt;set&gt;                                  |
 *       |--------------------------------------&gt;| At last the GBot sets the value of an unknown or an
 *       |                                       | erroneous buffer. The daemon rejects this message immediately.
 *       |                          &lt;rejected&gt;   |
 *       |&lt;--------------------------------------|
 *       |                                       |
 *</pre>
 *
 * <h4>Reading a buffer configuration</h4>
 *
 * <pre>
 *     GBot                                    daemon
 *       |                                       |
 *       |&lt;getBufferConfiguration&gt;               | The buffer manager wants to read the configuration of a buffer.
 *       |--------------------------------------&gt;| If this is a well known buffer, the daemon returns the
 *       |               &lt;bufferConfiguration&gt;   | buffer configuration.
 *       |&lt;--------------------------------------|
 *       |                                       |
 *       |                                       |
 *       |&lt;getBufferConfiguration&gt;               |
 *       |--------------------------------------&gt;| At last the buffer manager reads the configuration of an
 *       |                                       | unknown buffer. The daemon rejects this message immediately.
 *       |                          &lt;rejected&gt;   |
 *       |&lt;--------------------------------------|
 *       |                                       |
 *</pre>
 *
 * <h4>Updating a buffer configuration</h4>
 *
 * <pre>
 *     GBot                                    daemon
 *       |                                       |
 *       |&lt;getBufferConfiguration&gt;               | The buffer manager wants to read the configuration of a buffer.
 *       |--------------------------------------&gt;|
 *       |               &lt;bufferConfiguration&gt;   | The daemon returns the current buffer configuration.
 *       |&lt;--------------------------------------|
 *       |                                       |
 *       |                                       |
 *       |&lt;setBufferConfiguration&gt;               | After changing some configuration items, the new configuration
 *       |--------------------------------------&gt;| is sent to the daemon.
 *       |                                       |
 *       |                          &lt;accepted&gt;   | The daemon accepts the new configuration.
 *       |&lt;--------------------------------------|
 *       |                                    or |
 *       |                          &lt;rejected&gt;   | The buffer was released in the meantime. The daemon rejects an update.
 *       |&lt;--------------------------------------|
 *       |                                       |
 *</pre>
 *
 * <h4>Creating a buffer</h4>
 *
 * <p>This works like a buffer update. The only difference is, that the attribute "<tt>create</tt>" of the "<tt>setBufferConfiguration</tt>"
 * message is set to <tt>true</tt>. With this setting the daemon does not reject configuration for unknown buffers, it creates a new one.
 * </p>
 *
 * <h4>Releasing a buffer</h4>
 *
 * <pre>
 *     GBot                                    daemon
 *       |                                       |
 *       |&lt;releaseBuffer&gt;                        | Just the <tt>releaseBuffer</tt>-message ist sent from the buffer manager
 *       |--------------------------------------&gt;| to the daemon.
 *       |                                       |
 *       |                          &lt;accepted&gt;   | The daemon releases the buffer.
 *       |&lt;--------------------------------------|
 *       |                                    or |
 *       |                          &lt;rejected&gt;   | The buffer was released in the meantime. The daemon rejects the second release.
 *       |&lt;--------------------------------------|
 *       |                                       |
 *</pre>
 */
package at.ac.tuwien.infosys.g2021.common.communication;