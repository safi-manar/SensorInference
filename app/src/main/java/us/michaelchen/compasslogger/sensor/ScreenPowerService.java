package us.michaelchen.compasslogger.sensor;

import android.content.Intent;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ScreenPowerService extends RecordingService {
    public static final String SCREEN_STATE = "screen_state";
    public static final String SCREEN_ON = "screen_on";

    @Override
    protected void onHandleIntent(Intent intent) {
        if (!intent.hasExtra(SCREEN_STATE)) return;
        boolean screenOn = intent.getBooleanExtra(SCREEN_STATE, false);
        Map<String, Object> map = new HashMap<>();
        map.put(SCREEN_ON, screenOn);
        map.put(TIME, new Date().toString());
        updateDatabase(SCREEN_STATE, map);
    }
}
