package us.michaelchen.compasslogger.sensor;

import android.content.Intent;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class GenericReceiverService extends RecordingService {

    public static final String EVENT_KEY = "event";
    public static final String BROADCAST_EVENT_KEY = "broadcast events";

    @Override
    protected void onHandleIntent(Intent intent) {
        if (!intent.hasExtra(EVENT_KEY)) return;
        String event = intent.getStringExtra(EVENT_KEY);
        Map<String, Object> map = new HashMap<>();
        map.put(EVENT_KEY, event);
        map.put(TIME, new Date().toString());
        updateDatabase(BROADCAST_EVENT_KEY, map);
    }
}
