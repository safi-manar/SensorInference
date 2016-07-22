package edu.berkeley.icsi.sensormonitor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import edu.berkeley.icsi.sensormonitor.utils.FirebaseWrapper;
import edu.berkeley.icsi.sensormonitor.utils.PreferencesWrapper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        FirebaseWrapper.init();

        setContentView(R.layout.activity_info);

        /*Because alarms are reset upon a device restart,
         * we'll reset the alarm timestamp so that MasterSwitch
         * assumes that the alarms must be reset.   */
        PreferencesWrapper.resetLastAlarmTimestamp();

        launchCorrectActivity(this);

    }


    /**
     * Launches the correct activity based on the state of the user's progress.
     * If the survey has been submitted, the user is taken to the Info activity.
     * Otherwise, the user is taken back to the agreement consent activity.
     */
    private void launchCorrectActivity(Context context) {
        if (PreferencesWrapper.isSurveyCompleted()) {
            // Show the Info Activity

            Intent intent = new Intent(context, InfoActivity.class);
            startActivity(intent);
            MainActivity.this.finish();
        } else {
            // Show the Consent
            Intent intent = new Intent(context, ConsentActivity.class);
            startActivity(intent);
            MainActivity.this.finish();
        }

    }


}

