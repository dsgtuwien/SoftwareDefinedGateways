package at.ac.tuwien.infosys.g2021.samples;

import at.ac.tuwien.infosys.g2021.common.BufferState;
import at.ac.tuwien.infosys.g2021.common.SimpleData;
import at.ac.tuwien.infosys.g2021.intf.AbstractDataPointObserverImplementation;
import at.ac.tuwien.infosys.g2021.intf.BufferDescription;
import at.ac.tuwien.infosys.g2021.intf.DataPoint;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

/**
 * This is a very simple program which uses the GBot interface to log the states of all available buffers to the
 * console.
 */
public class LoggingGBot extends Thread {

    private final static DateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss.SSS");

    /**
     * This is an inner class, which implements the observer interface and logs all the
     * state changes to <tt>System.out</tt>.
     */
    private static class LoggingObserver extends AbstractDataPointObserverImplementation {

        /**
         * The overall state of the data point has changed.
         *
         * @param dataPoint the affected data point
         * @param oldOne    the left state
         * @param newOne    the current state of the data point
         */
        @Override
        public void dataPointStateChanged(DataPoint dataPoint, BufferState oldOne, BufferState newOne) {

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
        public void bufferChanged(DataPoint dataPoint, SimpleData oldOne, SimpleData newOne) {

            if (oldOne == null) {
                System.out.println(format.format(newOne.getTimestamp()) + ": the buffer '" + newOne.getBufferName() + "' has been assigned.");
                System.out.printf("\t\tbuffer state:\t%s%n", newOne.getState().name());
                switch (newOne.getState()) {
                    case READY:
                        System.out.printf("\t\tbuffer value:\t%f.2%n", newOne.getValue().doubleValue());
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
        }
    }

    /**
     * This is the implementation of the program. After initialization, the thread will wait for buffer changes
     * for an infinite time.
     */
    @Override
    public void run() {

        try {
            // At first we need a DataPoint instance.
            DataPoint dataPoint = new DataPoint();

            if (dataPoint.getState() != BufferState.RELEASED) {

                // Changes on the data point will be logged with our observer
                dataPoint.addDataPointObserver(new LoggingObserver());

                // Now we can query all available buffers
                Collection<BufferDescription> availableBuffers = dataPoint.queryBuffersByName(".*");
                if (availableBuffers.size() > 0) {

                    // All available buffers will assigned in read only mode. We will log actor states, but we don't control any actors.
                    availableBuffers.forEach((bufferDescription) -> dataPoint.assign(bufferDescription.getBufferName()));

                    // Now we wait for changes for an infinite time.
                    try {
                        while (true) wait();
                    }
                    catch (InterruptedException e) {
                        // An interrupt will also terminate this program.
                    }
                }
                else {

                    // There are no buffers available. We will log a short message and terminate the thread.
                    System.out.println(format.format(new Date()) + ": there are no buffers available. The program terminates now.");
                }

                // Finally we release all resources used by the data point und terminate the thread
                dataPoint.release();
            }
            else {

                // Oh shit, we are not able to establish a connection with the buffer daemon.
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
            LoggingGBot loggingGBot = new LoggingGBot();

            loggingGBot.start();

            try {
                loggingGBot.join();
            }
            catch (InterruptedException e) {

                /* This exception will be ignored, because the application
                 * will now terminate immediately.
                 */
            }
        }
        catch (Throwable e) {
            e.printStackTrace(System.err);
            System.exit(1);
        }

        System.exit(0);
    }
}

