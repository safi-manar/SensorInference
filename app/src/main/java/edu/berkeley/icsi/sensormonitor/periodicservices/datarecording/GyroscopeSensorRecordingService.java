package edu.berkeley.icsi.sensormonitor.periodicservices.datarecording;

import android.hardware.Sensor;
import android.os.Handler;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by ioreyes on 6/15/16.
 */
public class GyroscopeSensorRecordingService extends AbstractMotionSensorRecordingService {
    private static final List<Map<String, Object>> GYROSCOPE_BATCH = Collections.synchronizedList(new LinkedList<Map<String, Object>>());
    private static final Handler GYROSCOPE_HANDLER = new Handler();

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
    protected List<Map<String, Object>> getStaticBatchBuffer() {
        return GYROSCOPE_BATCH;
    }

    @Override
    protected Handler getStaticHandler() {
        return GYROSCOPE_HANDLER;
    }
}

