package edu.berkeley.icsi.sensormonitor.periodicservices.keepalive;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import edu.berkeley.icsi.sensormonitor.receiver.BootReceiver;

/**
 * Created by ioreyes on 8/9/16.
 */
public class BootReceiverKeepAliveService extends AbstractKeepAliveService {
    private static final IntentFilter BOOT_FILTER = new IntentFilter(Intent.ACTION_BOOT_COMPLETED);
    private static final BootReceiver BOOT_INTENT_RECEIVER = new BootReceiver();

    public BootReceiverKeepAliveService() {
        super("BootReceiverKeepAliveService");
    }

    @Override
    protected void keepAlive() {
        Context app = getApplicationContext();
        try {
            app.registerReceiver(BOOT_INTENT_RECEIVER, BOOT_FILTER);
        } catch(IllegalArgumentException e) {
            // Do nothing if it's already registered
        }
    }

    @Override
    protected void shutDown() {
        Context app = getApplicationContext();
        try {
            app.unregisterReceiver(BOOT_INTENT_RECEIVER);
        } catch (IllegalArgumentException e) {
            // Do nothing if it wasn't already registered
        }
    }
}
