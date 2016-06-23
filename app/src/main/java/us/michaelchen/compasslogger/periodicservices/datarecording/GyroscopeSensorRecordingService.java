package us.michaelchen.compasslogger.periodicservices.datarecording;

import android.hardware.Sensor;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by ioreyes on 6/15/16.
 */
public class GyroscopeSensorRecordingService extends AbstractMotionSensorRecordingService {
    private static final Map<String, Object> GYROSCOPE_BATCH = new LinkedHashMap<>();

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
    protected Map<String, Object> getStaticBatch() {
        return GYROSCOPE_BATCH;
    }
}
