package edu.berkeley.icsi.sensormonitor.periodicservices.datarecording;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener2;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
    private static final int MAX_EVENTS_PER_PERIOD = (int) (TimeConstants.PERIODIC_LENGTH / TimeConstants.SENSOR_DATA_POLL_INTERVAL);
    private static final int MAX_EVENTS_PER_PERIOD_WITH_TOLERANCE = (int) Math.floor(MAX_EVENTS_PER_PERIOD * 0.95);
    private static final long MIN_INTERVAL_BETWEEN_EVENTS_WITH_TOLERANCE = (long) Math.floor(TimeConstants.SENSOR_DATA_POLL_INTERVAL * 0.75);
    private static final String BATCH_KEY = "batch-%05d";

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
    private final Handler UNREGISTER_HANDLER = new Handler();
    private boolean isFlushing = false;

    protected AbstractMotionSensorRecordingService(String subclassName) {
        super(subclassName);
    }

    @Override
    protected Map<String, Object> readData(Intent intent) {
        // Determine the FIFO length
        final SensorManager SENSOR_MANAGER = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor sensor = SENSOR_MANAGER.getDefaultSensor(getSensorType());
        int batchSize = getBatchSize(sensor);

        if(batchSize > 0 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
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
            final Runnable UNREGISTER_RUNNABLE = new Runnable() {
                @Override
                public void run() {
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        long stopTime = System.currentTimeMillis() + TimeConstants.MAX_SENSOR_TIME;
                        isFlushing = SENSOR_MANAGER.flush(BATCH_SENSOR_LISTENER);
                        while(isFlushing && System.currentTimeMillis() < stopTime) {
                            try {
                                Thread.sleep(TimeConstants.SENSOR_DATA_POLL_INTERVAL);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        SENSOR_MANAGER.unregisterListener(BATCH_SENSOR_LISTENER);
                        UNREGISTER_HANDLER.removeCallbacksAndMessages(null);
                    }
                }
            };

            // Flush and unregister any existing listeners
            UNREGISTER_RUNNABLE.run();

            // Make a copy of any existing data in the batch and reset it
            Map<String, Object> previousBatch = getDownsampledBatch();
            operateOnBatchBuffer(BatchOps.CLEAR, null);

            // Set up the sensor to use the hardware FIFO batch queue
            int samplingPeriodMs = (int)TimeConstants.SENSOR_DATA_POLL_INTERVAL;
            int samplingPeriodUs = samplingPeriodMs * 1000;  // ms to us
            int maxReportLatencyUs = samplingPeriodUs * batchSize;
            int maxReportLatencyMs = maxReportLatencyUs / 1000; // us to ms
            SENSOR_MANAGER.registerListener(BATCH_SENSOR_LISTENER,
                                           sensor,
                                           samplingPeriodUs,
                                           maxReportLatencyUs);

            // Schedule to flush and stop the sensor when it's expected to be full
            UNREGISTER_HANDLER.postDelayed(UNREGISTER_RUNNABLE, maxReportLatencyMs);

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
     * @return A static list of processed batch data that persists between service invocations
     */
    protected abstract List<Map<String, Object>> getStaticBatchBuffer();

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
        if(getBatchSize(sensor) > 0) {
            return new WifiUploadDestination(this);
        }

        return super.getDataDestination();
    }

    /**
     *
     * @return An ordered mapping of data points, where consecutive points have at least
     * MIN_INTERVAL_BETWEEN_EVENTS_WITH_TOLERANCE amount of time between them. Null if empty.
     */
    private Map<String, Object> getDownsampledBatch() {
        List<Map<String, Object>> batch = operateOnBatchBuffer(BatchOps.COPY, null);

        if(!batch.isEmpty()) {
            Collections.sort(batch, BATCH_ENTRY_COMPARATOR);

            Map<String, Object> downsampled = new LinkedHashMap<>();
            long lastTimestamp = -1l;

            for(Map<String, Object> data : batch) {
                long currentTimestamp = (long)data.get(TIMESTAMP_KEY);
                long interval = currentTimestamp - lastTimestamp;

                if(lastTimestamp < 0l || interval >= MIN_INTERVAL_BETWEEN_EVENTS_WITH_TOLERANCE) {
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