package at.ac.tuwien.infosys.g2021.daemon;

import at.ac.tuwien.infosys.g2021.common.AdapterConfiguration;
import at.ac.tuwien.infosys.g2021.common.BufferConfiguration;
import at.ac.tuwien.infosys.g2021.common.DummyAdapterConfiguration;
import at.ac.tuwien.infosys.g2021.common.FilteringAdapterConfiguration;
import at.ac.tuwien.infosys.g2021.common.LowpassAdapterConfiguration;
import at.ac.tuwien.infosys.g2021.common.ScalingAdapterConfiguration;
import at.ac.tuwien.infosys.g2021.common.SimpleData;
import at.ac.tuwien.infosys.g2021.common.TriggeringAdapterConfiguration;
import at.ac.tuwien.infosys.g2021.common.util.NotYetImplementedError;
import java.util.ArrayList;
import java.util.List;

/**
 * This is just a chain of adapters. An adapter chain has a method interface like a single adapter
 * and returns the summary of the adoptions of all assigned adapters.
 */
class AdapterChain extends Adapter {

    // This is the list of assigned adapters.
    private List<Adapter> adapters;

    /**
     * Initializing with a given buffer configuration.
     *
     * @param config the buffer configuration
     */
    AdapterChain(BufferConfiguration config) {

        super(config);
        adapters = new ArrayList<>();

        // At first the adapters are created
        for (AdapterConfiguration adapterConfig : config.getAdapterChain()) {

            Adapter toInsert;

            switch (adapterConfig.kindOfAdapter()) {
                case DUMMY:
                    toInsert = new DummyAdapter((DummyAdapterConfiguration)adapterConfig);
                    break;

                case FILTER:
                    toInsert = new FilteringAdapter((FilteringAdapterConfiguration)adapterConfig);
                    break;

                case LOWPASS:
                    toInsert = new LowpassAdapter((LowpassAdapterConfiguration)adapterConfig);
                    break;

                case SCALE:
                    toInsert = new ScalingAdapter((ScalingAdapterConfiguration)adapterConfig);
                    break;

                case TRIGGER:
                    toInsert = new TriggeringAdapter((TriggeringAdapterConfiguration)adapterConfig);
                    break;

                default:
                    throw new NotYetImplementedError("unknown adapter implementation for adapter class: " +
                                                     adapterConfig.kindOfAdapter().name());
            }

            adapters.add(toInsert);
        }

        // If there are no adapters configured, a dummy is put into the chain
        if (adapters.size() == 0) adapters.add(new DummyAdapter(new DummyAdapterConfiguration()));

        // Now we install the producer - consumer dependencies
        for (int i = 1; i < adapters.size(); i++) adapters.get(i - 1).addValueChangeConsumer(adapters.get(i));
        adapters.get(adapters.size() - 1).addValueChangeConsumer(this);
    }

    /**
     * This method is called, whenever a shutdown sequence is initiated. The
     * shutdown is delegated to all the adapters and then the adapters are
     * deleted.
     */
    void shutdown() {

        synchronized (valueLock) {
            for (Adapter adapter : adapters) adapter.shutdown();
            adapters.clear();
            super.shutdown();
        }
    }

    /**
     * Putting a value into the chain. It is routed to the first adapter.
     *
     * @param value the input value
     */
    void put(SimpleData value) {

        synchronized (valueLock) {
            adapters.get(0).valueChanged(value);
        }
    }

    /**
     * This is the notification of a spontaneous value change of the
     * last adapter in the chain. This value becomes the current value
     * of the whole chain.
     *
     * @param newValue the new buffer value
     */
    @Override
    public void valueChanged(SimpleData newValue) { set(newValue); }
}
