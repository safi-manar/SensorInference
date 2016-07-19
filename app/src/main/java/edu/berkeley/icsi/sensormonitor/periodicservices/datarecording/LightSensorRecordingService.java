package edu.berkeley.icsi.sensormonitor.periodicservices.datarecording;

import android.hardware.Sensor;

/**
 * Created by ioreyes on 5/25/16.
 */
public class LightSensorRecordingService extends AbstractSensorRecordingService {
    public LightSensorRecordingService() {
        super("LightSensorRecordingService");
    }

    @Override
    protected int getSensorType() {
        return Sensor.TYPE_LIGHT;
    }

    @Override
    protected String broadcastKey() {
        return "light";
    }
}
