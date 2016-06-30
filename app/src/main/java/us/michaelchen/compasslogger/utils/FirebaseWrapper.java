package us.michaelchen.compasslogger.utils;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;


public class FirebaseWrapper {
    private static final String USER_DATA_KEY = "userData";

    private static String dbURL = null;
    private static DatabaseReference deviceDb = null;
    private static String deviceId = null;

    private static boolean isInit = false;

    /**
     * Initialize the Firebase connection
     */
    public static void init() {
        if(!isInit) {
            dbURL = PreferencesWrapper.getDbAddress();
            deviceId = PreferencesWrapper.getDeviceID();

            FirebaseDatabase db = FirebaseDatabase.getInstance();
            db.setPersistenceEnabled(true);
            DatabaseReference dbRef = db.getReferenceFromUrl(dbURL);
            deviceDb = dbRef.child(USER_DATA_KEY).child(deviceId);

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
    public static DatabaseReference getDb() {
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
