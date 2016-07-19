package edu.berkeley.icsi.sensormonitor.periodicservices.datarecording;

import android.hardware.Sensor;

/**
 * Created by ioreyes on 6/30/16.
 */
public class TemperatureSensorRecordingService extends AbstractSensorRecordingService {
    public TemperatureSensorRecordingService() {
        super("TemperatureSensorRecordingService");
    }

    @Override
    protected int getSensorType() {
        return Sensor.TYPE_AMBIENT_TEMPERATURE;
    }

    @Override
    protected String broadcastKey() {
        return "temperature";
    }
}
