package at.ac.tuwien.infosys.g2021.common.util;

import java.io.FileReader;
import java.util.Properties;

/**
 * This Class handles the units.<br><br>
 *
 * The mapping-information is read from properties-file.
 * Usage:
 * <tt>
 * String unitText1 = at.ac.tuwien.infosys.g2021.common.util.Unit.asText( 42 );
 * String unitText2 = at.ac.tuwien.infosys.g2021.common.util.Unit.asText( "1093.1.1" );
 * </tt>
 */
public class Unit {

    /** The Filename including the mapping between unitKey and unitText */
    private static String unitsFileName = "units.props";

    // The Properties, which holds the mapping.
    private static Properties units = new Properties();

    // This boolean indicates that units is already loaded correctly.
    private static boolean loadOK = false;

    // No public constructor
    private Unit() {}

    /** Gives the units. If the units are not loaded already, loading is performed.
     * @return the Units within the properties
     */
    private static Properties getUnits() {
        if( loadOK ) { return units; }
        else {
            // System.out.println( "\nLoading the units from file '" + unitsFileName + "'" );
            try { units.load(new FileReader(unitsFileName)); loadOK = true; }
            catch( Exception e ) { System.err.println( "\nCannot load units from file '" + unitsFileName ); }
        }
        return units;
    }

    /**
     * Converts the unixKey to a human readable text.
     *
     * The file 'units.props' includes the mapping between unitKeys and texts.
     * @param unit the unitKey
     * @return the human readable unit
     */
    public static String asText( int unit ) { return asText( String.valueOf(unit) ); }
    public static String asText( String unit ) {
        String unitText = getUnits().getProperty(unit);
        return unitText == null ? unit : unitText;
    }
}