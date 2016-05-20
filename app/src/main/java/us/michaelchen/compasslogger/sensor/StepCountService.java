package us.michaelchen.compasslogger.sensor;

import android.hardware.Sensor;

public class StepCountService extends SensorService {
    public static final String STEP_KEY = "steps";

    @Override
    String getKey() {
        return STEP_KEY;
    }

    @Override
    int getSensorType() {
        return Sensor.TYPE_STEP_COUNTER;
    }
}
