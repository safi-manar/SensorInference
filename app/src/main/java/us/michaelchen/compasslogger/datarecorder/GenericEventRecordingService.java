package us.michaelchen.compasslogger.datarecorder;

import android.content.Intent;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ioreyes on 5/24/16.
 */
public class GenericEventRecordingService extends AbstractRecordingService {
    private static final String EVENT_KEY = "event";

    public GenericEventRecordingService() {
        super("GenericEventRecordingService");
    }

    @Override
    protected String broadcastKey() {
        return "broadcast events";
    }

    @Override
    protected Map<String, Object> readData(Intent intent) {
        Map<String, Object> data = new HashMap<>();

        if (intent.hasExtra(EVENT_KEY)) {
            String event = intent.getStringExtra(EVENT_KEY);
            data.put(EVENT_KEY, event);
        }

        return data;
    }
}
