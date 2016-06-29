package us.michaelchen.compasslogger.periodicservices.datadestination;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import us.michaelchen.compasslogger.utils.DataTimeFormat;
import us.michaelchen.compasslogger.utils.PreferencesWrapper;

/**
 * Created by ioreyes on 6/23/16.
 */
public class WifiUploadDestination extends AbstractDataDestination {
    private static final String JSON_EXTENSION = ".json";
    private static final FileFilter JSON_FILTER = new FileFilter() {
        @Override
        public boolean accept(File pathname) {
            return pathname.getAbsolutePath().endsWith(JSON_EXTENSION);
        }
    };


    private static Context appContext = null;

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
            saveToCache(label, data);

            if(isWifiConnected()) {
                uploadAndClearCache(label);
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
     * Write the data as a JSON file to the app cache
     * @param label Sensor label
     * @param data Labeled measurements from the sensor
     */
    private void saveToCache(String label, Map<String, Object> data) {
        // Make a sensor-specific folder in the cache if necessary
        File sensorCacheFolder = new File(appContext.getCacheDir(), label);
        if(!sensorCacheFolder.exists()) {
            sensorCacheFolder.mkdirs();
        }

        // Write a pretty-printed JSON file in the cache
        try {
            JSONObject jsonData = toJSON(data);

            String shortID = PreferencesWrapper.getShortDeviceID();
            String readableTime = DataTimeFormat.current();
            String jsonFilename = String.format("%s--%s--%s" + JSON_EXTENSION, label, shortID, readableTime);

            File jsonFile = new File(sensorCacheFolder, jsonFilename);
            FileOutputStream fos = new FileOutputStream(jsonFile);
            fos.write(jsonData.toString(4).getBytes());     // 4 to pretty-print with tab = 4 spaces
            fos.close();
        } catch(IOException | JSONException e) {
            // Log any JSON or file-writing problems
            String tag = getClass().getSimpleName();
            Log.w(tag, e.getMessage());
        }
    }

    /**
     * Compress cached data, upload to remote server, and clear cache
     * @param label Sensor label
     */
    private void uploadAndClearCache(String label) {
        File sensorCacheFolder = new File(appContext.getCacheDir(), label);
        if(sensorCacheFolder.exists()) {
            // Compress the data
            try {
                String shortID = PreferencesWrapper.getShortDeviceID();
                String readableTime = DataTimeFormat.current();
                String zipFilename = String.format("%s--%s--%s.zip", label, shortID, readableTime);

                File zipFile = new File(sensorCacheFolder, zipFilename);
                FileOutputStream fos = new FileOutputStream(zipFile);
                ZipOutputStream zip = new ZipOutputStream(fos);

                for(File cachedJSONFile : sensorCacheFolder.listFiles(JSON_FILTER)) {
                    String path = cachedJSONFile.getAbsolutePath();
                    ZipEntry entry = new ZipEntry(path);
                    zip.putNextEntry(entry);

                    FileInputStream fis = new FileInputStream(cachedJSONFile);
                    byte[] buffer = new byte[1024];     // 1024 byte input buffer
                    int bytesRead = -1;
                    while((bytesRead = fis.read(buffer)) != -1) {
                        zip.write(buffer, 0, bytesRead);
                    }
                    zip.flush();
                    fis.close();
                    zip.closeEntry();
                }

                zip.close();
                fos.close();
            } catch(IOException e) {
                // Log any file-writing problems
                String tag = getClass().getSimpleName();
                Log.w(tag, e.getMessage());
            }

            // Upload to remote server
            // TODO

            // Clear cache
            for(File inCache : sensorCacheFolder.listFiles()) {
                inCache.delete();
            }
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
}
