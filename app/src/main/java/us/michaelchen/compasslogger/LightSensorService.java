package us.michaelchen.compasslogger;

import android.hardware.Sensor;

public class LightSensorService extends SensorService {

    public static final String TAG = "LightSensorService";
    public static final String LIGHT_KEY = "light";


    @Override
    String getKey() {
        return LIGHT_KEY;
    }

    @Override
    int getSensorType() {
        return Sensor.TYPE_LIGHT;
    }
}
