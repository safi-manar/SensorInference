package us.michaelchen.compasslogger.sensorservices;

import android.content.Intent;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ioreyes on 5/24/16.
 */
public class GenericReceiverService extends AbstractRecordingService {
    public static final String EVENT_KEY = "event";

    public GenericReceiverService() {
        super("GenericReceiverService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(intent.hasExtra(EVENT_KEY)) {
            String event = intent.getStringExtra(EVENT_KEY);

            Map<String, Object> map = new HashMap<>();
            map.put(EVENT_KEY, event);

            updateDatabase(map);
        }
    }

    @Override
    protected String broadcastKey() {
        return "broadcast events";
    }
}
