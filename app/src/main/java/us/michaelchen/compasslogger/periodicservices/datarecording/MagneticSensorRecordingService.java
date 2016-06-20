package us.michaelchen.compasslogger.periodicservices.datarecording;

import android.hardware.Sensor;
import android.hardware.SensorEvent;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by ioreyes on 6/15/16.
 */
public class MagneticSensorRecordingService extends AbstractSensorRecordingService {
    private static final String MAG_X = "magneticX";
    private static final String MAG_Y = "magneticY";
    private static final String MAG_Z = "magneticZ";

    public MagneticSensorRecordingService() {
        super("MagneticSensorRecordingService");
    }

    @Override
    protected int getSensorType() {
        return Sensor.TYPE_MAGNETIC_FIELD;
    }

    @Override
    protected String broadcastKey() {
        return "magnetic";
    }

    @Override
    protected Map<String, Object> processSensorData(SensorEvent event) {
        float[] values = event.values;

        Map<String, Object> data = new LinkedHashMap<>();
        data.put(MAG_X, values[0]);
        data.put(MAG_Y, values[1]);
        data.put(MAG_Z, values[2]);

        return data;
    }
}
