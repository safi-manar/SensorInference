package us.michaelchen.compasslogger;

import android.app.Application;

import us.michaelchen.compasslogger.utils.PreferencesWrapper;

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
