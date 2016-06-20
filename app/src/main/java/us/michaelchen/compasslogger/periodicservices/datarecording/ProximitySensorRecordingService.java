package us.michaelchen.compasslogger.periodicservices.datarecording;

import android.hardware.Sensor;

/**
 * Created by ioreyes on 5/25/16.
 */
public class ProximitySensorRecordingService extends AbstractSensorRecordingService {
    public ProximitySensorRecordingService() {
        super("ProximitySensorRecordingService");
    }

    @Override
    protected int getSensorType() {
        return Sensor.TYPE_PROXIMITY;
    }

    @Override
    protected String broadcastKey() {
        return "proximity";
    }
}
