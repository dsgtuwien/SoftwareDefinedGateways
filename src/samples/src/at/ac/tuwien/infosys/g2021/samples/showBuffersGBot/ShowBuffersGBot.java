package at.ac.tuwien.infosys.g2021.samples.showBuffersGBot;

import at.ac.tuwien.infosys.g2021.common.BufferDescription;
import at.ac.tuwien.infosys.g2021.common.BufferState;
import at.ac.tuwien.infosys.g2021.common.SimpleData;
import at.ac.tuwien.infosys.g2021.common.util.Unit;
import at.ac.tuwien.infosys.g2021.intf.AbstractDataPointObserverImplementation;
import at.ac.tuwien.infosys.g2021.intf.DataPoint;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * This is a very simple GBot which uses the DataPoint to log the states of all available buffers to the console.
 * The LoggingGBot has no logic. It just dumps argument values of the DataPointObserver methods to the console.
 */
public class ShowBuffersGBot {

    private final static DateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss.SSS");

    /**
     * This is the implementation of the program. After initialization, the thread will wait for buffer changes
     * for an infinite time.
     */
    public synchronized void doIt() {

        try {
            // At first we need a DataPoint instance.
            DataPoint dataPoint = new DataPoint();

            if (dataPoint.getState() != BufferState.ISOLATED) {

                // Now we can query all available buffers
                Collection<BufferDescription> availableBuffers = dataPoint.queryBuffersByName(".*");
                if( availableBuffers.size() > 0 ) {

                    // All available buffers will be assigned. We will log actor states, but we don't control any actors.
                    for (BufferDescription bufferDescription : availableBuffers) {

                        StringBuffer sb = new StringBuffer();
                        sb.append( "Buffername=" );
                        sb.append(bufferDescription.getBufferName());
                        sb.append( " " );
                        sb.append( bufferDescription.isHardwareBuffer() ? "HW" : "--" );
                        Map<String,String> metaInfo = bufferDescription.getBufferMetainfo();
                        for( String key : metaInfo.keySet() ) {
                            sb.append(String.format(" %s=%s", key, metaInfo.get(key) ) );
                            if( key.equals( "unit" ) ) { sb.append("=" + Unit.asText(metaInfo.get(key) ) ); }
                        }
                        System.out.println( sb.toString() );
                        // dataPoint.assign(bufferDescription.getBufferName());
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
            ShowBuffersGBot showBuffersGBot = new ShowBuffersGBot();
            showBuffersGBot.doIt();
        }
        catch (Throwable e) {
            e.printStackTrace(System.err);
            System.exit(1);
        }
        System.exit(0);
    }
}

