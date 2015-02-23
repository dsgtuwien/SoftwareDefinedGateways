package at.ac.tuwien.infosys.g2021.daemon;

import at.ac.tuwien.infosys.g2021.common.BufferClass;

/** The different supported classes of hardware ports. */
public enum PortClass {

    /** This is a simple input bit. The value read is mapped to 0 (low input) or 1 (high input). */
    DIGITAL_INPUT {
        @Override
        public boolean isActor() { return false; }
    },

    /** This is a simple output bit. The value less than 0.5 is mapped to 0 (low input). */
    DIGITAL_OUTPUT {
        @Override
        public boolean isActor() { return true; }
    },

    /** This is an analog input port. The value read is mapped to a double value. */
    ANALOG_INPUT {
        @Override
        public boolean isActor() { return false; }
    },

    /** This is an analog output port. */
    ANALOG_OUTPUT {
        @Override
        public boolean isActor() { return true; }
    };

    /**
     * Describes this kind of port an actor?
     *
     * @return <tt>true</tt>, if this kind of port is an actor
     */
    public abstract boolean isActor();

    /**
     * Describes this kind of port a sensor?
     *
     * @return <tt>true</tt>, if this kind of port is a sensor
     */
    public boolean isSensor() { return !isActor(); }

    /**
     * Get the kind of buffer for this port.
     *
     * @return the needed kind of buffer
     */
    BufferClass bufferClassForPort() { return isActor() ? BufferClass.ACTOR : BufferClass.SENSOR; }
}

