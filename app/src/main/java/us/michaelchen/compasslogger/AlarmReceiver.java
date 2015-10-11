package us.michaelchen.compasslogger;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.firebase.client.Firebase;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

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
    private Context context;

    public static final String ROTATION_KEY = "Rotation";
    public static final String AZIMUTH_KEY = "Azimuth";
    public static final String USER_DATA_KEY = "userData";
    public static final String LOCATION_KEY = "location";
    public static final String LATITUDE_KEY = "latitude";
    public static final String LONGITUDE_KEY = "longitude";
    public static final String GPS_KEY = "gps";

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
//        throw new UnsupportedOperationException("Not yet implemented");
        this.context = context;
        Firebase.setAndroidContext(context);
        firebase = new Firebase("https://luminous-torch-7892.firebaseio.com/");
        deviceId = getDeviceId();

        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ROTATION_VECTOR);
        if(sensors.size() > 0){
            sensorManager.registerListener(sensorEventListener, sensors.get(0), SensorManager.SENSOR_DELAY_UI);
        }
    }

    public String getDeviceId() {
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
            SensorManager.getRotationMatrixFromVector(rMat, event.values);
            // get the azimuth value (orientation[0]) in degree
            int azimuth = (int) ( Math.toDegrees( SensorManager.getOrientation( rMat, orientation )[0] ) + 360 ) % 360;

            Log.d(TAG, event.toString());
            Firebase dataRef = firebase.child(USER_DATA_KEY).child(deviceId);
            Firebase locationRef = dataRef.child(LOCATION_KEY);

            List<Float> values = new ArrayList<>(event.values.length);
            for (int i = 0; i < event.values.length; i++) {
                values.add(event.values[i]);
            }

            Map<String, Object> map = new HashMap<>();
            map.put(AZIMUTH_KEY, azimuth);
            map.put(ROTATION_KEY, values);
            String locationProvider = LocationManager.GPS_PROVIDER;
            int permissionCheck = ContextCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_FINE_LOCATION);
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                Location location = locationManager.getLastKnownLocation(locationProvider);
                HashMap<String, Double> coords = new HashMap<>();
                if (location == null) {
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }
                if (location != null) {
                    coords.put(LATITUDE_KEY, location.getLatitude());
                    coords.put(LONGITUDE_KEY, location.getLongitude());
                }
                map.put(GPS_KEY, coords);
            } else {
                // Get coarse location?
            }


            locationRef.push().setValue(map);
//            firebase.child("Message").setValue(values);
        }
    };
}
