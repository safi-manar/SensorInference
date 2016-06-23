package us.michaelchen.compasslogger.periodicservices.datarecording;

import android.hardware.Sensor;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by ioreyes on 6/15/16.
 */
public class AccelerometerSensorRecordingService extends AbstractMotionSensorRecordingService {
    private static final Map<String, Object> ACCELEROMETER_BATCH = new LinkedHashMap<>();

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
    protected Map<String, Object> getStaticBatch() {
        return ACCELEROMETER_BATCH;
    }
}
