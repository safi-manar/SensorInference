package edu.berkeley.icsi.sensormonitor.periodicservices.keepalive;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Created by ioreyes on 6/20/16.
 */
public class StepSensorKeepAliveService extends AbstractKeepAliveService {
    private static final SensorEventListener DO_NOTHING_LISTENER = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            // Do nothing, just keep the sensor alive
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // Do nothing, just keep the sensor alive
        }
    };


    public StepSensorKeepAliveService() {
        super("StepSensorKeepAliveService");
    }

    @Override
    protected void keepAlive() {
        Context app = getApplicationContext();

        SensorManager sensorManager = (SensorManager) app.getSystemService(Context.SENSOR_SERVICE);
        Sensor stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        sensorManager.registerListener(DO_NOTHING_LISTENER, stepSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void shutDown() {
        Context app = getApplicationContext();

        SensorManager sensorManager = (SensorManager) app.getSystemService(Context.SENSOR_SERVICE);

        sensorManager.unregisterListener(DO_NOTHING_LISTENER);
    }
}
