package at.ac.tuwien.infosys.g2021.intf;

import at.ac.tuwien.infosys.g2021.common.SimpleData;
import java.util.Spliterator;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

/**
 * This is a blocking queue, with a split-iterator, that blocks until the next element is
 * put into the queue. The split-iterator signals an end of input data, if
 * <ul>
 * <li>a dummy-<tt>{@link SimpleData}</tt> is found in the queue. This is done, when the
 * <tt>{@link DataPoint}</tt> is released or
 * </li>
 * <li>when the thread, which is executing the stream is interrupted. In this case the interrupt-
 * flag is not reset!
 * </li>
 * </ul>
 */
class ValueQueue extends LinkedBlockingQueue<SimpleData> {

    /** Initialization of the queue. */
    ValueQueue() { super(); }

    /**
     * Returns a {@link java.util.Spliterator} over the elements in this queue.
     *
     * @return a {@code Spliterator} over the elements in this queue
     */
    @Override
    public Spliterator<SimpleData> spliterator() { return new BlockingSplitIterator(this); }

    /** The iterators implementation. */
    private static class BlockingSplitIterator implements Spliterator<SimpleData> {

        // The queue
        private ValueQueue queue;

        /** Initialization.
         * @param queue The new queue
         * */
        BlockingSplitIterator(ValueQueue queue) { this.queue = queue; }

        /**
         * Returns a set of characteristics of this Spliterator.
         *
         * @return a representation of characteristics
         */
        @Override
        public int characteristics() { return CONCURRENT | NONNULL | ORDERED; }

        /**
         * Returns an estimate of the number of elements.
         *
         * @return the estimated size
         */
        @Override
        public long estimateSize() { return Long.MAX_VALUE; }

        /**
         * If a remaining element exists, performs the given action on it,
         * returning {@code true}; else returns {@code false}.
         *
         * @param action The action
         *
         * @return {@code false} if no remaining elements existed
         *         upon entry to this method, else {@code true}.
         *
         * @throws NullPointerException if the specified action is null
         */
        @Override
        public boolean tryAdvance(Consumer<? super SimpleData> action) {

            if (action == null) throw new NullPointerException("no action");

            try {
                SimpleData nextOne = queue.take();

                if (nextOne != null && !nextOne.isDummy()) {
                    action.accept(nextOne);
                    return true;
                }
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                // We signal the end of input data returning false.
            }

            return false;
        }

        /**
         * If this spliterator can be partitioned, returns a Spliterator
         * covering elements, that will, upon return from this method, not
         * be covered by this Spliterator.
         *
         * @return a {@code Spliterator} covering some portion of the
         *         elements, or {@code null} if this spliterator cannot be split
         */
        @Override
        public Spliterator<SimpleData> trySplit() { return null; }
    }
}
