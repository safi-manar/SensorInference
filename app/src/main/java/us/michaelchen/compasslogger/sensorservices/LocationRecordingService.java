package us.michaelchen.compasslogger.sensorservices;

import android.Manifest;
import android.content.Context;
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

    private final LocationListener LOCATION_LISTENER = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            // Put in database
            Map<String, Object> vals = new HashMap<>();
            vals.put(LATITUDE_KEY, location.getLatitude());
            vals.put(LONGITUDE_KEY, location.getLongitude());

            updateDatabase(vals);

            // Unregister from the location manager to prevent continuous GPS use
            unregisterLocationListener();
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

    private static LocationManager locationManager = null;

    public LocationRecordingService() {
        super("LocationRecordingService");
    }


    @Override
    protected String broadcastKey() {
        return "gps";
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(locationManager == null) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }

        registerLocationListener();
    }

    /**
     * Starts updates from the location service
     */
    private void registerLocationListener() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
           ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0.0f, LOCATION_LISTENER);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0.0f, LOCATION_LISTENER);
        }
    }

    /**
     * Stops updates from the location service
     */
    private void unregisterLocationListener() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
           ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.removeUpdates(LOCATION_LISTENER);
        }
    }
}
