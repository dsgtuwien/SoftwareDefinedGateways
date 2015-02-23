package at.ac.tuwien.infosys.g2021.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** This is simply a data container describing the features of a buffer. */
final public class BufferConfiguration {

    // The buffer class
    private BufferClass bufferClass;

    // The adapters for adjusting input and output values.
    private List<AdapterConfiguration> adapterChain;

    // The gatherer configuration
    private GathererConfiguration gatherer;

    // The gatherer configuration
    private Map<String, String> metainfo;

    /**
     * Initializing the data container. The buffer configuration contains then a sensor buffer, driven by the
     * dummy gatherer, which always returns 0.0 as input data.
     */
    public BufferConfiguration() {

        bufferClass = BufferClass.SENSOR;
        adapterChain = new ArrayList<>();
        gatherer = new DummyGathererConfiguration();
        metainfo = new HashMap<>();
    }

    /**
     * Returns the buffer class.
     *
     * @return the buffer class
     */
    public BufferClass getBufferClass() { return bufferClass; }

    /**
     * Selects the buffer class. If the buffer class changes, all not suitable adapters are removed from the adapter chain.
     *
     * @param bufferClass the buffer class. A <tt>null</tt>-value will cause a NullPointerException.
     */
    public void setBufferClass(BufferClass bufferClass) {

        if (bufferClass == null) throw new NullPointerException("argument is null");
        if (!gatherer.isBufferClassSupported(bufferClass)) throw new IllegalArgumentException("unserviceable gatherer");

        this.bufferClass = bufferClass;
    }

    /**
     * Returns the list of all adapter configurations. The adapter at the head of the list is executed at first.
     *
     * @return the list of adapter configurations
     */
    public List<AdapterConfiguration> getAdapterChain() { return adapterChain; }

    /**
     * Returns the current gatherer configuration.
     *
     * @return the current gatherer configuration
     */
    public GathererConfiguration getGatherer() { return gatherer; }

    /**
     * Selects a new gatherer configuration.
     *
     * @param gatherer the new gatherer configuration. This argument must not be <tt>null</tt>.
     */
    public void setGatherer(GathererConfiguration gatherer) {

        if (gatherer == null) throw new NullPointerException("argument is null");
        if (!gatherer.isBufferClassSupported(bufferClass)) throw new IllegalArgumentException("bad buffer class");

        this.gatherer = gatherer;
    }

    /**
     * Returns the buffer metainfo.
     *
     * @return the buffer meta info
     */
    public Map<String, String> getMetainfo() { return metainfo; }
}


