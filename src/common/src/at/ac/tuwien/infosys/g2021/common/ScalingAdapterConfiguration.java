package at.ac.tuwien.infosys.g2021.common;

/**
 * A scaling adapter calculates an output value y based on the current
 * input value x using the equation:
 * <pre>
 *     y = a * x**2 + b * x + c
 * </pre>
 * The factors a, b and c have to be configurated. The defaults for a, b and c are 0.0.
 */
public class ScalingAdapterConfiguration implements AdapterConfiguration {

    // The coefficients
    private double a;
    private double b;
    private double c;

    /**
     * Initialization with the default values.
     */
    public ScalingAdapterConfiguration() {}

    /**
     * Initialization with given coefficients.
     *
     * @param a the quadratic coefficient
     * @param b the linear factor
     * @param c a constant offset
     */
    public ScalingAdapterConfiguration(double a, double b, double c) {

        this();

        this.a = a;
        this.b = b;
        this.c = c;
    }

    /**
     * Every adapter configuration can return the kind of adapter.
     *
     * @return the kind of adapter
     */
    @Override
    public AdapterClass kindOfAdapter() { return AdapterClass.SCALE; }

    /**
     * Returns the quadratic coefficient.
     *
     * @return the quadratic coefficient
     */
    public double getA() { return a; }

    /**
     * Sets the quadratic coefficient.
     *
     * @param a the new quadratic coefficient
     */
    public void setA(double a) { this.a = a; }

    /**
     * Returns the linear factor.
     *
     * @return the linear factor
     */
    public double getB() { return b; }

    /**
     * Sets the linear factor.
     *
     * @param b the new linear factor
     */
    public void setB(double b) { this.b = b; }

    /**
     * Returns the constant offset.
     *
     * @return the constant offset
     */
    public double getC() { return c; }

    /**
     * Sets the constant offset.
     *
     * @param c the new constant offset
     */
    public void setC(double c) { this.c = c; }
}


