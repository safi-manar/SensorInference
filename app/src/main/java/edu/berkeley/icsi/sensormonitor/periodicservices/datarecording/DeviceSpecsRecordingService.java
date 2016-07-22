package edu.berkeley.icsi.sensormonitor.periodicservices.datarecording;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.berkeley.icsi.sensormonitor.R;
import edu.berkeley.icsi.sensormonitor.utils.PreferencesWrapper;

/**
 * Created by ioreyes on 6/2/16.
 */
public class DeviceSpecsRecordingService extends AbstractRecordingService {
    private static final String MANUFACTURER_KEY = "manufacturer";
    private static final String MODEL_KEY = "model";
    private static final String SDK_NUM_KEY = "sdkLevel";
    private static final String ANDROID_VERSION_KEY = "androidVersion";
    private static final String SENSOR_COUNT_KEY = "sensorCount";
    private static final String SENSOR_KEY = "sensor-%02d";
    private static final String VERSION_KEY = "gitBuild";
    private static final String VERIF_CODE = "VERIF-CODE";


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
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);
        data.put(SENSOR_COUNT_KEY, sensorList.size());

        int index = 0;
        for(Sensor s : sensorList) {
            String sensorKey = String.format(SENSOR_KEY, index++);
            data.put(sensorKey, s.getName());

            // Get hardware FIFO information for builds that have it
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                String fifoMaxKey = sensorKey + "-fifoMax";
                String fifoReservedKey = sensorKey + "-fifoReserved";

                data.put(fifoMaxKey, s.getFifoMaxEventCount());
                data.put(fifoReservedKey, s.getFifoReservedEventCount());
            }
        }

        // The hash value from Strings.xml will be replaced by Gradle build
        data.put(VERSION_KEY, getString(R.string.app_version));

        // Collect Verification Code
        data.put(VERIF_CODE, PreferencesWrapper.getVerifCode());

        return data;
    }
}
