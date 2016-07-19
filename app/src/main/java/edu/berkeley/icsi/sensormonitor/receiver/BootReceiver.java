package edu.berkeley.icsi.sensormonitor.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import edu.berkeley.icsi.sensormonitor.utils.MasterSwitch;

/**
 * Receiver used to restart periodic sensor polling after boot start-up
 */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            //The Context that is passed to onReceive() is blocked from
            // calling registerReceiver(), even with a null BroadcastReceiver.
            // So, get the application context.
            MasterSwitch.on(context.getApplicationContext());
        }
    }
}
