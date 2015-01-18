package at.ac.tuwien.infosys.g2021.daemon;

import at.ac.tuwien.infosys.g2021.common.SimpleData;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/** This is an abstract class, which can notify <tt>{@link ValueChangeConsumer}s</tt> about value changes. */
abstract class ValueChangeProducer {

    // The list of value change consumers
    private List<ValueChangeConsumer> consumers;

    /** There exists no gatherers without configuration. */
    protected ValueChangeProducer() {

        consumers = new CopyOnWriteArrayList<>();
    }

    /**
     * Exist at least a single value change consumer?
     *
     * @return <tt>true</tt>, if value change consumers exist
     */
    boolean hasValueChangeConsumers() { return consumers.size() > 0; }

    /**
     * Adds a new value change consumer to this class.
     *
     * @param c the new consumer
     */
    void addValueChangeConsumer(ValueChangeConsumer c) {

        consumers.remove(c);
        consumers.add(c);
    }

    /**
     * Removes a value change consumer from this class.
     *
     * @param c the obsolete consumer
     */
    void removeValueChangeConsumer(ValueChangeConsumer c) { consumers.remove(c); }

    /**
     * Distributes a value change.
     *
     * @param value the value to be distributed
     */
    protected void fireValueChange(SimpleData value) { for (ValueChangeConsumer consumer : consumers) consumer.valueChanged(value); }

    /** Stops distributing. */
    void shutdown() { consumers.clear(); }
}

