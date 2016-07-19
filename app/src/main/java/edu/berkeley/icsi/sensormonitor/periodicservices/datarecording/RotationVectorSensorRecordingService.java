package edu.berkeley.icsi.sensormonitor.periodicservices.datarecording;

import android.hardware.Sensor;

/**
 * Created by ioreyes on 6/22/16.
 */
public class RotationVectorSensorRecordingService extends AbstractSensorRecordingService {
    public RotationVectorSensorRecordingService() {
        super("RotationVectorSensorRecordingService");
    }

    @Override
    protected int getSensorType() {
        return Sensor.TYPE_ROTATION_VECTOR;
    }

    @Override
    protected String broadcastKey() {
        return "rotation";
    }
}
