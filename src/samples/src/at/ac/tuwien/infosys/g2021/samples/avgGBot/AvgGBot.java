package at.ac.tuwien.infosys.g2021.samples.avgGBot;

import at.ac.tuwien.infosys.g2021.common.*;
import at.ac.tuwien.infosys.g2021.intf.BufferManager;
import at.ac.tuwien.infosys.g2021.intf.DataPoint;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by siegl on 01.02.2015.
 * This is a very simple program which uses the GBot interface to
 *    read values from the buffer named 'raw' every 3 seconds
 *    and calculate the average of the last three values.
 * <br />
 * The buffer named 'raw' must be defined before starting this GBot by another instance.
 */
public class AvgGBot {

    private final static DateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss.SSS");
    private final static long periode = 3000;   // Sleeptime between the reading of two values - in milliseconds.
    private final static double on  = 0.0;
    private final static double off = 1.0;
    String bufferName;
    String LED1;
    String LED2;
    String LED3;
    DataPoint dataPoint = null;

    private double threshold1 = 40.0;
    private double threshold2 = 60.0;
    private int LEDstate = 0;


    /**
     * The work will be done in this method.
     */
    void doIt( String bName, String l1, String l2, String l3 ) {

        bufferName = ( bName.equals("") ? "AI11" : bName );
        LED1 = ( l1.equals("") ? "DO53" : l1 );
        LED2 = ( l2.equals("") ? "DO54" : l2 );
        LED3 = ( l3.equals("") ? "DO55" : l3 );

        // Some local variables
        double n1, n2, n3;          // Local storage of the last three read values.

        // Create a DataPoint to have a connection to the buffers
        dataPoint = new DataPoint();

        // The DataPoint must be able to connect to the Daemon-Endpoint
        if( dataPoint.getState() != BufferState.ISOLATED ) {
            // 1. try to set the new value
            try {
                // First - assign the dataPoints
                dataPoint.assign( bufferName );
                dataPoint.assign( LED1 );
                dataPoint.assign( LED2 );
                dataPoint.assign( LED3 );

                // turn all LEDs off
                dataPoint.set( LED1, off );
                dataPoint.set( LED2, off );
                dataPoint.set( LED3, off );

                // Printout a headline
                System.out.println("Timestamp                 n(0)   n(1)   n(2)       avg");
                // Now read the value the first, second and third time
                // Print the read values in the same line
                n3 = dataPoint.get( bufferName ).getValue().doubleValue();
                System.out.printf("\r%s  %5.2f", format.format(new Date()), n3 );
                Thread.sleep( periode );
                n2 = dataPoint.get( bufferName ).getValue().doubleValue();
                System.out.printf("\r%s  %5.2f  %5.2f", format.format(new Date()), n2, n3 );
                Thread.sleep( periode );
                n1 = dataPoint.get( bufferName ).getValue().doubleValue();
                // Calculate the average and print all out.
                double d = (n1 + n2 + n3) / 3;
                System.out.printf("\r%s  %5.2f  %5.2f  %5.2f  --> %5.2f%n", format.format(new Date()), n1, n2, n3, d );
                setLED( checkThreshold(d) );   // Check the current value and set LEDs
                Thread.sleep( periode );

                // Inside an infinite loop do it again ...
                while( true ) {
                    n3 = n2;
                    n2 = n1;
                    n1 = dataPoint.get( bufferName ).getValue().doubleValue();
                    d = (n1 + n2 + n3) / 3;
                    setLED( checkThreshold(d) );   // Check the current value and set LEDs
                    System.out.printf("%s  %5.2f  %5.2f  %5.2f  --> %5.2f%n", format.format(new Date()), n1, n2, n3, d );
                    Thread.sleep( periode );
                }
            }
            catch( IllegalArgumentException iae ) {   // The buffer named bufferName does not exist.
                iae.printStackTrace();
                System.out.println(format.format(new Date()) + ": no buffer named '" + bufferName + "' exists; terminate now.");
            }
            catch( InterruptedException ie ) {
                System.out.println(format.format(new Date()) + ": Main loop is interrupted; terminate now.");
            }
        }
        else {  // No connection to daemon-endpoint
            System.out.println( format.format(new Date()) + ": unable to connect the buffer daemon; terminate now." );
        }
    }

    /** Check Threshold of the value
     * The value of parameter d is compared to the areas 0..thr1, thr1..thr2, thr2..100
     * If comparsion results in a new area, the value 1, 2 or 3 is returnd.
     * If comparsion results in the same area as the last compare, 0 is returned.
     **/
    private int checkThreshold( double d ) {
        if ( d >= 0 && d < threshold1 ) {
            if( LEDstate != 1 ) { LEDstate = 1; }
            else { return 0; }
        }
        else if (      d < threshold2 ) {
            if( LEDstate != 2 ) { LEDstate = 2; }
            else { return 0; }
        }
        else if (      d <= 100.0     ) {
            if( LEDstate != 3 ) { LEDstate = 3; }
            else { return 0; }
        }
        else {
            if( LEDstate != 4 ) { LEDstate = 4; }
            else { return 0; }
        }
        return LEDstate;
    }

    /** Turn on one of three LEDs
     * One of the three LEDs LED1, LED2 and LED3 is turned on, the others are turned off
     **/
    private void setLED( int ledID ) {
        if( ledID == 0 ) { return; }
        dataPoint.set( LED1, (ledID == 1 ? on : off ) );
        dataPoint.set( LED2, (ledID == 2 ? on : off ) );
        dataPoint.set( LED3, (ledID == 3 ? on : off ) );
    }

    /**
     * The main entry point of this program.
     *
     * @param args bufferName newValue
     */
    public static void main( String[] args ) {
        try {
            AvgGBot avgGBot = new AvgGBot();
            avgGBot.doIt(
                    args.length > 0 ? args[0] : "",
                    args.length > 1 ? args[1] : "",
                    args.length > 2 ? args[2] : "",
                    args.length > 3 ? args[3] : ""
            );
        }
        catch( Exception e ) {
            System.out.println( "Execption: " + e.getMessage() );
            e.printStackTrace();
        }
        System.exit(0);
    }
}
