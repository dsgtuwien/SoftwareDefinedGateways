package at.ac.tuwien.infosys.g2021.daemon;

import at.ac.tuwien.infosys.g2021.common.DummyAdapterConfiguration;
import at.ac.tuwien.infosys.g2021.common.SimpleData;

/** This Adapter does no conversion. */
class DummyAdapter extends Adapter {

    /** Initialization
     * @param configuration The configuration
     */
    DummyAdapter(DummyAdapterConfiguration configuration) { super(configuration); }

    /**
     * This is the notification of a spontaneous value change. This adapter
     * routes the value change to all of its value change customers.
     *
     * @param newValue the new buffer value
     */
    @Override
    public void valueChanged(SimpleData newValue) { set(newValue); }
}
