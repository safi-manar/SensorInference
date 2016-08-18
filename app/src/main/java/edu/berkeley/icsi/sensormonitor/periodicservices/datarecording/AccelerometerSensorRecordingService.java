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
public class AccelerometerSensorRecordingService extends AbstractMotionSensorRecordingService {
    private static final List<Map<String, Object>> ACCELEROMETER_BATCH = Collections.synchronizedList(new LinkedList<Map<String, Object>>());
    private static final Handler ACCELEROMETER_HANDLER = new Handler();

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
    protected List<Map<String, Object>> getStaticBatchBuffer() {
        return ACCELEROMETER_BATCH;
    }

    @Override
    protected Handler getStaticHandler() {
        return ACCELEROMETER_HANDLER;
    }

    @Override
    protected int getSamplingPeriodMs() {
        final int FREQUENCY_HZ = 50;
        return 1000 / FREQUENCY_HZ;
    }
}
