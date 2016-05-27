package us.michaelchen.compasslogger.datarecorder;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ioreyes on 5/27/16.
 */
public class PowerRecordingService extends AbstractRecordingService {
    private static final IntentFilter BATTERY_FILTER =  new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
    private static final String CHARGING_KEY = "charging";
    private static final String CHARGING_USB_KEY = "usbCharging";
    private static final String CHARGING_AC_KEY = "acCharging";

    public PowerRecordingService() {
        super("PowerRecordingService");
    }

    @Override
    protected String broadcastKey() {
        return "power";
    }

    @Override
    protected Map<String, Object> readData(Intent intent) {
        // Request battery status information
        Intent batteryStatus = registerReceiver(null, BATTERY_FILTER);
        int statusCode = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        int chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);

        // Figure out the charging state
        boolean isCharging = statusCode == BatteryManager.BATTERY_STATUS_CHARGING ||
                statusCode == BatteryManager.BATTERY_STATUS_FULL;
        boolean onUSB = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
        boolean onAC = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;

        // Record the data
        Map<String, Object> vals = new HashMap<>();
        vals.put(CHARGING_KEY, isCharging);
        vals.put(CHARGING_USB_KEY, onUSB);
        vals.put(CHARGING_AC_KEY, onAC);

        return vals;
    }
}
