package us.michaelchen.compasslogger.datarecorder;

import android.content.Intent;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ioreyes on 5/31/16.
 */
public class ScreenRecordingService extends AbstractRecordingService {
    private static final String SCREEN_STATE = "screenState";

    public ScreenRecordingService() {
        super("ScreenRecordingService");
    }

    @Override
    protected String broadcastKey() {
        return "screen";
    }

    @Override
    protected Map<String, Object> readData(Intent intent) {
        Map<String, Object> data = new HashMap<>();

        boolean screenOn = intent.getAction().equals(Intent.ACTION_SCREEN_ON);
        boolean screenOff = intent.getAction().equals(Intent.ACTION_SCREEN_OFF);

        if(screenOn && !screenOff) {
            data.put(SCREEN_STATE, "on");
        } else if(!screenOn && screenOff) {
            data.put(SCREEN_STATE, "off");
        }

        return data;
    }
}
