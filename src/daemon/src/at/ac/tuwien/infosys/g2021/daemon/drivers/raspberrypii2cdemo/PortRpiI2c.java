package at.ac.tuwien.infosys.g2021.daemon.drivers.raspberrypii2cdemo;

import at.ac.tuwien.infosys.g2021.common.util.Loggers;
import at.ac.tuwien.infosys.g2021.daemon.drivers.PortCfg;
import com.eclipsesource.json.JsonObject;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * This Class represents the configuration of a Sensor/Actor - a Port - on an RaspberryPi I2C-Interface.
 *
 * The configuration is stored in a file formated in json.
 * See the apidoc of class PortCfg, for the description of the general config parameters.
 *
 * The section <font color="blue"><tt>hwCfg</tt></font> and the specific parameters for connection to devices on the I2C bus are described here.
 *
 * <pre>
 * { "portCfg" : [
 *    { ...
 *      "hwCfg" : {
 *              "<font color="blue">connection</font>" : "<b>I2C</b>",
 *              "<font color="blue">i2cMuxAddr</font>" : "<i>0x70</i>",
 *              "<font color="blue">i2cMuxCmd</font>"  : "<i>0x04</i>",
 *              "<font color="blue">i2cAddr</font>"    : "<i>0x51</i>",
 *              "<font color="blue">bitWeight</font>"  : "<i>16,32,64,128,0,0,0,0,0,0,0,0,1,2,4,8</i>" },
 *      ...
 *      }
 * ] }
 * </pre>
 *
 * On an I2C bus multiple devices reside. Communication is structured as master-slave - a master start communication and a slave completes it.
 * Each slave has a bus address assigned. A master has no address. The SDG acts as master only.
 * In some cases a I2C multiplexer resides between the master and the slave. The I2C multiplexer must be propper set.
 *
 * <ul>
 * <li><tt><font color="blue">connection</font></tt>: <tt><b>I2C</b></tt> If the value of this parameter differs,
 *              this config is not assigned to the I2C driver - the whole entry will be ignored.
 *              This config is mandatory.</li>
 * <li><tt><font color="blue">i2cMuxAddr</font></tt>: The address of the I2C multiplexer.
 *              This config is optional.</li>
 * <li><tt><font color="blue">i2cMuxCmd</font></tt>: The command for the I2C multiplexer.
 *              This config is optional.</li>
 * <li><tt><font color="blue">i2cAddr</font></tt>: The I2C address of the sensor device.
 *              This config is mandatory.</li>
 * <li><tt><font color="blue">bitWeight</font></tt>: A list of weights. The number of entries defines the number of Bytes read from the sensor device.
 *              This config is optional - the default is: "<tt>1, 2, 4, 8, 16, 32, 64, 128</tt>"</li>
 * </ul>
 */
public class PortRpiI2c extends PortCfg {

    /** The logger. */
    private final static Logger logger = Loggers.getLogger(RaspberryPiI2CDemoHardwareDriverImplementation.class);

    private boolean muxDefined = false;
    private boolean addrDefined = false;

    /** Number of Bytes to read from the I2C-chip. */
    private int lng = 1;

    /** The weight of the bits, received from the sensor */
    private double d[] = new double[] { 1.0, 2.0, 4.0, 8.0, 16.0, 23.0, 64.0, 128.0 };


    PortRpiI2c(JsonObject jsonObj, I2CBus i2cBus) {
        super( jsonObj );
        prepare( i2cBus );
    }


    /** Calculates some required values
     *
     * @param i2cBus The I2C bus on which the I2C device resides.
     */
    void prepare( I2CBus i2cBus ) {

        // Calculate the number of bytes to read from the I2C chip.
        // It is assumed, the parameter bitWeight consists of n times 8 elements - n >= 1
        String bitWeight = (String) hwCfg.get("bitWeight");
        if (bitWeight != null) {
            String a[] = bitWeight.split(",");
            lng = a.length / 8;
            d = new double[a.length];
            for( int i = 0; i < a.length; i++ ) d[i] = Double.valueOf( a[i] );
        }

        // Is a mux defined?
        String muxAddr = (String) hwCfg.get("i2cMuxAddr");
        String muxCmd = (String) hwCfg.get("i2cMuxCmd");
        int imax = 3;
        if (muxAddr != null && muxAddr.length() > 0 && muxCmd != null && muxCmd.length() > 0) {
            for (int i = 1; i < imax + 1 && !muxDefined; i++) {
                try {
                    i2cBus.getDevice(Integer.decode(muxAddr)).write(Byte.decode(muxCmd));
                    muxDefined = true;
                    logger.info("Port " + name + ": The muxDevice " + muxAddr + " is reachable.");
                } catch (IOException ioe) {
                    if( i == 1 ) {} // System.out.print( "Port " + name );
                    if( i < imax ) {} // System.out.print( "." + i + "." );
                    else { // i == imax
                        // System.out.println("." + i + ".");
                        logger.config("Port " + name + ": The muxDevice " + muxAddr + " is not reachable.");
                    }
                }
            }
        }

        // Is the I2C address defined?
        if ((String) hwCfg.get("i2cAddr") == null) {
            logger.config("Port " + name + ": The i2cAddr is not included.");
        }
        else {
            for (int i = 1; i < imax + 1 && !addrDefined; i++) {
                String addr = (String) hwCfg.get("i2cAddr");
                try {
                    I2CDevice device = i2cBus.getDevice(Integer.decode(addr));
                    device.read();
                    addrDefined = true;
                    logger.fine("Port " + name + ": The device " + addr + " is reachable.");
                    break;
                } catch (IOException e) {
                    if( i == 1 ) {} // System.out.print("Port " + name);
                    if( i < imax ) {} // System.out.print( ":" + i + ":" );
                    else { // i == imax
                        // System.out.println(":" + i + ":");
                        logger.config("Port " + name + ": The device " + addr + " is not reachable.");
                    }
                }
            }
        }
        // prepare postprocessing of the value
        doPrepare();
    }


    /** Retrieves the value from a sensor
     * If a I2C multiplexer is defined, the multiplexer is propper set.
     * Next the I2C device is read. The number of Bytes read are defined indirect by the number of elements in bitWeights
     *
     * @throws Exception If some problems occured when reading form the I2C bus.
     * @param i2cBus The I2C bus on which the I2C device resides.
     * @return The Bytes received from the I2C device.
     */
    byte[] getValue( I2CBus i2cBus ) throws Exception {

        if( ! addrDefined ) throw new Exception( "Port " + name + ": I2C-address is not defined in config." );

        // If a mux is defined, the mux must exists.
        if( muxDefined ) {
            String muxAddr = (String) hwCfg.get("i2cMuxAddr");
            String muxCmd = (String) hwCfg.get("i2cMuxCmd");
            try {
                i2cBus.getDevice(Integer.decode(muxAddr)).write(Byte.decode(muxCmd));
            }
            catch (IOException e) {
                throw new Exception( "Port " + name + ": The i2cMux is not reachable." );
            }
        }

        double r = 0;
        byte[] buffer = new byte[ lng ];
        int read = -1;
        try { read = i2cBus.getDevice(Integer.decode((String) hwCfg.get("i2cAddr"))).read(buffer, 0, lng); }
        catch (IOException e) {
            throw new Exception("Port " + name + ": The device is not reachable.");
        }
        if (read != buffer.length) {            // the buffer must filled completely
            throw new Exception("Port " + name + ": The device is not readable.");
        }
        return buffer;
    }


    /** Set a value at an actor
     * @throws Exception If some problems occured when sending to the actor.
     * @param i2cBus The I2C bus on which the I2C device resides.
     * @param value The value that should be set.
     */
    void setValue( I2CBus i2cBus, Number value ) throws Exception {

        // check portClass --> is it a DO?

        byte[] b = getValue(i2cBus);
        byte[] k = new byte[b.length];

        for( int i = 0; i < lng; i++ ) {
            if (d[0 + i*8] != 0.0) k[i] |= 0x01;
            if (d[1 + i*8] != 0.0) k[i] |= 0x02;
            if (d[2 + i*8] != 0.0) k[i] |= 0x04;
            if (d[3 + i*8] != 0.0) k[i] |= 0x08;
            if (d[4 + i*8] != 0.0) k[i] |= 0x10;
            if (d[5 + i*8] != 0.0) k[i] |= 0x20;
            if (d[6 + i*8] != 0.0) k[i] |= 0x40;
            if (d[7 + i*8] != 0.0) k[i] |= 0x80;
        }

        if( value.doubleValue() < 0.5 ) for (int i = 0; i < lng; i++) b[i] &= ~k[i];
        else              for (int i = 0; i < lng; i++) b[i] |=  k[i];

        i2cBus.getDevice(Integer.decode((String) hwCfg.get("i2cAddr"))).write( b, 0, b.length );
    }


    /** Returns the number of Bytes to be read from the I2C chip.
     * @return the number of Bytes.
     */
    int getByteLng() { return lng; }


    /** The received bits are weighted
     * Use the bit-weight for interpretation of the bits.
     * @param buffer The Bytes received from the I2C device.
     * @return The value resulted by the weighted bits
     */
    double bitWeighter( byte[] buffer ) {

        double r = 0;
        for( int j = 0; j < lng; j++ ) {
            for (int i = 0; i < 8; i++) {
                r += d[i+8*j] * Double.valueOf( (buffer[j] >> i) & 0x01 );
            }
        }
        return r;
    }
}