package us.michaelchen.compasslogger.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import us.michaelchen.compasslogger.datarecorder.ScreenRecordingService;

/**
 * Created by ioreyes on 5/27/16.
 */
public class ScreenIntentReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent != null) {
            intent.setClass(context, ScreenRecordingService.class);
            context.startService(intent);
        }
    }
}
