package at.ac.tuwien.infosys.g2021.intf;

import at.ac.tuwien.infosys.g2021.common.BufferState;
import at.ac.tuwien.infosys.g2021.common.SimpleData;

/**
 * All classes implementing this interface can be registered at data points. After registration, state and value
 * changes of the data points buffers will be signalled by method calls to the registered observer instance.
 *
 * @see DataPoint#addDataPointObserver(DataPointObserver)
 * @see DataPoint#removeDataPointObserver(DataPointObserver)
 */
public interface DataPointObserver {

    /**
     * The overall state of the data point has changed.
     *
     * @param dataPoint the affected data point
     * @param oldOne    the state left
     * @param newOne    the current state of the data point
     */
    public void dataPointStateChanged(DataPoint dataPoint, BufferState oldOne, BufferState newOne);

    /**
     * A buffer has been assigned to the data point.
     *
     * @param dataPoint  the data point
     * @param bufferName the name of the assigned buffer
     */
    public void bufferAssigned(DataPoint dataPoint, String bufferName);

    /**
     * A buffer has been detached from the data point.
     *
     * @param dataPoint  the data point
     * @param bufferName the name of the detached buffer
     */
    public void bufferDetached(DataPoint dataPoint, String bufferName);

    /**
     * A buffer has changed its state or value.
     *
     * @param dataPoint the data point
     * @param oldOne    the outdated buffer data. This argument may
     *                  be <tt>null</tt>, if the buffer just has been assigned.
     * @param newOne    the current buffer dataThis argument may
     *                  be <tt>null</tt>, if the buffer just has been detached.
     */
    public void bufferChanged(DataPoint dataPoint, SimpleData oldOne, SimpleData newOne);
}
