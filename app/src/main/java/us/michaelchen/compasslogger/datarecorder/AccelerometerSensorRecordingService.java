package us.michaelchen.compasslogger.datarecorder;

import android.hardware.Sensor;
import android.hardware.SensorEvent;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by ioreyes on 6/15/16.
 */
public class AccelerometerSensorRecordingService extends AbstractSensorRecordingService {
    private static final String ACC_X_KEY = "accX";
    private static final String ACC_Y_KEY = "accY";
    private static final String ACC_Z_KEZ = "accZ";

    public AccelerometerSensorRecordingService() {
        super("AccelerometerSensorRecordingService");
    }

    @Override
    protected int getSensorType() {
        return Sensor.TYPE_ACCELEROMETER;
    }

    @Override
    protected String broadcastKey() {
        return "accelerometer";
    }

    @Override
    protected Map<String, Object> processSensorData(SensorEvent event) {
        float[] values = event.values;

        Map<String, Object> data = new LinkedHashMap<>();
        data.put(ACC_X_KEY, values[0]);
        data.put(ACC_Y_KEY, values[1]);
        data.put(ACC_Z_KEZ, values[2]);

        return data;
    }
}
