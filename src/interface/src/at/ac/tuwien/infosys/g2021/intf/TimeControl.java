package at.ac.tuwien.infosys.g2021.intf;

import at.ac.tuwien.infosys.g2021.common.SimpleData;
import at.ac.tuwien.infosys.g2021.common.util.PanicError;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * This class offers a time triggered retrieval of all the buffer values. Instances of this class
 * can be created with the <tt>{@link DataPoint#getTimeControl()}</tt> method. The delay between
 * value retrievals is at least 100 milliseconds.
 *
 * @see DataPoint#getTimeControl()
 */
public class TimeControl {

    // The Timer used by all instances of TimeControl
    private static Timer timer = new Timer(true);

    // The DataPoint
    private DataPoint dataPoint;

    // The time settings
    private int initialDelay;
    private int delay;
    private int count;

    // Was the timer restarted
    private boolean restarted;

    // The remaining count
    private int remaining;

    // The currently running timer task
    private TimerTask task;

    // The result queue
    private BlockingQueue<Map<String, SimpleData>> resultQueue;

    // A thread synchronization object
    private final Object lock;

    /** This is the implementation of the timer task used in this class. */
    private class Executor extends TimerTask {

        /** Initialization of this instance. */
        Executor() { super(); }

        /** The action to be performed by this timer task. */
        @Override
        public void run() {

            try {
                timeout();
            }
            catch (InterruptedException e) {
                // The result queue is an unlimited queue. The timer thread must not wait, therefore an
                // interrupted exception causes panic.
                throw new PanicError(e);
            }
        }
    }

    /** Initialization of the timer. */
    private TimeControl() {

        super();

        lock = new Object();
    }

    /** Initialization of the timer.
     * @param dp the DataPoint
     * */
    TimeControl(DataPoint dp) {

        this();

        dataPoint = dp;
        resultQueue = new LinkedBlockingQueue<>();
        restarted = true;

        setDelay(0);
    }

    /**
     * Returns the queue, where buffer data is put into.
     *
     * @return the result queue, which is never <tt>null</tt>
     */
    public BlockingQueue<Map<String, SimpleData>> getResultQueue() { return resultQueue; }

    /**
     * Retrieves the eldest data of the result queue, waiting until data becomes available if necessary.
     *
     * @return the eldest data retrieved from the data point
     *
     * @throws java.lang.InterruptedException if the calling thread is interrupted while waiting
     */
    public Map<String, SimpleData> get() throws InterruptedException { return resultQueue.take(); }

    /**
     * Starts the <code>Timer</code>, causing it to start retrieving buffer data.
     *
     * @see #stop
     */
    public void start() {

        synchronized (lock) {

            if (task == null) {
                task = new Executor();
                if (!restarted) {
                    if (count == 0 || remaining > 0) timer.schedule(task, delay);
                    else task = null;
                }
                else {
                    remaining = count;
                    if (initialDelay < 1) task.run();
                    else timer.schedule(task, initialDelay);
                    restarted = false;
                }
            }
        }
    }

    /**
     * Returns <code>true</code> if the <code>Timer</code> is running.
     *
     * @return <code>true</code> if the <code>Timer</code> is running.
     *
     * @see #start
     */
    public boolean isRunning() {

        synchronized (lock) {
            return task != null;
        }
    }

    /**
     * Stops the <code>Timer</code>, causing it to stop retrieving buffer data.
     *
     * @see #start
     */
    public void stop() {

        synchronized (lock) {

            if (task != null) {
                task.cancel();
                task = null;
            }
        }
    }

    /** Restarts the <code>Timer</code>, clearing the result queue und start data retrieval with its initial delay. */
    public void restart() {

        synchronized (lock) {
            stop();
            restarted = true;
            resultQueue.clear();
            start();
        }
    }

    /**
     * Handles a timeout.
     *
     * @throws java.lang.InterruptedException if this thread is interrupted while putting data into the result queue
     */
    private void timeout() throws InterruptedException {

        synchronized (lock) {
            if (task != null) {
                resultQueue.put(dataPoint.getAll());
                if (count != 0) remaining--;
                task = null;
                restarted = false;
                start();
            }
        }
    }

    /**
     * Returns the initial delay of the <code>Timer</code> in milliseconds.
     *
     * @return the initial delay of the <code>Timer</code> in milliseconds.
     */
    public int getInitialDelay() { return initialDelay; }

    /**
     * Set the initial delay of the <code>Timer</code> in milliseconds. This method has no effect to the
     * current delay of a running <code>Timer</code>.
     *
     * @param initialDelay the initial delay of the <code>Timer</code> in milliseconds.
     */
    public void setInitialDelay(int initialDelay) { this.initialDelay = Math.max(0, initialDelay); }

    /**
     * Returns the delay between two retrievals of the <code>Timer</code> in milliseconds.
     *
     * @return the delay between two retrievals of the <code>Timer</code> in milliseconds.
     */
    public int getDelay() { return delay; }

    /**
     * Set the delay between two retrievals of the <code>Timer</code> in milliseconds. This method has no effect to the
     * current delay of a running <code>Timer</code>.
     *
     * @param delay the delay of the <code>Timer</code> in milliseconds.
     */
    public void setDelay(int delay) { this.delay = Math.max(100, delay); }

    /**
     * Gets the number of retrievals after the <code>Timer</code> will stop. The result is 0, if there is no
     * limit of retrievals.
     *
     * @return the number of the retrievals.
     */
    public int getCount() { return count; }

    /**
     * Sets the number of retrievals after the <code>Timer</code> will stop. A value &lt;= 0 means,
     * that no limit of retrievals exists and the timer must not stop automatically. This method has no
     * effect to the current count of a running <code>Timer</code>.
     *
     * @param count the number of the retrievals.
     */
    public void setCount(int count) { this.count = Math.max(0, count); }

    /**
     * Gets the number of remaining retrievals after a the running <code>Timer</code> will stop automatically. The result is 0, if
     * the timer isn't running or there is no limit of retrievals.
     *
     * @return the remaining number of the retrievals.
     */
    public int getRemaining() { return isRunning() ? remaining : 0; }
}
