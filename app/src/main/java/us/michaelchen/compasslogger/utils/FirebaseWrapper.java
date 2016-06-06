package us.michaelchen.compasslogger.utils;

import android.content.Context;

import com.firebase.client.Firebase;

import us.michaelchen.compasslogger.R;

/**
 * Created by ioreyes on 6/6/16.
 */
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
}
