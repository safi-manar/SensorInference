package us.michaelchen.compasslogger.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.Map;

public class FirebaseWrapper {
    private static final String USER_DATA_KEY = "userData";

    private static DatabaseReference deviceDb = null;
    private static StorageReference deviceStore = null;

    /**
     * Initialize the Firebase connection
     */
    public static void init() {
        if(!isInit()) {
            String deviceId = PreferencesWrapper.getDeviceID();

            FirebaseDatabase db = FirebaseDatabase.getInstance();
            db.setPersistenceEnabled(true);
            DatabaseReference dbRef = db.getReference();
            deviceDb = dbRef.child(USER_DATA_KEY).child(deviceId);

            FirebaseStorage store = FirebaseStorage.getInstance();
            StorageReference storeRef = store.getReference();
            deviceStore = storeRef.child(deviceId);
        }
    }

    /**
     *
     * @return True if the Firebase connection is initialized
     */
    public static boolean isInit() {
        return deviceDb != null && deviceStore != null;
    }

    /**
     * Push the data to the Firebase backend.
     * @param key Identifying key
     * @param data Label-value mapping of data to be submitted
     */
    public static void push(String key, Map<String, Object> data) {
        // Ensure that Firebase is initialized
        init();

        // Pushes to the Firebase with a hierarchy as follows:
        /*
        *   UUID
        *       Sensor
        *           TimeStamp
        *               DataEntry.
        * */
        String timeStamp = DataTimeFormat.current();
        deviceDb.child(key).child(timeStamp).setValue(data);
    }

    /**
     * Upload the file to the Firebase Storage backend
     * @param file Reference to the file to upload
     * @return UploadTask to monitor the status of the upload
     */
    public static UploadTask upload(File file) {
        // Ensure that Firebase is initialized
        init();

        Uri uri = Uri.fromFile(file);
        StorageReference fileStore = deviceStore.child(uri.getLastPathSegment());
        return fileStore.putFile(uri);
    }

    /**
     * Upload the given files sequentially
     * @param context Calling Android context
     * @param paths Array of files to upload
     * @param deleteOnSuccess Set to true to delete files upon successful upload
     */
    public static void uploadSequentially(Context context, File[] paths, boolean deleteOnSuccess) {
        String[] pathStrings = new String[paths.length];
        for(int n = 0; n < paths.length; n++) {
            pathStrings[n] = paths[n].getAbsolutePath();
        }

        Intent intent = new Intent(context, FirebaseSequentialUploadService.class);
        intent.putExtra(FirebaseSequentialUploadService.PATHS_EXTRA, pathStrings);
        intent.putExtra(FirebaseSequentialUploadService.DELETE_EXTRA, deleteOnSuccess);
        context.startService(intent);
    }

    /**
     * Report an exception to Firebase
     * @param e Exception describing the crash
     */
    public static void reportCrash(Exception e) {
        FirebaseCrash.report(e);
    }

    /**
     * Report a crash message to Firebase
     * @param message Message to report to Firebase
     */
    public static void reportCrash(String message) {
        Exception e = new Exception(message);
        reportCrash(e);
    }
}
