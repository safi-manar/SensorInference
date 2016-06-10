package us.michaelchen.compasslogger.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.Toast;

import us.michaelchen.compasslogger.datarecorder.DeviceSpecsRecordingService;
import us.michaelchen.compasslogger.receiver.GenericIntentReceiver;
import us.michaelchen.compasslogger.receiver.PeriodicReceiver;
import us.michaelchen.compasslogger.stepkeepalive.StepSensorKeepAliveService;

/**
 * Created by ioreyes on 6/2/16.
 */
public class MasterSwitch {

    private static SharedPreferences prefs = null;
    private static final String PREFS_NAME = "CompassLoggerPrefs";
    private static final String FIRST_RUN = "firstRun";

    private static final String ALARM_TIMESTAMP = "alarmTimeStamp";


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
    private static GenericIntentReceiver genericIntentReceiver = null;

    // Used by step counter
    private static SensorManager sensorManager = null;

    /**
     * Turns on all the data collection services
     * @param c Calling Android context
     */
    public static void on(Context c) {

        if(!isRunning(c)) {
            if(isFirstRun()) {
                recordDeviceSpecs(c);
                // Now, update the FIRST_RUN check.
                setFirstRun();
            }

            startStepCounter(c);
            startAsynchronous(c);
            startPeriodics(c);

            // Now, update the timestamp.
            updateTimeStamp(c);
        }
    }

    /**
     * Turns off all the data collection services
     * @param c Calling Android context
     */
    public static void off(Context c) {
        if(isRunning(c)) {
            stopStepCounter(c);
            stopAsynchronous(c);
            stopPeriodics(c);

            // Reset the timestamp for future iterations
            // to assume a MasterSwitch.On() alarm reset.
            prefs.edit().putLong(ALARM_TIMESTAMP, 0).commit();
        }
    }


    /* Updates SharedPreferences with a variable that
    *  stores the current timestamp.
    *  @param c Calling Android context     */
    public static void updateTimeStamp(Context c) {
        long currentTime = getCurrentTime();

        prefs = c.getSharedPreferences(PREFS_NAME, 0);
        prefs.edit().putLong(ALARM_TIMESTAMP, currentTime).commit();
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
    public static boolean isRunning(Context c) {
        prefs = c.getSharedPreferences(PREFS_NAME, 0); // Update the static prefs variable
        return (getPrevTimeStamp(c) > getExpectedSafePrevTimeStamp(c));
    }


    /* Gets the what we expect the previous timestamp to be
    *   factored by 110% of the PERIODIC_LENGTH, making it
    *   "safe" from inexact time scheduling errors.
    *  @param c Calling Android context     */
    private static long getExpectedSafePrevTimeStamp(Context c) {
        return ( getCurrentTime() -  ( (long) (TimeConstants.PERIODIC_LENGTH * TimeConstants.PERIODIC_SAFE_FACTOR)));
    }


    /* Gets the current system time.
    *  @param c Calling Android context     */
    private static long getCurrentTime() {
        return System.currentTimeMillis();
    }

    /* Gets the previous time stamp from prefs (updated by isRunning()) .
   *  @param c Calling Android context     */
    private static long getPrevTimeStamp(Context c) {
        return prefs.getLong(ALARM_TIMESTAMP, 0);
    }


    /* Sets the first run to false.*/
    private static void setFirstRun() {
        prefs.edit().putBoolean(FIRST_RUN, false).commit();
    }

    /*Determines whether the MasterSwitch.On has been called before.*/
    private static boolean isFirstRun() {
        return prefs.getBoolean(FIRST_RUN, false);
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
                TimeConstants.PERIODIC_LENGTH,
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

        // Try to unregister the receiver in case the phone sleeps
        // and MainActivity.onCreate() is called again, leading to
        // a new MasterSwitch.on call.
        try {
            c.unregisterReceiver(genericIntentReceiver);
        } catch (IllegalArgumentException e) {
            // We deliberately do nothing in the catch block.
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
        Intent intent = new Intent(c, StepSensorKeepAliveService.class);

        c.startService(intent);
    }

    /**
     * Deactivate the step sensor
     * @param c Calling Android context
     */
    private static void stopStepCounter(Context c) {
        Intent intent = new Intent(c, StepSensorKeepAliveService.class);
        intent.putExtra(StepSensorKeepAliveService.DEACTIVATE_EXTRA, true);

        c.startService(intent);
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
