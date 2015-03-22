package at.ac.tuwien.infosys.g2021.daemon.drivers;

import at.ac.tuwien.infosys.g2021.common.util.Unit;
import at.ac.tuwien.infosys.g2021.daemon.PortClass;
import com.eclipsesource.json.JsonObject;

import java.util.*;

/** This class holds the configuration of one Sensor/Actor.
 *
 * The configuration is stored in a file formated in json - exampled in the following.
 * <pre>
 * { "portCfg" : [
 *    { "<font color="blue">name</font>" : "<i>AI22</i>",
 *      "<font color="blue">portClass</font>" : <i>3</i>,
 *      "<font color="blue">active</font>" : <i>true</i>,
 *      "<font color="blue">metaInfo</font>" : { "unit" : "<i>3</i>", "description" : "<i>AI22: -20..+50°C</i>" },
 *      "<font color="blue">hwCfg</font>" : { "connection" : "I2C", "i2cMuxAddr" : "<i>0x70</i>", "i2cMuxCmd" : "<i>0x04</i>", "i2cAddr" : "<i>0x51</i>", "bitWeight" : "<i>16,32,64,128,0,0,0,0,0,0,0,0,1,2,4,8</i>" },
 *      "<font color="blue">valCorr</font>" : { "linCoffs" : "<i>0.0, 1.020, 6.120, 12.75, 19.125, 25.500, 31.875, 40.035, 65.025, 105.060, 128.775, 168.300, 200.940, 255.0</i>",
 *                    "linSteps" : "<i>0.0, 10.00, 20.00, 30.00, 40.000, 50.000, 60.000, 70.000, 75.000,  80.000,  85.000,  90.000,  95.000, 100.0</i>",
 *                    "polyCoffs"  : "<i>-20, 0.70</i>"
 *      } }
 * ] }
 * </pre>
 * The main area-name is <tt>portCfg</tt>. Within that all configuration data for ports - sensors and actors - are defined.
 * For each port there are 6 sections defined:
 * <ul>
 * <li><tt><font color="blue">name</font></tt>: The unique name of the port. The name is a user defined string. This config is mandatory.</li>
 * <li><tt><font color="blue">portClass</font></tt>: The type of the port - valid values are <tt>1</tt>..<tt>4</tt>.
 *              <tt>1=DI 2=DO 3=AI 4=AO</tt> This config is mandatory.</li>
 * <li><tt><font color="blue">active</font></tt>: <tt>true</tt> means, this port is in use. <tt>false</tt> means, this configuration will be ignored. This config is mandatory.</li>
 * <li><tt><font color="blue">metaInfo</font></tt>: Some usefull info for the GBot to select the right port (buffer).
 *              Each parameter defined in this section can be seen by the GBot in the metainfo of the buffer.
 *              The parameter <tt>unit</tt> is a key, which will be mapped with info from <tt></tt>units.properties</tt>.
 *              It is not recomended to give a string, because the recognition in the GBot becomes a problem.
 *              This config is optional.</li>
 * <li><tt><font color="blue">hwCfg</font></tt>: Necessary data for communication to the physical sensor and interpredating the bits which holds the sensor value.
 *              This config data is specific to the hardware.
 *              This config is mandatory.</li>
 * <li><tt><font color="blue">valCorr</font></tt>: Finally, the configurations data for postpreperation of the sensor value.
 *              currently there exists 2 types of correction: (1) linearization and (2) polynomial correction.
 *              This config is optional.</li>
 * </ul>
 */
public class PortCfg {

    public String name;         // The name of this sensor/actor
    boolean state = true;       // Is the sensor/actor active or inactive?
    PortClass portClass;        // The type of this sensor/actor: DI, DO, AI or AO
    HashMap<String,String> metaInfo = new HashMap<String,String>();   // The metainfo; e.g. unit, description, position ...
    public HashMap<String,Object> hwCfg    = new HashMap<String,Object>();   // Specific info for communication to the sensor/actor
    HashMap<String,String> valCorr  = new HashMap<String,String>();   // Specific info for correction of the value


    /** Indicates wether linearization is configured or not. */
    private boolean linearization = false;
    /** The steps for the linearization */
    private double s[] = null;
    /** coefficients for the linearization */
    private double c[] = null;

    /** Indicates wether coefficients for a polynominal correction are defined. */
    private boolean polynom = false;
    /** coefficients for the polynominal correction. */
    private double p[] = null;


    static HashMap<String,PortCfg> allCfgs = new HashMap<String,PortCfg>();


    /** Creation of this object in classic way
     * @param name The name of this PortCfg - a unique string
     * @param state indicates if this configuration is active or inactive
     * @param portClass indicates the type of port
     */
    PortCfg(String name, boolean state, int portClass ) {
        this.name = name;
        this.state = state;
        this.portClass = PortClass.get(portClass);
    }

    /** Creation of this object by analysing the cfg-data
     * @param jsonObj The JSON object which holds the configuration.
     */
    public PortCfg( JsonObject jsonObj ) {
        try {
            // Read some mandatory values from the configuration
            this.name  = jsonObj.asObject().get("name").asString();
            this.state = jsonObj.asObject().get("active").asBoolean();
            this.portClass = PortClass.get(jsonObj.asObject().get("portClass").asInt());
        }
        catch( Exception e ) {
            this.name = null;
            return;
        }

        // Read metaInfo - this section is optional
        HashMap<String, String> metaInfo = new HashMap<String, String>();
        try {
            for (String miName : jsonObj.asObject().get("metaInfo").asObject().names()) {
                try {
                    metaInfo.put(miName, jsonObj.asObject().get("metaInfo").asObject().get(miName).asString());
                }
                catch (java.lang.UnsupportedOperationException uoe12) {
                    System.err.println("Config-File metaInfo: unsupported type of " + miName);
                    System.exit(-51);
                }
            }
        }
        catch( NullPointerException n ) { // OK, take some defaults.
            metaInfo.put("description", jsonObj.asObject().get("name").asString());
            metaInfo.put( "unit", "" );
        }
        setMetaInfo(metaInfo);

        // Read hwConfig - this section is mandatory
        HashMap<String,Object> hwCfg = new HashMap<String,Object   >();
        for( String hwName : jsonObj.asObject().get( "hwCfg" ).asObject().names() ) {
            try { hwCfg.put(hwName, jsonObj.asObject().get("hwCfg").asObject().get(hwName).asString()); }
            catch ( java.lang.UnsupportedOperationException uoe12 ) {
                System.err.println("Config-File hwCfg: unsupported type of " + hwName );
                System.exit( -52 );
            }
        }
        setHwCfg(hwCfg);

        // Read some parameters for correcting the value - this section is optional
        HashMap<String,String> valCorr = new HashMap<String,String>();
        try {
            for (String vcName : jsonObj.asObject().get("valCorr").asObject().names()) {
                try {
                    valCorr.put(vcName, jsonObj.asObject().get("valCorr").asObject().get(vcName).asString());
                }
                catch (java.lang.UnsupportedOperationException uoe13) {
                    System.err.println("Config-File valCorr: unsupported type of " + vcName);
                    System.exit(-53);
                }
            }
        }
        catch( NullPointerException e ) {} // OK, no attribute valCorr in portCfg.
        setValCorr(valCorr);
    }


    public String    getName() {      return name; }
    public boolean   getState() {     return state; }
    public PortClass getPortClass() { return portClass; }
    public HashMap<String,String> getMetaInfo() { return metaInfo; }
    public HashMap<String,Object> getHwCfg() {    return hwCfg; }
    public HashMap<String,String> getValCorr() {  return valCorr; }

    void setName( String name ) { this.name = name; }
    void setState( boolean state ) { this.state = state; }
    void setPortClassKey( int portClassKey ) { this.portClass = PortClass.get(portClassKey); }
    void setPortClass( PortClass portClass ) { this.portClass = portClass; }
    void setMetaInfo( HashMap<String,String> metaInfo ) { this.metaInfo = metaInfo; }
    void setHwCfg( HashMap<String,Object> hwCfg ) { this.hwCfg = hwCfg; }
    void setValCorr( HashMap<String,String> valCorr ) { this.valCorr = valCorr; }


    /** prepare the postcorrection of the value */
    public void doPrepare() {

        // Linearization
        String linCoffs = (String) valCorr.get("linCoffs");
        String linSteps = (String) valCorr.get("linSteps");
        // Are the parameters consistent?
        if (linCoffs != null && linSteps != null) {
            String cs[] = linCoffs.split(",");
            String ss[] = linSteps.split(",");
            if (cs.length != ss.length) {
                System.err.println("Port " + getName() + ": Misconfigured linearization.");
            } else {
                // Prepare numeric values
                c = new double[cs.length + 1];
                s = new double[cs.length + 1];
                for (int i = 0; i < cs.length; i++) c[i] = Double.valueOf(cs[i]);
                c[cs.length] = c[cs.length - 1];
                for (int i = 0; i < ss.length; i++) s[i] = Double.valueOf(ss[i]);
                s[cs.length] = s[cs.length - 1];
                linearization = true;
            }
        }

        // Polynom calculation
        String sn = (String) getValCorr().get("polyCoffs");    // Read the coefficients a0, a1, a2, ...
        if (sn != null) {
            String[] sns = sn.split(",");
            if (sns.length != 0) {      // If the list of coefficients is empty, keep the old value
                p = new double[sns.length];
                for (int i = 0; i < sns.length; i++) p[i] = Double.valueOf(sns[i]);
                polynom = true;
            }
        }
    }


    /** Linearization of the value
     * @param r The value, which has to be corrected.
     * @return The new value.
     */
    public double linearizer( double r ) {

        if( !linearization ) return r;  // Do it only if configured

        // calculate the linearized value
        double res = s[s.length-1];   // If r is above the highes Bereich
        for (int i = 1; i < c.length; i++) { // ein Bereich ist:  c[i-1] .. c[i]
            if (r <= c[i]) {    //  es gilt:  c[i-1] < c[i]     es gilt:  c[i-1] < value < c[i]
                //    |---  % von value im Bereich  ---|   |- Stufenbreite-|   |-Offset der Stufe-|
                res = (r - c[i - 1]) / (c[i] - c[i - 1]) * (s[i] - s[i - 1]) + s[i - 1];
                break;  // The result is found.
            }
        }
        return res;
    }


    /** Polynom correction of the value
     * @param r The value, which has to be corrected.
     * @return The new value.
     */
    public double polynominizer( double r ) {

        if( !polynom ) return r;    // Do it only if configured

        double res = 0;
        for (int i = 0; i < p.length; i++) {  // Loop each term of the polynom
            if (i == 0) res = p[0];
            else if (i == 1) res += p[1] * r;
            else {                  // i >= 2
                double h = r;       // calculate r^n
                for (int j = 1; j < i; j++) h *= r;
                res += p[i] * h;    // add the term (an * r^n) to the sum
            }
        }
        return res;
    }


    /**
     * Return a String with all the information within this PortCfg.
     * @return the String
     */
    public String dump() {
        StringBuffer sb = new StringBuffer();
        sb.append("\n name=").append(this.name);
        sb.append("\n portClass=").append(PortClass.toString(this.portClass));
        sb.append("\n state=").append(state ? "active" : "passive");
        sb.append("\n metaInfo:");
        for( String key : metaInfo.keySet() ) {
            if( key.equals( "unit" ) ) {
                try {
                    sb.append("\n    unit=").append(metaInfo.get("unit")).append("=").append(Unit.asText(metaInfo.get("unit")));
                } catch (java.lang.UnsupportedOperationException uoe11) {
                    sb.append("\n    unit=<undefined>");
                }
                continue;
            }
            sb.append("\n    ").append(key).append("=").append(metaInfo.get(key));
        }
        sb.append("\n hwCfg:");
        for( String key : hwCfg.keySet() ) {
            sb.append("\n    ").append(key).append("=").append((String) hwCfg.get(key));
        }
        sb.append("\n valCorr:");
        for( String key : valCorr.keySet() ) {
            sb.append("\n    ").append(key).append("=").append((String) valCorr.get(key));
        }
        sb.append( "\n" );
        return sb.toString();
    }
}
