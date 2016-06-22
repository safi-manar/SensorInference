package us.michaelchen.compasslogger.periodicservices.datarecording;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Build;

import java.util.LinkedHashMap;
import java.util.Map;

import us.michaelchen.compasslogger.utils.DataTimeFormat;
import us.michaelchen.compasslogger.utils.TimeConstants;

/**
 * Created by ioreyes on 6/22/16.
 */
public abstract class AbstractMotionSensorRecordingService extends AbstractSensorRecordingService {
    private static final int MAX_EVENTS_PER_PERIOD = (int) (TimeConstants.PERIODIC_LENGTH / TimeConstants.SENSOR_DATA_POLL_INTERVAL);
    private static final int MAX_EVENTS_PER_PERIOD_WITH_TOLERANCE = (int) Math.floor(MAX_EVENTS_PER_PERIOD * 0.95);

    private static final String BATCH_KEY = "batch-%05d";

    protected AbstractMotionSensorRecordingService(String subclassName) {
        super(subclassName);
    }

    private int batchSize = -1;
    private Sensor sensor = null;
    private Map<String, Object> dataBatch = new LinkedHashMap<>();

    @Override
    protected Map<String, Object> readData(Intent intent) {
        // Initialize the FIFO length if it's currently unknown
        if(batchSize < 0 && sensor == null) {
            init();
        }

        if(batchSize > 0 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // Reset the data buffer
            dataBatch.clear();

            // Take advantage of the FIFO hardware queue if it's present
            int samplingPeriodMs = (int)TimeConstants.SENSOR_DATA_POLL_INTERVAL;
            int samplingPeriodUs = samplingPeriodMs * 1000;  // ms to us
            int maxReportLatencyUs = samplingPeriodUs * batchSize;
            int maxReportLatencyMs = maxReportLatencyUs / 1000; // us to ms

            SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            sensorManager.registerListener(SENSOR_LISTENER,
                                           sensor,
                                           samplingPeriodUs,
                                           maxReportLatencyUs);

            // Put the thread to sleep while data is going into the FIFO
            try {
                Thread.sleep(maxReportLatencyMs);
            } catch(InterruptedException e) {
                // Do nothing
                e.printStackTrace();
            }

            sensorManager.flush(SENSOR_LISTENER);
            sensorManager.unregisterListener(SENSOR_LISTENER);

            return dataBatch;
        } else {
            // Do the default "snapshot" single data point collection if batching is unavailable
            return super.readData(intent);
        }
    }

    /**
     * Initialize the sensor and batch size fields
     */
    private void init() {
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(getSensorType());

        // FIFO capability is only available for SDK level 19 and up
        if(sensor != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int guaranteedFIFO = sensor.getFifoReservedEventCount();

            // The sensor will either fill up the FIFO, or collect as much as it can within a
            // reporting period, whichever is less
            batchSize = Math.min(guaranteedFIFO, MAX_EVENTS_PER_PERIOD_WITH_TOLERANCE);
        } else {
            batchSize = 0;
        }
    }

    @Override
    protected Map<String, Object> processSensorData(SensorEvent event) {
        long timestamp = toTimestampUTC(event.timestamp);
        float[] values = event.values;

        Map<String, Object> subData = new LinkedHashMap<>();
        subData.put(TIMESTAMP_KEY, timestamp);
        subData.put(READABLE_TIME_KEY, DataTimeFormat.format(timestamp));
        for(int n = 0; n < values.length; n++) {
            String valuesKey = String.format(VALUES_KEY, n);
            subData.put(valuesKey, values[n]);
        }

        String topKey = String.format(BATCH_KEY, dataBatch.size());
        dataBatch.put(topKey, subData);

        return dataBatch;
    }
}
