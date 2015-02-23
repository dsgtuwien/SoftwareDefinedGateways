package at.ac.tuwien.infosys.g2021.samples.cfgTask;

import at.ac.tuwien.infosys.g2021.common.BufferClass;
import at.ac.tuwien.infosys.g2021.common.BufferConfiguration;
import at.ac.tuwien.infosys.g2021.common.BufferState;
import at.ac.tuwien.infosys.g2021.common.LowpassAdapterConfiguration;
import at.ac.tuwien.infosys.g2021.common.ScalingAdapterConfiguration;
import at.ac.tuwien.infosys.g2021.common.SimpleData;
import at.ac.tuwien.infosys.g2021.common.TestGathererConfiguration;
import at.ac.tuwien.infosys.g2021.common.TriggeringAdapterConfiguration;
import at.ac.tuwien.infosys.g2021.intf.AbstractDataPointObserverImplementation;
import at.ac.tuwien.infosys.g2021.common.BufferDescription;
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
public class CfgTask {

    private final static DateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss.SSS");

    // The data point
    private DataPoint dataPoint;

    /**
     * This is the implementation of the program. At first there are some buffers configurated in the buffer daemon.
     */
    public synchronized void doIt() {

        try {
            // At first we create a DataPoint instance to look for the daemon.
            // dataPoint = new DataPoint();
            // if( dataPoint.getState() != BufferState.ISOLATED ) {

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
            // }
            // else {
                // We are not able to establish a connection with the buffer daemon.
            //     System.out.println(format.format(new Date()) + ": unable to connect the buffer daemon. The program terminates now.");
            // }
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
            CfgTask cfgTask = new CfgTask();
            cfgTask.doIt();
        }
        catch( Throwable e ) {
            e.printStackTrace(System.err);
            System.exit(1);
        }
        System.exit(0);
    }
}

