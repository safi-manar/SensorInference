package us.michaelchen.compasslogger.periodicservices.keepalive;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import us.michaelchen.compasslogger.receiver.GenericIntentReceiver;

/**
 * Created by ioreyes on 6/20/16.
 */
public class GenericReceiverKeepAliveService extends AbstractKeepAliveService {
    private static final String[] ASYNCHRONOUS_EVENTS = {
            Intent.ACTION_SCREEN_OFF,
            Intent.ACTION_SCREEN_ON,
            Intent.ACTION_APPLICATION_RESTRICTIONS_CHANGED,
            Intent.ACTION_APP_ERROR,
            Intent.ACTION_BATTERY_LOW,
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_CAMERA_BUTTON,
            Intent.ACTION_CLOSE_SYSTEM_DIALOGS,
            Intent.ACTION_DEVICE_STORAGE_LOW,
            Intent.ACTION_HEADSET_PLUG,
            Intent.ACTION_PACKAGE_ADDED,
            Intent.ACTION_PACKAGE_CHANGED,
            Intent.ACTION_SYNC,
            Intent.ACTION_USER_PRESENT,
            Intent.ACTION_AIRPLANE_MODE_CHANGED,
            Intent.ACTION_POWER_CONNECTED,
            Intent.ACTION_POWER_DISCONNECTED,
            Intent.ACTION_SHUTDOWN,
    };
    private static final GenericIntentReceiver GENERIC_INTENT_RECEIVER = new GenericIntentReceiver();


    public GenericReceiverKeepAliveService() {
        super("GenericReceiverKeepAliveService");
    }

    @Override
    protected void keepAlive() {
        IntentFilter filter = new IntentFilter();
        for (String event : ASYNCHRONOUS_EVENTS) {
            filter.addAction(event);
        }

        Context app = getApplicationContext();
        try {
            app.registerReceiver(GENERIC_INTENT_RECEIVER, filter);
        } catch (IllegalArgumentException e) {
            // Do nothing if it's already registered
        }
    }

    @Override
    protected void shutDown() {
        Context app = getApplicationContext();
        try {
            app.unregisterReceiver(GENERIC_INTENT_RECEIVER);
        } catch (IllegalArgumentException e) {
            // Do nothing if it wasn't already registered
        }
    }
}
