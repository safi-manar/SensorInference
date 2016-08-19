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

    // Cap the total amount uploaded over a cell network; exceeding this will limit all future uploads to wi-fi only
    private static final int CELL_CAP_MB = 300;
    private static final int CELL_CAP_B = 1024 * 1024 * CELL_CAP_MB;    // b to kb to mb

    // (Soft) Cap the total amount of data that can be kept in the cache; exceeding this may trigger a cell network upload
    private static final int CACHE_CAP_MB = 25;
    private static final int CACHE_CAP_B = 1024 * 1024 * CACHE_CAP_MB;   // b to kb to mb

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
            increaseActive();   // Only allow the last active thread to upload
            saveToCache(label, data);

            // Upload under the following conditions:
            // - Wi-Fi is available
            // ---OR---
            // - Will stay under the cell upload cap
            //      ---AND---
            //      - App is about to be uninstalled
            //      ---OR---
            //      - The local cache has grown too large
            int cacheSizeBytes = getCacheSize();

            boolean isWifiConnected = isWifiConnected();
            boolean isBelowCellCap = PreferencesWrapper.getCellUploadBytes() + cacheSizeBytes <= CELL_CAP_B;
            boolean isCacheTooLarge = getCacheSize() >= CACHE_CAP_B;
            boolean isCellUploadAllowed = isBelowCellCap && (isUninstallationImminent() || isCacheTooLarge);
            boolean shouldUpload = isWifiConnected || isCellUploadAllowed;

            if(isLastActive() && shouldUpload) {
                uploadAndClearCache();

                // Keep track of cell network uploads
                if(!isWifiConnected) {
                    PreferencesWrapper.incrementCellUploadBytes(cacheSizeBytes);
                }
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
     * @return The size of the cache, in bytes
     */
    private int getCacheSize() {
        File wifiCacheFolder = new File(appContext.getCacheDir(), CACHE_SUBDIR_NAME);
        int sizeB = 0;

        if(wifiCacheFolder.exists()) {
            File[] zipFiles = wifiCacheFolder.listFiles(ZIP_FILTER);

            for(File f : zipFiles) {
                sizeB += f.length();
            }
        }

        return sizeB;
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
