package at.ac.tuwien.infosys.g2021.daemon.drivers.pacificControl;

import at.ac.tuwien.infosys.g2021.common.util.Loggers;
import at.ac.tuwien.infosys.g2021.daemon.drivers.PortCfg;
import at.ac.tuwien.infosys.g2021.daemon.drivers.raspberrypii2cdemo.RaspberryPiI2CDemoHardwareDriverImplementation;
import com.eclipsesource.json.JsonObject;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Created by siegl on 24.03.2015.
 *
 * This Class represents the configuration of a Sensor/Actor - a Port - on an Gateway G2021.
 *
 * The configuration is stored in a file formated in json.
 * See the apidoc of class PortCfg, for the description of the general config parameters.
 *
 * The section <font color="blue"><tt>hwCfg</tt></font> and the specific parameters for retrieving data via http are described here.
 *
 * <pre>
 * { "portCfg" : [
 *    { ...
 *      "hwCfg" : {
 *              "<font color="blue">connection</font>" : "<b>pacificControl</b>" },
 *              "<font color="blue">url</font>" : "<b>http://...</b>" },
 *              "<font color="blue">interval</font>" : "<b>15</b>" },
 *      ...
 *      }
 * ] }
 * </pre>
 *
 * On G2021 sedona is installed and running. This installation provied access to sensors (DI, AI) and actors (DO, AO),
 * using http and requesting some specific URLs.
 *
 * <ul>
 * <li><tt><font color="blue">connection</font></tt>: <tt><b>pacificControl</b></tt> If the value of this parameter differs,
 *              this config is not assigned to the G2021 - the whole entry will be ignored.
 *              This config is mandatory.</li>
 * <li><tt><font color="blue">url</font></tt>: <tt>http://localhost/...</tt> This configuration defines the URL for accessing
 *              data from the G2021.</li>
 * <li><tt><font color="blue">interval</font></tt>: <tt>15</tt> The time in seconds how often the http-request should be performed.</li>
 *              Zero or negative values or invalid values will be ignored - the default is 30 seconds.
 * </ul>
 */
public class PortPC extends PortCfg {

        /** The logger. */
        private final static Logger logger = Loggers.getLogger(PacificControlHardwareDriverImpelmentation.class);


        PortPC(JsonObject jsonObj) { super( jsonObj ); }


        PortPC(String name, boolean state, int portClass ) {
            super( name, state, portClass );
        }


        /** Retrieves the value from a sensor
        byte[] getValue( I2CBus i2cBus ) throws Exception {

            byte[] buffer = new byte[ 0 ];
            return buffer;
        }
         */




        /** Set a value at an actor
         * @throws Exception If some problems occured when sending to the actor.
         */
        void setValue( Number value ) throws Exception {
            // check portClass --> is it a DO?
        }
}
