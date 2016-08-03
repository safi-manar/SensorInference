package edu.berkeley.icsi.sensormonitor.periodicservices.deadline;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import edu.berkeley.icsi.sensormonitor.R;
import edu.berkeley.icsi.sensormonitor.utils.MasterSwitch;
import edu.berkeley.icsi.sensormonitor.utils.PreferencesWrapper;

/**
 * Created by Manar on 6/3/2016.
 */


/* DeadlineActivity is called when the timer has reached the deadline. It prompts the
* user with a dialog to complete the exit survey, which will be followed by an uninstall prompt.*/
public class DeadlineActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        promptExitSurveyDialog();
        //getWindow().setBackgroundDrawable(new ColorDrawable(0));
    }


    private void promptExitSurveyDialog() {
        AlertDialog exitSurveyDialog = createExitSurveyDialog();
        //exitSurveyDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        exitSurveyDialog.show();

    }


    private AlertDialog createExitSurveyDialog() {
        final Context context = this;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setNeutralButton(R.string.deadline_postpone_button, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                PreferencesWrapper.postponeUninstallDeadline();
                moveTaskToBack(true);
                finish();
            }
        });
        builder.setNegativeButton(R.string.deadline_uninstall_button, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent(context, UninstallPromptActivity.class);
                startActivity(intent);
            }
        });
        builder.setPositiveButton(R.string.deadline_proceed_button, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent(context, ExitSurveyFormActivity.class);
                startActivity(intent);
            }
        });

        builder.setMessage(R.string.deadline_message).setTitle(R.string.deadline_message_title);

        // Prevent users from dismissing the dialog by tapping outside it
        builder.setCancelable(false);

        return builder.create();
    }


    /**
     * To avoid a bug where the back button on the dialog takes the user to the
     * transparent activity screen where interaction does nothing (and gives the
     * impression that the phone is locked up / crashing), override the back button
     * to close the activity.
     */
    @Override
    public void onBackPressed()
    {
        Log.d("DeadlineActivity", "Back button pressed inside of dialog.");
        moveTaskToBack(true);
        finish();
    }



}

