package us.michaelchen.compasslogger.periodicservices.datarecording;

import android.hardware.Sensor;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by ioreyes on 6/15/16.
 */
public class GyroscopeSensorRecordingService extends AbstractMotionSensorRecordingService {
    private static final List<Map<String, Object>> GYROSCOPE_BATCH = Collections.synchronizedList(new LinkedList<Map<String, Object>>());

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
    protected List<Map<String, Object>> getStaticList() {
        return GYROSCOPE_BATCH;
    }
}

