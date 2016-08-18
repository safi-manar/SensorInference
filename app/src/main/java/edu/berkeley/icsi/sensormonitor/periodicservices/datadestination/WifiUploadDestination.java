package edu.berkeley.icsi.sensormonitor.periodicservices.datadestination;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import edu.berkeley.icsi.sensormonitor.utils.DataTimeFormat;
import edu.berkeley.icsi.sensormonitor.utils.FirebaseWrapper;
import edu.berkeley.icsi.sensormonitor.utils.PreferencesWrapper;
import edu.berkeley.icsi.sensormonitor.utils.TimeConstants;

/**
 * Created by ioreyes on 6/23/16.
 */
public class WifiUploadDestination extends AbstractDataDestination {
    private static final String JSON_EXTENSION = ".json";
    private static final String ZIP_EXTENSION = ".zip";
    private static final FileFilter ZIP_FILTER = new FileFilter() {
        @Override
        public boolean accept(File pathname) {
            return pathname.getAbsolutePath().endsWith(ZIP_EXTENSION);
        }
    };
    private static final String CACHE_SUBDIR_NAME = "wifiUploads";

    private static Context appContext = null;
    private static int activeCount = 0;

    /**
     *
     * @param context Calling Android context, necessary to access disk resources
     */
    public WifiUploadDestination(Context context) {
        this.appContext = context.getApplicationContext();
    }

    @Override
    public void submit(String label, Map<String, Object> data) {
        if(appContext != null && data != null) {
            increaseActive();
            saveToCache(label, data);

            // Upload if any are true:
            // - Wi-Fi is available
            // - App is about to be uninstalled
            // - The local cache has grown too large
            boolean shouldUpload = isWifiConnected() || isUninstallationImminent() || isCacheTooLarge();
            if(isLastActive() && shouldUpload) {
                uploadAndClearCache();
            }
        }
    }

    /**
     * Checks if there's an active wi-fi connection
     * @return True if there's an active wi-fi connection
     */
    private boolean isWifiConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo active = connectivityManager.getActiveNetworkInfo();

        if(active != null) {
            return active.getType() == ConnectivityManager.TYPE_WIFI;
        } else {
            return false;
        }
    }

    /**
     *
     * @return True if the app is about to be uninstalled
     */
    private boolean isUninstallationImminent() {
        long currentTime = System.currentTimeMillis();
        long uninstallTime = PreferencesWrapper.getUninstallDeadline();

        return uninstallTime - currentTime <= TimeConstants.PRE_DEADLINE_WINDOW;
    }

    /**
     *
     * @return True if the total size of the zip file cache exceeds 25 MBs
     */
    private boolean isCacheTooLarge() {
        final int MAX_SIZE_MB = 25;
        final int MAX_SIZE_B = MAX_SIZE_MB * 1024 * 1024;

        File wifiCacheFolder = new File(appContext.getCacheDir(), CACHE_SUBDIR_NAME);
        if(wifiCacheFolder.exists()) {
            File[] zipFiles = wifiCacheFolder.listFiles(ZIP_FILTER);
            int sizeB = 0;

            for(File f : zipFiles) {
                sizeB += f.length();
            }

            return sizeB >= MAX_SIZE_B;
        }

        return false;
    }

    /**
     * Write the data as a zipped JSON file in the app cache
     * @param label Sensor label
     * @param data Labeled measurements from the sensor
     */
    private void saveToCache(String label, Map<String, Object> data) {
        // Make a subfolder in the cache if necessary
        File wifiCacheFolder = new File(appContext.getCacheDir(), CACHE_SUBDIR_NAME);
        if(!wifiCacheFolder.exists()) {
            wifiCacheFolder.mkdirs();
        }

        String shortID = PreferencesWrapper.getShortDeviceID();
        String readableTime = DataTimeFormat.current();
        String baseFilename = String.format("%s--%s--%s", label, shortID, readableTime);

        // Write a pretty-printed zipped JSON file in the cache
        try {
            JSONObject jsonData = toJSON(data);

            File zipFile = new File(wifiCacheFolder, baseFilename + ZIP_EXTENSION);
            FileOutputStream fos = new FileOutputStream(zipFile);
            ZipOutputStream zos = new ZipOutputStream(fos);

            ZipEntry entry = new ZipEntry(baseFilename + JSON_EXTENSION);
            zos.putNextEntry(entry);

            byte[] dataBytes = jsonData.toString(4).getBytes();  // 4 to pretty-print with 1 tab = 4 spaces
            zos.write(dataBytes, 0, dataBytes.length);
            zos.closeEntry();

            zos.close();
            fos.close();
        } catch(IOException | JSONException e) {
            // Log any JSON or file-writing problems
            String tag = getClass().getSimpleName();
            Log.w(tag, e.getMessage());
        }
    }

    /**
     * Upload zipped JSONs to remote server, delete on success
     */
    private void uploadAndClearCache() {
        File wifiCacheFolder = new File(appContext.getCacheDir(), CACHE_SUBDIR_NAME);
        if(wifiCacheFolder.exists()) {
            File[] zipFiles = wifiCacheFolder.listFiles(ZIP_FILTER);
            final boolean DELETE_ON_SUCCESS = true;

            FirebaseWrapper.uploadSequentially(appContext, zipFiles, DELETE_ON_SUCCESS);
        }
    }

    /**
     * Convert data to a JSON structure
     * @param data Mapping of labels to values (may be nested)
     * @throws JSONException If there's a problem creating JSON data
     * @return JSON representation of data
     */
    private JSONObject toJSON(Map<String, Object> data) throws JSONException {
        JSONObject json = new JSONObject();

        for(String label : data.keySet()) {
            Object value = data.get(label);

            if(value instanceof Map) {
                JSONObject subJson = toJSON((Map)value);
                json.put(label, subJson);
            } else {
                json.put(label, value.toString());
            }
        }

        return json;
    }

    /**
     * Increment the number of active writers in a thread-safe manner
     */
    private synchronized void increaseActive() {
        activeCount++;
    }

    /**
     * Decrement the number of active writers in a thread-safe manner
     * @return True if this was the last active writer
     */
    private synchronized boolean isLastActive() {
        // Expected case
        if(activeCount > 0) {
            return --activeCount == 0;
        }

        // Weird case where it goes zero or negative
        // (this shouldn't happen, but checking for sanity's sake)
        activeCount = 0;
        return true;
    }
}
