package at.ac.tuwien.infosys.g2021.daemon.drivers.pacificControl;

import at.ac.tuwien.infosys.g2021.common.BufferState;
import at.ac.tuwien.infosys.g2021.common.SimpleData;
import at.ac.tuwien.infosys.g2021.common.util.Loggers;
import at.ac.tuwien.infosys.g2021.daemon.HardwareDriverInterface;
import at.ac.tuwien.infosys.g2021.daemon.PortClass;
import at.ac.tuwien.infosys.g2021.daemon.PortDescription;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CFactory;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

import java.util.*;
import java.util.logging.Logger;

/**
 * Created by siegl on 24.03.2015.
 * <pre>
 * { "portCfg" : [
 *    { "hwCfg" : { "connection" : "pacificControl",
 *                         "url" : "file:pacificControl.json",
 *                    "interval" : 5 }
 *    },
 *    ...
 * ] }
 * </pre>
 */
public class PacificControlHardwareDriverImpelmentation implements HardwareDriverInterface {

    /** The logger */
    private final static Logger logger = Loggers.getLogger(PacificControlHardwareDriverImpelmentation.class);

    /** The name of the file which holds the configuration */
    private String cfgFileName = "portCfg.json";

    /** The url to read data from sedona */
    private URL url;

    /** The url to set data to sedona */
    private String urlSet = null;

    /** The priority used in set-requests - default=9 */
    private int        priority = 9;

    /** The interval the values are pulled from the url */
    private int        interval = 30; // Default interval is 30 Seconds

    /** The default value for interval if the config results in an error or is zero or negativ. */
    private int defaultInterval = 30;

    private int   intervalCount =  0;


    /** All defined and available sensors/actors - ports */
    private HashMap<String,PortPC> allPorts = new HashMap<String,PortPC>();

    /**
     * Is this a suitable hardware driver and can this driver work with the current hardware?
     */
    @Override
    public boolean isSuitable() { return true; }

    /**
     * Is this hardware driver is the best choice for this hardware?
     */
    @Override
    public boolean isBestChoice() { return true; };

    /**
     * Returns the name of the driver for logging purposes.
     *
     * @return the name of the driver
     */
    @Override
    public String getName() { return "Driver for Pacific Control G2021 interface"; }

    /**
     * All chips (defined in ChipSet) are checked for reachability.
     * If a chip is reachable, it is referenced in 'chips'.
     *
     * This method is called, after creating all necessary instances.
     */
    @Override
    public void initialize() {}


    /**
     * Looks for the information from the url from sedona
     * @return the json formated data from the url or <tt>null</tt> of the json cannot retrieved.
     */
    // Read the values from URL
    private JsonObject getValuesFromUrl() {

        JsonObject jsonObj = null;
        try {
            URLConnection connection = url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.US_ASCII));
            try { jsonObj = JsonObject.readFrom( reader ); }
            catch( IOException ioe ) { logger.config("Error in reading configuration for PacificControl."); }
        }
        catch (Exception e) {
            logger.config("Error in reading config for PacificControl from url " + url.toString() );
        }
        return jsonObj;
    }


    /**
     * Requests a set action to an actor with a http request.
     *
     * The basic elements of the URL are defined in configuration.
     * <pre>
     * rootpath/request/set?<i>point-name</i>&<i>new-value</i>&<i>set-priority</i>
     * </pre>
     *
     * @param port  The name of the port
     * @param value The new value to be set
     */
    private void setValueFromUrl( String port, Number value ) {

        // Check if the setURL is defined in config
        if( urlSet == null ) {
            logger.info( "Setting " + port + "=" + value.doubleValue() + " not possible: setURL config missing." );
            return;
        }

        JsonObject jsonObj = null;
        try {
            URL u = new URL( urlSet.toString() + "?" + port + "&" + value.doubleValue() + "&" + priority );
            System.out.println( "===>>> urlSet = " + u + " <<<===" );
            URLConnection connection = u.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.US_ASCII));
            try { jsonObj = JsonObject.readFrom( reader ); }
            catch( IOException ioe ) { logger.config("Error in setting values for PacificControl."); }
        }
        catch (Exception e) {
            logger.config("Error in setting value for PacificControl with url " + urlSet.toString() );
        }
    }


    /* *** g e t  P o r t s ***/
    /**
     * Returns the available ports and their properties.
     *
     * The configuration must contain <tt>"connection" : "pacificControl"</tt><br>
     * If parameter <tt>"url"</tt> is invalid, the program terminates.<br>
     *
     * <pre>
     * { "hwCfg" : { "connection" : "pacificControl",
     *               "url" : "file:pacificControl.json",
     *               "urlSet" : "http://pcSrv.json/set",
     *               "interval" : 30 } }
     *  }
     * </pre>
     *
     * @return the port properties
     */
    @Override
    public Collection<PortDescription> getPorts() {

        // All found and active sensors/actors are collected here.
        Collection<PortDescription> result = new ArrayList<>();


        // Read configuration from portCfg.json
        try {
            JsonObject jsonObj = JsonObject.readFrom( new FileReader( cfgFileName ) );
            boolean found = false;
            for( JsonValue jv : jsonObj.get("portCfg").asArray() ) {    // read all entries in section 'portCfg'

                // check: is this entry that for 'pacificControl'?
                if( "pacificControl".equals( jv.asObject().get("hwCfg").asObject().get( "connection").asString() ) ) {

                    try {                   // reading parameter 'url'
                        url = new URL(jv.asObject().get("hwCfg").asObject().get("url").asString());
                        found = true;
                    }
                    catch( Exception e ) {  // parameter 'url' is missing
                        logger.config( "Config-File " + cfgFileName + ": Invalid configuration for pacificControl." );
                        System.exit( -30 ); // invalid configuration
                    }

                    try {                   // reading parameter 'urlSet'
                        urlSet = jv.asObject().get("hwCfg").asObject().get("urlSet").asString();
                    }
                    catch( Exception e ) {} // keep the default

                    try {                   // reading parameter 'interval'
                        interval = jv.asObject().get("hwCfg").asObject().get( "interval").asInt();
                        if( interval < 1 ) { interval = defaultInterval; }
                    }
                    catch( Exception e ) {} // keep the default
                }
            }
            if( ! found ) {                 // the
                System.err.println("Config-File " + cfgFileName + ": Missing config for PacificControl.");
                return result;
            }
        }
        catch( FileNotFoundException fnfe ) {
            logger.config( "Config-File " + cfgFileName + " not found." );
            System.exit( -31 );
        }
        catch( IOException ioe ) {
            logger.config( "Config-File " + cfgFileName + " not readable." );
            System.exit( -32 );
        }

        // Read the configuration from URL
        JsonObject jsonObj = getValuesFromUrl();

        // In case of error in reading the data from url, break now.
        if( jsonObj == null ) { return result; }

        // Analyse the configuration
        for( JsonValue value : jsonObj.get("list").asArray() ) {
            String name = null;
            int portClass;
            try {
                // read the name of the port
                try { name = value.asObject().get("name").asString(); }
                catch( Exception e ) {
                    logger.config("Error in cfg PacificControl: invalide parameter 'name'.");
                    continue;
                }

                // read the 'type' of the port: DI, DO, AI, AO
                portClass = value.asObject().get("type").asInt();

                if( name != null ) {
                    PortPC portCfg = new PortPC( name, true, portClass );

                    // read the 'unit', if available
                    HashMap<String, String> metaInfo = new HashMap<String, String>();
                    try {
                        String unit = value.asObject().get("unit").asString();
                        metaInfo.put("unit", unit);
                        portCfg.setMetaInfo(metaInfo);
                    }
                    catch( Exception e ) {} // don't care

                    allPorts.put(portCfg.getName(), portCfg);
                    result.add( new PortDescription( name, PortClass.get(portClass), metaInfo));
                    logger.fine( "PortCfg " + portCfg.getName() + portCfg.dump());
                }
                else {
                    logger.config("Invalid configuration in PacificControl " + name);
                    continue;
                }
            }
            catch( Exception e ) {
                logger.config("Error in cfg PacificControl.");
                continue;
            }
        }
        return result;
    };

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
        if( intervalCount == 0 ) {
            JsonObject jsonObj = getValuesFromUrl();
            if( jsonObj == null ) {             // No fresh values are available - all are invalid
                for( PortPC port : allPorts.values() ) {
                    result.add(new SimpleData( port.getName(), now, BufferState.FAULTED));
                }
            }
            else {                              // Some fresh values are available :-)
                JsonArray values = jsonObj.get("list").asArray();
                for( PortPC port : allPorts.values() ) {    // Look for desired names
                    String name = port.getName();
                    boolean found = false;  // Search the name in all available names
                    for( int i = 0; i < values.size(); i++ ) {
                        if ( values.get(i).asObject().get("name").asString().equals( name ) ) {
                            found = true;   // The desired name is found - look for the value now

                            try {           // Check validity of this entry
                                if( ! values.get(i).asObject().get( "valid" ).asBoolean() ) {
                                    System.out.println( "===>>> value of name '" + name + "' is invalid <<<===" );
                                    result.add(new SimpleData(port.getName(), now, BufferState.FAULTED));
                                    continue;
                                }
                            }
                            catch( Exception e ) {} // No attribute valid available - assuming valid=true

                            try {           // Read the value
                                double value = values.get(i).asObject().get("value").asDouble();
                                result.add(new SimpleData(name, now, BufferState.READY, value));
                                System.out.println("===>>> name '" + name + "' = " + value + " <<<===");
                                continue;
                            }
                            catch( Exception e ) {  // correct value missing
                                System.out.println( "===>>> name '" + name + "' has no correct value <<<===" );
                                result.add(new SimpleData(port.getName(), now, BufferState.FAULTED));
                           }
                        }
                    }
                    if( !found ) {          // The desired name is not included or value is not readable
                        System.out.println( "===>>> name '" + name + "' not included <<<===" );
                        result.add(new SimpleData(port.getName(), now, BufferState.FAULTED));
                    }
                }
            }
        }
        if( intervalCount > interval ) { intervalCount = 0; }
        else { intervalCount++; }

        return result;
    }

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
            setValueFromUrl( port, value );
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
