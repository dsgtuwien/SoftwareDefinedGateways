package at.ac.tuwien.infosys.g2021.intf;

import at.ac.tuwien.infosys.g2021.common.BufferState;
import at.ac.tuwien.infosys.g2021.common.SimpleData;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/** This is a test of the value queue and its splititerator. */
public class ValueQueueTest {

    // This class is a thread, which reads values from the queue using a stream
    private class ReaderThread extends Thread {

        ReaderThread() { super("queue reader"); }

        @Override
        public void run() {
            queue.stream().forEach(data -> read = data);
            read = null;
        }
    }

    // The test object
    private BlockingQueue<SimpleData> queue;

    // The reading Thread
    private ReaderThread reader;

    // The last value read from the stream
    private SimpleData read;

    /** Setting up the queue reader. */
    @Before
    public void setUp() throws IOException {

        queue = new ValueQueue();
        read = null;
        reader = new ReaderThread();
        reader.start();
    }

    /** Stopping the queue reader. */
    @After
    public void tearDown() throws InterruptedException {

        reader.interrupt();
        reader.join(30000L);
        reader = null;
        queue = null;
        read = null;
    }

    /**
     * We put n values into the queue and check, that the reader can
     * read them. After putting a dummy value into the queue, the
     * end of the stream is reached.
     */
    @Test
    public void test() throws InterruptedException {

        Thread.sleep(100L);
        assertNull(read);

        double value = 0.0;
        int iterations = 5;

        for (int i = 0; i < iterations; i++) {

            SimpleData entry = new SimpleData(new Date(), BufferState.READY, value);
            queue.put(entry);
            Thread.sleep(100L);

            assertNotNull(read);
            assertEquals(entry, read);

            read = null;
            value += 0.5;
        }

        queue.put(new SimpleData());
        Thread.sleep(100L);

        assertNull(read);
    }
}


