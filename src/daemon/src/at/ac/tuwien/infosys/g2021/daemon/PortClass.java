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

    /**
     * Converts a key to the corresponding PortClass.<br><br>
     *
     * key-mapping:
     *  <pre>
     *  1 .. DIGITAL_INPUT
     *  2 .. DIGITAL_OUTPUT
     *  3 .. ANALOG_INPUT
     *  4 .. ANALOG_OUTPUT
     *  </pre>
     * @param key 1..4, other values leeds to System.exit(-41)
     * @return the corresponding PortClass.
     */
    public static PortClass get( int key ) {
        switch( key ) {
            case 1: return PortClass.DIGITAL_INPUT;
            case 2: return PortClass.DIGITAL_OUTPUT;
            case 3: return PortClass.ANALOG_INPUT;
            case 4: return PortClass.ANALOG_OUTPUT;
            default: System.err.println(" PortClass.get: invalid key=" + key); System.exit(-41);
        }
        return PortClass.DIGITAL_INPUT; // unreachable, but the javac requests this return statement.
    }

    /**
     * Maps a PortClass to a human readable String
     *
     * @param pc the PortClass which should be mapped.
     * @return the String 'DI', 'DO', 'AI' or 'AO'.
     */
    public static String toString( PortClass pc ) {
        switch( pc ) {
            case DIGITAL_INPUT  : return "DI";
            case DIGITAL_OUTPUT : return "DO";
            case ANALOG_INPUT   : return "AI";
            case ANALOG_OUTPUT  : return "AO";
            default: System.err.println(" PortClass.toString: invalid PortClass"); System.exit(-42);
        }
        return ""; // unreachable, but the javac requests this return statement.
    }
}