package edu.berkeley.icsi.sensormonitor;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class PermissionActivity extends AppCompatActivity {

    protected static final String[] PERMISSIONS = {android.Manifest.permission.ACCESS_FINE_LOCATION};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);

        programButtons(this);
    }

    private void programButtons(final Context context) {

        final Button agreeButton = (Button) findViewById(R.id.permissions_continue_button);
        agreeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                checkPermissions(context);
            }
        });

    }



    /* This method explicitly requests the permissions in the PERMISSIONS[] String array.
    * Android allows only one permission to be requested at a time. Additionally, the requestPermissions
    * method runs on an asychronous thread-- that is, the system does not wait on the user to respond to
    * the permissions dialog before continuing the flow of the for loop. As a result, we must
    * explicitly break the flow of this method on every requestPermissions call, and then restart checkPermissions().
    * Thus, we run an independent checkPermissions() call for each individual permission.
    * */
    private void checkPermissions(Context context) {
        for (int i = 0; i < PERMISSIONS.length; i++) {
            if (ContextCompat.checkSelfPermission(this, PERMISSIONS[i]) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{PERMISSIONS[i]}, i);
                // break the flow of the for loop to bypass asynchronous threading and subsequent permission requests.
                return;
            }
        }


        /*Since denied permissions handling is done by onRequestPermissionsResult, if the code has
        * gotten this far, then the user must have accepted all the permissions.
        * The app must now prompt the user to fill out the Google Form and collect sensor data. */
        // Show the Survey Redirect Activity
        Intent intent = new Intent(context, SurveyRedirectActivity.class);
        startActivity(intent);
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
            checkPermissions(this);
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

        builder.setNeutralButton(R.string.permission_denied_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finishAffinity();

            }
        });

        builder.setMessage(R.string.permission_denied_response)
                .setTitle(R.string.permission_denied_title);

        // Prevent users from dismissing the dialog by tapping outside it
        builder.setCancelable(false);

        AlertDialog dialog = builder.create();
        return dialog;
    }



}
