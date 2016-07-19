package edu.berkeley.icsi.sensormonitor.periodicservices;

import android.app.IntentService;
import android.content.Intent;

import edu.berkeley.icsi.sensormonitor.utils.PreferencesWrapper;

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
