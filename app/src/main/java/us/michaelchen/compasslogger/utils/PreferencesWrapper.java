package us.michaelchen.compasslogger.utils;

import android.content.Context;
import android.content.SharedPreferences;

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
    private static final String USER_CONSENTED = "user_consented";
    private static final String SURVEY_COMPLETED = "survey_completed";
    private static final String UNINSTALL_DEADLINE = "uninstall_deadline";
    private static final String ALARM_TIMESTAMP = "alarm_timestamp";
    private static final String FIRST_RUN = "first_run";
    private static final String DEVICE_ID = "device_id)";

    private static SharedPreferences prefs = null;

    /**
     * Iniitialize the preferences
     * @param c Calling Android context
     */
    public static void init(Context c) {
        prefs = c.getSharedPreferences(PREFS_NAME, 0);
    }

    /**
     *
     * @return True if the user has agreed to the consent disclosure
     */
    /*Determines whether the user has given consent to the experiment by checking
    * the value of PREFS_AGREED.
    * PREFS_AGREED = true : The user has previously launched the app and accepted the consent agreement dialog.
    * PREFS_AGREED = false: The user may or may not have launched the app; but permissions have not
    *                       been accepted. */
    public static boolean isUserConsented() {
        return prefs.getBoolean(USER_CONSENTED, false);
    }

    /**
     * Set that the user has agreed to the consent disclosure
     */
    public static void setUserConsented() {
        prefs.edit().putBoolean(USER_CONSENTED, true).commit();
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
     * @param deadlineMillis Uninstallation timestamp in milliseconds
     */
    public static void setUninstallDeadline(long deadlineMillis) {
        prefs.edit().putLong(UNINSTALL_DEADLINE, deadlineMillis).commit();
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
}
