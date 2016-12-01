package edu.berkeley.icsi.sensormonitor.periodicservices.dailysurveys;

import android.app.IntentService;
import android.content.Intent;

import edu.berkeley.icsi.sensormonitor.utils.PreferencesWrapper;

/**
 * Created by Manar on 7/1/2016.
 */
public class DailySurveyService extends IntentService {

    public DailySurveyService() {
        super("DailySurveyService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        checkDeadline();
    }


    /**
     * Check the current time against the daily survey deadline times.
     */
    /**
     *  There is a bug where the survey dialog sets the overlay flag to true, and if
     *  the user never accepts or if there is a crash in the 4-year window, the flag
     *  is never set to false. So, we manually set the flag to false. Also, set
     *  DailySurveyActivity.builder.setOnCancelListener() to force the flag unsetting.
     */
    private void checkDeadline() {
        if (isPassedWindow()) {
            PreferencesWrapper.setDailyOverlayUnFlagged();
            // Force the flag to false.
            PreferencesWrapper.updateDailyDeadline();
        } else if (isPassedDeadline() && !PreferencesWrapper.isGPSSpeedExceed20KPH()
                                    && !PreferencesWrapper.isDailyDialogOverlayed()
                                    && PreferencesWrapper.isPastSixHoursSinceInstall()) {
            startDailySurveyActivity();
        }
    }


    /**
     *
     * @return true if the current time is more than DAILY_SURVEY_WINDOW
     *          passed the DAILY_SURVEY_DEADLINE.
     */
    public static boolean isPassedWindow() {
        return (System.currentTimeMillis() > (PreferencesWrapper.getDailyDeadlineThreshold()));
    }


    /**
     *
     * @return true if the current time is passed the nominal (possibly postponed)
     *          daily deadline.
     *
     */
    public static boolean isPassedDeadline() {
        long currentTime = System.currentTimeMillis();
        long deadlineTime = PreferencesWrapper.getDailyDeadline();
        return (currentTime > deadlineTime);

    }



    /**
     * Starts the Daily survey activity
     */
    private void startDailySurveyActivity() {
        Intent deadlineDialog = new Intent(this, DailySurveyActivity.class);
        deadlineDialog.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(deadlineDialog);
    }

}

