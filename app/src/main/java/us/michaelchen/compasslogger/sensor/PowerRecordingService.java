package us.michaelchen.compasslogger.sensor;

import android.app.IntentService;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class PowerRecordingService extends RecordingService {
    public static final String CHARGING_KEY = "charging";
    public static final String CHARGING_USB_KEY = "usbCharging";
    public static final String CHARGING_AC_KEY = "acCharging";
    public static final String POWER_KEY = "power";
    public static final String TAG = "PowerService";

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            super.initContext(this);
            IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = context.registerReceiver(null, ifilter);
            int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL;
            int chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
            boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
            boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
            Map<String, Object> map = new HashMap<>();
            map.put(CHARGING_KEY, isCharging);
            map.put(CHARGING_USB_KEY, usbCharge);
            map.put(CHARGING_AC_KEY, acCharge);
            map.put(TIME, new Date().toString());

            // add to firebase entry
            updateDatabase(POWER_KEY, map);
//            Firebase dataRef = firebase.child(USER_DATA_KEY).child(deviceId);
//            Firebase ref = dataRef.child(POWER_KEY);
//            ref.push().setValue(map);
        }
    }

}
