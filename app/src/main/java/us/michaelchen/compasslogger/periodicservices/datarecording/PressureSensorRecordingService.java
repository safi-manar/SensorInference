package us.michaelchen.compasslogger.periodicservices.datarecording;

import android.hardware.Sensor;

/**
 * Created by ioreyes on 6/30/16.
 */
public class PressureSensorRecordingService extends AbstractSensorRecordingService {
    public PressureSensorRecordingService() {
        super("PressureSensorRecordingService");
    }

    @Override
    protected int getSensorType() {
        return Sensor.TYPE_PRESSURE;
    }

    @Override
    protected String broadcastKey() {
        return "pressure";
    }
}
