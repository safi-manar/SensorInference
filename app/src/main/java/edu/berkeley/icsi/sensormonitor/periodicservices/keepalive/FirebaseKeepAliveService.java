package edu.berkeley.icsi.sensormonitor.periodicservices.keepalive;

import edu.berkeley.icsi.sensormonitor.utils.FirebaseWrapper;

/**
 * Created by ioreyes on 7/6/16.
 */
public class FirebaseKeepAliveService extends AbstractKeepAliveService {
    public FirebaseKeepAliveService() {
        super("FirebaseKeepAliveService");
    }

    @Override
    protected void keepAlive() {
        FirebaseWrapper.init();
    }

    @Override
    protected void shutDown() {
        // Do nothing
    }
}
