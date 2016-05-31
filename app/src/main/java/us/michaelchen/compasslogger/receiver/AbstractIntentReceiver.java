package us.michaelchen.compasslogger.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by ioreyes on 5/31/16.
 */
public abstract class AbstractIntentReceiver extends BroadcastReceiver {
    @Override
    public final void onReceive(Context context, Intent intent) {
        if(intent != null) {
            intent.setClass(context, getTargetService());
            context.startService(intent);
        }
    }

    /**
     *
     * @return The class of the service to invoke when receiving an intent
     */
    protected abstract Class getTargetService();
}
