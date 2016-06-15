package us.michaelchen.compasslogger.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import us.michaelchen.compasslogger.datarecorder.AccelerometerSensorRecordingService;
import us.michaelchen.compasslogger.datarecorder.GenericEventRecordingService;
import us.michaelchen.compasslogger.datarecorder.GyroscopeSensorRecordingService;
import us.michaelchen.compasslogger.datarecorder.LightSensorRecordingService;
import us.michaelchen.compasslogger.datarecorder.LocationRecordingService;
import us.michaelchen.compasslogger.datarecorder.MagneticSensorRecordingService;
import us.michaelchen.compasslogger.datarecorder.PowerRecordingService;
import us.michaelchen.compasslogger.datarecorder.ProximitySensorRecordingService;
import us.michaelchen.compasslogger.datarecorder.ScreenRecordingService;
import us.michaelchen.compasslogger.datarecorder.StepSensorRecordingService;
import us.michaelchen.compasslogger.deadline.DeadlineService;
import us.michaelchen.compasslogger.periodicupdate.PeriodicTimeUpdateService;
import us.michaelchen.compasslogger.stepkeepalive.StepSensorKeepAliveService;

/**
 * Receiver used for periodic sensor polling
 */
public class PeriodicReceiver extends BroadcastReceiver {
    private static final Class[] PERIODICS = new Class[] {
            // To keep track of when the periodic last happened
            PeriodicTimeUpdateService.class,

            // Keep the step counter is alive at each periodic
            StepSensorKeepAliveService.class,

            // To record various system-broadcasted events
            GenericEventRecordingService.class,

            // To record screen and power state
            ScreenRecordingService.class,
            PowerRecordingService.class,

            // To record various sensor values
            AccelerometerSensorRecordingService.class,
            GyroscopeSensorRecordingService.class,
            LightSensorRecordingService.class,
            MagneticSensorRecordingService.class,
            ProximitySensorRecordingService.class,
            StepSensorRecordingService.class,

            // This requires additional permissions from the user
            LocationRecordingService.class,

            // For the deadline package.
            DeadlineService.class,
    };

    @Override
    public void onReceive(Context context, Intent intent) {
        for(Class c : PERIODICS) {
            context.startService(new Intent(context, c));
        }
    }
}
