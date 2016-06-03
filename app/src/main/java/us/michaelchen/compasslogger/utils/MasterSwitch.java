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
    private static boolean firstRun = true;
    private static boolean running = false;

    // Used by periodics
    private static final int BROADCAST_MINUTES = 1;
    private static final int BROADCAST_PERIOD = 1000 * 60 * BROADCAST_MINUTES;
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
    private static GenericIntentReceiver genericIntentReceiver = null;

    // Used by step counter
    private static SensorManager sensorManager = null;
    private static SensorEventListener STEP_LISTENER = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            // Do nothing, just keep the sensor alive
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // Do nothing, just keep the sensor alive
        }
    };

    /**
     * Turns on all the data collection services
     * @param c Calling Android context
     */
    public static void on(Context c) {
        if(!running) {
            if(firstRun) {
                recordDeviceSpecs(c);
                firstRun = false;
            }

            startStepCounter(c);
            startAsynchronous(c);
            startPeriodics(c);

            running = true;
        }
    }

    /**
     * Turns off all the data collection services
     * @param c Calling Android context
     */
    public static void off(Context c) {
        if(running) {
            stopStepCounter(c);
            stopAsynchronous(c);
            stopPeriodics(c);

            running = false;
        }
    }

    /**
     *
     * @return True if the data collection services are active
     */
    public static boolean isRunning() {
        return running;
    }

    /**
     * Start periodic events
     * @param c Calling Android context
     */
    private static void startPeriodics(Context c) {
        if(periodicIntent == null) {
            Intent alarmIntent = new Intent(c, PeriodicReceiver.class);
            periodicIntent = PendingIntent.getBroadcast(c, 0, alarmIntent, 0);
        }

        AlarmManager manager = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis(),
                BROADCAST_PERIOD,
                periodicIntent);

        Toast.makeText(c, "Alarms Set", Toast.LENGTH_SHORT).show();
    }

    /**
     * Stop periodic events
     * @param c Calling Android context
     */
    private static void stopPeriodics(Context c) {
        if(periodicIntent != null) {
            AlarmManager manager = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
            manager.cancel(periodicIntent);
        }
    }

    /**
     * Start listening to asynchronous events associated with intents
     * @param c Calling Android context
     */
    private static void startAsynchronous(Context c) {
        if(genericIntentReceiver  == null) {
            genericIntentReceiver = new GenericIntentReceiver();
        }

        IntentFilter filter = new IntentFilter();
        for (String event : ASYNCHRONOUS_EVENTS) {
            filter.addAction(event);
        }
        c.registerReceiver(genericIntentReceiver, filter);
    }

    /**
     * Stop listening to asynchronous events
     * @param c Calling Android context
     */
    private static void stopAsynchronous(Context c) {
        if(genericIntentReceiver != null) {
            c.unregisterReceiver(genericIntentReceiver);
        }
    }

    /**
     * Activate the step sensor
     * @param c Calling Android context
     */
    private static void startStepCounter(Context c) {
        if(sensorManager == null) {
            sensorManager = (SensorManager) c.getSystemService(Context.SENSOR_SERVICE);
        }

        Sensor stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        sensorManager.registerListener(STEP_LISTENER, stepSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    /**
     * Deactivate the step sensor
     * @param c Calling Android context
     */
    private static void stopStepCounter(Context c) {
        if(sensorManager == null) {
            sensorManager = (SensorManager) c.getSystemService(Context.SENSOR_SERVICE);
        }

        sensorManager.unregisterListener(STEP_LISTENER);
    }

    /**
     * Launches service to get device hardware/software information
     * @param c Calling Android context
     */
    private static void recordDeviceSpecs(Context c) {
        Intent intent = new Intent(c, DeviceSpecsRecordingService.class);
        c.startService(intent);
    }
}
