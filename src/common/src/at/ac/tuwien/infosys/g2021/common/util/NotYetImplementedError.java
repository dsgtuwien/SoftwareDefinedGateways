package at.ac.tuwien.infosys.g2021.common.util;

/**
 * This error signals the usage of a not implemented feature.
 */
public class NotYetImplementedError extends Error {

    /**
     * Creating this error.
     */
    public NotYetImplementedError() { super("not yet implemented"); }

    /**
     * Creating this error.
     *
     * @param message a message
     */
    public NotYetImplementedError(String message) { super(message); }

    /**
     * Creating this error.
     *
     * @param message a message
     * @param exc     the causing throwable
     */
    public NotYetImplementedError(String message, Throwable exc) { super(message, exc); }

    /**
     * Creating this error.
     *
     * @param exc the causing throwable
     */
    public NotYetImplementedError(Throwable exc) { super(exc); }
}
