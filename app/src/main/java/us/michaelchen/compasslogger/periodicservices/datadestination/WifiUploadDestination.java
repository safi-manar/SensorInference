package us.michaelchen.compasslogger.periodicservices.datadestination;

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

import us.michaelchen.compasslogger.utils.DataTimeFormat;
import us.michaelchen.compasslogger.utils.FirebaseWrapper;
import us.michaelchen.compasslogger.utils.PreferencesWrapper;

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
     * Write the data as a zipped JSON file in the app cache
     * @param label Sensor label
     * @param data Labeled measurements from the sensor
     */
    private void saveToCache(String label, Map<String, Object> data) {
        // Make a sensor-specific folder in the cache if necessary
        File sensorCacheFolder = new File(appContext.getCacheDir(), label);
        if(!sensorCacheFolder.exists()) {
            sensorCacheFolder.mkdirs();
        }

        String shortID = PreferencesWrapper.getShortDeviceID();
        String readableTime = DataTimeFormat.current();
        String baseFilename = String.format("%s--%s--%s", label, shortID, readableTime);

        // Write a pretty-printed zipped JSON file in the cache
        try {
            JSONObject jsonData = toJSON(data);

            File zipFile = new File(sensorCacheFolder, baseFilename + ZIP_EXTENSION);
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
     * @param label Sensor label
     */
    private void uploadAndClearCache(String label) {
        File sensorCacheFolder = new File(appContext.getCacheDir(), label);
        if(sensorCacheFolder.exists()) {
            File[] zipFiles = sensorCacheFolder.listFiles(ZIP_FILTER);
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
}
