package edu.berkeley.icsi.sensormonitor.periodicservices.datarecording;

import android.annotation.TargetApi;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener2;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.berkeley.icsi.sensormonitor.periodicservices.datadestination.AbstractDataDestination;
import edu.berkeley.icsi.sensormonitor.periodicservices.datadestination.WifiUploadDestination;
import edu.berkeley.icsi.sensormonitor.utils.TimeConstants;

/**
 * Created by ioreyes on 6/22/16.
 */
public abstract class AbstractMotionSensorRecordingService extends AbstractSensorRecordingService {
    // Label for batch entries
    private static final String BATCH_KEY = "batch-%05d";

    // Comparator used to sort events by their time
    private final Comparator<Map<String, Object>> BATCH_ENTRY_COMPARATOR = new Comparator() {

        @Override
        public int compare(Object lhs, Object rhs) {
            Map<String, Object> left = (Map<String, Object>)lhs;
            Map<String, Object> right = (Map<String, Object>)rhs;

            // Compare batch data by timestamp
            long leftTimestamp = (long)left.get(TIMESTAMP_RAW_KEY);
            long rightTimestamp = (long)right.get(TIMESTAMP_RAW_KEY);
            long difference = leftTimestamp - rightTimestamp;

            if(difference < 0l) {
                return -1;
            } else if(difference > 0l) {
                return 1;
            }

            return 0;
        }
    };

    // Synchronization variables
    private boolean isFlushing = false;

    protected AbstractMotionSensorRecordingService(String subclassName) {
        super(subclassName);
    }

    @Override
    protected final Map<String, Object> readData(Intent intent) {
        // Determine the FIFO length
        final SensorManager SENSOR_MANAGER = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor sensor = SENSOR_MANAGER.getDefaultSensor(getSensorType());
        int samplingPeriodMs = getSamplingPeriodMs();
        int batchReportSize = getBatchReportSize(sensor, samplingPeriodMs);

        if(batchReportSize > 0 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return batch(sensor, batchReportSize, samplingPeriodMs);
        } else {
            // Do the default "snapshot" single data point collection if batching is unavailable
            return super.readData(intent);
        }
    }

    /**
     *
     * @return The time between consecutive sensor events, in milliseconds
     */
    protected int getSamplingPeriodMs() {
        return (int) TimeConstants.SENSOR_DATA_POLL_INTERVAL;
    }

    /**
     * Set up batch collection, and retrieve the results of the previous batch, if available
     * @param sensor Sensor to collect data from
     * @param batchReportSize The number of measurements in a batch
     * @param samplingPeriodMs The minimum time between consecutive batch measurements
     * @return The events from the previous batch, null if not available
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private Map<String, Object> batch(Sensor sensor, int batchReportSize, int samplingPeriodMs) {
        // Compute batch sampling and reporting intervals
        int samplingPeriodUs = samplingPeriodMs * 1000; // ms to us
        int maxReportLatencyUs = samplingPeriodUs * batchReportSize;
        final int maxReportLatencyMs = maxReportLatencyUs / 1000; // us to ms

        // Set up a listener for sensor data and flush events
        final SensorEventListener2 BATCH_SENSOR_LISTENER = new SensorEventListener2() {
            @Override
            public void onFlushCompleted(Sensor sensor) {
                isFlushing = false;
            }

            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                Map<String, Object> data = processSensorData(sensorEvent);
                operateOnBatchBuffer(BatchOps.ADD, data);
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };

        // Implement explicit flushing of FIFO data and unregistering of listeners
        final SensorManager SENSOR_MANAGER = (SensorManager) getSystemService(SENSOR_SERVICE);
        final Runnable FLUSH_RUNNABLE = new Runnable() {
            private Date creation = new Date();
            private int delay = maxReportLatencyMs;

            @Override
            public void run() {
                Log.d("FLUSH_RUNNABLE", String.format("Created %s | Ran %s", creation.toString(), new Date().toString()));
                long stopTime = System.currentTimeMillis() + TimeConstants.MAX_SENSOR_TIME;
                isFlushing = SENSOR_MANAGER.flush(BATCH_SENSOR_LISTENER);
                while(isFlushing && System.currentTimeMillis() < stopTime) {
                    try {
                        Thread.sleep(getSamplingPeriodMs());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                // Schedule to flush the sensor when it's (mostly) expected to be full
                getStaticHandler().postDelayed(this, delay);
            }
        };

        Log.d("BATCH", new Date().toString());

        // Stop and flush any existing listeners
        FLUSH_RUNNABLE.run();
        getStaticHandler().removeCallbacksAndMessages(null);
        SENSOR_MANAGER.unregisterListener(BATCH_SENSOR_LISTENER);

        // Make a copy of any existing data in the batch and reset it
        int minimumInterval = (int) Math.floor(0.75 * samplingPeriodMs);
        Map<String, Object> previousBatch = getDownsampledBatch(minimumInterval);
        operateOnBatchBuffer(BatchOps.CLEAR, null);

        // Set up the sensor to use the hardware FIFO batch queue
        SENSOR_MANAGER.registerListener(BATCH_SENSOR_LISTENER,
                                        sensor,
                                        samplingPeriodUs,
                                        maxReportLatencyUs);

        // Schedule to flush (mostly) expected to be full
        getStaticHandler().postDelayed(FLUSH_RUNNABLE, maxReportLatencyMs);

        // Return any data collected from the previous reporting cycle
        return previousBatch;
    }

    /**
     *
     * @param sensor
     * @param samplingPeriodMs Time interval between batched sensor events, in milliseconds.
     * @return The number of events in a reported batch. Either the number of events in a reporting period,
     * or the size of the FIFO, whichever is less.
     */
    private int getBatchReportSize(Sensor sensor, int samplingPeriodMs) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int samplesInPeriod = (int) (TimeConstants.PERIODIC_LENGTH / samplingPeriodMs);
            int guaranteedFifo = sensor.getFifoReservedEventCount();

            // The sensor will collect as many measurements as can fit in a reporting period, or
            // fill up the FIFO queue, whichever is less
            int reportSizeWithTolerance = (int) Math.floor(0.95 * Math.min(samplesInPeriod, guaranteedFifo));
            return reportSizeWithTolerance;
        } else {
            // 0 if batching is unsupported by the Android version
            return 0;
        }
    }

    /**
     *
     * @return A static list of processed batch data that persists between service invocations
     */
    protected abstract List<Map<String, Object>> getStaticBatchBuffer();

    /**
     *
     * @return A static handler to queue runnables for this particular sensor
     */
    protected abstract Handler getStaticHandler();

    private enum BatchOps {
        ADD,
        CLEAR,
        COPY
    }

    /**
     * Atomic operations on the static batch buffer
     * @param op Operation to perform; may be BatchOps.ADD, CLEAR, or COPY
     * @param data Data to append to the buffer if BatchOps.ADD; may be null
     * @return If ADD or CLEAR, a reference to the batch buffer. If copy, a reference to a new shallow
     * copy of the batch buffer.
     */
    private synchronized List<Map<String, Object>> operateOnBatchBuffer(BatchOps op, Map<String, Object> data) {
        List<Map<String, Object>> batchBuffer = getStaticBatchBuffer();

        switch(op) {
            case ADD:
                if(data != null) {
                    batchBuffer.add(data);
                }
                break;

            case CLEAR:
                batchBuffer.clear();
                break;

            case COPY:
                return new ArrayList<>(batchBuffer);

        }

        return batchBuffer;
    }

    @Override
    protected final AbstractDataDestination getDataDestination() {
        // Use disk-and-wifi-upload destination if batching is available, the default if not
        final SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(getSensorType());
        if(getBatchReportSize(sensor, getSamplingPeriodMs()) > 0) {
            return new WifiUploadDestination(this);
        }

        return super.getDataDestination();
    }

    /**
     *
     * @param minimumIntervalMs The desired minimum amount of time between consecutive measurements,
     *                          in milliseconds
     * @return An ordered mapping of data points, where consecutive points have a minimum amount
     * of time between them
     */
    private Map<String, Object> getDownsampledBatch(long minimumIntervalMs) {
        List<Map<String, Object>> batch = operateOnBatchBuffer(BatchOps.COPY, null);

        if(!batch.isEmpty()) {
            Collections.sort(batch, BATCH_ENTRY_COMPARATOR);

            Map<String, Object> downsampled = new LinkedHashMap<>();
            long lastTimestamp = -1l;

            for(Map<String, Object> data : batch) {
                long currentTimestamp = (long)data.get(TIMESTAMP_KEY);
                long interval = currentTimestamp - lastTimestamp;

                if(lastTimestamp < 0l || interval >= minimumIntervalMs) {
                    String batchLabel = String.format(BATCH_KEY, downsampled.size());
                    downsampled.put(batchLabel, data);

                    lastTimestamp = currentTimestamp;
                }
            }

            return downsampled;
        }

        return null;
    }
}
