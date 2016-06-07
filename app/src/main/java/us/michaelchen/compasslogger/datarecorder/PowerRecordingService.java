package us.michaelchen.compasslogger.datarecorder;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by ioreyes on 5/27/16.
 */
public class PowerRecordingService extends AbstractRecordingService {
    private static final IntentFilter BATTERY_FILTER =  new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
    private static final String CHARGING_KEY = "charging";
    private static final String CHARGING_USB_KEY = "usbCharging";
    private static final String CHARGING_AC_KEY = "acCharging";
    private static final String CHARGE_LEVEL_KEY = "chargeLevel";
    private static final String TEMPERATURE_KEY = "temperature";
    private static final String VOLTAGE_KEY = "voltage";

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

        int currentChargeLevel = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int maxChargeLevel = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        String charge = String.format("%d / %d", currentChargeLevel, maxChargeLevel);

        int temperature = batteryStatus.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
        int voltage = batteryStatus.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);

        // Figure out the charging state
        boolean isCharging = statusCode == BatteryManager.BATTERY_STATUS_CHARGING ||
                             statusCode == BatteryManager.BATTERY_STATUS_FULL;
        boolean onUSB = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
        boolean onAC = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;

        // Record the data
        Map<String, Object> data = new LinkedHashMap<>();
        data.put(CHARGING_KEY, isCharging);
        data.put(CHARGING_USB_KEY, onUSB);
        data.put(CHARGING_AC_KEY, onAC);
        data.put(CHARGE_LEVEL_KEY, charge);
        data.put(TEMPERATURE_KEY, temperature);
        data.put(VOLTAGE_KEY, voltage);

        return data;
    }
}
