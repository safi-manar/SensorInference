package edu.berkeley.icsi.sensormonitor.periodicservices.datarecording;

import android.app.IntentService;
import android.content.Intent;

import java.util.Map;

import edu.berkeley.icsi.sensormonitor.periodicservices.datadestination.AbstractDataDestination;
import edu.berkeley.icsi.sensormonitor.periodicservices.datadestination.FirebaseDestination;

/**
 * Created by ioreyes on 5/24/16.
 */
public abstract class AbstractRecordingService extends IntentService {
    protected static final String TIMESTAMP_KEY = "timestamp";
    protected static final String READABLE_TIME_KEY = "timeReadable";

    protected String tag = null;

    protected AbstractRecordingService(String subclassName) {
        super(subclassName);
        tag = subclassName;
    }

    @Override
    protected final void onHandleIntent(Intent intent) {
        String label = broadcastKey();
        Map<String, Object> data = readData(intent);

        getDataDestination().submit(label, data);
    }

    /**
     * Defines the unique identifying broadcast key for a given data soure
     * @return The unique identifying key
     */
    protected abstract String broadcastKey();

    /**
     * Reads the system data requested by the intent
     * @param intent
     * @return A map of labeled readouts from the data source
     */
    protected abstract Map<String, Object> readData(Intent intent);

    /**
     * Defines where recorded data will be saved (override in subclasses to change)
     * @return An object representing where data will be saved
     */
    protected AbstractDataDestination getDataDestination() {
        return FirebaseDestination.DESTINATION;
    }
}
