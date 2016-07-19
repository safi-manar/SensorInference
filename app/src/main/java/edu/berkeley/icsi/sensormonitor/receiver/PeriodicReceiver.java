package edu.berkeley.icsi.sensormonitor.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import edu.berkeley.icsi.sensormonitor.periodicservices.Periodics;

/**
 * Receiver used for periodic sensor polling
 */
public class PeriodicReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        for(Class c : Periodics.ALL) {
            context.startService(new Intent(context, c));
        }
    }
}
