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
    protected static final String TIMESTAMP_TYPE_KEY = "timestampType";
    protected static final String TIMESTAMP_RAW_KEY = "timestampRaw";

    private enum TimestampType {
        EPOCH_MS,
        EPOCH_NS,
        BOOT_MS,
        BOOT_NS,
        ARBITRARY,
    };

    private static final String VALUES_KEY = "values-%02d";

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
    protected Map<String, Object> readData(Intent intent) {
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

        if(sensor != null) {
            sensorManager.registerListener(SENSOR_LISTENER, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
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
        Map<String, Object> timestamp = getTimestamp(event);
        float[] values = event.values;

        Map<String, Object> data = new HashMap<>();
        data.putAll(timestamp);

        for(int n = 0; n < values.length; n++) {
            String valuesKey = String.format(VALUES_KEY, n);
            data.put(valuesKey, values[n]);
        }

        return data;
    }

    /**
     * Get the timestamp from a SensorEvent
     * @param event
     * @return A map containing the raw timestamp, its definition (as a time since epoch/boot in ms/ns, or
     * arbitrary), and if not arbitrary, the timestamp in milliseconds since epoch and its human-readable representation
     */
    private Map<String, Object> getTimestamp(SensorEvent event) {
        // Determine the definition of the event timestamp:
        // 1. Milliseconds since boot
        //    The event happened within 2 periodic lengths in the past and the timestamp has the same number of digits as the uptime timestamp
        // 2. Nanoseconds since boot
        //    The event happened within 2 periodic length in the past and the timestamp has the same nubmer of digits as the uptime timestamp in nanoseconds
        // 3. Milliseconds since epoch
        //    If not boot-relative, at least 40 years have elapsed and the timestamp has the same number of digits as the epoch time in millis
        // 4. Nanoseconds since epoch
        //    If not boot-relative, at least 40 years have elapsed and the timestamp has the same number of digits as the epoch time in nanos
        // 5. Arbitrary milliseconds or nanoseconds
        //    None of the above are true
        long eventTimestamp = event.timestamp;

        long epochMS = System.currentTimeMillis();
        long epochNS = epochMS * (long)1e6;         // ms to ns
        long bootMS = SystemClock.elapsedRealtime();
        long bootNS = bootMS * (long)1e6;           // ms to ns
        long periodicMS = TimeConstants.PERIODIC_LENGTH;
        long periodicNS = periodicMS * (long)1e6;   // ms to ns

        int timestampDigits = (int) Math.floor(Math.log10(eventTimestamp));
        int epochDigits = (int) Math.floor(Math.log10(epochMS));
        int bootDigits = (int) Math.floor(Math.log10(bootMS));

        boolean isBootMS = timestampDigits == bootDigits && bootMS - eventTimestamp < 2 * periodicMS;
        boolean isBootNS = timestampDigits == bootDigits + 6 && bootNS - eventTimestamp < 2 * periodicNS;
        boolean isEpochMS = timestampDigits == epochDigits && eventTimestamp > TimeConstants.FORTY_YEARS_MS;
        boolean isEpochNS = timestampDigits == epochDigits + 6 && eventTimestamp > TimeConstants.FORTY_YEARS_NS; // 10^6 more ns than ms
        boolean isArbitrary = !(isEpochMS || isEpochNS || isBootMS || isBootNS);

        Map<String, Object> timestampMap = new HashMap<>();
        long timestampMS = -1;
        String timestampReadable = "";
        String timestampType = "";

        if(isBootMS) {
            timestampMS = epochMS - bootMS + eventTimestamp;
            timestampReadable = DataTimeFormat.format(timestampMS);
            timestampType = TimestampType.BOOT_MS.name();
        } else if(isBootNS) {
            timestampMS = epochMS - bootMS + (eventTimestamp / (long) 1e6);  // ns to ms
            timestampReadable = DataTimeFormat.format(timestampMS);
            timestampType = TimestampType.BOOT_NS.name();
        } else if(isEpochMS) {
            timestampMS = eventTimestamp;
            timestampReadable = DataTimeFormat.format(timestampMS);
            timestampType = TimestampType.EPOCH_MS.name();
        } else if(isEpochNS) {
            timestampMS = eventTimestamp / (long)1e6; // ns to ms
            timestampReadable = DataTimeFormat.format(timestampMS);
            timestampType = TimestampType.EPOCH_NS.name();
        } else if(isArbitrary) {
            timestampMS = -1;   // Con't convert to epoch time
            timestampReadable = ""; // Nothing to convert
            timestampType = TimestampType.ARBITRARY.name();
        }
        timestampMap.put(super.TIMESTAMP_KEY, timestampMS);
        timestampMap.put(super.READABLE_TIME_KEY, timestampReadable);
        timestampMap.put(TIMESTAMP_RAW_KEY, eventTimestamp);
        timestampMap.put(TIMESTAMP_TYPE_KEY, timestampType);

        return timestampMap;
    }

    /**
     * Defines which sensor to use
     * @return A Sensor.SENSOR_TYPE enumerated value
     */
    protected abstract int getSensorType();
}
