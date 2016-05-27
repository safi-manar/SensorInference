package us.michaelchen.compasslogger.sensorservices;

import android.hardware.Sensor;

/**
 * Created by ioreyes on 5/25/16.
 */
public class StepSensorService extends AbstractSensorService {
    public StepSensorService() {
        super("StepSensorService");
    }

    @Override
    protected int getSensorType() {
        return Sensor.TYPE_STEP_COUNTER;
    }

    @Override
    protected String broadcastKey() {
        return "steps";
    }
}
