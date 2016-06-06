package us.michaelchen.compasslogger.utils;

import android.content.Context;

import com.firebase.client.Firebase;

import java.util.Map;

import us.michaelchen.compasslogger.R;

/**
 * Created by ioreyes on 6/6/16.
 */
public class FirebaseWrapper {
    private static final String USER_DATA_KEY = "userData";
    private static final String TIME_KEY = "submitTime";

    private static String dbURL = null;
    private static Firebase deviceDb = null;
    private static String deviceId = null;

    private static boolean isInit = false;

    /**
     * Initialize the Firebase connection
     * @param c Android context
     */
    public static void init(Context c) {
        if(!isInit) {
            Firebase.setAndroidContext(c);

            dbURL = c.getString(R.string.firebase_url);
            deviceId = DeviceID.get(c);

            Firebase db = new Firebase(dbURL);
            deviceDb = db.child(USER_DATA_KEY).child(deviceId);

            isInit = true;
        }
    }

    /**
     *
     * @return True if the Firebase connection is initialized
     */
    public static boolean isInit() {
        return isInit;
    }

    /**
     *
     * @return A handle to this device's entry in the Firebase backend
     */
    public static Firebase getDb() {
        return deviceDb;
    }

    /**
     *
     * @return The URL to the Firebase backend
     */
    public static String getURL() {
        return dbURL;
    }

    /**
     * Push the data to the Firebase backend.
     * @param key Identifying key
     * @param data Label-value mapping of data to be submitted
     */
    public static void push(String key, Map<String, Object> data) {
        if(isInit) {
            // Add time data if it's not present
            if(!data.containsKey(TIME_KEY)) {
                data.put(TIME_KEY, DataTimeFormat.current());
            }

            // Push to database
            deviceDb.child(key).push().setValue(data);
        }
    }
}
