package us.michaelchen.compasslogger;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public abstract class SensorService extends RecordingService {

    public static final String TAG = "SensorService";
    private SensorManager sensorManager;

    abstract String getKey();
    abstract int getSensorType();

    @Override
    protected void onHandleIntent(Intent intent) {
        super.onHandleIntent(intent);
        if (intent != null) {
            super.initContext(this);
            sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            Sensor sensor = sensorManager.getDefaultSensor(getSensorType());
            sensorManager.registerListener(sensorEventListener, sensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    private SensorEventListener sensorEventListener = new SensorEventListener() {

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            // TODO Auto-generated method stub
            sensorManager.unregisterListener(this);
            float value = event.values[0];
            Log.d(TAG, event.toString());

            Map<String, Object> map = new HashMap<>();
            map.put(TIME, new Date().toString());
            map.put(getKey(), value);
            updateDatabase(getKey(), map);
        }
    };
}
