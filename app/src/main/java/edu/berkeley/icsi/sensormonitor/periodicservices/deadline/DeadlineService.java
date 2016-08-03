package edu.berkeley.icsi.sensormonitor.periodicservices.deadline;

import android.app.IntentService;
import android.content.Intent;

import edu.berkeley.icsi.sensormonitor.utils.PreferencesWrapper;
import edu.berkeley.icsi.sensormonitor.utils.TimeConstants;

/**
 * Created by Manar on 6/3/2016.
 */
public class DeadlineService extends IntentService {

    public DeadlineService() {
        super("DeadlineService");
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


    /*Checks the current time against the deadline time.*/
    private void checkDeadline() {
        if (isPassedDeadline() && !PreferencesWrapper.isGPSSpeedExceed20KPH()
                && !PreferencesWrapper.isDeadlineDialogOverlayed()) {
            startDeadlineActivity();
        }
    }


    /*Returns whether the deadline has been reached.*/
    private boolean isPassedDeadline() {
        long currentTime = System.currentTimeMillis();
        long deadlineTime = PreferencesWrapper.getUninstallDeadline();

        return (currentTime > deadlineTime);

    }

    /*Starts the DeadLine Activity dialog to prompt the user to uninstall.*/
    private void startDeadlineActivity() {
        Intent deadlineDialog = new Intent(this, DeadlineActivity.class);
        deadlineDialog.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(deadlineDialog);
    }

}

