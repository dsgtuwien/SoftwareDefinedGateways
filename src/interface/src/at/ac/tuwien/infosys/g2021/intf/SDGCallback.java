package at.ac.tuwien.infosys.g2021.intf;

import at.ac.tuwien.infosys.g2021.common.SimpleData;
import java.util.Map;

/**
 * All classes implementing this interface are notified in constant time intervals.
 *
 * @see DataPoint#addCallback(long, SDGCallback...)
 * @see DataPoint#removeCallback(SDGCallback...)
 */
public interface SDGCallback {

    /**
     * Time is elapsed.
     *
     * @param dataPoint the current data point
     * @param data      the current buffer values
     */
    public void onTimeout(DataPoint dataPoint, Map<String, SimpleData> data);
}

