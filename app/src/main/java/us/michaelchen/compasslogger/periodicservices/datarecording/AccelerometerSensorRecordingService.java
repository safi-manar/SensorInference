package us.michaelchen.compasslogger.periodicservices.datarecording;

import android.hardware.Sensor;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by ioreyes on 6/15/16.
 */
public class AccelerometerSensorRecordingService extends AbstractMotionSensorRecordingService {
    private static final List<Map<String, Object>> ACCELEROMETER_BATCH = Collections.synchronizedList(new LinkedList<Map<String, Object>>());

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
    protected List<Map<String, Object>> getStaticList() {
        return ACCELEROMETER_BATCH;
    }
}
