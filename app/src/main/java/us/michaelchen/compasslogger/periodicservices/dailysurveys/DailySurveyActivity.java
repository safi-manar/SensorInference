package us.michaelchen.compasslogger.periodicservices.dailysurveys;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import java.util.prefs.Preferences;

import us.michaelchen.compasslogger.R;
import us.michaelchen.compasslogger.utils.MasterSwitch;
import us.michaelchen.compasslogger.utils.PreferencesWrapper;

/**
 * Created by Manar on 7/6/2016.
 */



/**
 * DailySurveyActivity is called when the time has reached DAILY_SURVEY_DEADLINE.
 * It prompts the user with a dialog to complete the survey,
 */
public class DailySurveyActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        promptDailySurveyDialog();

    }


    private void promptDailySurveyDialog() {
        AlertDialog dailySurveyDialog = createDailySurveyDialog();
        dailySurveyDialog.show();
    }



    /**
     *
     * @return the AlertDialog for the daily survey.
     */
    private AlertDialog createDailySurveyDialog() {
        final Context context = this;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);


        builder.setPositiveButton(R.string.dailysurvey_yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Now show the WebView FormActivity
                Intent intent = new Intent(context, DailySurveyFormActivity.class);
                startActivity(intent);
            }
        });

        builder.setNeutralButton(R.string.dailysurvey_postpone, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                postponeDeadline();
            }
        });

        builder.setNegativeButton(R.string.dailysurvey_no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                skipTodaySurvey();
            }
        });


        builder.setMessage(R.string.dailysurvey_message).setTitle(R.string.dailysurvey_message_title);

        /*To avoid a bug where the back button on the dialog takes the user to the
        * transparent activity screen where interaction does nothing (and gives the
        * impression that the phone is locked up / crashing), override the back button
        * to close the activity. */
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                Log.d("DailySurveyActivity", "Back button pressed inside of dialog.");
                moveTaskToBack(true);
                finish();
            }
        });

        return builder.create();
    }


    /**
     * Postpones the daily deadline by DAILY_SURVEY_POSTPONEMENT
     */
    private void postponeDeadline() {
        PreferencesWrapper.postponeDailyDeadline();
    }


    /**
     * Skips today's survey.
     */
    private void skipTodaySurvey() {
        PreferencesWrapper.updateDailyDeadline();
    }



}

