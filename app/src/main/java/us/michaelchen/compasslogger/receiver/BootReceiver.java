package us.michaelchen.compasslogger.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import us.michaelchen.compasslogger.utils.MasterSwitch;

/**
 * Receiver used to restart periodic sensor polling after boot start-up
 */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            MasterSwitch.on(context);
        }
    }
}
