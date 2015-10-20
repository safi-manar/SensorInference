package us.michaelchen.compasslogger;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import com.firebase.client.Firebase;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by michael on 10/13/15.
 */
public class PowerAlarmReceiver extends AlarmReceiver {

    public static final String CHARGING_KEY = "charging";
    public static final String CHARGING_USB_KEY = "usbCharging";
    public static final String CHARGING_AC_KEY = "acCharging";
    public static final String POWER_KEY = "power";

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

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
        Firebase dataRef = firebase.child(USER_DATA_KEY).child(deviceId);
        Firebase powerRef = dataRef.child(POWER_KEY);
        powerRef.push().setValue(map);

    }
}
