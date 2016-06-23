package us.michaelchen.compasslogger.periodicservices.datarecording;

import android.hardware.Sensor;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by ioreyes on 6/15/16.
 */
public class GyroscopeSensorRecordingService extends AbstractMotionSensorRecordingService {
    private static final ConcurrentMap<String, Object> GYROSCOPE_BATCH = new ConcurrentHashMap<>();

    public GyroscopeSensorRecordingService() {
        super("GyroscopeSensorRecordingService");
    }

    @Override
    protected int getSensorType() {
        return Sensor.TYPE_GYROSCOPE;
    }

    @Override
    protected String broadcastKey() {
        return "gyroscope";
    }

    @Override
    protected ConcurrentMap<String, Object> getStaticBatch() {
        return GYROSCOPE_BATCH;
    }
}
