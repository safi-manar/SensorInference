package us.michaelchen.compasslogger;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import us.michaelchen.compasslogger.datarecorder.DeviceSpecsRecordingService;
import us.michaelchen.compasslogger.receiver.GenericIntentReceiver;

/**
 * Records asynchronous events, as listed in the intents below
 */
public class EventMonitoringService extends Service {
    private static final String[] GENERIC_INTENTS = {
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

    @Override
    public void onCreate() {
        super.onCreate();

        registerGenericFilter();

        // TODO This should really be in MainActivity, but putting it here until MainActivity is cleaned up
        recordDeviceSpecs();
    }

    private void registerGenericFilter() {
        IntentFilter filter = new IntentFilter();
        for (String action : GENERIC_INTENTS) {
            filter.addAction(action);
        }
        registerReceiver(new GenericIntentReceiver(), filter);
    }

    private void recordDeviceSpecs() {
        Intent intent = new Intent(this, DeviceSpecsRecordingService.class);
        startService(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
