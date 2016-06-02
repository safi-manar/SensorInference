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

import us.michaelchen.compasslogger.utils.BetterLocation;
import us.michaelchen.compasslogger.utils.DataTimeFormat;

/**
 * Created by ioreyes on 5/27/16.
 */
public class LocationRecordingService extends AbstractRecordingService {
    private static final String FIX_TIME_KEY = "fixTime";
    private static final String LATITUDE_KEY = "lat";
    private static final String LONGITUDE_KEY = "lon";
    private static final String PROVIDER_KEY = "provider";

    private static final int POLL_INTERVAL_MS = 500;
    private static final int FIX_WINDOW_SECS = 3;
    private static final int FIX_WINDOW_MS = FIX_WINDOW_SECS * 1000;

    private Location bestLocation = null;
    private long endTime = Long.MIN_VALUE;
    private boolean hasPermissions = false;
    private LocationManager locationManager = null;

    private final LocationListener LOCATION_LISTENER = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            bestLocation = BetterLocation.compare(location, bestLocation);
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
        return "location";
    }

    @Override
    protected Map<String, Object> readData(Intent intent) {
        // Open up the GPS for FIX_WINDOW seconds and grab the best location in that time
        registerLocationListener();
        while(hasPermissions && System.currentTimeMillis() < endTime) {
            try {
                Thread.sleep(POLL_INTERVAL_MS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        unregisterLocationListener();

        Map<String, Object> data = new HashMap<>();
        if(bestLocation != null) {
            data.put(FIX_TIME_KEY, DataTimeFormat.format(bestLocation.getTime()));
            data.put(LATITUDE_KEY, bestLocation.getLatitude());
            data.put(LONGITUDE_KEY, bestLocation.getLongitude());
            data.put(PROVIDER_KEY, bestLocation.getProvider());
        }

        return data;
    }

    /**
     * Starts updates from the location service
     */
    private void registerLocationListener() {
        endTime = System.currentTimeMillis() + FIX_WINDOW_MS;
        hasPermissions = false;
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            hasPermissions = true;

            for(String provider : locationManager.getAllProviders()) {
                // Register the listener to all providers
                locationManager.requestLocationUpdates(provider, 0, 0.0f, LOCATION_LISTENER);

                // Get the best last known location from all the providers
                Location loc = locationManager.getLastKnownLocation(provider);
                bestLocation = BetterLocation.compare(loc, bestLocation);
            }
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
