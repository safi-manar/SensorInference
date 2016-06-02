package us.michaelchen.compasslogger;

import android.app.Application;

import com.firebase.client.Firebase;

/**
 * Created by ioreyes on 6/2/16.
 */
public class LoggerApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
    }
}
