package at.ac.tuwien.infosys.g2021.common;

/**
 * An enumeration of all available kinds of adapter.
 */
public enum AdapterClass {

    // This kind of adapter does nothing. The output value is the input value.
    DUMMY,

    // This kind of adapter calculates an output value y based on the current
    // input value x using the equation y = a * x**2 + b * x + c. The factors a,
    // b and c have to be configured.
    SCALE,

    // This adapters toggle between two output values. The upper and the lower
    // input threshold value and the output values are configuration items.
    TRIGGER,

    // This kind of adapters acts as lowpass filter. In this case, the time constant
    // is configurable. LowpassAdapter instances will be triggered by an internal 1 sec
    // time ticker and will generate new values every second!
    LOWPASS,

    // This adapters suppress marginal input value changes. The maximum difference between
    // the last sent output value and the current input value is a configuration item.
    FILTER
}
