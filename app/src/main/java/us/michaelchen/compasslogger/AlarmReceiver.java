package us.michaelchen.compasslogger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import us.michaelchen.compasslogger.sensor.LightSensorService;
import us.michaelchen.compasslogger.sensor.LocationRecordingService;
import us.michaelchen.compasslogger.sensor.PowerRecordingService;
import us.michaelchen.compasslogger.sensor.ProximitySensorService;
import us.michaelchen.compasslogger.sensor.StepCountService;

public class AlarmReceiver extends BroadcastReceiver {
    public static final String TAG = "AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        // Location Service
        Intent i = new Intent(context, LocationRecordingService.class);
        context.startService(i);

        i = new Intent(context, PowerRecordingService.class);
        context.startService(i);

        i = new Intent(context, LightSensorService.class);
        context.startService(i);

        i = new Intent(context, ProximitySensorService.class);
        context.startService(i);

        i = new Intent(context, StepCountService.class);
        context.startService(i);
    }
}
