package us.michaelchen.compasslogger.datarecorder;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ioreyes on 5/25/16.
 */
public abstract class AbstractSensorRecordingService extends AbstractRecordingService {
    private Map<String, Object> data = null;

    private final SensorEventListener SENSOR_LISTENER = new SensorEventListener() {

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            data = processSensorData(event);
        }
    };

    protected AbstractSensorRecordingService(String subclassName) {
        super(subclassName);
    }

    @Override
    protected final Map<String, Object> readData(Intent intent) {
        registerSensorListener();
        while(data == null) {

        }
        unregisterSensorListener();

        return data;
    }

    /**
     * Starts updates from the sensor
     */
    private void registerSensorListener() {
        data = null;

        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(getSensorType());
        sensorManager.registerListener(SENSOR_LISTENER, sensor, SensorManager.SENSOR_DELAY_UI);
    }

    /**
     * Stops updates from the sensor
     */
    private void unregisterSensorListener() {
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.unregisterListener(SENSOR_LISTENER);
    }

    /**
     * Read sensor data.
     * @param event
     * @return A map of labels and corresponding values
     */
    protected Map<String, Object> processSensorData(SensorEvent event) {
        float value = event.values[0];

        Map<String, Object> data = new HashMap<>();
        data.put(broadcastKey(), value);

        return data;
    }

    /**
     * Defines which sensor to use
     * @return A Sensor.SENSOR_TYPE enumerated value
     */
    protected abstract int getSensorType();
}
