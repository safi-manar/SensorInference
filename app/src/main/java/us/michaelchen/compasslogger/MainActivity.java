package us.michaelchen.compasslogger;

import android.Manifest;
import android.app.Activity;
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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.client.Firebase;

import java.util.UUID;

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

    public static final int BROADCAST_PERIOD = 60000*10; //60 seconds * 10 mins
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
        startAlarms();
        if (!checkFormComplete()) {
            Intent intent = new Intent(this, FormActivity.class);
            intent.putExtra(INTENT_DEVICE_ID, getDeviceId());
            startActivity(intent);
        }
    }

    String getDeviceId() {
        final TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        final String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String deviceId = deviceUuid.toString();
        return deviceId;
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

    static void startAlarms(Context context) {
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        int interval = BROADCAST_PERIOD;

        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
        manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);

//        Intent i = new Intent(context, ScreenPowerService.class);
//        context.startService(i);
        Intent i = new Intent(context, BackgroundService.class);
        context.startService(i);
    }

    void startAlarms() {
        startAlarms(this);
        Toast.makeText(this, "Alarms Set", Toast.LENGTH_SHORT).show();
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
