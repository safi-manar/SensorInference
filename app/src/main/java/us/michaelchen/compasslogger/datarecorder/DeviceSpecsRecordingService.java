package us.michaelchen.compasslogger.datarecorder;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import us.michaelchen.compasslogger.R;
import us.michaelchen.compasslogger.utils.PreferencesWrapper;

/**
 * Created by ioreyes on 6/2/16.
 */
public class DeviceSpecsRecordingService extends AbstractRecordingService {
    private static final String MANUFACTURER_KEY = "manufacturer";
    private static final String MODEL_KEY = "model";
    private static final String SDK_NUM_KEY = "sdkLevel";
    private static final String ANDROID_VERSION_KEY = "androidVersion";
    private static final String SENSOR_COUNT_KEY = "sensorCount";
    private static final String SENSOR_KEY = "sensor-%d";
    private static final String VERSION_KEY = "gitBuild";
    private static final String MTURK_ID = "MTURK-ID";

    public DeviceSpecsRecordingService() {
        super("DeviceSpecsRecordingService");
    }

    @Override
    protected String broadcastKey() {
        return "deviceSpecs";
    }

    @Override
    protected Map<String, Object> readData(Intent intent) {
        Map<String, Object> data = new LinkedHashMap<>();

        // Get device model and OS
        data.put(MANUFACTURER_KEY, Build.MANUFACTURER);
        data.put(MODEL_KEY, Build.MODEL);
        data.put(SDK_NUM_KEY, Build.VERSION.SDK_INT);
        data.put(ANDROID_VERSION_KEY, Build.VERSION.RELEASE);

        // Get sensor information
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);
        data.put(SENSOR_COUNT_KEY, sensorList.size());

        int index = 0;
        for(Sensor s : sensorList) {
            data.put(String.format(SENSOR_KEY, index++), s.getName());
        }

        // The hash value from Strings.xml will be replaced by Gradle build
        data.put(VERSION_KEY, getString(R.string.app_version));

        // Get MTURK ID
        data.put(MTURK_ID, PreferencesWrapper.getMTURK());

        return data;
    }
}
