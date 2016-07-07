package us.michaelchen.compasslogger.periodicservices;

import us.michaelchen.compasslogger.periodicservices.dailysurveys.DailySurveyService;
import us.michaelchen.compasslogger.periodicservices.datarecording.AccelerometerSensorRecordingService;
import us.michaelchen.compasslogger.periodicservices.datarecording.GenericEventRecordingService;
import us.michaelchen.compasslogger.periodicservices.datarecording.GyroscopeSensorRecordingService;
import us.michaelchen.compasslogger.periodicservices.datarecording.HumiditySensorRecordingService;
import us.michaelchen.compasslogger.periodicservices.datarecording.LightSensorRecordingService;
import us.michaelchen.compasslogger.periodicservices.datarecording.LocationRecordingService;
import us.michaelchen.compasslogger.periodicservices.datarecording.MagneticSensorRecordingService;
import us.michaelchen.compasslogger.periodicservices.datarecording.PowerRecordingService;
import us.michaelchen.compasslogger.periodicservices.datarecording.PressureSensorRecordingService;
import us.michaelchen.compasslogger.periodicservices.datarecording.ProximitySensorRecordingService;
import us.michaelchen.compasslogger.periodicservices.datarecording.RotationVectorSensorRecordingService;
import us.michaelchen.compasslogger.periodicservices.datarecording.ScreenRecordingService;
import us.michaelchen.compasslogger.periodicservices.datarecording.StepSensorRecordingService;
import us.michaelchen.compasslogger.periodicservices.datarecording.TemperatureSensorRecordingService;
import us.michaelchen.compasslogger.periodicservices.deadline.DeadlineService;
import us.michaelchen.compasslogger.periodicservices.keepalive.FirebaseKeepAliveService;
import us.michaelchen.compasslogger.periodicservices.keepalive.GenericReceiverKeepAliveService;
import us.michaelchen.compasslogger.periodicservices.keepalive.StepSensorKeepAliveService;

/**
 * Created by ioreyes on 6/20/16.
 */
public class Periodics {
    public static final Class[] ALL = new Class[]{
            // To keep track of when the periodic last happened
            PeriodicTimeUpdateService.class,

            // To keep receivers and low-power sensors alive
            GenericReceiverKeepAliveService.class,
            FirebaseKeepAliveService.class,
            StepSensorKeepAliveService.class,

            // To record various system-broadcasted events
            GenericEventRecordingService.class,

            // To record screen and power state
            ScreenRecordingService.class,
            PowerRecordingService.class,

            // To record various sensor values
            AccelerometerSensorRecordingService.class,
            GyroscopeSensorRecordingService.class,
            HumiditySensorRecordingService.class,
            LightSensorRecordingService.class,
            MagneticSensorRecordingService.class,
            PressureSensorRecordingService.class,
            ProximitySensorRecordingService.class,
            RotationVectorSensorRecordingService.class,
            StepSensorRecordingService.class,
            TemperatureSensorRecordingService.class,

            // For the dailysurveys package.
            DailySurveyService.class,
            // For the deadline package.
            DeadlineService.class,
    };

    public static final Class[] KEEP_ALIVE = new Class[] {
            GenericReceiverKeepAliveService.class,
            FirebaseKeepAliveService.class,
            StepSensorKeepAliveService.class,
    };
}
