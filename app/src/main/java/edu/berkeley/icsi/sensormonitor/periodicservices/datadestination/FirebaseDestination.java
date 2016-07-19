package edu.berkeley.icsi.sensormonitor.periodicservices.datadestination;

import java.util.Map;

import edu.berkeley.icsi.sensormonitor.utils.FirebaseWrapper;

/**
 * Created by ioreyes on 6/23/16.
 */
public class FirebaseDestination extends AbstractDataDestination {
    public static final FirebaseDestination DESTINATION = new FirebaseDestination();

    @Override
    public void submit(String label, Map<String, Object> data) {
        if(FirebaseWrapper.isInit() && data != null) {
            FirebaseWrapper.push(label, data);
        }
    }
}
