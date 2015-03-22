package at.ac.tuwien.infosys.g2021.samples.callbackGBot;

import at.ac.tuwien.infosys.g2021.common.*;
import at.ac.tuwien.infosys.g2021.intf.BufferManager;
import at.ac.tuwien.infosys.g2021.intf.DataPoint;
import at.ac.tuwien.infosys.g2021.intf.SDGCallback;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * This is a very simple GBot which uses the DataPoint to log the values of buffers to the console - using the callback.
 */
public class CallbackGBot implements SDGCallback {

    public class CallbackClass1 implements SDGCallback {
        int k = 1;
        CallbackClass1( int id ) { this.k = k; }
        public void onTimeout( DataPoint dataPoint, Map<String, SimpleData> data ) {
            CallbackGBot.this.doCallback(dataPoint, data, k);
        }
    }

    int id = 0;
    private final static DateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss.SSS");

    public void onTimeout( DataPoint dataPoint, Map<String, SimpleData> data ) {
        doCallback( dataPoint, data, 0 );
    }
    public void doCallback( DataPoint dataPoint, Map<String, SimpleData> data, int k ) {
        int i = id++;
        StringBuffer sb = new StringBuffer();
        sb.append(format.format(new Date()) + " CallbackClass" + k + " #" + i + "     is called." + "  data.size=" + data.size());
        for (String key : data.keySet()) {
            sb.append(String.format(" %s=%.2f", key, data.get(key).getValue().doubleValue()));
        }
        System.out.println(sb.toString());
        try {
            Thread.sleep(4000);
            System.out.println(format.format(new Date()) + " CallbackClass" + k + "     #" + i + " is finished.");
        } catch (Exception e) {
            System.out.println(format.format(new Date()) + " CallbackClass" + k + "     #" + i + " is interrupted.");
        }
    }


    public void doIt() {
        try {
            DataPoint dataPoint = new DataPoint();
            if (dataPoint.getState() != BufferState.ISOLATED) {

                BufferManager bufferManager = new BufferManager();
                BufferConfiguration bufferConfiguration = new BufferConfiguration();
                bufferConfiguration.setBufferClass(BufferClass.SENSOR);
                bufferConfiguration.setGatherer(new TestGathererConfiguration());
                bufferManager.create("raw", bufferConfiguration);
                bufferConfiguration = new BufferConfiguration();
                bufferConfiguration.setBufferClass(BufferClass.SENSOR);
                bufferConfiguration.getAdapterChain().add(new ScalingAdapterConfiguration(0.0, 100.0, -50.0));
                bufferConfiguration.setGatherer(new TestGathererConfiguration());
                bufferManager.create("scaled", bufferConfiguration);
                bufferManager.release();

                dataPoint.assign( "raw" );
                System.out.println(format.format(new Date()) + " addCallback    1 delay=" + 5000 );
                CallbackClass1 cb1 = new CallbackClass1( 1 );
                dataPoint.addCallback(5000, cb1 );
                Thread.sleep(1000);
                System.out.println(format.format(new Date()) + " addCallback    0 delay=" + 5000);
                dataPoint.addCallback(5000, this);
                Thread.sleep(29000);
                System.out.println(format.format(new Date()) + " removeCallback 1");
                dataPoint.removeCallback(cb1);
                dataPoint.assign("scaled");
                System.out.println(format.format(new Date()) + " addCallback    0 delay=" + 1000);
                dataPoint.addCallback(1000, this);
                Thread.sleep(10000);
                dataPoint.detach("scaled");
                Thread.sleep(10000);
                System.out.println(format.format(new Date()) + " addCallback    0 delay=" + 7000);
                dataPoint.addCallback(7000, this);
                Thread.sleep(20000);
                System.out.println(format.format(new Date()) + " removeCallback 0");
                dataPoint.removeCallback(this);
                System.out.println(format.format(new Date()) + " addCallback    0 1 delay=" + 1000);
                dataPoint.addCallback(1000, this, cb1);
                Thread.sleep(10000);
                System.out.println(format.format(new Date()) + " removeCallback 0");
                dataPoint.removeCallback(this);
                Thread.sleep(10000);
                System.out.println(format.format(new Date()) + " removeCallback 1");
                dataPoint.removeCallback(cb1);
                Thread.sleep(10000);
                dataPoint.release();
            }
            else {
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
            CallbackGBot callbackGBot = new CallbackGBot();
            callbackGBot.doIt();
        }
        catch (Throwable e) {
            e.printStackTrace(System.err);
            System.exit(1);
        }
        System.exit(0);
    }
}

