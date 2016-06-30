package us.michaelchen.compasslogger.periodicservices.datarecording;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Handler;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import us.michaelchen.compasslogger.periodicservices.datadestination.AbstractDataDestination;
import us.michaelchen.compasslogger.periodicservices.datadestination.WifiUploadDestination;
import us.michaelchen.compasslogger.utils.TimeConstants;

/**
 * Created by ioreyes on 6/22/16.
 */
public abstract class AbstractMotionSensorRecordingService extends AbstractSensorRecordingService {
    private static final int MAX_EVENTS_PER_PERIOD = (int) (TimeConstants.PERIODIC_LENGTH / TimeConstants.SENSOR_DATA_POLL_INTERVAL);
    private static final int MAX_EVENTS_PER_PERIOD_WITH_TOLERANCE = (int) Math.floor(MAX_EVENTS_PER_PERIOD * 0.99);
    private static final String BATCH_KEY = "batch-%05d";

    private final SensorEventListener BATCH_SENSOR_LISTENER = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            // Atomically update the batch
            putInBatch(event);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // Do nothing
        }

        private synchronized void putInBatch(SensorEvent event) {
            // Process event and add to static batch
            Map<String, Object> data = processSensorData(event);
            Map<String, Object> batch = getStaticBatch();

            String key = String.format(BATCH_KEY, batch.size());
            batch.put(key, data);
        }
    };
    private final Handler UNREGISTER_HANDLER = new Handler();

    protected AbstractMotionSensorRecordingService(String subclassName) {
        super(subclassName);
    }

    @Override
    protected Map<String, Object> readData(Intent intent) {
        // Determine the FIFO length
        final SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(getSensorType());
        int batchSize = getBatchSize(sensor);

        if(batchSize > 0 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // Make a copy of any existing data in the batch and reset it
            Map<String, Object> batch = getStaticBatch();
            Map<String, Object> previousBatch = null;
            if(!batch.isEmpty()) {
                previousBatch = new LinkedHashMap<>(batch);
            }
            batch.clear();

            // Set up the sensor to use the hardware FIFO batch queue
            int samplingPeriodMs = (int)TimeConstants.SENSOR_DATA_POLL_INTERVAL;
            int samplingPeriodUs = samplingPeriodMs * 1000;  // ms to us
            int maxReportLatencyUs = samplingPeriodUs * batchSize;
            int maxReportLatencyMs = maxReportLatencyUs / 1000; // us to ms
            sensorManager.registerListener(BATCH_SENSOR_LISTENER,
                                           sensor,
                                           samplingPeriodUs,
                                           maxReportLatencyUs);

            // Flush and stop the sensor when the batch is expected to be full
            UNREGISTER_HANDLER.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        sensorManager.flush(BATCH_SENSOR_LISTENER);
                        sensorManager.unregisterListener(BATCH_SENSOR_LISTENER);
                    }
                }
            }, maxReportLatencyMs);

            // Return any data collected from the previous reporting cycle
            return previousBatch;
        } else {
            // Do the default "snapshot" single data point collection if batching is unavailable
            return super.readData(intent);
        }
    }

    /**
     * Calculate the expected size of a batch, defined as either the reserved FIFO size for the
     * sensor or the number of FIFO events that can occur within a periodic interval (with a small
     * tolerance), whichever is less
     * @param sensor
     * @return Positive value to indicate the expected batch size, 0 if FIFO is unavailable or unsupported
     */
    private int getBatchSize(Sensor sensor) {
        // FIFO capability is only available for SDK level 19 and up
        if(sensor != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int guaranteedFIFO = sensor.getFifoReservedEventCount();

            // The sensor will either fill up the FIFO, or collect as much as it can within a
            // reporting period, whichever is less
            return Math.min(guaranteedFIFO, MAX_EVENTS_PER_PERIOD_WITH_TOLERANCE);
        } else {
            return 0;
        }
    }

    /**
     *
     * @return A static map in which batch data will be stored and persist between service instances
     */
    protected abstract ConcurrentMap<String, Object> getStaticBatch();

    @Override
    protected final AbstractDataDestination getDataDestination() {
        // Use disk-and-wifi-upload destination if batching is available, the default if not
        final SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(getSensorType());
        if(getBatchSize(sensor) > 0) {
            return new WifiUploadDestination(this);
        }

        return super.getDataDestination();
    }
}
