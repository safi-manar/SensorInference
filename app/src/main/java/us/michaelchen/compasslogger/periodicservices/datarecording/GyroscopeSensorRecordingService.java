package us.michaelchen.compasslogger.periodicservices.datarecording;

import android.hardware.Sensor;

/**
 * Created by ioreyes on 6/15/16.
 */
public class GyroscopeSensorRecordingService extends AbstractSensorRecordingService {
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

}
