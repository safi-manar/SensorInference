package edu.berkeley.icsi.sensormonitor.periodicservices.keepalive;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by ioreyes on 6/20/16.
 */
public abstract class AbstractKeepAliveService extends IntentService {
    public static final String ACTION_SHUTDOWN = "AbstractKeepAliveService_shutdown";

    protected AbstractKeepAliveService(String name) {
        super(name);
    }

    @Override
    protected final void onHandleIntent(Intent intent) {
        if(intent.getAction() != null && intent.getAction().equals(ACTION_SHUTDOWN)) {
            shutDown();
        } else {
            keepAlive();
        }
    }

    /**
     * Keep a particular asset (e.g., sensor, service, or receiver) active
     */
    protected abstract void keepAlive();

    /**
     * Stop a particular asset and clean up after it
     */
    protected abstract void shutDown();
}
