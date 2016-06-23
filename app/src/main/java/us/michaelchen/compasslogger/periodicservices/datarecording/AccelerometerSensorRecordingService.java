package us.michaelchen.compasslogger.periodicservices.datarecording;

import android.hardware.Sensor;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by ioreyes on 6/15/16.
 */
public class AccelerometerSensorRecordingService extends AbstractMotionSensorRecordingService {
    private static final ConcurrentMap<String, Object> ACCELEROMETER_BATCH = new ConcurrentHashMap<>();

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
    protected ConcurrentMap<String, Object> getStaticBatch() {
        return ACCELEROMETER_BATCH;
    }
}
