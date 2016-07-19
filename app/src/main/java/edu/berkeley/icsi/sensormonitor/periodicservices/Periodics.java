package edu.berkeley.icsi.sensormonitor.periodicservices;

import edu.berkeley.icsi.sensormonitor.periodicservices.dailysurveys.DailySurveyService;
import edu.berkeley.icsi.sensormonitor.periodicservices.datarecording.AccelerometerSensorRecordingService;
import edu.berkeley.icsi.sensormonitor.periodicservices.datarecording.GenericEventRecordingService;
import edu.berkeley.icsi.sensormonitor.periodicservices.datarecording.GyroscopeSensorRecordingService;
import edu.berkeley.icsi.sensormonitor.periodicservices.datarecording.HumiditySensorRecordingService;
import edu.berkeley.icsi.sensormonitor.periodicservices.datarecording.LightSensorRecordingService;
import edu.berkeley.icsi.sensormonitor.periodicservices.datarecording.MagneticSensorRecordingService;
import edu.berkeley.icsi.sensormonitor.periodicservices.datarecording.PowerRecordingService;
import edu.berkeley.icsi.sensormonitor.periodicservices.datarecording.PressureSensorRecordingService;
import edu.berkeley.icsi.sensormonitor.periodicservices.datarecording.ProximitySensorRecordingService;
import edu.berkeley.icsi.sensormonitor.periodicservices.datarecording.RotationVectorSensorRecordingService;
import edu.berkeley.icsi.sensormonitor.periodicservices.datarecording.ScreenRecordingService;
import edu.berkeley.icsi.sensormonitor.periodicservices.datarecording.StepSensorRecordingService;
import edu.berkeley.icsi.sensormonitor.periodicservices.datarecording.TemperatureSensorRecordingService;
import edu.berkeley.icsi.sensormonitor.periodicservices.deadline.DeadlineService;
import edu.berkeley.icsi.sensormonitor.periodicservices.keepalive.FirebaseKeepAliveService;
import edu.berkeley.icsi.sensormonitor.periodicservices.keepalive.GenericReceiverKeepAliveService;
import edu.berkeley.icsi.sensormonitor.periodicservices.keepalive.StepSensorKeepAliveService;

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
