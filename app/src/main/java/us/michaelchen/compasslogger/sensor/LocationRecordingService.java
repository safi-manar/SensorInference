package us.michaelchen.compasslogger.sensor;

import android.Manifest;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;

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
public class LocationRecordingService extends RecordingService {

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            super.initContext(this);
            sensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
            List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ROTATION_VECTOR);
            if(sensors.size() > 0){
                sensorManager.registerListener(sensorEventListener, sensors.get(0), SensorManager.SENSOR_DELAY_UI);
            }
        }
    }

    public static final String TAG = "LocationService";
    private SensorManager sensorManager;

    public static final String ROTATION_KEY = "rotation";
    public static final String AZIMUTH_KEY = "azimuth";
    public static final String LOCATION_KEY = "location";
    public static final String LATITUDE_KEY = "latitude";
    public static final String LONGITUDE_KEY = "longitude";
    public static final String GPS_KEY = "gps";

    private SensorEventListener sensorEventListener = new SensorEventListener(){

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            sensorManager.unregisterListener(this);
            float[] rMat = new float[9];
            float[] orientation = new float[3];
            SensorManager.getRotationMatrixFromVector(rMat, event.values);
            // get the azimuth value (orientation[0]) in degree
            int azimuth = (int) ( Math.toDegrees( SensorManager.getOrientation( rMat, orientation )[0] ) + 360 ) % 360;

            Log.d(TAG, event.toString());

            List<Float> values = new ArrayList<>(event.values.length);
            for (int i = 0; i < event.values.length; i++) {
                values.add(event.values[i]);
            }

            Map<String, Object> map = new HashMap<>();
            map.put(TIME, new Date().toString());
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
            updateDatabase(LOCATION_KEY, map);
        }
    };
}
