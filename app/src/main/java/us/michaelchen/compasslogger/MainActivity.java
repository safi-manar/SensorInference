package us.michaelchen.compasslogger;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.firebase.client.Firebase;

import us.michaelchen.compasslogger.receiver.PeriodicReceiver;
import us.michaelchen.compasslogger.utils.DeviceID;

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
        Firebase.setAndroidContext(this);

        if (checkFirstLaunch()) {
            checkPermissions();
        } else {
            AlertDialog dialog = agreementDialog();
            dialog.show();
        }

    }

    boolean checkFirstLaunch() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, 0);
        return prefs.getBoolean(PREFS_AGREED, false);
    }

    boolean checkFormComplete() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, 0);
        return prefs.getBoolean(PREFS_FORM_COMPLETE, false);
    }

    private AlertDialog agreementDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                SharedPreferences prefs = getSharedPreferences(PREFS_NAME, 0);
//                prefs.edit().putBoolean(PREFS_AGREED, true).commit();
                checkPermissions();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                MainActivity.this.finish();
                System.exit(0);
            }
        });

        builder.setMessage(R.string.agreement_message)
                .setTitle(R.string.agreement_title);

        AlertDialog dialog = builder.create();
        return dialog;
    }

    void onPermissionCheckSuccess() {
        startAlarms(this);
        startEventMonitoring();
        if (!checkFormComplete()) {
            Intent intent = new Intent(this, FormActivity.class);
            intent.putExtra(INTENT_DEVICE_ID, DeviceID.get(this));
            startActivity(intent);
        }
    }

    void checkPermissions() {
        for (int i = 0; i < permissions.length; i++) {
            if (ContextCompat.checkSelfPermission(this,
                    permissions[i])
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{permissions[i]},
                        i);
            } else {
                permissionStateChecks++;
            }
        }

        if (permissionStateChecks == permissions.length) {
            onPermissionCheckSuccess();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            permissionStateChecks++;
            if (permissionStateChecks == permissions.length) {
                onPermissionCheckSuccess();
            }

        } else {
            // permission denied, boo! Exit
            finish();
            System.exit(-1);
        }
        return;
    }

    public static void startAlarms(Context context) {
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent alarmIntent = new Intent(context, PeriodicReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                                    System.currentTimeMillis(),
                                    BROADCAST_PERIOD,
                                    pendingIntent);

        Toast.makeText(context, "Alarms Set", Toast.LENGTH_SHORT).show();
    }

    /**
     * Starts asynchronous event monitoring
     */
    private void startEventMonitoring() {
        Intent i = new Intent(this, EventMonitoringService.class);
        startService(i);
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

}
