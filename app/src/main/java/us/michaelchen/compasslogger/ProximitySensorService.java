package us.michaelchen.compasslogger;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.firebase.client.Firebase;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class ProximitySensorService extends RecordingService {

    public static final String TAG = "LightSensorService";
    private SensorManager sensorManager;
    public static final String PROXIMITY_KEY = "proximity";


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            super.initContext(this);
            sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
            Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
            sensorManager.registerListener(sensorEventListener, sensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    private SensorEventListener sensorEventListener = new SensorEventListener(){

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            // TODO Auto-generated method stub
            try {
                sensorManager.unregisterListener(this);
                float luminance = event.values[0];

                Log.d(TAG, event.toString());
                Firebase dataRef = firebase.child(USER_DATA_KEY).child(deviceId);
                Firebase ref = dataRef.child(PROXIMITY_KEY);

                List<Float> values = new ArrayList<>(event.values.length);
                for (int i = 0; i < event.values.length; i++) {
                    values.add(event.values[i]);
                }

                Map<String, Object> map = new HashMap<>();
                map.put(TIME, new Date().toString());
                map.put(PROXIMITY_KEY, luminance);

                ref.push().setValue(map);
            } catch (RuntimeException e) {
                Log.e(TAG, TAG, e);
            }
        }
    };
}
