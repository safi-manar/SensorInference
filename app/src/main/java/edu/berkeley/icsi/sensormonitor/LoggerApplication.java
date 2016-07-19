package edu.berkeley.icsi.sensormonitor;

import android.app.Application;

import edu.berkeley.icsi.sensormonitor.utils.PreferencesWrapper;

/**
 * Created by ioreyes on 6/2/16.
 */

public class LoggerApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        PreferencesWrapper.init(this);
    }
}
