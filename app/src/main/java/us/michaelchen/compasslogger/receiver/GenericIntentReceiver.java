package us.michaelchen.compasslogger.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import us.michaelchen.compasslogger.periodicservices.datarecording.GenericEventRecordingService;

/**
 * Receiver used for on-demand event recording
 */
public class GenericIntentReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        intent.putExtra(GenericEventRecordingService.BUFFER_EXTRA, true);
        intent.setClass(context, GenericEventRecordingService.class);
        context.startService(intent);
    }
}
