package us.michaelchen.compasslogger.periodicservices.datarecording;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.SystemClock;

import java.util.HashMap;
import java.util.Map;

import us.michaelchen.compasslogger.utils.DataTimeFormat;
import us.michaelchen.compasslogger.utils.TimeConstants;

/**
 * Created by ioreyes on 5/25/16.
 */
public abstract class AbstractSensorRecordingService extends AbstractRecordingService {
    private static final String READABLE_EVENT_TIME_KEY = "eventTimeReadable";
    private static final String EVENT_TIMESTAMP_KEY = "eventTimeStamp";
    private static final String VALUES_KEY = "values-%d";

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
        // Activate the sensor until a readout is collected from it
        registerSensorListener();
        long stopTime = System.currentTimeMillis() + TimeConstants.MAX_SENSOR_TIME;
        while(data == null && System.currentTimeMillis() < stopTime) {
            try {
                Thread.sleep(TimeConstants.SENSOR_DATA_POLL_INTERVAL);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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
        sensorManager.registerListener(SENSOR_LISTENER, sensor, SensorManager.SENSOR_DELAY_NORMAL);
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
    protected final Map<String, Object> processSensorData(SensorEvent event) {
        long timestamp = toTimestampUTC(event.timestamp);
        float[] values = event.values;

        Map<String, Object> data = new HashMap<>();
        data.put(EVENT_TIMESTAMP_KEY, timestamp);
        data.put(READABLE_EVENT_TIME_KEY, DataTimeFormat.format(timestamp));

        for(int n = 0; n < values.length; n++) {
            String valuesKey = String.format(VALUES_KEY, n);
            data.put(valuesKey, values[n]);
        }

        return data;
    }

    /**
     *
     * @param eventTimestamp Timestamp from the SensorEvent in nanoseconds since uptime
     * @return Equivalent timestamp in milliseconds since epoch
     */
    private long toTimestampUTC(long eventTimestamp) {
        long currentMS = System.currentTimeMillis();
        long uptimeMS = SystemClock.elapsedRealtime();
        long timestampMS = eventTimestamp / 1000000; // ns to ms

        return currentMS - uptimeMS + timestampMS;
    }

    /**
     * Defines which sensor to use
     * @return A Sensor.SENSOR_TYPE enumerated value
     */
    protected abstract int getSensorType();
}
