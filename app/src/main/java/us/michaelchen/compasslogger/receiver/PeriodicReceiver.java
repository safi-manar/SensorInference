package us.michaelchen.compasslogger.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import us.michaelchen.compasslogger.datarecorder.GenericEventRecordingService;
import us.michaelchen.compasslogger.datarecorder.LightSensorRecordingService;
import us.michaelchen.compasslogger.datarecorder.LocationRecordingService;
import us.michaelchen.compasslogger.datarecorder.PowerRecordingService;
import us.michaelchen.compasslogger.datarecorder.ProximitySensorRecordingService;
import us.michaelchen.compasslogger.datarecorder.RotationSensorRecordingService;
import us.michaelchen.compasslogger.datarecorder.ScreenRecordingService;
import us.michaelchen.compasslogger.datarecorder.StepSensorRecordingService;
import us.michaelchen.compasslogger.deadline.DeadlineService;
import us.michaelchen.compasslogger.stepkeepalive.StepSensorKeepAliveService;
import us.michaelchen.compasslogger.utils.MasterSwitch;

/**
 * Receiver used for periodic sensor polling
 */
public class PeriodicReceiver extends BroadcastReceiver {
    private static final Class[] PERIODICS = new Class[] {
            // Keep the step counter is alive at each periodic
            StepSensorKeepAliveService.class,

            GenericEventRecordingService.class,

            ScreenRecordingService.class,
            PowerRecordingService.class,

            LightSensorRecordingService.class,
            ProximitySensorRecordingService.class,
            RotationSensorRecordingService.class,
            StepSensorRecordingService.class,

            // This requires additional permissions from the user
            LocationRecordingService.class,

            // For the deadline package.
            DeadlineService.class
    };

    @Override
    public void onReceive(Context context, Intent intent) {
        for(Class c : PERIODICS) {
            context.startService(new Intent(context, c));
        }

        // Now that the services have recorded new updates,
        // update the timestamp to reflect the latest update.
        MasterSwitch.updateTimeStamp(context);
    }
}
