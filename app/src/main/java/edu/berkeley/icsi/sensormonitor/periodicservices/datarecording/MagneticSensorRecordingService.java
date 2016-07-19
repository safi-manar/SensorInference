package edu.berkeley.icsi.sensormonitor.periodicservices.datarecording;

import android.hardware.Sensor;

/**
 * Created by ioreyes on 6/15/16.
 */
public class MagneticSensorRecordingService extends AbstractSensorRecordingService {
    public MagneticSensorRecordingService() {
        super("MagneticSensorRecordingService");
    }

    @Override
    protected int getSensorType() {
        return Sensor.TYPE_MAGNETIC_FIELD;
    }

    @Override
    protected String broadcastKey() {
        return "magnetic";
    }

}
