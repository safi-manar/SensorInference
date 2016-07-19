package edu.berkeley.icsi.sensormonitor.periodicservices.datadestination;

import java.util.Map;

/**
 * Created by ioreyes on 6/23/16.
 */
public abstract class AbstractDataDestination {

    /**
     * Submits data to a destination (e.g., database, file, network resource, etc.)
     * @param label Label describing this data submission
     * @param data Key/value pairs of labeled data
     */
    public abstract void submit(String label, Map<String, Object> data);
}
