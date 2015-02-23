package at.ac.tuwien.infosys.g2021.samples.ledOnOffGBot;

import at.ac.tuwien.infosys.g2021.common.BufferState;
import at.ac.tuwien.infosys.g2021.common.SimpleData;
import at.ac.tuwien.infosys.g2021.intf.AbstractDataPointObserverImplementation;
import at.ac.tuwien.infosys.g2021.common.BufferDescription;
import at.ac.tuwien.infosys.g2021.intf.DataPoint;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

/**
 * This GBot looks for all available Buffers and print there names to the console.
 * Next the Buffers DI31, DI32, DO51 and DO52 are assigned.
 * When DI31 changes its state, DO51 will be changed - a Switch turns on a LED
 * When DI32 changes its state, DO52 will be changed.
 *
 * The changes are recognized by an Observer. The GBot itself is waiting to be triggered by the DataPoint.
 */
public class LedOnOffGBot extends Thread {

    private final static DateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss.SSS");
    private final static String Switch1 = "DI31"; // switch for green LED
    private final static String Switch2 = "DI32"; // switch for red LED
    private final static double on  = 0.0;
    private final static double off = 1.0;
    private final static String LED1 = "DO51"; // Green LED
    private final static String LED2 = "DO52"; // Red   LED



    /**
     * This is an inner class, which implements the observer interface and logs all the
     * state changes to <tt>System.out</tt>.
     */
    private static class LedOnOffObserver extends AbstractDataPointObserverImplementation {

        /**
         * The overall state of the data point has changed.
         *
         * @param dataPoint the affected data point
         * @param oldOne    the left state
         * @param newOne    the current state of the data point
         */
        @Override
        public void dataPointStateChanged( DataPoint dataPoint, BufferState oldOne, BufferState newOne ) {
            System.out.println(format.format(new Date()) + ": the overall state of all buffers has changed from " + oldOne.name() + " to " + newOne.name() + ".");
        }

        /**
         * A buffer has changed its state or value.
         *
         * @param dataPoint the data point
         * @param oldOne    the outdated buffer data
         * @param newOne    the current buffer data
         */
        @Override
        public void bufferChanged( DataPoint dataPoint, SimpleData oldOne, SimpleData newOne ) {

            // Write some information about the new state of the switch to the output
            if (oldOne == null) {
                System.out.println(format.format(newOne.getTimestamp()) + ": the buffer '" + newOne.getBufferName() + "' has been assigned.");
                System.out.printf("\t\tbuffer state:\t%s%n", newOne.getState().name());
                switch (newOne.getState()) {
                    case READY:
                        System.out.printf("\t\tbuffer value:\t%.2f%n", newOne.getValue().doubleValue());
                        break;

                    default:
                        // In all other buffer states, there is no value available!
                        break;
                }
            }
            else if (newOne == null) {
                System.out.println(format.format(oldOne.getTimestamp()) + ": the buffer '" + oldOne.getBufferName() + "' has been detached.");
            }
            else {
                if (newOne.getState() != oldOne.getState()) {
                    System.out.println(format.format(newOne.getTimestamp()) + ": the state of the buffer '" + newOne.getBufferName() +
                                       "' has changed from " + oldOne.getState().name() + " to " + newOne.getState().name() + ".");
                }
                if (newOne.getValue() != null) {
                    if (oldOne.getValue() == null) {
                        System.out.printf("%s: the buffer '%s' contains the value %.2f.%n",
                                          format.format(newOne.getTimestamp()), newOne.getBufferName(), newOne.getValue().doubleValue());
                    }
                    else {
                        System.out.printf("%s: the value of the buffer '%s' has changed from %.2f to %.2f.%n",
                                          format.format(newOne.getTimestamp()), newOne.getBufferName(),
                                          oldOne.getValue().doubleValue(), newOne.getValue().doubleValue());
                    }
                }
            }

            // Now do the main work - turn on or off the LED
            if(       newOne.getBufferName().equals( Switch1 ) ) {
                dataPoint.set( LED1, newOne.getValue().doubleValue() == on ? on : off );
            }
            else if ( newOne.getBufferName().equals( Switch2 ) ) {
                dataPoint.set( LED2, newOne.getValue().doubleValue() == on ? on : off );
            }
        }
    }

    /**
     * This is the implementation of the program. After initialization, the thread will wait for buffer changes
     * for an infinite time.
     */
    @Override
    public synchronized void run() {

        try {
            // At first we need a DataPoint instance.
            DataPoint dataPoint = new DataPoint();

            if( dataPoint.getState() != BufferState.ISOLATED) {

                // Now we can query all available buffers
                Collection<BufferDescription> availableBuffers = dataPoint.queryBuffersByName( ".*" );
                if( availableBuffers.size() > 0 ) {
                    // Print the Buffernames to the console
                    for( BufferDescription bufferDescription : availableBuffers ) {
                        System.out.println(" " + bufferDescription.getBufferName());
                    }
                }
                else {     // There are no buffers available. We will log a short message and terminate the thread.
                    System.out.println( format.format(new Date() ) + ": there are no buffers available. The program terminates now.");
                }

                // All required buffers will be assigned.

                dataPoint.assign( Switch1 );
                dataPoint.assign( Switch2 );
                dataPoint.assign( LED1 );
                dataPoint.assign( LED2 );

                // Set the LEDs to initial values
                dataPoint.set( LED1, dataPoint.get( Switch1 ).getValue().doubleValue() == on ? on : off);
                dataPoint.set( LED2, dataPoint.get( Switch2 ).getValue().doubleValue() == on ? on : off);

                // Changes on the data point will be logged with our observer
                dataPoint.addDataPointObserver( new LedOnOffObserver() );

                // Now we wait for changes for an infinite time.
                try { while( true ) wait(); }
                catch (InterruptedException e) {}    // An interrupt will also terminate this program.

                // Finally we release all resources used by the data point und terminate the thread
                dataPoint.release();
            }
            else {

                // Oh, we are not able to establish a connection with the buffer daemon.
                System.out.println(format.format(new Date()) + ": unable to connect the buffer daemon. The program terminates now.");
            }
        }
        catch (Exception e) {
            System.out.println(format.format(new Date()) + ": an unexpected exception has occurred:");
            e.printStackTrace();
        }
    }

    /**
     * The main entry point of this program.
     *
     * @param args will be ignored
     */
    public static void main(String[] args) {

        try {
            LedOnOffGBot loggingGBot = new LedOnOffGBot();

            loggingGBot.start();

            try { loggingGBot.join(); }
            catch (InterruptedException e) {
                /* This exception will be ignored, because the application will now terminate immediately. */
            }
        }
        catch (Throwable e) {
            e.printStackTrace(System.err);
            System.exit(1);
        }
        System.exit(0);
    }
}

