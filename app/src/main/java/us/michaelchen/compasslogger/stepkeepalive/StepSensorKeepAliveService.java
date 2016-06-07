package us.michaelchen.compasslogger.stepkeepalive;

import android.app.IntentService;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Created by ioreyes on 6/7/16.
 */
public class StepSensorKeepAliveService extends IntentService {
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
    public static final String DEACTIVATE_EXTRA = "deactivate";

    public StepSensorKeepAliveService() {
        super("StepSensorKeepAliveService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
            if(intent.hasExtra(DEACTIVATE_EXTRA)) {
                deactivateStepSensor();
            } else {
                activateStepSensor();
            }
    }

    /**
     * Register a dummy listener to the step sensor to keep it alive
     */
    private void activateStepSensor() {
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        sensorManager.registerListener(DO_NOTHING_LISTENER, stepSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    /**
     * Unregister the dummy listener
     */
    private void deactivateStepSensor() {
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        sensorManager.unregisterListener(DO_NOTHING_LISTENER);
    }
}
