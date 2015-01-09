package at.ac.tuwien.infosys.g2021.common.communication;

import at.ac.tuwien.infosys.g2021.common.SimpleData;

/**
 * Objects implementing this interface can be notified about spontaneous value
 * changes of buffers.
 */
public interface ValueChangeObserver {

    /**
     * This is the notification of a spontaneous value change.
     *
     * @param newValue the new buffer value
     */
    public void valueChanged(SimpleData newValue);

    /**
     * This is the notification about the lost connection to the daemon.
     */
    public void communicationLost();
}
