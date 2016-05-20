package us.michaelchen.compasslogger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import us.michaelchen.compasslogger.sensor.ScreenPowerService;

public class ScreenReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            return;
        }

        boolean screenOn = false;
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            screenOn = false;
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            screenOn = true;
        }
        Intent i = new Intent(context, ScreenPowerService.class);
        i.putExtra(ScreenPowerService.SCREEN_STATE, screenOn);
        context.startService(i);
    }
}
