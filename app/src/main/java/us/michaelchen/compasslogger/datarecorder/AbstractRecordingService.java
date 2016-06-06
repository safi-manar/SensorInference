package us.michaelchen.compasslogger.datarecorder;

import android.app.IntentService;
import android.content.Intent;

import com.firebase.client.Firebase;

import java.util.Map;

import us.michaelchen.compasslogger.utils.DataTimeFormat;
import us.michaelchen.compasslogger.utils.FirebaseWrapper;

/**
 * Created by ioreyes on 5/24/16.
 */
public abstract class AbstractRecordingService extends IntentService {
    private static final String TIME_KEY = "submitTime";

    private static Firebase deviceDb = null;

    protected String tag = null;

    protected AbstractRecordingService(String subclassName) {
        super(subclassName);
        tag = subclassName;

        if(deviceDb == null) {
            deviceDb = FirebaseWrapper.getDb();
        }
    }

    @Override
    protected final void onHandleIntent(Intent intent) {
        updateDatabase(readData(intent));
    }

    /**
     * Push the key-value pair to the database
     * @param value
     */
    private void updateDatabase(Map<String, Object> value) {
        if(value != null) {
            // Add time data if it's not present
            if(!value.containsKey(TIME_KEY)) {
                value.put(TIME_KEY, DataTimeFormat.current());
            }

            // Push to database
            deviceDb.child(broadcastKey()).push().setValue(value);
        }
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
}
