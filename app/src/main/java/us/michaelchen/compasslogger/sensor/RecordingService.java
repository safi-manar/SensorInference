package us.michaelchen.compasslogger.sensor;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.firebase.client.Firebase;

import java.util.Map;
import java.util.UUID;

public abstract class RecordingService extends IntentService {

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

    @Override
    protected void onHandleIntent(Intent intent) {

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

    private String deviceId() {
        final TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        final String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String deviceId = deviceUuid.toString();
        return deviceId;
    }

    void updateDatabase(String key, Map<String, Object> value) {
        try {
            Firebase.setAndroidContext(this.getApplication());
            final Firebase firebase = new Firebase(FIREBASE_URL);
            deviceId = deviceId();          // TODO Replace deviceId() with DeviceID.get(this) to prevent future device lookups
            deviceDb = firebase.child(USER_DATA_KEY).child(deviceId);
            deviceDb.child(key).push().setValue(value);
        } catch (RuntimeException e) {
            Log.e(TAG, "Update firebase", e);
        }
    }

}
