package us.michaelchen.compasslogger.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import us.michaelchen.compasslogger.datarecorder.PowerRecordingService;

/**
 * Created by ioreyes on 5/27/16.
 */
public class PowerIntentReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent != null) {
            intent.setClass(context, PowerRecordingService.class);
            context.startService(intent);
        }
    }
}
