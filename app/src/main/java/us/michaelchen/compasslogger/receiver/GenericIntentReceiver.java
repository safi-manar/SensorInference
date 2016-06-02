package us.michaelchen.compasslogger.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import us.michaelchen.compasslogger.datarecorder.GenericEventRecordingService;

/**
 * Receiver used for on-demand event recording
 */
public class GenericIntentReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        intent.setClass(context, GenericEventRecordingService.class);
        context.startService(intent);
    }
}
