package us.michaelchen.compasslogger.datarecorder;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ioreyes on 5/26/16.
 */
public class RotationSensorRecordingService extends AbstractSensorRecordingService {
    private static final String AZIMUTH_KEY = "azimuth";
    private static final String ROTATION_KEY = "rotations";

    public RotationSensorRecordingService() {
        super("RotationSensorRecordingService");
    }

    @Override
    protected int getSensorType() {
        return Sensor.TYPE_ROTATION_VECTOR;
    }

    @Override
    protected String broadcastKey() {
        return "rotation";
    }

    @Override
    protected Map<String, Object> processSensorData(SensorEvent event) {
        float[] values = event.values;
        float[] rotMatrix = new float[9];
        float[] orientation = new float[3];

        // Calculate the azimuth
        SensorManager.getRotationMatrixFromVector(rotMatrix, values);
        int azimuth = (int) ( Math.toDegrees( SensorManager.getOrientation( rotMatrix, orientation )[0] ) + 360 ) % 360;

        // List-ify the rotation sensor values
        List<Float> rotations = new ArrayList<>(values.length);
        for(float f : values) {
            rotations.add(f);
        }

        // Package up the data
        Map<String, Object> vals = new HashMap<>();
        vals.put(AZIMUTH_KEY, azimuth);
        vals.put(ROTATION_KEY, rotations);

        return vals;
    }
}
