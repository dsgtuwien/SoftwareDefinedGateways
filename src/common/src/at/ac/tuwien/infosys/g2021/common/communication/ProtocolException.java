package at.ac.tuwien.infosys.g2021.common.communication;

import java.io.IOException;

/**
 * This exception signals a communication protocol error.
 */
public class ProtocolException extends IOException {

    /**
     * Creating this error.
     */
    public ProtocolException() { super("protocol error"); }

    /**
     * Creating this error.
     *
     * @param message a message
     */
    public ProtocolException(String message) { super(message); }

    /**
     * Creating this error.
     *
     * @param message a message
     * @param exc     the causing throwable
     */
    public ProtocolException(String message, Throwable exc) { super(message, exc); }

    /**
     * Creating this error.
     *
     * @param exc the causing throwable
     */
    public ProtocolException(Throwable exc) { super("protocol error", exc); }
}
