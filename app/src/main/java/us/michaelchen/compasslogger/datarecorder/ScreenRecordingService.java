package us.michaelchen.compasslogger.datarecorder;

import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.os.PowerManager;
import android.view.Display;

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

        boolean screenOn = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            // Use the new display state method for SDK version 20 and up
            DisplayManager displayManager = (DisplayManager) getSystemService(DISPLAY_SERVICE);
            for(Display display : displayManager.getDisplays()) {
                screenOn = screenOn || display.getState() != Display.STATE_OFF;
            }
        } else {
            // Use the deprecated power state method for SDK version 19 and down
            PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
            screenOn = powerManager.isScreenOn();
        }

        if(screenOn) {
            data.put(SCREEN_STATE, "on");
        } else {
            data.put(SCREEN_STATE, "off");
        }

        return data;
    }
}
