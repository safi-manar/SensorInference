package us.michaelchen.compasslogger.sensorservices;

import android.hardware.Sensor;

/**
 * Created by ioreyes on 5/25/16.
 */
public class LightSensorService extends AbstractSensorService {
    public LightSensorService() {
        super("LightSensorService");
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
