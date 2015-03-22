package at.ac.tuwien.infosys.g2021.samples.demoGBot;

import at.ac.tuwien.infosys.g2021.common.BufferClass;
import at.ac.tuwien.infosys.g2021.common.BufferConfiguration;
import at.ac.tuwien.infosys.g2021.common.BufferDescription;
import at.ac.tuwien.infosys.g2021.common.BufferState;
import at.ac.tuwien.infosys.g2021.common.LowpassAdapterConfiguration;
import at.ac.tuwien.infosys.g2021.common.ScalingAdapterConfiguration;
import at.ac.tuwien.infosys.g2021.common.SimpleData;
import at.ac.tuwien.infosys.g2021.common.TestGathererConfiguration;
import at.ac.tuwien.infosys.g2021.common.TriggeringAdapterConfiguration;
import at.ac.tuwien.infosys.g2021.intf.AbstractDataPointObserverImplementation;
import at.ac.tuwien.infosys.g2021.intf.BufferManager;
import at.ac.tuwien.infosys.g2021.intf.DataPoint;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

/**
 * This is a very simple program which uses the GBot interface to log the states of all available buffers to the
 * console.
 */
public class DemoGBot extends Thread {

    private final static DateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss.SSS");

    // The data point
    private DataPoint dataPoint = null;

    /** This is an inner class, which implements the observer interface. */
    private class LoggingObserver extends AbstractDataPointObserverImplementation {

        /**
         * A buffer has changed its state or value.
         *
         * @param dataPoint the data point
         * @param oldOne    the outdated buffer data
         * @param newOne    the current buffer data
         */
        @Override
        public void bufferChanged(DataPoint dataPoint, SimpleData oldOne, SimpleData newOne) {

            if (newOne.getBufferName().equals("lowpass")) {
                try {
                    System.out.printf("%s %5.2f     %3d     %5.1f       %2d%n",
                                      format.format(new Date()),
                                      dataPoint.get("raw").getValue().doubleValue(),
                                      dataPoint.get("scaled").getValue().intValue(),
                                      dataPoint.get("lowpass").getValue().doubleValue(),
                                      dataPoint.get("trigger").getValue().intValue());
                }
                catch (NullPointerException e) {
                    // This exception is thrown, if at least one of the buffers is not ready.
                    System.out.printf("%s ?????     ???     ?????        ?%n",
                                      format.format(new Date()));
                }
            }
        }
    }

    /**
     * This is the implementation of the program. At first there are some buffers configurated in the buffer daemon.
     * Then the thread will create a DataPoint with this buffers and log the buffer values for an infinite time.
     */
    @Override
    public synchronized void run() {

        try {
            // At first we create a DataPoint instance to look for the daemon.
            dataPoint = new DataPoint();
            if (dataPoint.getState() != BufferState.ISOLATED) {

                // Now we configure some buffers in the daemon. This must be done with a BufferManager object.
                BufferManager bufferManager = new BufferManager();

                // At first we create a sensor with a test gatherer. This buffer will contain the raw input with
                // values between 0.0 and 1.0. Therefore no adapters are necessary.
                BufferConfiguration bufferConfiguration = new BufferConfiguration();
                bufferConfiguration.setBufferClass(BufferClass.SENSOR);
                bufferConfiguration.setGatherer(new TestGathererConfiguration());
                bufferManager.create("raw", bufferConfiguration);

                // Now we create buffer with scaled values of the gatherer.
                bufferConfiguration = new BufferConfiguration();
                bufferConfiguration.setBufferClass(BufferClass.SENSOR);
                bufferConfiguration.getAdapterChain().add(new ScalingAdapterConfiguration(0.0, 100.0, -50.0));
                bufferConfiguration.setGatherer(new TestGathererConfiguration());
                bufferManager.create("scaled", bufferConfiguration);

                // This buffer uses a lowpass adapter.
                bufferConfiguration = new BufferConfiguration();
                bufferConfiguration.setBufferClass(BufferClass.SENSOR);
                bufferConfiguration.getAdapterChain().add(new ScalingAdapterConfiguration(0.0, 100.0, -50.0));
                bufferConfiguration.getAdapterChain().add(new LowpassAdapterConfiguration(0.75));
                bufferConfiguration.setGatherer(new TestGathererConfiguration());
                bufferManager.create("lowpass", bufferConfiguration);

                // The last buffer works as trigger.
                bufferConfiguration = new BufferConfiguration();
                bufferConfiguration.setBufferClass(BufferClass.SENSOR);
                bufferConfiguration.getAdapterChain().add(new TriggeringAdapterConfiguration(0.25, 0.75, 0.0, 1.0));
                bufferConfiguration.setGatherer(new TestGathererConfiguration());
                bufferManager.create("trigger", bufferConfiguration);

                // The buffer manager has done its work.
                bufferManager.release();

                // Back to the data point, we attach all out buffers
                Collection<BufferDescription> availableBuffers = dataPoint.queryBuffersByName(".*");
                for (BufferDescription bufferDescription : availableBuffers) dataPoint.assign(bufferDescription.getBufferName());

                // The observer will notify us about value changes
                dataPoint.addDataPointObserver(new LoggingObserver());

                // Writing the header line
                System.out.printf("                          raw  scaled  low pass  trigger%n");

                // now we can wait for notifications to log the buffer states
                try {
                    wait();
                }
                catch (InterruptedException e) { /* An interrupt stops the GBot */ }

                // Finally we release all resources used by the data point und terminate the thread
                dataPoint.release();
            }
            else {
                // We are not able to establish a connection with the buffer daemon.
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
            DemoGBot demoGBot = new DemoGBot();
            demoGBot.start();

            try {
                demoGBot.join();
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

