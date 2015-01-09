package at.ac.tuwien.infosys.g2021.common.util;

/**
 * This error signals a nonrecoverable system failure.
 */
public class PanicError extends Error {

    /**
     * Creating this error.
     */
    public PanicError() { super("panic"); }

    /**
     * Creating this error.
     *
     * @param message a message
     */
    public PanicError(String message) { super(message); }

    /**
     * Creating this error.
     *
     * @param message a message
     * @param exc     the causing throwable
     */
    public PanicError(String message, Throwable exc) { super(message, exc); }

    /**
     * Creating this error.
     *
     * @param exc the causing throwable
     */
    public PanicError(Throwable exc) { super(exc); }
}
