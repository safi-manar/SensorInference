package us.michaelchen.compasslogger.datarecorder;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ioreyes on 5/27/16.
 */
public class LocationRecordingService extends AbstractRecordingService {
    private static final String LATITUDE_KEY = "lat";
    private static final String LONGITUDE_KEY = "lon";

    private Map<String, Object> data = null;
    private boolean hasPermissions = false;

    private final LocationListener LOCATION_LISTENER = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            data = new HashMap<>();
            data.put(LATITUDE_KEY, location.getLatitude());
            data.put(LONGITUDE_KEY, location.getLongitude());
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    public LocationRecordingService() {
        super("LocationRecordingService");
    }


    @Override
    protected String broadcastKey() {
        return "gps";
    }

    @Override
    protected Map<String, Object> readData(Intent intent) {
        registerLocationListener();
        while(hasPermissions && data == null) {

        }
        unregisterLocationListener();

        return data;
    }

    /**
     * Starts updates from the location service
     */
    private void registerLocationListener() {
        data = null;
        hasPermissions = false;

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
           ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            hasPermissions = true;

            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0.0f, LOCATION_LISTENER);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0.0f, LOCATION_LISTENER);
        }
    }

    /**
     * Stops updates from the location service
     */
    private void unregisterLocationListener() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
           ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.removeUpdates(LOCATION_LISTENER);
        }
    }
}
