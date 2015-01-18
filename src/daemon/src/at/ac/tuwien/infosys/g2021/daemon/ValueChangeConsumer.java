package at.ac.tuwien.infosys.g2021.daemon;

import at.ac.tuwien.infosys.g2021.common.SimpleData;

/** A value change consumer is an interface, which is notified about changed values. */
public interface ValueChangeConsumer {

    /**
     * This is the notification of a spontaneous value change.
     *
     * @param newValue the new buffer value
     */
    public void valueChanged(SimpleData newValue);
}
