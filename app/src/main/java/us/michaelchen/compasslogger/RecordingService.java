package us.michaelchen.compasslogger;

import android.app.IntentService;
import android.content.Context;
import android.util.Log;

import com.firebase.client.Firebase;

import java.util.Map;

public abstract class RecordingService extends IntentService {

    protected Firebase firebase;
    protected Firebase deviceDb;
    protected String deviceId;
    protected Context context;

    public static final String TIME = "time";
    public static final String USER_DATA_KEY = "userData";
    public static final String TAG = "RecordingService";

    private static final String FIREBASE_URL = "https://luminous-torch-7892.firebaseio.com/";

    public RecordingService() {
        super("RecordingService");
    }

    protected synchronized void initContext(Context context) {
        this.context = context;
        deviceId = getDeviceId();
        Firebase.setAndroidContext(context);
        try {
            firebase = new Firebase(FIREBASE_URL);
            deviceDb = firebase.child(USER_DATA_KEY).child(deviceId);
        } catch (RuntimeException e) {
            e.printStackTrace();
            Log.e(TAG, "Error in firebase", e);
        }
    }

    public String getDeviceId() {
        return MainActivity.deviceId;
    }

    void updateDatabase(String key, Map<String, Object> value) {
        try {
            deviceDb.child(key).push().setValue(value);
        } catch (RuntimeException e) {
            Log.e(TAG, "Update firebase", e);
        }
    }

}
