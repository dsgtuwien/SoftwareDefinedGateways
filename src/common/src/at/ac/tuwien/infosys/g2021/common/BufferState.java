package at.ac.tuwien.infosys.g2021.common;

/**
 * <p>
 * Here are the states defined, which <tt>DataPoints</tt> and <tt>buffers</tt> may encounter during their lifecycle.
 * </p>
 */
public enum BufferState {

    /** This is state immediately after the creation of the object. Internal initialization is in progress. */
    INITIALIZING,

    /**
     * The object is ready for reading data (<tt>{@link at.ac.tuwien.infosys.g2021.common.SimpleData}</tt>-objects) and for changing actor
     * values.
     */
    READY,

    /**
     * An error has been occurred. The gathering of <tt>{@link at.ac.tuwien.infosys.g2021.common.SimpleData}s</tt>-objects has been stopped. For
     * <tt>buffers</tt> this states means, that the buffer isn't usable any more. It must be set to the initial state using the
     * <tt>BufferManager</tt>. <tt>DataPoints</tt> encounter this state
     * after one of the assigned <tt>buffers</tt> achieve the <tt>FAULTED</tt>-state.
     */
    FAULTED,

    /** The object has been released and all the system resources has been closed. */
    RELEASED,

    /** The connection to the buffer daemon is broken. */
    ISOLATED
}


