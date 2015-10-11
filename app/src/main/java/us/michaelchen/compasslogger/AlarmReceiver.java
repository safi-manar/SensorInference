package us.michaelchen.compasslogger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
import android.telephony.TelephonyManager;

import com.firebase.client.Firebase;

import java.util.UUID;

public class AlarmReceiver extends BroadcastReceiver {
    public static final String TAG = "AlarmReceiver";
    protected Firebase firebase;
    protected String deviceId;
    protected Context context;

    public static final String TIME = "time";
    public static final String USER_DATA_KEY = "userData";

    private static final String FIREBASE_URL = "https://luminous-torch-7892.firebaseio.com/";

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        Firebase.setAndroidContext(context);
        firebase = new Firebase(FIREBASE_URL);
        deviceId = getDeviceId();
    }

    public String getDeviceId() {
        final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        final String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String deviceId = deviceUuid.toString();
        return deviceId;
    }
}
