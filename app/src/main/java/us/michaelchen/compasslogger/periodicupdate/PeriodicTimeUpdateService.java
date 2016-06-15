package us.michaelchen.compasslogger.periodicupdate;

import android.app.IntentService;
import android.content.Intent;

import us.michaelchen.compasslogger.utils.PreferencesWrapper;

/**
 * Created by ioreyes on 6/15/16.
 */
public class PeriodicTimeUpdateService extends IntentService {
    public PeriodicTimeUpdateService() {
        super("PeriodicTimeUpdateService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        PreferencesWrapper.updateLastAlarmTimestamp();
    }
}
