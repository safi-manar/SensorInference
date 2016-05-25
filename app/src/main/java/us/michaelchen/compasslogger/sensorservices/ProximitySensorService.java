package us.michaelchen.compasslogger.sensorservices;

import android.hardware.Sensor;

/**
 * Created by ioreyes on 5/25/16.
 */
public class ProximitySensorService extends AbstractSensorService {
    public ProximitySensorService() {
        super("ProximitySensorService");
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
