package us.michaelchen.compasslogger.datarecorder;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ioreyes on 6/2/16.
 */
public class DeviceSpecsRecordingService extends AbstractRecordingService {
    private static final String MANUFACTURER_KEY = "manufacturer";
    private static final String MODEL_KEY = "model";
    private static final String SDK_NUM_KEY = "sdkLevel";
    private static final String SENSOR_COUNT_KEY = "sensorCount";
    private static final String SENSOR_KEY = "sensor-%d";

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

        // Get sensor information
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);
        data.put(SENSOR_COUNT_KEY, sensorList.size());

        int index = 0;
        for(Sensor s : sensorList) {
            data.put(String.format(SENSOR_KEY, index++), s.getName());
        }

        return data;
    }
}
