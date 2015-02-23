package at.ac.tuwien.infosys.g2021.common;

/** An enumeration of all available kinds of gatherers. */
public enum GathererClass {

    /**
     * This kind of gatherer can be used as actor or sensor. It can be set to any value and returns
     * the last value set. The initial value is 0.
     */
    DUMMY,

    /**
     * This kind of gatherer can be used as actor or sensor. The initial value is 0. Every second the
     * value is incremented by 0.1. If the value becomes greater than 1.0, the buffer value is decremented
     * by 1.0.
     */
    TEST,

    /** This kind of gatherer can be used as sensor for hardware ports. */
    SENSOR,

    /** This kind of gatherer can be used as actor for hardware ports. */
    ACTOR
}

