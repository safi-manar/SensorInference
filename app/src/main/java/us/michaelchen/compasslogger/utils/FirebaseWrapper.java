package us.michaelchen.compasslogger.utils;

import android.content.Context;

import com.firebase.client.Firebase;

import java.util.Map;

import us.michaelchen.compasslogger.R;


public class FirebaseWrapper {
    private static final String USER_DATA_KEY = "userData";

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
            deviceId = PreferencesWrapper.getDeviceID();

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
            // Pushes to the Firebase with a hierarchy as follows:
            /*
            *   UUID
            *       Sensor
            *           TimeStamp
            *               DataEntry.
            * */
            String timeStamp = DataTimeFormat.current();
            deviceDb.child(key).child(timeStamp).setValue(data);

        }
    }
}
