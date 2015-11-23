package us.michaelchen.compasslogger;

import android.hardware.Sensor;

public class ProximitySensorService extends SensorService {

    public static final String TAG = "ProxSensorService";
    public static final String PROXIMITY_KEY = "proximity";


    @Override
    String getKey() {
        return PROXIMITY_KEY;
    }

    @Override
    int getSensorType() {
        return Sensor.TYPE_PROXIMITY;
    }
}
