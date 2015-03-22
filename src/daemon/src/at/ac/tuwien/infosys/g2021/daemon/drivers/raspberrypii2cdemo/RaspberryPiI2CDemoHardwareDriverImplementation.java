package at.ac.tuwien.infosys.g2021.daemon.drivers.raspberrypii2cdemo;

import at.ac.tuwien.infosys.g2021.common.BufferState;
import at.ac.tuwien.infosys.g2021.common.SimpleData;
import at.ac.tuwien.infosys.g2021.common.util.Loggers;
import at.ac.tuwien.infosys.g2021.daemon.HardwareDriverInterface;
import at.ac.tuwien.infosys.g2021.daemon.PortDescription;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CFactory;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

/** This is a dummy implementation. It guarantees the existence of at least one usable driver on every hardware. */
public class RaspberryPiI2CDemoHardwareDriverImplementation implements HardwareDriverInterface {

    /** The logger */
    private final static Logger logger = Loggers.getLogger(RaspberryPiI2CDemoHardwareDriverImplementation.class);

    /** An object for thread synchronization */
    private final Object lock;

    /** All defined and available sensors/actors - ports */
    private HashMap<String,PortRpiI2c> allPorts = new HashMap<String,PortRpiI2c>();

    /** The selection of the adc multiplexer. */
    private int currentMultiplexerSetting;

    /** Creating the driver instance. */
    public RaspberryPiI2CDemoHardwareDriverImplementation() {
        lock = new Object();
    }


    /* *** g e t  N a m e ***/
    /**
     * Returns the name of the driver for logging purposes.
     */
    @Override
    public String getName() { return "Driver for Raspberry Pi I2C interface"; }


    /* *** i s  S u i t a b l e ***/
    /**
     * Is this a suitable hardware driver and can this driver work with the current hardware?
     * <p>
     * This method is used to select the current hardware driver of the daemon. It must not throw an exception
     * in any case! If this methods return <tt>false</tt>, no other methods of this driver are called.
     *
     * @return <tt>true</tt>, if this is a suitable driver
     */
    @Override
    public boolean isSuitable() {

        try {
            I2CBus i2cBus = I2CFactory.getInstance( I2CBus.BUS_1 );
            return true;
        }
        catch (Throwable e) {  // This call throws an exception if there is no device.
            return false;
        }
    }


    /* *** i s  B e s t  C h o i c e ***/
    /**
     * Is this driver the best choice for this hardware? If this method returns <tt>false</tt> and another
     * driver is also suitable, the other driver will be used.
     */
    @Override
    public boolean isBestChoice() { return false; }


    /* *** i n i t i a l i z e ***/
    /**
     * All chips (defined in ChipSet) are checked for reachability.
     * If a chip is reachable, it is referenced in 'chips'.
     *
     * This method is called, after creating all necessary instances.
     */
    @Override
    public void initialize() {}


    /* *** g e t  P o r t  C f g s ***/
    /**
     * Read the configuration of sensors and actors from configuration file 'portCfg.json',
     * analyse it and create for each a PortCfg.
     *
     * @return All found and correct port configurations.
     */
    ArrayList<PortRpiI2c> getPortCfgs() {

        ArrayList<PortRpiI2c> portCfgs = new ArrayList<PortRpiI2c>();
        JsonObject jsonObj = null;
        String cfgFileName = "portCfg.json";

        // Read data from file.
        try {
            jsonObj = JsonObject.readFrom( new FileReader( cfgFileName ) );
        }
        catch( FileNotFoundException fnfe ) {
            logger.config( "Config-File " + cfgFileName + " not found." );
            System.exit( -31 );
        }
        catch( IOException ioe ) {
            logger.config( "Config-File " + cfgFileName + " not readable." );
            System.exit( -32 );
        }

        // Read the port-configuration from JSON-object
        try {
            I2CBus i2cBus = I2CFactory.getInstance(I2CBus.BUS_1);
            for( JsonValue value : jsonObj.get("portCfg").asArray() ) {
                PortRpiI2c portCfg = new PortRpiI2c( value.asObject(), i2cBus );
                if( portCfg.getName() != null ) { portCfgs.add( portCfg ); }
                logger.fine( "PortCfg " + portCfg.getName() + portCfg.dump());
            }
        }
        catch( IOException ioe ) {
            logger.config( "\nI2C-Bus not available." );
            System.exit(-58);
        }
        catch( NullPointerException npe ) {
            logger.config( "\nCorrupt Config-File " + cfgFileName );
            System.exit( -59 );
        }

        return portCfgs;
    }


    /* *** g e t  P o r t s ***/
    /**
     * Returns the available ports and their properties.
     * @return the port properties
     */
    @Override
    public Collection<PortDescription> getPorts() {

        // All found and active sensors/actors are collected here.
        Collection<PortDescription> result = new ArrayList<>();

        try {
            I2CBus i2cBus = I2CFactory.getInstance(I2CBus.BUS_1);

            // Find all defined Actors/Sensors
            for (PortRpiI2c portCfg : getPortCfgs()) {

                // The sensor/actor must be connected to the I2C-bus - otherwise this driver is not responsible for it.
                if (!("I2C".equals((String) portCfg.getHwCfg().get("connection")))) continue;

                // Check if the sensor/actor is active
                if (!portCfg.getState()) continue;

                System.out.println(portCfg.dump());

                double r = 0;
                byte[] b = null;
                try {
                    b = portCfg.getValue(i2cBus);       // read the bits from the i2c-device/chip
                } catch (Exception e) {
                    System.err.println( e.getMessage() );
                    // e.printStackTrace();
                    continue;
                }
                r = portCfg.bitWeighter(b);             // Weighting the bits
                r = portCfg.linearizer(r);              // Now the linearisation - correction of the characteristic
                r = portCfg.polynominizer(r);           // Polynom correction y = a0 + a1*r + a2*r^2 + ...
                System.out.println(portCfg.getName() + "=" + r);

                // All tests are successful -> keep this portCfg.
                allPorts.put(portCfg.getName(), portCfg);
                result.add(new PortDescription(portCfg.getName(), portCfg.getPortClass(), portCfg.getMetaInfo()));
            }
            logger.config(String.format( "There are %d I\u00B2C ports known.", result.size()) );
        }
        catch( IOException ioe ) {
            logger.config("I2C-Bus not available.");
            System.exit( -61 );
        }
        return result;
    }


    /* *** g e t  A l l ***/
    /**
     * Returns the states and values of all available ports.
     *
     * @return the state and value of all available ports
     */
    @Override
    public Collection<SimpleData> getAll() {

        Collection<SimpleData> result = new ArrayList<>();
        Date now = new Date();

        synchronized (lock) {

            try {
                I2CBus i2cBus = I2CFactory.getInstance(I2CBus.BUS_1);
                for (PortRpiI2c port : allPorts.values()) {
                    try {
                        byte[] b = port.getValue(i2cBus);
                        double value = port.bitWeighter(b);
                        value = port.linearizer(value);
                        value = port.polynominizer(value);
                        result.add(new SimpleData(port.getName(), now, BufferState.READY, value));
                        logger.fine( port.getName() + "=" + value);
                    } catch (Exception e) {
                        result.add(new SimpleData(port.getName(), now, BufferState.FAULTED));
                    }
                }
            }
            catch(IOException ioe) {
                logger.config("I2CBus not available.");
                System.exit( -62 );
            }
        }
        return result;
    }


    /* *** s e t ***/
    /**
     * Sets the output of an actor.
     *
     * @param port  the name of the port
     * @param value the new output value
     *
     * @return <tt>true</tt>, if the port was set correctly
     *
     * @throws UnsupportedOperationException if the port is not known or a sensor port
     */
    @Override
    public boolean set(String port, Number value) throws UnsupportedOperationException {


        try {
            allPorts.get(port).setValue( I2CFactory.getInstance( I2CBus.BUS_1 ), value);
            return true;
        }
        catch( Exception e ) {
            logger.config("Error while setting new value: " + e.getMessage());
            return false;
        }
    }


    /* *** s h u t d o w n ***/
    /** This method is called, whenever a shutdown sequence is initiated. */
    @Override
    public void shutdown() { release(); }


    /* *** r e l e a s e ***/
    /** This method is called immediately after a shutdown, immediately before the process is stopped. */
    @Override
    public void release() {}
}