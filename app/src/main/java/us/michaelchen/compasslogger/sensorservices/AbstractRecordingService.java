package us.michaelchen.compasslogger.sensorservices;

import android.app.IntentService;

import com.firebase.client.Firebase;

import java.util.Date;
import java.util.Map;

import us.michaelchen.compasslogger.R;
import us.michaelchen.compasslogger.utils.DeviceID;

/**
 * Created by ioreyes on 5/24/16.
 */
public abstract class AbstractRecordingService extends IntentService {
    private static final String USER_DATA_KEY = "userData";
    private static final String TIME_KEY = "time";

    private static String deviceId = null;
    private static Firebase deviceDb = null;


    protected AbstractRecordingService(String subclassName) {
        super(subclassName);

        init();
    }

    /**
     * Initialize the device ID and database connection
     */
    private void init() {
        // Get the installation-persistent random device ID
        if(deviceId == null) {
            deviceId = DeviceID.get(this);
        }

        // Set up the database connection
        if(deviceDb == null) {
            String dbURL = getResources().getString(R.string.firebase_url);
            Firebase fb = new Firebase(dbURL);
            deviceDb = fb.child(USER_DATA_KEY).child(deviceId);
        }
    }

    /**
     * Push the key-value pair to the database
     * @param value
     */
    protected final void updateDatabase(Map<String, Object> value) {
        // Add time data if it's not present:w
        if(!value.containsKey(TIME_KEY)) {
            value.put(TIME_KEY, new Date().toString());
        }

        // Push to database
        deviceDb.child(broadcastKey()).push().setValue(value);
    }

    /**
     * Defines the unique identifying broadcast key for a given sensor service
     * @return The unique identifying key
     */
    protected abstract String broadcastKey();
}
