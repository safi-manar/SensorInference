package edu.berkeley.icsi.sensormonitor.periodicservices.dailysurveys;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import edu.berkeley.icsi.sensormonitor.utils.PreferencesWrapper;
import edu.berkeley.icsi.sensormonitor.utils.TimeConstants;

/**
 * Created by Manar on 7/1/2016.
 */
public class DailySurveyService extends IntentService {

    public DailySurveyService() {
        super("DailySurveyService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        // Wait a few seconds for a new GPS fix (to infer if the user is in a moving vehicle)
        long endTime = System.currentTimeMillis() + (TimeConstants.MAX_SENSOR_TIME * 2);
        while(System.currentTimeMillis() < endTime) {
            try {
                Thread.sleep(TimeConstants.SENSOR_DATA_POLL_INTERVAL);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        checkDeadline();
    }


    /**
     * Check the current time against the daily survey deadline times.
     */
    private void checkDeadline() {
        long currentTime = System.currentTimeMillis();
        boolean isPassedWindow = currentTime > PreferencesWrapper.getDailyDeadlineThreshold();
        boolean isPassedDeadline = currentTime > PreferencesWrapper.getDailyDeadline();

        if (isPassedWindow) {
            PreferencesWrapper.updateDailyDeadline();
        } else if (isPassedDeadline &&
                   !PreferencesWrapper.isGPSSpeedExceed20KPH() &&
                   !PreferencesWrapper.isDialogOverlayed()) {

            // Start the daily survey activity
            Intent deadlineDialog = new Intent(this, DailySurveyActivity.class);
            deadlineDialog.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(deadlineDialog);
        }
    }
}

