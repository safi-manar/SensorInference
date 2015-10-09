package us.michaelchen.compasslogger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.firebase.client.Firebase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AlarmReceiver extends BroadcastReceiver {

    public static final String TAG = "AlarmReceiver";
    private SensorManager sensorManager;
    private Firebase firebase;
    private String deviceId;

    public static final String ROTATION_KEY = "Rotation";
    public static final String AZIMUTH_KEY = "Azimuth";
    public AlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
//        throw new UnsupportedOperationException("Not yet implemented");
        Firebase.setAndroidContext(context);
        firebase = new Firebase("https://luminous-torch-7892.firebaseio.com/");
        deviceId = getDeviceId(context);

        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ROTATION_VECTOR);
        if(sensors.size() > 0){
            sensorManager.registerListener(sensorEventListener, sensors.get(0), SensorManager.SENSOR_DELAY_UI);
        }
    }

    public String getDeviceId(Context context) {
        final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        final String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String deviceId = deviceUuid.toString();
        return deviceId;
    }

    private SensorEventListener sensorEventListener = new SensorEventListener(){

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            // TODO Auto-generated method stub
            sensorManager.unregisterListener(this);
            float[] rMat = new float[9];
            float[] orientation = new float[3];
            SensorManager.getRotationMatrixFromVector( rMat, event.values );
            // get the azimuth value (orientation[0]) in degree
            int azimuth = (int) ( Math.toDegrees( SensorManager.getOrientation( rMat, orientation )[0] ) + 360 ) % 360;

            Log.d(TAG, event.toString());
            Firebase dataRef = firebase.child("usersData").child(deviceId);

            List<Float> values = new ArrayList<>(event.values.length);
            for (int i = 0; i < event.values.length; i++) {
                values.add(event.values[i]);
            }

            Map<String, Object> map = new HashMap<>();
            map.put(AZIMUTH_KEY, azimuth);
            map.put(ROTATION_KEY, values);

            dataRef.push().setValue(map);
//            firebase.child("Message").setValue(values);
        }
    };
}
