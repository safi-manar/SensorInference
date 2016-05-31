package us.michaelchen.compasslogger;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import us.michaelchen.compasslogger.receiver.GenericIntentReceiver;
import us.michaelchen.compasslogger.receiver.PowerIntentReceiver;
import us.michaelchen.compasslogger.receiver.ScreenIntentReceiver;

/**
 * Records asynchronous events, as listed in the intents below
 */
public class EventMonitoringService extends Service {
    private static final String[] GENERIC_INTENTS = {
            Intent.ACTION_SCREEN_OFF,
            Intent.ACTION_APPLICATION_RESTRICTIONS_CHANGED,
            Intent.ACTION_APP_ERROR,
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

    @Override
    public void onCreate() {
        super.onCreate();

        registerScreenFilter();
        registerPowerFilter();
        registerGenericFilter();
    }

    private void registerScreenFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(new ScreenIntentReceiver(), filter);
    }

    private void registerPowerFilter() {
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(new PowerIntentReceiver(), filter);
    }

    private void registerGenericFilter() {
        IntentFilter filter = new IntentFilter();
        for (String action : GENERIC_INTENTS) {
            filter.addAction(action);
        }
        registerReceiver(new GenericIntentReceiver(), filter);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
