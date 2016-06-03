package us.michaelchen.compasslogger;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;

import com.firebase.client.Firebase;

import us.michaelchen.compasslogger.utils.DeviceID;
import us.michaelchen.compasslogger.utils.MasterSwitch;

public class MainActivity extends AppCompatActivity {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */

    /**
     * Used to store the last screen title. .
     */
    private CharSequence mTitle;
    private int permissionStateChecks = 0;
    private String[] permissions = {Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_FINE_LOCATION};

    private static final int BROADCAST_MINUTES = 1;
    private static final int BROADCAST_PERIOD = 1000 * 60 * BROADCAST_MINUTES;
    final String PREFS_NAME = "CompassLoggerPrefs";
    final String PREFS_AGREED = "user_agreed";
    final String PREFS_FORM_COMPLETE = "user_finished_form";

    static final String INTENT_DEVICE_ID = "device_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTitle = getTitle();

        if (checkAgreementConsent()) {
            checkPermissions();
        } else {
            AlertDialog dialog = agreementDialog();
            dialog.show();
        }

    }

    /*Determines whether the user has given consent to the experiment by checking
    * the value of PREFS_AGREED.
    * PREFS_AGREED = true : The user has previously launched the app and accepted the consent agreement dialog.
    * PREFS_AGREED = false: The user may or may not have launched the app; but permissions have not
    *                       been accepted. */
    private boolean checkAgreementConsent() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, 0);
        return prefs.getBoolean(PREFS_AGREED, false);
    }


    /*Determines whether the Google Form has been completed by checking
    * the value of PREFS_FORM_COMPLETE.
    * PREFS_FORM_COMPLETE = true : The user has submitted the Google form..
    * PREFS_FORM_COMPLETE = false: The user may or may not have viewed the Google form; but the form has not
     *                              been submitted. */
    private boolean checkFormComplete() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, 0);
        return prefs.getBoolean(PREFS_FORM_COMPLETE, false);
    }



    /* Displays the agreement ("Consent to Participate in Research..." ) dialog.
    *
    *
    * */
    private AlertDialog agreementDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // The Agree button adds PREFS_AGREED = true to the SharedPreferences.
        // It then calls CheckPermissions() //TODO Why??
        builder.setPositiveButton(R.string.agree, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                SharedPreferences prefs = getSharedPreferences(PREFS_NAME, 0);
                prefs.edit().putBoolean(PREFS_AGREED, true).commit();
                checkPermissions();
            }
        });

        // The cancel button simply closes the app. Since PREFS_AGREED was not set to true,
        // the next relaunch will return false for checkAgreementConsent.
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                MainActivity.this.finish();
                System.exit(0);
            }
        });

        builder.setMessage(R.string.agreement_message)
                .setTitle(R.string.agreement_title);

        AlertDialog dialog = builder.create();
        return dialog;
    }


    /* This method explicitly requests the permissions in the permissions[] String array.
    * Android allows only one permission to be requested at a time. Additionally, the requestPermissions
    * method runs on an asychronous thread-- that is, the system does not wait on the user to respond to
    * the permissions dialog before continuing the flow of the for loop. As a result, we must
    * explicitly break the flow of this method on every requestPermissions call, and then restart checkPermissions().
    * Thus, we run an independent checkPermissions() call for each invidual permission.
    * */
    private void checkPermissions() {
       int grantedCount = 0;
        for (int i = 0; i < permissions.length; i++) {
            if (ContextCompat.checkSelfPermission(this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{permissions[i]}, i);
                // break the flow of the for loop to bypass asynchronous threading and subsequent permission requests.
                return;
            }
        }


        /*Since denied permissions handling is done by onRequestPermissionsResult, if the code has
        * gotten this far, then the user must have accepted all the permissions.
        * The app must now prompt the user to fill out the Google Form and collect sensor data.*/
        collectSurveyFormAndSensorData();
    }


    /* Upon a requestPermissions call from checkPermissions, the system prompts a user with the permission
    * dialog box. The user's response invokes this method.
    * However, this callback method is also invoked any time the system implicitly requests the permissions.
    *
    * */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        /*As aforementioned, in order to bypass the asynchronous threading, we must run an independent
        * checkPermissions() call for every permission requested and then run the final checkPermissions
        * after all permissions have been requested and accepted.*/

        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // The permission has been granted. Now, re-run the checkPermissions for any remaining permissions.
            checkPermissions();
        } else {
            AlertDialog permissionDenied = permissionDeniedDialog();
            permissionDenied.show();
        }

    }


    /* This message is displayed when the user denies a permission request.
    *   It tells the user that the permissions must be accepted for the app to work
    *   and for the subject to be paid for the research.*/
    private AlertDialog permissionDeniedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
                System.exit(-1);
            }
        });

        builder.setMessage(R.string.denied_permission_response)
                .setTitle(R.string.denied_permission_title);

        AlertDialog dialog = builder.create();
        return dialog;
    }



    /*This method will be called when all permissions have been accepted. It will
     * prompt the user to fill out the Google Form survey, and then begin collecting sensor data.
     * In the case that a user does not have time to complete -- or simply ignores-- the form,
     * the chance that the user will find the motivation to revisit the app at a later time to
     * fill out the form is slim. Without the form data, the data from the user's phone is useless.
     * As such, phone sensor data collection should occur after submission of the form.
     * */
    private void collectSurveyFormAndSensorData() {
        if (!checkFormComplete()) {
            // Remind the user about the survey, and since the dialog thread will be
            // asynchronous, let the AlertDialog handle the initiation of the alarms.
            AlertDialog surveyForm = surveyFormDialog(this);
            surveyForm.show();


            // Register to receive survey completion intent.
            // We are registering an observer (surveyReceiver) to receive Intents
            // with actions named "custom-event-name".
            LocalBroadcastManager.getInstance(this).registerReceiver(surveyReceiver,
                    new IntentFilter("survey-complete"));

            // Now that the survey has been completed, tag the user as having completed it.
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, 0);
            prefs.edit().putBoolean(PREFS_FORM_COMPLETE, true).commit();


        } else {
            // The AlertDialog thread was asynchronous, so the dialog itself handled the startAlarms.
            // If the form has already been completed, sensor data collection can begin.
            MasterSwitch.on(this);
            //TODO Collect a timestamp at this point to be used to calculate a 1 week duration and remind the user
            // that the app may be uninstalled.
        }
    }



    /* This message is displayed prompting the user to continue with the
    * research by filling out the Google Form. */
    private AlertDialog surveyFormDialog(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Now show the WebView FormActivity
                Intent intent = new Intent(context, FormActivity.class);
                intent.putExtra(INTENT_DEVICE_ID, DeviceID.get(context));
                startActivity(intent);
            }
        });


        builder.setMessage(R.string.survey_form_message)
                .setTitle(R.string.survey_form_title);

        AlertDialog dialog = builder.create();
        return dialog;
    }

    /*The handler for received Intents. This will be called whenever an Intent
    * with an action named "survey-complete" is broadcasted.
    * */
    private BroadcastReceiver surveyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Now that the user has completed the form, sensor data collection can begin.
            MasterSwitch.on(context);
            //TODO Collect a timestamp at this point to be used to calculate a 1 week duration and remind the user
            // that the app may be uninstalled.
        }
    };
}
