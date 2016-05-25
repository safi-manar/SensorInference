package us.michaelchen.compasslogger.sensor;

import android.app.IntentService;
import android.content.Context;
import android.util.Log;

import com.firebase.client.Firebase;

import java.util.Map;

import us.michaelchen.compasslogger.R;
import us.michaelchen.compasslogger.utils.DeviceID;

public abstract class RecordingService extends IntentService {

    protected Firebase deviceDb;
    protected String deviceId;
    protected Context context;

    public static final String TIME = "time";
    public static final String USER_DATA_KEY = "userData";
    public static final String TAG = "RecordingService";

    private static String firebaseUrl = null;

    public RecordingService() {
        super("RecordingService");
    }

    protected synchronized void initContext(Context context) {
        this.context = context;
        try {

        } catch (RuntimeException e) {
            e.printStackTrace();
            Log.e(TAG, "Error in firebase", e);
            Log.d(TAG, "child: " + USER_DATA_KEY + " : child: " + deviceId);
        }
    }

    void updateDatabase(String key, Map<String, Object> value) {
        if(firebaseUrl == null) {
            firebaseUrl = getResources().getString(R.string.firebase_url);
        }

        try {
            Firebase.setAndroidContext(this.getApplication());
            final Firebase firebase = new Firebase(firebaseUrl);
            deviceId = DeviceID.get(this);
            deviceDb = firebase.child(USER_DATA_KEY).child(deviceId);
            deviceDb.child(key).push().setValue(value);
        } catch (RuntimeException e) {
            Log.e(TAG, "Update firebase", e);
        }
    }

}
