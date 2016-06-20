package us.michaelchen.compasslogger.periodicservices.datarecording;

import android.hardware.Sensor;
import android.hardware.SensorEvent;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by ioreyes on 6/15/16.
 */
public class GyroscopeSensorRecordingService extends AbstractSensorRecordingService {
    private static final String ANG_SPEED_X = "angSpeedX";
    private static final String ANG_SPEED_Y = "angSpeedY";
    private static final String ANG_SPEED_Z = "angSpeedZ";

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


    @Override
    protected Map<String, Object> processSensorData(SensorEvent event) {
        float[] values = event.values;

        Map<String, Object> data = new LinkedHashMap<>();
        data.put(ANG_SPEED_X, values[0]);
        data.put(ANG_SPEED_Y, values[1]);
        data.put(ANG_SPEED_Z, values[2]);

        return data;
    }
}
