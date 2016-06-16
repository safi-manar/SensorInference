package us.michaelchen.compasslogger.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.Toast;

import us.michaelchen.compasslogger.datarecorder.DeviceSpecsRecordingService;
import us.michaelchen.compasslogger.receiver.GenericIntentReceiver;
import us.michaelchen.compasslogger.receiver.PeriodicReceiver;

/**
 * Created by ioreyes on 6/2/16.
 */
public class MasterSwitch {
    // Used by periodics
    private static PendingIntent periodicIntent = null;

    // Used by asynchronous
    private static final String[] ASYNCHRONOUS_EVENTS = {
            Intent.ACTION_SCREEN_OFF,
            Intent.ACTION_SCREEN_ON,
            Intent.ACTION_APPLICATION_RESTRICTIONS_CHANGED,
            Intent.ACTION_APP_ERROR,
            Intent.ACTION_BATTERY_LOW,
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_CAMERA_BUTTON,
            Intent.ACTION_CLOSE_SYSTEM_DIALOGS,
            Intent.ACTION_DEVICE_STORAGE_LOW,
            Intent.ACTION_HEADSET_PLUG,
            Intent.ACTION_PACKAGE_ADDED,
            Intent.ACTION_PACKAGE_CHANGED,
            Intent.ACTION_SYNC,
            Intent.ACTION_USER_PRESENT,
            Intent.ACTION_AIRPLANE_MODE_CHANGED,
            Intent.ACTION_POWER_CONNECTED,
            Intent.ACTION_POWER_DISCONNECTED,
            Intent.ACTION_SHUTDOWN,
    };
    private static final GenericIntentReceiver GENERIC_INTENT_RECEIVER = new GenericIntentReceiver();

    // Used by the step counter
    private static final SensorEventListener DO_NOTHING_LISTENER = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            // Do nothing, just keep the sensor alive
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // Do nothing, just keep the sensor alive
        }
    };

    private static Context app = null;

    /**
     * Turns on all the data collection services
     * @param c Calling Android context
     */
    public static void on(Context c) {
        // Associate all spawned services and receivers to the Application,
        // not component Activities and Services (which have much shorter lifespans)
        if(app == null) {
            app = c.getApplicationContext();
        }

        if(!isRunning()) {
            if(PreferencesWrapper.isFirstRun()) {
                recordDeviceSpecs();
                // Now, update the FIRST_RUN check.
                PreferencesWrapper.setFirstRun();
            }

            startStepCounter();
            startAsynchronous();
            startPeriodics();
        }
    }

    /**
     * Turns off all the data collection services
     * @param c Calling Android context
     */
    public static void off(Context c) {
        // Associate all spawned services and receivers to the Application,
        // not component Activities and Services (which have much shorter lifespans)
        if(app == null) {
            app = c.getApplicationContext();
        }

        if(isRunning()) {
            stopStepCounter();
            stopAsynchronous();
            stopPeriodics();

            // Reset the timestamp for future iterations
            // to assume a MasterSwitch.On() alarm reset.
            PreferencesWrapper.resetLastAlarmTimestamp();
        }
    }

    /**
     *
     * @return True if the data collection services are active by
     * checking whether the current time is within a safe-factored
     * PERIODIC_LENGTH of the previous time. In other words, if the
     * alarms are active, then the previous time stamp should be
     * greater than the safe (110%) interval of the PERIODIC_LENGTH.
     * Will logically return false when prevTimeStamp = 0.
     */
    public static boolean isRunning() {
        long prevTimeStamp = PreferencesWrapper.getLastAlarmTimestamp();
        long currentTimeStamp = System.currentTimeMillis();
        long interval = currentTimeStamp - prevTimeStamp;

        // The service is considered to still be running if the last periodic was within
        // the last period length (plus a TimeConstants.PERIODIC_SAFE_FACTOR tolerance)
        return interval < TimeConstants.PERIODIC_SAFE_INTERVAL;
    }

    /**
     * Start periodic events
     */
    private static void startPeriodics() {
        if(periodicIntent == null) {
            Intent alarmIntent = new Intent(app, PeriodicReceiver.class);
            periodicIntent = PendingIntent.getBroadcast(app, 0, alarmIntent, 0);
        }

        AlarmManager manager = (AlarmManager) app.getSystemService(Context.ALARM_SERVICE);
        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis(),
                TimeConstants.PERIODIC_LENGTH,
                periodicIntent);

        Toast.makeText(app, "Alarms Set", Toast.LENGTH_SHORT).show();

        // Note that the periodic started at this time
        PreferencesWrapper.updateLastAlarmTimestamp();
    }

    /**
     * Stop periodic events
     */
    private static void stopPeriodics() {
        if(periodicIntent != null) {
            AlarmManager manager = (AlarmManager) app.getSystemService(Context.ALARM_SERVICE);
            manager.cancel(periodicIntent);
        }
    }

    /**
     * Start listening to asynchronous events associated with intents
     */
    private static void startAsynchronous() {
        IntentFilter filter = new IntentFilter();
        for (String event : ASYNCHRONOUS_EVENTS) {
            filter.addAction(event);
        }

        // Try to unregister the receiver in case the phone sleeps
        // and MainActivity.onCreate() is called again, leading to
        // a new MasterSwitch.on call.
        try {
            app.unregisterReceiver(GENERIC_INTENT_RECEIVER);
        } catch (IllegalArgumentException e) {
            // We deliberately do nothing in the catch block.
        }

        app.registerReceiver(GENERIC_INTENT_RECEIVER, filter);
    }

    /**
     * Stop listening to asynchronous events
     */
    private static void stopAsynchronous() {
        app.unregisterReceiver(GENERIC_INTENT_RECEIVER);
    }

    /**
     * Activate the step sensor
     */
    private static void startStepCounter() {
        SensorManager sensorManager = (SensorManager) app.getSystemService(Context.SENSOR_SERVICE);
        Sensor stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        sensorManager.registerListener(DO_NOTHING_LISTENER, stepSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    /**
     * Deactivate the step sensor
     */
    private static void stopStepCounter() {
        SensorManager sensorManager = (SensorManager) app.getSystemService(Context.SENSOR_SERVICE);

        sensorManager.unregisterListener(DO_NOTHING_LISTENER);
    }

    /**
     * Launches service to get device hardware/software information
     */
    private static void recordDeviceSpecs() {
        Intent intent = new Intent(app, DeviceSpecsRecordingService.class);
        app.startService(intent);
    }
}
