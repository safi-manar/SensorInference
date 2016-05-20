package us.michaelchen.compasslogger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import us.michaelchen.compasslogger.sensor.GenericReceiverService;
import us.michaelchen.compasslogger.sensor.ScreenPowerService;

public class GenericReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            return;
        }
        Intent i = new Intent(context, GenericReceiverService.class);
        i.putExtra(GenericReceiverService.EVENT_KEY, intent.getAction());
        context.startService(i);
    }
}
