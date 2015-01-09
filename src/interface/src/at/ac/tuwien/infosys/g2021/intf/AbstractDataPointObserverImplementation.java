package at.ac.tuwien.infosys.g2021.intf;

import at.ac.tuwien.infosys.g2021.common.BufferState;
import at.ac.tuwien.infosys.g2021.common.SimpleData;

/**
 * This is an abstract implementation of the <tt>{@link DataPointObserver}</tt> interface.
 * All methods are implemented as dummy methods and can be overloaded for convenience.
 */
abstract public class AbstractDataPointObserverImplementation implements DataPointObserver {

    /**
     * Initialization of an instance.
     */
    protected AbstractDataPointObserverImplementation() {}

    /**
     * The overall state of the data point has changed.
     *
     * @param dataPoint the affected data point
     * @param oldOne    the left state
     * @param newOne    the current state of the data point
     */
    @Override
    public void dataPointStateChanged(DataPoint dataPoint, BufferState oldOne, BufferState newOne) {}

    /**
     * A buffer has been assigned to the data point.
     *
     * @param dataPoint  the data point
     * @param bufferName the name of the assigned buffer
     */
    @Override
    public void bufferAssigned(DataPoint dataPoint, String bufferName) {}

    /**
     * A buffer has been detached from the data point.
     *
     * @param dataPoint  the data point
     * @param bufferName the name of the detached buffer
     */
    @Override
    public void bufferDetached(DataPoint dataPoint, String bufferName) {}

    /**
     * A buffer has changed its state or value.
     *
     * @param dataPoint the data point
     * @param oldOne    the outdated buffer data. This argument may
     *                  be <tt>null</tt>, if the buffer just has been assigned.
     * @param newOne    the current buffer dataThis argument may
     *                  be <tt>null</tt>, if the buffer just has been detached.
     */
    @Override
    public void bufferChanged(DataPoint dataPoint, SimpleData oldOne, SimpleData newOne) {}
}
