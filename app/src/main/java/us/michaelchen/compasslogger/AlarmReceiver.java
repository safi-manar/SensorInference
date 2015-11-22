package us.michaelchen.compasslogger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

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

    }
}
