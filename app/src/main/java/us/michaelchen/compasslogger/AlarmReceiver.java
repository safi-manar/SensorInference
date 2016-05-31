package us.michaelchen.compasslogger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import us.michaelchen.compasslogger.datarecorder.LightSensorRecordingService;
import us.michaelchen.compasslogger.datarecorder.LocationRecordingService;
import us.michaelchen.compasslogger.datarecorder.ProximitySensorRecordingService;
import us.michaelchen.compasslogger.datarecorder.RotationSensorRecordingService;
import us.michaelchen.compasslogger.datarecorder.StepSensorRecordingService;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, LightSensorRecordingService.class));
        context.startService(new Intent(context, ProximitySensorRecordingService.class));
        context.startService(new Intent(context, RotationSensorRecordingService.class));
        context.startService(new Intent(context, StepSensorRecordingService.class));

        // This requires permissions from the user
        context.startService(new Intent(context, LocationRecordingService.class));
    }
}
