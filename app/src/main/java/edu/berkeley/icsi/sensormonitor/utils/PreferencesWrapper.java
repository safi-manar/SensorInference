package edu.berkeley.icsi.sensormonitor.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

import java.util.UUID;

/**
 * Created by ioreyes on 6/9/16.
 */
public class PreferencesWrapper {
    /**
     * Names for preferences
     */
    private static final String PREFS_NAME = "CompassLoggerPrefs";

    // Field names
    private static final String SURVEY_COMPLETED = "survey_completed";
    private static final String UNINSTALL_DEADLINE = "uninstall_deadline";
    private static final String ALARM_TIMESTAMP = "alarm_timestamp";
    private static final String FIRST_RUN = "first_run";
    private static final String DEVICE_ID = "device_id";
    private static final String VERIF_CODE = "verif_code";

    private static final String REAL_DAILY_DEADLINE = "real_daily_deadline";
    // Nominal variable to allow calculation of deadline postponement.
    private static final String NOMINAL_DAILY_DEADLINE = "nominal_daily_deadline";
    private static final String DAILY_SURVEY_OVERLAY = "daily_survey_overlay";

    private static final String LAST_GPS_DISTANCE = "last_gps_distance";
    private static final String LAST_GPS_INTERVAL  = "last_gps_interval";

    private static SharedPreferences prefs = null;

    /**
     * Iniitialize the preferences
     * @param c Calling Android context
     */
    public static void init(Context c) {
        prefs = c.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }


    /**
     *
     * @return True if the user has completed the survey form
     */
    public static boolean isSurveyCompleted() {
        return prefs.getBoolean(SURVEY_COMPLETED, false);
    }

    /**
     * Set that the user has completed the survey form
     */
    public static void setSurveyCompleted() {
        prefs.edit().putBoolean(SURVEY_COMPLETED, true).commit();
    }

    /**
     *
     * @return The timestamp in milliseconds when the app should prompt to uninstall itself. 0 if unset.
     */
    public static long getUninstallDeadline() {
        return prefs.getLong(UNINSTALL_DEADLINE, 0);
    }

    /**
     * Set when the app should prompt to uninstall itself
     *
     */
    public static void setUninstallDeadline() {
        long currentTime = System.currentTimeMillis();
        // Uninstallation timestamp in milliseconds
        long deadlineMillis = currentTime + TimeConstants.DEADLINE_LENGTH;
        prefs.edit().putLong(UNINSTALL_DEADLINE, deadlineMillis).commit();
    }

    /**
     * Adds UNINSTALL_POSTPONEMENT to the uninstall deadline.
     */
    public static void postponeUninstallDeadline() {
        prefs.edit().putLong(UNINSTALL_DEADLINE, getUninstallDeadline() + TimeConstants.UNINSTALL_POSTPONEMENT).commit();
    }

    /**
     *
     * @return The timestamp in milliseconds when the last alarm occurred. 0 if unset.
     */
    public static long getLastAlarmTimestamp() {
        return prefs.getLong(ALARM_TIMESTAMP, 0);
    }

    /**
     * Update the stored last alarm time to the current system time in milliseconds.
     */
    public static void updateLastAlarmTimestamp() {
        prefs.edit().putLong(ALARM_TIMESTAMP, System.currentTimeMillis()).commit();
    }

    /**
     * Reset the stored last alarm time to 0
     */
    public static void resetLastAlarmTimestamp() {
        prefs.edit().putLong(ALARM_TIMESTAMP, 0l).commit();
    }

    /**
     *
     * @return True if this is the first time the app has been run since install
     */
    public static boolean isFirstRun() {
        return prefs.getBoolean(FIRST_RUN, true);
    }

    /**
     * Updates the "first run" preference to false
     */
    public static void setFirstRun() {
        prefs.edit().putBoolean(FIRST_RUN, false).commit();
    }

    /**
     *
     * @return An install-persistent random UUID for this device
     */
    public static String getDeviceID() {
        String id = prefs.getString(DEVICE_ID, null);

        // Generate and store a new UUID if it doesn't exist
        if(id == null) {
            id = UUID.randomUUID().toString();
            prefs.edit().putString(DEVICE_ID, id).commit();
        }

        return id;
    }

    /**
     *
     * @return The first block of the install-persistent random UUID for this device
     */
    public static String getShortDeviceID() {
        String id = getDeviceID();

        String[] blocks = id.split("-");  // The "-" symbol delimits the different UUID blocks

        return blocks[0];
    }

    /**
     * Creates the verification code as the first block
     * in the UUID.
     */
    public static String getVerifCode() {
        String vCode = prefs.getString(VERIF_CODE, null);
        if (vCode == null) {
            vCode = getShortDeviceID();
            prefs.edit().putString(VERIF_CODE, vCode).commit();
        }
        return vCode;
    }


    /**
     *
     * @return the NOMINAL_DAILY_DEADLINE, as opposed to REAL_DAILY_DEADLINE
     * because the nominal value may have been modified for postponement by
     * the user.
     */
    public static long getDailyDeadline() {
        return prefs.getLong(NOMINAL_DAILY_DEADLINE, 0);
    }

    /**
     *
     * @return the threshold time for when the daily survey will remain active.
     */
    public static long getDailyDeadlineThreshold() {
        return (prefs.getLong(REAL_DAILY_DEADLINE, 0) + (TimeConstants.DAILY_SURVEY_WINDOW));
    }


    /**
     * Adds 24 hours to the daily deadline.
     */
    public static void updateDailyDeadline() {
        // Get the real previous deadline (getDailyDeadline() would not work).
        long prevDeadline = prefs.getLong(REAL_DAILY_DEADLINE, 0);

        prefs.edit().putLong(REAL_DAILY_DEADLINE, prevDeadline + TimeConstants.ONE_DAY).commit();
        // The nominal deadline is reset to the real deadline.
        prefs.edit().putLong(NOMINAL_DAILY_DEADLINE, prevDeadline + TimeConstants.ONE_DAY).commit();
    }


    /**
     * Adds DAILY_SURVEY_POSTPONEMENT to the nominal daily deadline.
     */
    public static void postponeDailyDeadline() {
        prefs.edit().putLong(NOMINAL_DAILY_DEADLINE, getDailyDeadline() + TimeConstants.DAILY_SURVEY_POSTPONEMENT).commit();
    }

    /**
     * Sets the initial deadline, and guarantees that it can only be done once (to avoid
     * issue where MainActivity.surveyReceiver is called multiple times upon multiple
     * Google Form submit clicks).
     */
    public static void setInitialDailyDeadline() {
        long prevDeadline = prefs.getLong(REAL_DAILY_DEADLINE, 0);
        // If the daily deadline has not been initially set, do so here.
        if (prevDeadline == 0) {
            long dailyDeadlineMillis = DataTimeFormat.getDailyDeadlineInMillis();
            prefs.edit().putLong(REAL_DAILY_DEADLINE, dailyDeadlineMillis).commit();
            prefs.edit().putLong(NOMINAL_DAILY_DEADLINE, dailyDeadlineMillis).commit();
        }
    }



    /**
     * @return true if there is currently a DailySurveyActivity dialog overlayed;
     * ie, if the user has yet to interact with the current DailySurvey dialog.
     */
    public static boolean isDialogOverlayed() {
        return prefs.getBoolean(DAILY_SURVEY_OVERLAY, false);
    }

    /**
     * Represent the DailySurvey dialog as being currently overlayed on the screen
     * and that the user has yet to interact with it.
     */
    public static void setOverlayFlagged() {
        prefs.edit().putBoolean(DAILY_SURVEY_OVERLAY, true).commit();
    }

    /**
     * Represent the DailySurvey dialog as no longer overlayed on the screen
     */
    public static void setOverlayUnFlagged() {
        prefs.edit().putBoolean(DAILY_SURVEY_OVERLAY, false).commit();
    }


    /**
     * Store a distance and time between GPS coordinates
     * @param loc1
     * @param loc2
     */
    public static void setGPSDistanceAndTime(Location loc1, Location loc2) {
        float distance = loc1.distanceTo(loc2);
        prefs.edit().putFloat(LAST_GPS_DISTANCE, distance).commit();
        prefs.edit().putLong(LAST_GPS_INTERVAL, Math.abs(loc1.getTime() - loc2.getTime())).commit();
    }

    /**
     *
     * @return The last stored distance between GPS coordinates in meters. -1.0 if not set.
     */
    public static float getGPSDistance() {
        return prefs.getFloat(LAST_GPS_DISTANCE, -1.0f);
    }

    /**
     *
     * @return The time in milliseconds between the recorded GPS coordinates. -1 if not set.
     */
    public static long getGPSTime() {
        return prefs.getLong(LAST_GPS_INTERVAL, -1l);
    }

    /**
     *
     * @return The speed taken between along the last stored distance between GPS coordinates in
     * km/h. -1.0 if unavailable.
     */
    public static double getGPSSpeed() {
        float distanceM = getGPSDistance();
        long timeMS = getGPSTime();

        if(distanceM > 0.0f && timeMS > 1l) {
            double distanceKM = distanceM / 1000.0;
            double timeHR = timeMS / (1000.0 * 60.0 * 60.0);    // (ms -> sec -> min -> hr)

            return distanceKM / timeHR;
        }

        return -1.0f;
    }

    /**
     *
     * @return True if the average speed between the last two GPS points is at least 20 km/h
     */
    public static boolean isGPSSpeedExceed20KPH() {
        final double SPEED_THRESHOLD = 20.0;
        return getGPSSpeed() >= SPEED_THRESHOLD;
    }
}
